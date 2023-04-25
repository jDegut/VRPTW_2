package view;

import model.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * GraphView class : displays the graph of the VRPTW problem (using GraphStream)
 * @author Julian DEGUT
 */
public class GraphView {
	private final Graph graph;

	/**
	 * Constructor
	 * @param solution
	 */
	public GraphView(Solution solution) {
		graph = new SingleGraph("VRP Solution");
		graph.setAttribute("ui.stylesheet", "node { text-size: 20px; } edge { text-size: 20px; }");
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		graph.setStrict(false);
		for(Vertex v : solution.getVertices()) {
			org.graphstream.graph.Node n = graph.addNode(String.valueOf(v.getId()));
			n.setAttribute("xy", v.getX(), v.getY());
			if(v instanceof Client) n.setAttribute("ui.label", v.getId());
			if(v instanceof Depot) n.setAttribute("ui.style", "fill-color: rgb(255, 0, 0);");
		}
		int i = 0;
		for(Vehicle v : solution.getVehicles()) {
			int a = 0;
			for(int j=0; j<v.getVertices().size() - 1; j++) {
				Vertex source = v.getVertices().get(j);
				Vertex destination = v.getVertices().get(j+1);
				graph.addEdge(i + " " + a, source.getId(), destination.getId());
				if(graph.getEdge(i + " " + a) != null) graph.getEdge(i + " " + a).setAttribute("ui.style", "fill-color: rgb(" + (i * 70) % 256 + ", " + (i * 10) % 256 + ", " + (255 - (i * 20) % 256) + ");");
				a++;
			}
			i++;
		}
		graph.display(false);
	}

	/**
	 * Update the graph
	 * @param solution the new solution
	 */
	public void update(Solution solution) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		graph.getEdgeSet().clear();
		int i = 0;
		for(Vehicle v : solution.getVehicles()) {
			int a = 0;
			for(int j=0; j<v.getVertices().size() - 1; j++) {
				Vertex source = v.getVertices().get(j);
				Vertex destination = v.getVertices().get(j+1);
				graph.addEdge(i + " " + a, source.getId(), destination.getId());
				if(graph.getEdge(i + " " + a) != null) graph.getEdge(i + " " + a).setAttribute("ui.style", "fill-color: rgb(" + (i * 70) % 256 + ", " + (i * 10) % 256 + ", " + (255 - (i * 20) % 256) + ");");
				a++;
			}
			i++;
		}
	}
}
