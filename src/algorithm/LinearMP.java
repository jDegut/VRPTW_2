package algorithm;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import data.Data;
import model.*;
import view.GraphView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LinearMP {

	record DataModel(
			List<Vertex> vertices,
			double[][] distances,
			double[][] timeWindows,
			int nbVehicles,
			int depotIndex,
			double[] demands,
			int capacityMax,
			double[] serviceTime
	) {}

	private static DataModel build(Data data) {
		List<Vertex> vertices = new ArrayList<>();
		vertices.add(data.getDepot());
		vertices.addAll(data.getClients());
		int nbVehicles = 1;
		int depotIndex = 0;
		int capacityMax = data.getMaxQuantity();
		double[] serviceTime = new double[vertices.size()+1];
		double[][] distances = new double[vertices.size()+1][vertices.size()+1];
		double[][] timeWindows = new double[vertices.size()+1][2];
		double[] demands = new double[vertices.size()+1];
		for (int i = 0; i < vertices.size(); i++) {
			Vertex from = vertices.get(i);
			if(from instanceof Depot depot) {
				serviceTime[i] = 0;
				timeWindows[i][0] = depot.getReadyTime();
				timeWindows[i][1] = depot.getDueTime();
				demands[i] = 0;
			}
			if(from instanceof Client client) {
				serviceTime[i] = client.getServiceTime();
				timeWindows[i][0] = client.getReadyTime();
				timeWindows[i][1] = client.getDueTime();
				demands[i] = client.getDemand();
			}
			for (int j = 0; j < vertices.size(); j++) {
				Vertex to = vertices.get(j);
				distances[i][j] = from.getDistance(to);
			}
			distances[i][vertices.size()] = from.getDistance(vertices.get(depotIndex));
		}
		serviceTime[vertices.size()] = serviceTime[depotIndex];
		distances[vertices.size()] = distances[depotIndex];
		timeWindows[vertices.size()] = timeWindows[depotIndex];
		demands[vertices.size()] = demands[depotIndex];
		return new DataModel(vertices, distances, timeWindows, nbVehicles, depotIndex, demands, capacityMax, serviceTime);
	}

	public static void solve(Data dataFile) {
		DataModel data = build(dataFile);
		Loader.loadNativeLibraries();

		int nbVertices = data.distances.length;

		MPSolver solver = MPSolver.createSolver("SCIP");

		if(solver == null) {
			System.out.println("[ERROR] Can't create the solver SCIP");
			return;
		}
		if(data.depotIndex != 0) {
			System.out.println("[ERROR] Data can't be analyzed (depotIndex != 0)");
			return;
		}

		// Variables
		MPVariable[][][] x = new MPVariable[nbVertices][nbVertices][data.nbVehicles];
		MPVariable[][] h = new MPVariable[nbVertices][data.nbVehicles];
		for (int k = 0; k < data.nbVehicles; k++) {
			for (int i = 0; i < nbVertices; i++) {
				for (int j = 0; j < nbVertices; j++) {
					if (i != j) {
						x[i][j][k] = solver.makeBoolVar("x_" + i + "_" + j + "_" + k);
					}
				}
				h[i][k] = solver.makeIntVar(data.timeWindows[i][0], data.timeWindows[i][1], "h_" + i + "_" + k);
			}
		}

		// Contraintes
		MPConstraint[] c0 = new MPConstraint[data.nbVehicles];
		MPConstraint[] c1 = new MPConstraint[data.nbVehicles];
		for(int k = 0; k < data.nbVehicles; k++) {
			c0[k] = solver.makeConstraint(1, 1);
			for(int i = 0; i < nbVertices; i++) {
				if(i != data.depotIndex && i != nbVertices-1)
					c0[k].setCoefficient(x[0][i][k], 1);
			}
			c1[k] = solver.makeConstraint(1, 1);
			for(int i = 0; i < nbVertices; i++) {
				if(i != data.depotIndex && i != nbVertices-1)
					c1[k].setCoefficient(x[i][nbVertices-1][k], 1);
			}
		}

		MPConstraint[][] c2 = new MPConstraint[data.nbVehicles][nbVertices];
		for(int k = 0; k < data.nbVehicles; k++) {
			for(int p = 0; p < nbVertices; p++) {
				if(p != data.depotIndex && p != nbVertices-1) {
					c2[k][p] = solver.makeConstraint(0, 0);
					for(int i = 0; i < nbVertices; i++) {
						if(i != p)
							c2[k][p].setCoefficient(x[i][p][k], 1);
					}
					for(int j = 0; j < nbVertices; j++) {
						if(j != p)
							c2[k][p].setCoefficient(x[p][j][k], -1);
					}
				}
			}
		}

		MPConstraint[] c3 = new MPConstraint[nbVertices];
		for(int i = 0; i < nbVertices; i++) {
			if(i != data.depotIndex && i != nbVertices-1) {
				c3[i] = solver.makeConstraint(1, 1);
				for(int k = 0; k < data.nbVehicles; k++) {
					for(int j = 0; j < nbVertices; j++) {
						if(j != i)
							c3[i].setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}

		MPConstraint[] c4 = new MPConstraint[data.nbVehicles];
		for(int k = 0; k < data.nbVehicles; k++) {
			c4[k] = solver.makeConstraint(0, data.capacityMax);
			for(int i = 0; i < nbVertices; i++) {
				for(int j = 0; j < nbVertices; j++) {
					if(i != j)
						c4[k].setCoefficient(x[i][j][k], data.demands[i]);
				}
			}
		}

		MPConstraint[][][] c5 = new MPConstraint[data.nbVehicles][nbVertices][nbVertices];
		double M = 1e6;
		for(int k = 0; k < data.nbVehicles; k++) {
			for(int i = 0; i < nbVertices; i++) {
				for(int j = 0; j < nbVertices; j++) {
					if(i != j) {
						c5[k][i][j] = solver.makeConstraint(Double.NEGATIVE_INFINITY, M - data.serviceTime[i] - data.distances[i][j]);
						c5[k][i][j].setCoefficient(h[i][k], 1);
						c5[k][i][j].setCoefficient(h[j][k], -1);
						c5[k][i][j].setCoefficient(x[i][j][k], M);
					}
				}
			}
		}

		// Fonction objective
		MPObjective objective = solver.objective();
		for (int k = 0; k < data.nbVehicles; k++) {
			for (int i = 0; i < nbVertices; i++) {
				for (int j = 0; j < nbVertices; j++) {
					if (i != j) {
						objective.setCoefficient(x[i][j][k], data.distances[i][j]);
					}
				}
			}
		}
		objective.setMinimization();

		System.out.println("Modèle créé");

		Date start = new Date();
		MPSolver.ResultStatus resultStatus = solver.solve();
		Date end = new Date();

		if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
			System.out.println("Solution trouvée:");
			List<Vehicle> vehicles = new ArrayList<>();
			for (int k = 0; k < data.nbVehicles; k++) {
				Vehicle v = new Vehicle((Depot) data.vertices.get(data.depotIndex), data.capacityMax);
				int index = data.depotIndex;
				System.out.println("Véhicule " + (k + 1) + " :");
				for (int i = 0; i < nbVertices; i++) {
					for (int j = 0; j < nbVertices; j++) {
						if (i != j && x[i][j][k].solutionValue() > 0.5) {
							System.out.printf("  De %d vers %d\n", i, j);
						}
					}
				}
				while(index != nbVertices-1) {
					for (int j = 0; j < nbVertices; j++) {
						if (index != j && x[index][j][k].solutionValue() > 0.5) {
							index = j;
							if(j == nbVertices-1) break;
							v.addClient((Client) data.vertices.get(j));
							break;
						}
					}
				}
				vehicles.add(v);
			}
			Solution s = new Solution(dataFile, vehicles);
			new GraphView(s);
			System.out.println(s.getTotalDistance());

			System.out.println("Coût total: " + objective.value());
			System.out.println("Temps de calcul: " + (end.getTime() - start.getTime()) + "ms");
		} else {
			System.out.println("Aucune solution trouvée.");
		}

	}
}