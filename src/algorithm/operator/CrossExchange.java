package algorithm.operator;

import model.Client;
import model.Neighbor;
import model.Solution;
import model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * CrossExchange class : implements the CrossExchange operator
 * @author Julian DEGUT
 */
public class CrossExchange extends Operator {

	/**
	 * Constructor
	 */
	public CrossExchange() {
		super(OperatorType.INTER, 1);
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

		int index1 = random.nextInt(clients1.size());
		int index2 = random.nextInt(clients1.size());
		if(index1 > index2) {
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		int index3 = random.nextInt(clients2.size());
		int index4 = random.nextInt(clients2.size());
		if(index3 > index4) {
			int tmp = index3;
			index3 = index4;
			index4 = tmp;
		}

		List<List<Client>> newClients = cross(clients1, clients2, index1, index2, index3, index4);
		List<Client> newClients1 = newClients.get(0);
		List<Client> newClients2 = newClients.get(1);
		Vehicle newV1 = new Vehicle(v1.getDepot(), newClients1, solution.getData().getMaxQuantity());
		Vehicle newV2 = new Vehicle(v2.getDepot(), newClients2, solution.getData().getMaxQuantity());
		if(newV1.isValid() && newV2.isValid())
			return new Neighbor(
					this,
					List.of(v1, v2),
					List.of(newV1, newV2),
					null,
					List.of(index1, index2, index3, index4),
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
		return null;
	}

	/**
	 * Cross method : cross two routes
	 * @param clients1
	 * @param clients2
	 * @param index1
	 * @param index2
	 * @param index3
	 * @param index4
	 * @return newClients (list)
	 */
	private List<List<Client>> cross(List<Client> clients1, List<Client> clients2, int index1, int index2, int index3, int index4) {
		List<Client> first1 = new ArrayList<>(clients1.subList(0, index1));
		List<Client> first2 = new ArrayList<>(clients2.subList(0, index3));
		List<Client> middle1 = new ArrayList<>(clients2.subList(index3, index4 + 1));
		List<Client> middle2 = new ArrayList<>(clients1.subList(index1, index2 + 1));
		List<Client> last1 = new ArrayList<>(clients1.subList(index2 + 1, clients1.size()));
		List<Client> last2 = new ArrayList<>(clients2.subList(index4 + 1, clients2.size()));

		List<Client> newClients1 = new ArrayList<>(first1);
		newClients1.addAll(middle1);
		newClients1.addAll(last1);
		List<Client> newClients2 = new ArrayList<>(first2);
		newClients2.addAll(middle2);
		newClients2.addAll(last2);
		return new ArrayList<>() {{add(newClients1); add(newClients2);}};
	}
}
