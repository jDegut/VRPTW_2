import algorithm.*;
import data.Data;
import model.Solution;
import view.GraphView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * Main class : contains the main method
 * @author Julian DEGUT
 */
public class Main {

	/**
	 * Main method : execute the program
	 */
	public static void main(String[] args) {

		Data data = new Data("./Data/data202.vrp");
		LinearMP.solve(data);
//		executeAlgorithm(data, Tabu.class);
//		getOptimumForAData(data,10);

	}

	/**
	 * Execute an algorithm on a data file
	 * @param data data file
	 * @param algorithm algorithm to execute (class)
	 */
	private static void executeAlgorithm(Data data, Class<? extends VRPTW> algorithm) {
		Solution solution = VRPTW.init(data);
		int oldCost = (int) solution.getTotalDistance();
		int oldNbVehicles = solution.getVehicles().size();

		Solution end;
		if (algorithm.equals(Randomizer.class)) {
			System.out.println("Randomizer algorithm found");
			end = new Randomizer(1000).search(solution, true);
		} else if (algorithm.equals(HillClimbing.class)) {
			System.out.println("HillClimbing algorithm found");
			end = new HillClimbing(100).search(solution, true);
		} else if (algorithm.equals(Tabu.class)) {
			System.out.println("Tabu algorithm found");
			end = new Tabu(10, 1000, 100).search(solution, true);
		} else if (algorithm.equals(SimulatedAnnealing.class)) {
			System.out.println("SimulatedAnnealing algorithm found");
			end = new SimulatedAnnealing(0.1, 1000, 0.9).search(solution);
		} else {
			System.out.println("Algorithm not found");
			return;
		}

		System.out.println("\nInitial solution cost : " + oldCost);
		System.out.println("Final solution cost : " + (int) end.getTotalDistance());
		System.out.println("Cost decrease : " + (oldCost - (int) end.getTotalDistance()));
		System.out.println("Vehicles number decrease : " + (oldNbVehicles - end.getVehicles().size()) + " : " + oldNbVehicles + "->" + end.getVehicles().size());
	}

	/**
	 * Get the best optimum for a data file
	 * @param data
	 * @param nbIterations
	 * @return optimum
	 */
	private static void getOptimumForAData(Data data, int nbIterations) {
		ArrayList<Solution> solutions = new ArrayList<>();
		Date start = new Date();

		IntStream.range(0, nbIterations).parallel().forEach(i -> {
			Solution solution = VRPTW.getRandomSolution(data);
			solution = new SimulatedAnnealing(0.1, 1000, 0.9).search(solution);
			synchronized (solutions) {
				solutions.add(solution);
			}
		});
		Solution bestSolution = solutions
				.stream()
				.min(Comparator.comparingDouble(Solution::getTotalDistance))
				.orElseThrow(NoSuchElementException::new);
		new GraphView(bestSolution);
		System.out.printf("\n--- Optimum with %d iterations ---\n", nbIterations);
		System.out.println("cost : " + bestSolution.getTotalDistance() + " - nb vehicles : " + bestSolution.getVehicles().size());
		System.out.println("Time : " + (new Date().getTime() - start.getTime()) + "ms");
	}
}