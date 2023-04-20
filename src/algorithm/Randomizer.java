package algorithm;

import algorithm.operator.Operator;
import model.Neighbor;
import model.Solution;
import view.CostEvolutionView;
import view.GraphView;

import java.util.ArrayList;

/**
 * Randomizer class : implements the Randomizer algorithm
 * @author Julian DEGUT
 */
public class Randomizer extends VRPTW {
	private final int maxIterations;

	/**
	 * Constructor
	 * @param maxIterations
	 */
	public Randomizer(int maxIterations) {
		this.maxIterations = maxIterations;
		super.costs = new ArrayList<>();
	}

	/**
	 * Search method : starts the randomizer algorithm
	 * @param solution (initial random solution)
	 * @param dynamic (true if the graph is displayed & refreshed)
	 * @return solution
	 */
	public Solution search(Solution solution, boolean dynamic) {
		if(dynamic) super.graphView = new GraphView(solution);
		super.costs.add(solution.getTotalDistance());
		Neighbor randomNeighbor;
		for(int i = 0; i<maxIterations; i++) {
			randomNeighbor = getRandomNeighbor(solution);
			if(fitness(randomNeighbor) < solution.getTotalDistance()) {
				Operator operator = randomNeighbor.getOperator();
				operator.execute(solution, randomNeighbor);
				if(dynamic) graphView.update(solution);
				super.costs.add(solution.getTotalDistance());
			}
		}
		if(dynamic) CostEvolutionView.displayChart(costs);
		return solution;
	}

	/**
	 * Search method : starts the randomizer algorithm
	 * @param solution (initial random solution)
	 * @return solution
	 */
	public Solution search(Solution solution) {
		return search(solution, false);
	}
}
