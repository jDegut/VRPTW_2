package algorithm.operator;

import model.Client;
import model.Neighbor;
import model.Solution;
import model.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * TwoOpt class : implements the TwoOpt operator
 * @author Julian DEGUT
 */
public class TwoOpt extends Operator {

	/**
	 * Constructor
	 */
	public TwoOpt() {
		super(OperatorType.INTRA, 3);
	}

	/**
	 * Inter Scan method : get a neighbor solution
	 * @param solution
	 * @return neighbor
	 */
	@Override
	public Neighbor inter(Solution solution, Vehicle v1, Vehicle v2) {
		return null;
	}

	/**
	 * Intra Scan method : get a neighbor solution
	 * @param solution
	 * @return neighbor
	 */
	@Override
	public Neighbor intra(Solution solution, Vehicle v) {
		List<Client> clients = v.getClients();
		Client c1 = getRandomClient(clients);
		while(clients.indexOf(c1) == 1 && clients.size() == 3)
			c1 = getRandomClient(clients);
		Client c2 = null;
		while(c2 == null || isAdjacent(clients, c1, c2))
			c2 = getRandomClient(clients, c1);

		List<Client> newClients = twoOpt(clients, c1, c2);
		Vehicle newV = new Vehicle(v.getDepot(), newClients, solution.getData().getMaxQuantity());
		if(newV.isValid())
			return new Neighbor(
					this,
					List.of(v),
					List.of(newV),
					List.of(c1, c2),
					null,
					getNewCost(solution, v, newV, null, null)
			);
		return null;
	}

	/**
	 * Check if two clients are adjacent
	 * @param clients
	 * @param client1
	 * @param client2
	 * @return true if they are adjacent, false otherwise
	 */
	private boolean isAdjacent(List<Client> clients, Client client1, Client client2) {
		return Math.abs(clients.indexOf(client1) - clients.indexOf(client2)) == 1;
	}

	/**
	 * Apply 2-Opt
	 * @param clients
	 * @param client1
	 * @param client2
	 * @return new list
	 */
	private List<Client> twoOpt(List<Client> clients, Client client1, Client client2) {
		int index1 = clients.indexOf(client1);
		int index2 = clients.indexOf(client2);
		if (index1 > index2) {
			index1 = clients.indexOf(client2);
			index2 = clients.indexOf(client1);
		}

		List<Client> first = new ArrayList<>(clients.subList(0, index1));
		List<Client> reversed = new ArrayList<>(clients.subList(index1, index2 + 1));
		List<Client> last = new ArrayList<>(clients.subList(index2 + 1, clients.size()));
		Collections.reverse(reversed);
		return Stream.concat(Stream.concat(first.stream(), reversed.stream()), last.stream()).toList();
	}

}
