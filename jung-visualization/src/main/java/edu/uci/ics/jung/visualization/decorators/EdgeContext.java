package edu.uci.ics.jung.visualization.decorators;

import edu.uci.ics.graph.Graph;

public class EdgeContext<V,E> {
	
	public Graph<V,E> graph;
	public E edge;
	
	public EdgeContext(Graph<V, E> graph, E edge) {
		super();
		this.graph = graph;
		this.edge = edge;
	}
}

