package model;

import data.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Solution class : contains all the information about a solution
 * @author Julian DEGUT
 */
public class Solution {

	private final Data data;
	private final List<Vehicle> vehicles;
	private final List<Vertex> vertices;

	/**
	 * Constructor : creates a solution from a data and a list of vehicles
	 * @param data
	 * @param vehicles
	 */
	public Solution(Data data, List<Vehicle> vehicles) {
		this.data = data;
		this.vehicles = vehicles;
		vertices = new ArrayList<>(){{ add(data.getDepot()); addAll(data.getClients()); }};
	}

	/**
	 * Get the total distance of the solution
	 * @return totalDistance
	 */
	public double getTotalDistance() {
		return vehicles.stream()
				.mapToDouble(Vehicle::getDistance)
				.sum();
	}

	/**
	 * Get data
	 * @return data
	 */
	public Data getData() {
		return data;
	}

	/**
	 * Get vehicles
	 * @return vehicles
	 */
	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	/**
	 * Get vertices
	 * @return vertices
	 */
	public List<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Copy the solution
	 * @return solution
	 */
	public Solution copy() {
		List<Vehicle> vehicles = new ArrayList<>();
		for(Vehicle vehicle : this.vehicles) {
			vehicles.add(vehicle.copy());
		}
		return new Solution(this.data, vehicles);
	}

}
