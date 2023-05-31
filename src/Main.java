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
	 * TODO Parameters to change
	 */
	private static final int MAX_ITERATIONS = 10000;
	private static final int MAX_NEIHGBORS = 250;
	private static final int TABU_LIST_SIZE = 10;
	private static final double FINAL_TEMPERATURE = 0.01;
	private static final double COOLING_RATE = 0.9;

	/**
	 * TODO Algorithm to execute
	 */
	private static final Class<? extends VRPTW> CHOSEN_ALGORITHM = SimulatedAnnealing.class;

	/**
	 * Main method : execute the program
	 */
	public static void main(String[] args) {

		Data data = new Data("./Data/data1202.vrp");
		// TODO Uncomment to execute the linear program
//		LinearMP.solve(data);
		// TODO Uncomment to execute the chosen algorithm
		executeAlgorithm(data);
		// TODO Uncomment to get the best optimum for a data file
//		getOptimumForAData(data,10);
	}

	/**
	 * Execute an algorithm on a data file
	 *
	 * @param data data file
	 */
	private static void executeAlgorithm(Data data) {
		Solution solution = VRPTW.init(data);
		int oldCost = (int) solution.getTotalDistance();
		int oldNbVehicles = solution.getVehicles().size();

		Solution end;
		if (CHOSEN_ALGORITHM.equals(Randomizer.class)) {
			System.out.println("Randomizer algorithm found");
			end = new Randomizer(MAX_ITERATIONS).search(solution, true);
		} else if (CHOSEN_ALGORITHM.equals(HillClimbing.class)) {
			System.out.println("HillClimbing algorithm found");
			end = new HillClimbing(MAX_NEIHGBORS).search(solution, true);
		} else if (CHOSEN_ALGORITHM.equals(Tabu.class)) {
			System.out.println("Tabu algorithm found");
			end = new Tabu(TABU_LIST_SIZE, MAX_ITERATIONS, MAX_NEIHGBORS).search(solution, true);
		} else if (CHOSEN_ALGORITHM.equals(SimulatedAnnealing.class)) {
			System.out.println("SimulatedAnnealing algorithm found");
			end = new SimulatedAnnealing(FINAL_TEMPERATURE, MAX_ITERATIONS, COOLING_RATE).search(solution);
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
			solution = new SimulatedAnnealing(FINAL_TEMPERATURE, MAX_ITERATIONS, COOLING_RATE).search(solution);
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