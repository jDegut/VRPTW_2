package algorithm;

import algorithm.operator.*;
import data.Data;
import model.*;
import view.GraphView;

import java.util.*;
import java.util.stream.IntStream;

/**
 * VRPTW class : implements the VRPTW algorithm
 * @author Julian DEGUT
 */
public abstract class VRPTW {
	protected static final Random random = new Random();
	protected GraphView graphView;
	protected List<Double> costs;
	private static final List<Operator> operators = List.of(
			new CrossExchange(),
			new Exchange(OperatorType.INTER),
			new Exchange(OperatorType.INTRA),
			new Relocate(OperatorType.INTER),
			new Relocate(OperatorType.INTRA),
			new Reverse(),
			new TwoOpt()
	);

	/**
	 * Search method : starts the VRPTW algorithm chosen
	 * @param solution (initial random solution)
	 * @param dynamic (true if the graph is displayed & refreshed)
	 * @return
	 */
	protected abstract Solution search(Solution solution, boolean dynamic);

	/**
	 * Search method : starts the VRPTW algorithm chosen
	 * @param solution (initial random solution)
	 * @return
	 */
	protected abstract Solution search(Solution solution);

	/**
	 * Init method : initializes the solution (random)
	 * @param data
	 * @return
	 */
	public static Solution init(Data data) {
		return getRandomSolution(data);
	}

	/**
	 * Get the theoretical minimum number of vehicles (without time windows)
	 * @param data
	 * @return minVehicle
	 */
	public static int getMinVehicles(Data data) {
		int clientsDemand = data.getClients()
				.stream()
				.mapToInt(Client::getDemand)
				.sum();
		return (int) Math.ceil((double) clientsDemand / data.getMaxQuantity());
	}

	/**
	 * Get a random solution
	 * @param data
	 * @return solution
	 */
	public static Solution getRandomSolution(Data data) {
		LinkedList<Vehicle> vehicles = new LinkedList<>(); // LinkedList : better for insertion & deletion
		LinkedList<Client> clients = new LinkedList<>(data.getClients());
		Depot depot = data.getDepot();
		while(!clients.isEmpty()) {
			Vehicle v = new Vehicle(depot, data.getMaxQuantity());
			ArrayList<Client> marked = new ArrayList<>();

			while(marked.size() < clients.size()) {
				Client destination = clients.stream()
						.filter(c -> !marked.contains(c))
						.skip(random.nextInt(clients.size()))
						.findFirst()
						.orElse(null);
				if(destination == null) break;
				if(!v.addClient(destination)) marked.add(destination);
				else clients.remove(destination);
			}
			vehicles.add(v);
		}
		return new Solution(data, vehicles);
	}

	/**
	 * Get the neighborhood of a solution (using parallel streams)
	 * @param solution
	 * @return neighborhood
	 */
	public static ArrayList<Neighbor> getNeighborhood(Solution solution, int MAX_NEIGHBORS) {
		ArrayList<Neighbor> neighborhood = new ArrayList<>();
		operators.parallelStream().forEach(operator -> IntStream.range(0, MAX_NEIGHBORS).parallel().forEach(i -> {
			Neighbor neighbor = operator.scan(solution);
			if (neighbor != null) {
				synchronized (neighborhood) {
					neighborhood.add(neighbor);
				}
			}
		}));
		Collections.shuffle(neighborhood);
		return neighborhood;
	}

	/**
	 * Get a single random neighbor
	 * @param solution
	 * @return
	 */
	public static Neighbor getRandomNeighbor(Solution solution) {
		Neighbor neighbor = null;
		while(neighbor == null) {
			neighbor = operators.get(new Random().nextInt(operators.size())).scan(solution);
		}
		return neighbor;
	}

	/**
	 * Get the fitness of a neighbor
	 * @param neighbor
	 * @return neighborCost
	 */
	protected double fitness(Neighbor neighbor) {
		return neighbor.getCost();
	}

}
