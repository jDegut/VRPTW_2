package model;

/**
 * Depot class : implements the Depot class
 * @author Julian DEGUT
 */
public class Depot extends Vertex {

	private final int readyTime;
	private final int dueTime;

	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param readyTime
	 * @param dueTime
	 */
	public Depot(int x, int y, int readyTime, int dueTime) {
		super(x, y);
		this.readyTime = readyTime;
		this.dueTime = dueTime;
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
	 * Get string representation (Did)
	 * @return
	 */
	@Override
	public String toString() {
		return "Depot-" + id;
	}
}
