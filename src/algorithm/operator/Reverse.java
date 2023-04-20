package algorithm.operator;

import model.Client;
import model.Neighbor;
import model.Solution;
import model.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reverse class : implements the Reverse operator
 * @author Julian DEGUT
 */
public class Reverse extends Operator {

	/**
	 * Constructor
	 */
	public Reverse() {
		super(OperatorType.INTRA, 1);
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
		List<Client> newClients = new ArrayList<>(clients);

		Collections.reverse(newClients);

		Vehicle newV = new Vehicle(v.getDepot(), newClients, solution.getData().getMaxQuantity());
		if(newV.isValid())
			return new Neighbor(
					this,
					List.of(v),
					List.of(newV),
					null,
					null,
					getNewCost(solution, v, newV, null, null)
			);
		return null;
	}
}
