package algorithm.operator;

import model.Client;
import model.Neighbor;
import model.Solution;
import model.Vehicle;

import java.util.List;

/**
 * Exchange class : implements the Exchange operator
 * @author Julian DEGUT
 */
public class Exchange extends Operator {

	/**
	 * Constructor
	 * @param operatorType
	 */
	public Exchange(OperatorType operatorType) {
		super(operatorType, operatorType == OperatorType.INTRA ? 2 : 1);
	}

	/**
	 * Inter Scan method : get a neighbor solution
	 * @param solution
	 * @return neighbor
	 */
	@Override
	public Neighbor inter(Solution solution, Vehicle v1, Vehicle v2) {
		List<Client> clients1 = v1.getClients();
		List<Client> clients2 = v2.getClients();

		Client c1 = getRandomClient(clients1);
		Client c2 = getRandomClient(clients2);

		List<Client> newClients1 = exchange(clients1, c1, c2, true);
		List<Client> newClients2 = exchange(clients2, c2, c1, true);

		Vehicle newV1 = new Vehicle(v1.getDepot(), newClients1, solution.getData().getMaxQuantity());
		Vehicle newV2 = new Vehicle(v2.getDepot(), newClients2, solution.getData().getMaxQuantity());
		if(newV1.isValid() && newV2.isValid())
			return new Neighbor(
					this,
					List.of(v1, v2),
					List.of(newV1, newV2),
					List.of(c1, c2),
					null,
					getNewCost(solution, v1, newV1, v2, newV2)
			);
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
		Client c2 = null;
		while(c2 == null) {
			c2 = getRandomClient(clients, c1);
		}

		List<Client> newClients = exchange(clients, c1, c2, false);
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
	 * Exchange method : exchange two clients
	 * @param clients
	 * @param client1
	 * @param client2
	 * @param inter
	 * @return clients
	 */
	private List<Client> exchange(List<Client> clients, Client client1, Client client2, boolean inter) {
		if(inter)
			return clients.stream()
					.map(client -> {
						if (client.equals(client1))
							return client2;
						return client;
					}).toList();
		else
			return clients.stream()
					.map(client -> {
						if (client.equals(client1))
							return client2;
						if (client.equals(client2))
							return client1;
						return client;
					}).toList();
	}
}
