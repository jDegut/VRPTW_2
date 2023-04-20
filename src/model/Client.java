package model;

/**
 * Client class : implements the Client class
 * @author Julian DEGUT
 */
public class Client extends Vertex {

	private final int readyTime;
	private final int dueTime;
	private final int demand;
	private final int serviceTime;

	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param readyTime
	 * @param dueTime
	 * @param demand
	 * @param serviceTime
	 */
	public Client(int x, int y, int readyTime, int dueTime, int demand, int serviceTime) {
		super(x, y);
		this.readyTime = readyTime;
		this.dueTime = dueTime;
		this.demand = demand;
		this.serviceTime = serviceTime;
	}

	/**
	 * Get ready time
	 * @return readyTime
	 */
	public int getReadyTime() {
		return readyTime;
	}

	/**
	 * Get due time
	 * @return dueTime
	 */
	public int getDueTime() {
		return dueTime;
	}

	/**
	 * Get demand
	 * @return demand
	 */
	public int getDemand() {
		return demand;
	}

	/**
	 * Get service time
	 * @return serviceTime
	 */
	public int getServiceTime() {
		return serviceTime;
	}
}
