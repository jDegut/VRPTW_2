package model;

/**
 * Vertex class : implements the Vertex class
 * @author Julian DEGUT
 */
public abstract class Vertex {
	protected static int idCounter = 0;
	protected final int id;
	protected final int x;
	protected final int y;

	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public Vertex(int x, int y) {
		this.id = idCounter++;
		this.x = x;
		this.y = y;
	}

	/**
	 * Get id
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get x
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get y
	 * @return y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the euclidean distance between two vertices
	 * @param v
	 * @return distance
	 */
	public double getDistance(Vertex v) {
		return Math.sqrt(Math.pow(this.x - v.x, 2) + Math.pow(this.y - v.y, 2));
	}

}
