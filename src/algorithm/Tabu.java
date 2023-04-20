package algorithm;

import algorithm.operator.Operator;
import model.Neighbor;
import model.Solution;
import view.CostEvolutionView;
import view.GraphView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Tabu class : implements the Tabu algorithm
 * @author Julian DEGUT
 */
public class Tabu extends VRPTW {

	private final int tabuListSize;
	private final int maxIterations;
	private final int maxNeighbors;

	/**
	 * Constructor
	 * @param tabuListSize
	 * @param maxIterations
	 */
	public Tabu(int tabuListSize, int maxIterations, int maxNeighbors) {
		this.tabuListSize = tabuListSize;
		this.maxIterations = maxIterations;
		this.maxNeighbors = maxNeighbors;
		super.costs = new ArrayList<>();
	}

	/**
	 * Search method : starts the Tabu algorithm
	 * @param solution (initial random solution)
	 * @param dynamic (true if the graph is displayed & refreshed)
	 * @return solution
	 */
	public Solution search(Solution solution, boolean dynamic) {
		if(dynamic) super.graphView = new GraphView(solution);
		super.costs.add(solution.getTotalDistance());
		long tabuBlocked = 0;
		LinkedList<String> tabuList = new LinkedList<>();
		Solution bestSolution = solution.copy();
		for(int i=0; i<maxIterations; i++) {
			List<Neighbor> neighborhood = getNeighborhood(solution, maxNeighbors);
			if(neighborhood.size() > 0) {
				tabuBlocked += neighborhood.stream()
						.filter(neighbor -> tabuList.contains(neighbor.toString()))
						.count();
				Neighbor bestNeighbor = neighborhood.stream()
						.filter(neighbor -> !tabuList.contains(neighbor.toString()))
						.min((n1, n2) -> Double.compare(fitness(n1), fitness(n2)))
						.orElse(neighborhood.get(0));
				if(fitness(bestNeighbor) > solution.getTotalDistance())
					tabuList.addFirst(bestNeighbor.toString());
				Operator operator = bestNeighbor.getOperator();
				operator.execute(solution, bestNeighbor);
				super.costs.add(solution.getTotalDistance());

				bestSolution = solution.getTotalDistance() < bestSolution.getTotalDistance() ? solution.copy() : bestSolution;
				if(dynamic) graphView.update(bestSolution);

				if(tabuList.size() > tabuListSize)
					tabuList.removeLast();
			}
		}
		System.out.println("Tabu blocked: " + tabuBlocked);
		if(dynamic) CostEvolutionView.displayChart(costs);
		return bestSolution;
	}

	/**
	 * Search method : starts the Tabu algorithm
	 * @param solution (initial random solution)
	 * @return solution
	 */
	public Solution search(Solution solution) {
		return search(solution, false);
	}

}
