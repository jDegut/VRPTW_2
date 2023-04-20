package algorithm.operator;

import model.Client;
import model.Neighbor;
import model.Solution;
import model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Relocate class : implements the Relocate operator
 * @author Julian DEGUT
 */
public class Relocate extends Operator {

	/**
	 * Constructor
	 */
	public Relocate(OperatorType operatorType) {
		super(operatorType, operatorType == OperatorType.INTRA ? 2 : 1);
	}

	/**
	 * Inter scan method : get a neighbor solution
	 * @param solution
	 * @return neighbor
	 */
	@Override
	public Neighbor inter(Solution solution, Vehicle v1, Vehicle v2) {
		List<Client> clients1 = v1.getClients();
		List<Client> clients2 = v2.getClients();

		Client c1 = getRandomClient(clients1);
		int index = random.nextInt(clients2.size());

		List<Client> newClients1 = new ArrayList<>(clients1) {{remove(c1); }};
		List<Client> newClients2 = new ArrayList<>(clients2) {{add(index, c1); }};

		Vehicle newV1 = new Vehicle(v1.getDepot(), newClients1, solution.getData().getMaxQuantity());
		Vehicle newV2 = new Vehicle(v2.getDepot(), newClients2, solution.getData().getMaxQuantity());
		if(newV1.isValid() && newV2.isValid())
			return new Neighbor(
					this,
					List.of(v1, v2),
					List.of(newV1, newV2),
					List.of(c1),
					List.of(clients1.indexOf(c1), index),
					getNewCost(solution, v1, newV1, v2, newV2)
			);
		return null;
	}

	/**
	 * Intra scan method : get a neighbor solution
	 * @param solution
	 * @return neighbor
	 */
	@Override
	public Neighbor intra(Solution solution, Vehicle v) {
		List<Client> clients = v.getClients();

		Client c = getRandomClient(clients);
		int oldIndex = clients.indexOf(c);
		List<Client> newClients = new ArrayList<>(clients) {{remove(c); }};
		int index = oldIndex;
		while(index == oldIndex)
			index = random.nextInt(clients.size());
		newClients.add(index, c);

		Vehicle newV = new Vehicle(v.getDepot(), newClients, solution.getData().getMaxQuantity());
		if(newV.isValid())
			return new Neighbor(
					this,
					List.of(v),
					List.of(newV),
					List.of(c),
					List.of(oldIndex, index),
					getNewCost(solution, v, newV, null, null)
			);
		return null;
	}
}
