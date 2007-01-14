package edu.uci.ics.graph.util;

import edu.uci.ics.graph.Graph;

public class EdgeContext<V,E> {
	
	public Graph<V,E> graph;
	public E edge;
	
	public EdgeContext(Graph<V, E> graph, E edge) {
		this.graph = graph;
		this.edge = edge;
	}
}

