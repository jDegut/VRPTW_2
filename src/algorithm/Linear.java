package algorithm;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import data.Data;
import model.*;
import view.GraphView;

import java.util.ArrayList;
import java.util.List;

public class Linear {

	private static final int PRECISION_COEFF = 1000;

	record DataModel(
			List<Vertex> vertices,
			long[][] distances,
			long[][] timeWindows,
			int nbVehicles,
			int depotIndex,
			long[] demands,
			int capacityMax,
			long[] serviceTime
	) {}

	private static DataModel build(Data data) {
		List<Vertex> vertices = new ArrayList<>();
		vertices.add(data.getDepot());
		vertices.addAll(data.getClients());
		int nbVehicles = 100;
		int depotIndex = 0;
		int capacityMax = data.getMaxQuantity();
		long[] serviceTime = new long[vertices.size()];
		long[][] distances = new long[vertices.size()][vertices.size()];
		long[][] timeWindows = new long[vertices.size()][2];
		long[] demands = new long[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			Vertex from = vertices.get(i);
			if(from instanceof Depot depot) {
				serviceTime[i] = 0;
				timeWindows[i][0] = (long) depot.getReadyTime() * PRECISION_COEFF;
				timeWindows[i][1] = (long) depot.getDueTime() * PRECISION_COEFF;
				demands[i] = 0;
			}
			if(from instanceof Client client) {
				serviceTime[i] = (long) client.getServiceTime() * PRECISION_COEFF;
				timeWindows[i][0] = (long) client.getReadyTime() * PRECISION_COEFF;
				timeWindows[i][1] = (long) client.getDueTime() * PRECISION_COEFF;
				demands[i] = client.getDemand();
			}
			for (int j = 0; j < vertices.size(); j++) {
				Vertex to = vertices.get(j);
				distances[i][j] = (long) (from.getDistance(to) * PRECISION_COEFF);
			}
		}
		return new DataModel(vertices, distances, timeWindows, nbVehicles, depotIndex, demands, capacityMax, serviceTime);
	}

	public static void solve(Data data) {
		Loader.loadNativeLibraries();
		DataModel dataModel = build(data);
		int nbNodes = dataModel.distances.length;
		int nbVehicles = dataModel.nbVehicles;
		int depotIndex = dataModel.depotIndex;

		RoutingIndexManager manager = new RoutingIndexManager(nbNodes, nbVehicles, depotIndex);
		RoutingModel routing = new RoutingModel(manager);

		final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
			int fromNode = manager.indexToNode(fromIndex);
			int toNode = manager.indexToNode(toIndex);
			return dataModel.serviceTime[fromNode] + dataModel.distances[fromNode][toNode];
		});
		final int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
			// Convert from routing variable Index to user NodeIndex.
			int fromNode = manager.indexToNode(fromIndex);
			return dataModel.demands[fromNode];
		});

		routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

		// Distance = time and maxTimePerVehicle is the due time of the depot
		long maxTimePerVehicle = dataModel.timeWindows[0][1];
		routing.addDimension(transitCallbackIndex, maxTimePerVehicle, maxTimePerVehicle,
				false, // start cumul to zero
				"Time");

		routing.addDimension(demandCallbackIndex, 0, // null capacity slack
				dataModel.capacityMax, // vehicle maximum capacities
				true, // start cumul to zero
				"Capacity");

		RoutingDimension timeDimension = routing.getMutableDimension("Time");
		// Add time window constraints for each location except depot.
		for (int i = 1; i < dataModel.timeWindows.length; ++i) {
			long index = manager.nodeToIndex(i);
			timeDimension.cumulVar(index).setRange(dataModel.timeWindows[i][0], dataModel.timeWindows[i][1]);
		}
		// Add time window constraints for each vehicle start node.
		for (int i = 0; i < dataModel.nbVehicles; ++i) {
			long index = routing.start(i);
			timeDimension.cumulVar(index).setRange(dataModel.timeWindows[0][0], dataModel.timeWindows[0][1]);
		}
		for (int i = 0; i < dataModel.nbVehicles; ++i) {
			routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.start(i)));
			routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.end(i)));
		}

		RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
				.toBuilder()
				.setFirstSolutionStrategy(FirstSolutionStrategy.Value.AUTOMATIC)
				.setTimeLimit(Duration.newBuilder().setSeconds(10).build())
				.build();

		Assignment solution = routing.solveWithParameters(searchParameters);

		if(solution != null)
			printSolution(data, dataModel, routing, manager, solution);
		else
			System.out.println("Can't find a solution");
	}

	static void printSolution(Data data, DataModel dataModel, RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
		List<Vehicle> vehicles = new ArrayList<>();
		int nbVehicles = 0;
		for (int i = 0; i < dataModel.nbVehicles; ++i) {
			Vehicle v = new Vehicle((Depot) dataModel.vertices.get(dataModel.depotIndex), dataModel.capacityMax);
			long index = routing.start(i);
			while (!routing.isEnd(index)) {
				int nodeIndex = manager.indexToNode(index);
				if(nodeIndex != dataModel.depotIndex)
					v.addClient((Client) dataModel.vertices.get(nodeIndex));
				index = solution.value(routing.nextVar(index));
			}
			if(v.getClients().size() > 0) nbVehicles++;
			vehicles.add(v);
		}
		Solution s = new Solution(data, vehicles);
		new GraphView(s);
		System.out.println("Number of vehicles used : " + nbVehicles);
		System.out.println("Total cost of the solution : " + s.getTotalDistance());
	}

}
