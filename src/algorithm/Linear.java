package algorithm;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import data.Data;
import model.Client;
import model.Depot;
import model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class Linear {

	private static int PRECISION_COEFF = 1000;

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
		int nbVehicles = 1;
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
				serviceTime[i] = client.getServiceTime();
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
					return dataModel.distances[fromNode][toNode];
		});
		routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

		RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
						.toBuilder()
						.setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
						.build();

		Assignment solution = routing.solveWithParameters(searchParameters);

		printSolution(routing, manager, solution);
	}

	static void printSolution(
			RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
		// Solution cost.
		System.out.println("Objective: " + solution.objectiveValue() / PRECISION_COEFF);
		// Inspect solution.
		System.out.println("Route:");
		long routeDistance = 0;
		StringBuilder route = new StringBuilder();
		long index = routing.start(0);
		while (!routing.isEnd(index)) {
			route.append(manager.indexToNode(index)).append(" -> ");
			long previousIndex = index;
			index = solution.value(routing.nextVar(index));
			routing.getArcCostForVehicle(previousIndex, index, 0);
		}
		route.append(manager.indexToNode(routing.end(0)));
		System.out.println(route);
		System.out.println("Route distance: " + routeDistance);
	}

}
