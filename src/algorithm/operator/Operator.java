package algorithm.operator;

import model.Client;
import model.Neighbor;
import model.Solution;
import model.Vehicle;

import java.util.List;
import java.util.Random;

/**
 * Operator class : implements the Operator class
 * @author Julian DEGUT
 */
public abstract class Operator {

	protected static Random random = new Random();
	private final OperatorType type;
	private final int minClientsPerVehicle;

	/**
	 * Constructor
	 * @param type
	 * @param minClientsPerVehicle
	 */
	protected Operator(OperatorType type, int minClientsPerVehicle) {
		this.type = type;
		this.minClientsPerVehicle = minClientsPerVehicle;
	}

	/**
	 * Inter scan method
	 * @param solution
	 * @param v1
	 * @param v2
	 * @return
	 */
	protected abstract Neighbor inter(Solution solution, Vehicle v1, Vehicle v2);

	/**
	 * Intra scan method
	 * @param solution
	 * @param v
	 * @return
	 */
	protected abstract Neighbor intra(Solution solution, Vehicle v);

	/**
	 * Generic scan method : get a neighbor solution
	 * @param solution
	 * @return neighbor
	 */
	public Neighbor scan(Solution solution) {
		List<Vehicle> vehicles = solution.getVehicles();
		if(type == OperatorType.INTER) {
			Vehicle v1 = getRandomVehicle(vehicles);
			Vehicle v2 = getRandomVehicle(vehicles, v1);
			if(v1 == null || v2 == null) return null;
			return inter(solution, v1, v2);
		} else {
			Vehicle v = getRandomVehicle(vehicles);
			if(v == null) return null;
			return intra(solution, v);
		}
	}

	/**
	 * Execute method : execute a neighbor solution
	 */
	public void execute(Solution solution, Neighbor neighbor) {
		Vehicle vehicle1 = neighbor.getVehicles().get(0);
		Vehicle newVehicle1 = neighbor.getNewVehicles().get(0);
		if(newVehicle1.getClients().size() > 0) // InterRelocate case : route1.getPath().size() can be equal to 1
			vehicle1.setClients(newVehicle1.getClients());
		else
			solution.getVehicles().remove(vehicle1);

		Vehicle vehicle2;
		Vehicle newVehicle2;
		if(neighbor.getVehicles().size() > 1 && neighbor.getNewVehicles().size() > 1) {
			vehicle2 = neighbor.getVehicles().get(1);
			newVehicle2 = neighbor.getNewVehicles().get(1);
			vehicle2.setClients(newVehicle2.getClients());
		}
	}

	/**
	 * Get a random vehicle
	 * @param vehicles
	 * @return
	 */
	private Vehicle getRandomVehicle(List<Vehicle> vehicles) {
		return vehicles.stream()
				.filter(v -> v.getClients().size() >= minClientsPerVehicle)
				.skip(random.nextInt(vehicles.size()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get a random vehicle excluding the given one
	 * @param vehicles
	 * @param exclude
	 * @return
	 */
	private Vehicle getRandomVehicle(List<Vehicle> vehicles, Vehicle exclude) {
		return vehicles.stream()
				.filter(v -> v.getClients().size() >= minClientsPerVehicle)
				.filter(v -> v != exclude)
				.skip(random.nextInt(vehicles.size()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get a random client
	 * @param clients
	 * @return
	 */
	protected Client getRandomClient(List<Client> clients) {
		return clients.stream()
				.skip(random.nextInt(clients.size()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get a random client excluding the given one
	 * @param clients
	 * @param exclude
	 * @return
	 */
	protected Client getRandomClient(List<Client> clients, Client exclude) {
		return clients.stream()
				.filter(c -> c != exclude)
				.skip(random.nextInt(clients.size()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get new cost of a neighbor solution
	 * @param solution
	 * @param v1
	 * @param newV1
	 * @param v2
	 * @param newV2
	 * @return
	 */
	protected double getNewCost(Solution solution, Vehicle v1, Vehicle newV1, Vehicle v2, Vehicle newV2) {
		if(v2 != null && newV2 != null)
			return solution.getTotalDistance() - v1.getDistance() - v2.getDistance() + newV1.getDistance() + newV2.getDistance();
		else
			return solution.getTotalDistance() - v1.getDistance() + newV1.getDistance();
	}

	/**
	 * ToString method
	 * @return
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "-" + type;
	}

}
