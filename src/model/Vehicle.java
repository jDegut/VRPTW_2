package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Vehicle class : contains all the information about a vehicle
 * @author Julian DEGUT
 */
public class Vehicle {

	private static int idCounter = 0;
	private final int id;
	private final Depot depot;
	private final List<Client> clients;
	private double distance;
	private double time;
	private final int maxCapacity;
	private boolean valid;

	/**
	 * Constructor : creates a vehicle from a depot and a max capacity
	 * @param depot
	 * @param maxCapacity
	 */
	public Vehicle(Depot depot, int maxCapacity) {
		this.id = idCounter++;
		this.depot = depot;
		this.clients = new LinkedList<>();
		this.distance = 0;
		this.time = 0;
		this.maxCapacity = maxCapacity;
		this.valid = update();
	}

	/**
	 * Constructor : creates a vehicle from a depot, a list of clients and a max capacity
	 * @param depot
	 * @param clients
	 * @param maxCapacity
	 */
	public Vehicle(Depot depot, List<Client> clients, int maxCapacity) {
		this.id = idCounter++;
		this.depot = depot;
		this.clients = clients;
		this.distance = 0;
		this.time = 0;
		this.maxCapacity = maxCapacity;
		this.valid = update();
	}

	/**
	 * Add a client to the vehicle
	 * @param client to add
	 * @return true if the vehicle is valid after adding the client, false otherwise
	 */
	public boolean addClient(Client client) {
		clients.add(client);
		boolean valid = update();
		if(valid) {
			this.valid = true;
			return true;
		}
		clients.remove(client);
		return false;
	}

	/**
	 * Update the information of the vehicle (distance, time, valid)
	 * @return true if the vehicle is valid, false otherwise
	 */
	private boolean update() {
		double tempTime = 0;
		double tempDistance = 0;
		int tempCapacity = 0;
		Vertex source = depot;

		for(Client c : clients) {
			tempDistance += source.getDistance(c);
			tempTime += source.getDistance(c);
			if(tempTime > c.getDueTime() || tempCapacity + c.getDemand() > maxCapacity)
				return false;
			tempTime = Math.max(tempTime, c.getReadyTime()) + c.getServiceTime();
			tempCapacity -= c.getDemand();
			source = c;
		}

		tempDistance += source.getDistance(depot);
		if(tempTime + source.getDistance(depot) <= depot.getDueTime() && maxCapacity - tempCapacity >= 0) {
			this.distance = tempDistance;
			this.time = tempTime;
			return true;
		}
		return false;
	}

	/**
	 * Get the id
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the depot
	 * @return depot
	 */
	public Depot getDepot() {
		return depot;
	}

	/**
	 * Get the clients
	 * @return clients
	 */
	public List<Client> getClients() {
		return clients;
	}

	/**
	 * Set the clients
	 * @param clients
	 */
	public void setClients(List<Client> clients) {
		this.clients.clear();
		this.clients.addAll(clients);
		this.valid = update();
	}

	/**
	 * Get the max capacity
	 * @return max capacity
	 */
	public List<Vertex> getVertices() {
		return new ArrayList<>() {{
			add(depot);
			addAll(clients);
			add(depot);
		}};
	}

	/**
	 * Get the distance of the vehicle
	 * @return distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Is the vehicle valid ?
	 * @return true if the vehicle is valid, false otherwise
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Copy the vehicle
	 * @return a copy of the vehicle
	 */
	public Vehicle copy() {
		return new Vehicle(depot, new ArrayList<>(clients), maxCapacity);
	}

}
