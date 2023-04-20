package model;

import algorithm.operator.Operator;

import java.util.List;

/**
 * Neighbor class : contains all the information about a neighbor
 * @author Julian DEGUT
 */
public class Neighbor {

	private final Operator operator;
	private final List<Vehicle> vehicles;
	private final List<Vehicle> newVehicles;
	private final List<Client> clients;
	private final List<Integer> indexes;
	private final double cost;

	/**
	 * Default Constructor
	 */
	public Neighbor() {
		this.operator = null;
		this.vehicles = null;
		this.newVehicles = null;
		this.clients = null;
		this.indexes = null;
		this.cost = 0;
	}

	/**
	 * Constructor
	 * @param operator
	 * @param vehicles
	 * @param newVehicles
	 * @param clients
	 * @param indexes
	 * @param cost
	 */
	public Neighbor(Operator operator,
					List<Vehicle> vehicles,
					List<Vehicle> newVehicles,
					List<Client> clients,
					List<Integer> indexes,
					double cost) {
		this.operator = operator;
		this.vehicles = vehicles;
		this.newVehicles = newVehicles;
		this.clients = clients;
		this.indexes = indexes;
		this.cost = cost;
	}

	/**
	 * Get the operator
	 * @return operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Get the routes
	 * @return routes
	 */
	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	/**
	 * Get the new routes
	 * @return newRoutes
	 */
	public List<Vehicle> getNewVehicles() {
		return newVehicles;
	}

	/**
	 * Get the clients
	 * @return clients
	 */
	public List<Client> getClients() {
		return clients;
	}

	/**
	 * Get the indexes
	 * @return indexes
	 */
	public List<Integer> getIndexes() {
		return indexes;
	}

	/**
	 * Get the cost
	 * @return cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * ToString method
	 * @return neighbor as a string
	 */
	@Override
	public String toString() {
		String routes = "";
		String clients = "";
		if(this.vehicles != null)
			routes = this.vehicles.stream()
					.map(Vehicle::getId)
					.map(String::valueOf)
					.reduce((s1, s2) -> s1 + "/" + s2)
					.orElse("");
		if(this.clients != null)
			clients = this.clients.stream()
					.map(Client::getId)
					.map(String::valueOf)
					.reduce((s1, s2) -> s1 + "/" + s2)
					.orElse("");
		return "[operator=" + operator + ", vehicles=" + routes + ", clients=" + clients + ", indexes=" + indexes + "]";
	}

}