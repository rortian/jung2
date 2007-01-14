package edu.uci.ics.graph.util;

import edu.uci.ics.graph.Graph;

public class VertexContext<V,E> {
	
	public Graph<V,E> graph;
	public V vertex;
	
	public VertexContext(Graph<V, E> graph, V vertex) {
		this.graph = graph;
		this.vertex = vertex;
	}
}

