package data;

import model.Client;
import model.Depot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class : contains all the data of the problem
 * @author Julian DEGUT
 */
public class Data {

	protected String name;
	protected String comment;
	protected String type;
	protected String coordinates;
	protected int nbDepots;
	protected int nbClients;
	protected int maxQuantity;
	protected Depot depot;
	protected final List<Client> clients;
	protected final File file;

	/**
	 * Default constructor
	 */
	public Data() {
		this.name = "unnamed";
		this.comment = "";
		this.type = "vrptw";
		this.coordinates = "cartesian";
		this.nbDepots = -1;
		this.nbClients = -1;
		this.maxQuantity = -1;
		this.depot = null;
		this.clients = new ArrayList<>();
		this.file = null;
	}

	/**
	 * Constructor
	 * @param path
	 */
	public Data(String path) {
		this.name = "unnamed";
		this.comment = "";
		this.type = "vrptw";
		this.coordinates = "cartesian";
		this.nbDepots = -1;
		this.nbClients = -1;
		this.maxQuantity = -1;
		this.depot = null;
		this.clients = new ArrayList<>();
		this.file = new File(path);

		if(build()) {
			System.out.println("Data " + name + " loaded");
		} else {
			throw new RuntimeException("Error loading data");
		}
	}

	/**
	 * Build the data from the file
	 * @return true if the data is correctly loaded, false otherwise
	 */
	public boolean build() {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + file.getAbsolutePath());
			return false;
		} catch (IOException e) {
			System.err.println("Error reading file: " + file.getAbsolutePath());
			return false;
		}

		String[] lines = sb.toString().split("\\n");
		parseHeader(lines);
		if(!createDepot(lines)) {
			System.err.println("Error creating depots");
			return false;
		}
		if(!createClients(lines)) {
			System.err.println("Error creating clients");
			return false;
		}
		return true;
	}

	/**
	 * Parse the header of the file
	 * @param lines
	 */
	private void parseHeader(String[] lines) {
		for(String line : lines) {
			String[] splitLine = line.split(": ");
			if(splitLine.length == 2) {
				switch (splitLine[0]) {
					case "NAME" -> this.name = splitLine[1].trim();
					case "COMMENT" -> this.comment = splitLine[1].trim();
					case "TYPE" -> this.type = splitLine[1].trim();
					case "COORDINATES" -> this.coordinates = splitLine[1].trim();
					case "NB_DEPOTS" -> this.nbDepots = Integer.parseInt(splitLine[1].trim());
					case "NB_CLIENTS" -> this.nbClients = Integer.parseInt(splitLine[1].trim());
					case "MAX_QUANTITY" -> this.maxQuantity = Integer.parseInt(splitLine[1].trim());
				}
			}
		}
	}

	/**
	 * Create the depots from the file
	 * @param lines
	 * @return true if the depots are correctly created, false otherwise
	 */
	private boolean createDepot(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.startsWith("DATA_DEPOTS")) {
				String data;
				for (int j = i + 1; j < lines.length; j++) {
					data = lines[j];
					if (data.isEmpty()) {
						break;
					}
					try {
						String[] split = data.split(" ");
						if (split.length == 5) {
							depot = new Depot(
									Integer.parseInt(split[1]),
									Integer.parseInt(split[2]),
									Integer.parseInt(split[3]),
									Integer.parseInt(split[4]));
						}
					} catch (NumberFormatException e) {
						System.err.println("Error parsing depot data: " + data);
					}
				}
				break;
			}
		}
		return depot != null;
	}

	/**
	 * Create the clients from the file
	 * @param lines
	 * @return true if the clients are correctly created, false otherwise
	 */
	private boolean createClients(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.startsWith("DATA_CLIENTS")) {
				String data;
				for (int j = i + 1; j < lines.length; j++) {
					data = lines[j];
					if (data.isEmpty()) {
						break;
					}
					try {
						String[] split = data.split(" ");
						clients.add(new Client(
								Integer.parseInt(split[1]),
								Integer.parseInt(split[2]),
								Integer.parseInt(split[3]),
								Integer.parseInt(split[4]),
								Integer.parseInt(split[5]),
								Integer.parseInt(split[6])
						));
					} catch (NumberFormatException e) {
						System.err.println("Error parsing client data: " + data);
					}
				}
				break;
			}
		}
		return clients.size() == nbClients;
	}

	/**
	 * Get the name of the data
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the comment of the data
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the type of the data
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the coordinates of the data
	 * @return coordinates
	 */
	public String getCoordinates() {
		return coordinates;
	}

	/**
	 * Get the number of depots
	 * @return nbDepots
	 */
	public int getNbDepots() {
		return nbDepots;
	}

	/**
	 * Get the number of clients
	 * @return nbClients
	 */
	public int getNbClients() {
		return nbClients;
	}

	/**
	 * Get the maximum quantity
	 * @return maxQuantity
	 */
	public int getMaxQuantity() {
		return maxQuantity;
	}

	/**
	 * Get the depots
	 * @return depots
	 */
	public Depot getDepot() {
		return depot;
	}

	/**
	 * Get the clients
	 * @return clients;
	 */
	public List<Client> getClients() {
		return clients;
	}
}
