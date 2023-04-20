package algorithm;

import algorithm.operator.Operator;
import model.Neighbor;
import model.Solution;
import view.CostEvolutionView;
import view.GraphView;

import java.util.ArrayList;
import java.util.List;

/**
 * SimulatedAnnealing class : implements the Simulated Annealing algorithm
 * @author Julian DEGUT
 */
public class SimulatedAnnealing extends VRPTW {
	private final double finalTemperature;
	private final int maxIterationsPerTemp;
	private final double coolingRate;

	/**
	 * Constructor
	 * @param finalTemperature
	 * @param maxIterationsPerTemp
	 * @param coolingRate
	 */
	public SimulatedAnnealing(double finalTemperature, int maxIterationsPerTemp, double coolingRate) {
		this.finalTemperature = finalTemperature;
		this.maxIterationsPerTemp = maxIterationsPerTemp;
		this.coolingRate = coolingRate;
		super.costs = new ArrayList<>();
	}

	/**
	 * Search method : starts the simulated annealing algorithm
	 * @param solution (initial random solution)
	 * @param dynamic (true if the graph is displayed & refreshed)
	 * @return solution
	 */
	public Solution search(Solution solution, boolean dynamic) {
		if(dynamic) super.graphView = new GraphView(solution);
		super.costs.add(solution.getTotalDistance());
		double temperature = getInitialTemperature(solution);
		System.out.println("Initial temperature: " + temperature);
		Solution bestSolution = solution.copy();
		while (temperature > finalTemperature) {
			for(int i=0; i<maxIterationsPerTemp; i++) {
				Neighbor randomNeighbor = getRandomNeighbor(solution);
				double probability = acceptanceProbability(solution.getTotalDistance(), fitness(randomNeighbor), temperature);
				if(random.nextDouble() <= probability) {
					Operator operator = randomNeighbor.getOperator();
					operator.execute(solution, randomNeighbor);
					super.costs.add(solution.getTotalDistance());
					bestSolution = solution.getTotalDistance() < bestSolution.getTotalDistance() ? solution.copy() : bestSolution;
					if(dynamic) super.graphView.update(bestSolution);
				}
			}
			temperature *= coolingRate;
		}
		System.out.println("Final temperature: " +temperature);
		if(dynamic) CostEvolutionView.displayChart(costs);
		return bestSolution;
	}

	/**
	 * Search method : starts the simulated annealing algorithm
	 * @param solution (initial random solution)
	 * @return solution
	 */
	public Solution search(Solution solution) {
		return search(solution, false);
	}

	/**
	 * Acceptance probability method : returns the acceptance probability
	 * @param currentCost
	 * @param newCost
	 * @param temperature
	 * @return probability
	 */
	private double acceptanceProbability(double currentCost, double newCost, double temperature) {
		if(newCost < currentCost) {
			return 1.0;
		}
		return Math.exp((currentCost - newCost) / temperature);
	}

	/**
	 * Initial temperature method : returns the initial temperature
	 * @param solution
	 * @return temperature
	 */
	private double getInitialTemperature(Solution solution) {
		List<Neighbor> neighborhood = getNeighborhood(solution, 100);
		double maxDelta = 0;
		for(Neighbor neighbor : neighborhood) {
			double delta = fitness(neighbor) - solution.getTotalDistance();
			if(delta > maxDelta) {
				maxDelta = delta;
			}
		}
		return -maxDelta / Math.log(0.8);
	}
}
