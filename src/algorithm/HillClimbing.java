package algorithm;

import algorithm.operator.Operator;
import model.Neighbor;
import model.Solution;
import view.CostEvolutionView;
import view.GraphView;

import java.util.ArrayList;
import java.util.List;

/**
 * HillClimbing class : implements the Hill Climbing algorithm
 * @author Julian DEGUT
 */
public class HillClimbing extends VRPTW {
	private final int maxNeighbors;

	/**
	 * Constructor
	 * @param maxNeighbors
	 */
	public HillClimbing(int maxNeighbors) {
		this.maxNeighbors = maxNeighbors;
		super.costs = new ArrayList<>();
	}

	/**
	 * Search method : starts the hill climbing algorithm
	 * @param solution (initial random solution)
	 * @param dynamic (true if the graph is displayed & refreshed)
	 * @return solution
	 */
	public Solution search(Solution solution, boolean dynamic) {
		if(dynamic) super.graphView = new GraphView(solution);
		super.costs.add(solution.getTotalDistance());
		boolean continued = true;
		Neighbor bestNeighbor;
		while(continued) {
			List<Neighbor> neighborhood = getNeighborhood(solution, maxNeighbors);
			if(neighborhood.size() != 0) {
				bestNeighbor = neighborhood.stream()
						.min((n1, n2) -> Double.compare(fitness(n1), fitness(n2)))
						.orElse(neighborhood.get(0));
				if(fitness(bestNeighbor) < solution.getTotalDistance()) {
					Operator operator = bestNeighbor.getOperator();
					operator.execute(solution, bestNeighbor);
					if(dynamic) graphView.update(solution);
					super.costs.add(solution.getTotalDistance());
				} else
					continued = false;
			} else
				continued = false;
		}
		if(dynamic) CostEvolutionView.displayChart(costs);
		return solution;
	}

	/**
	 * Search method : starts the hill climbing algorithm
	 * @param solution (initial random solution)
	 * @return solution
	 */
	public Solution search(Solution solution) {
		return search(solution, false);
	}
}
