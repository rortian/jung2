package edu.uci.ics.jung.graph;

import edu.uci.ics.graph.UndirectedEdge;
import edu.uci.ics.graph.util.Pair;

public class UndirectedSparseEdge<V> implements UndirectedEdge<V> {
	
	Pair<V> endpoints;
	
	public UndirectedSparseEdge(V one, V two) {
		endpoints = new Pair<V>(one, two);
	}
	public Pair<V> getEndpoints() {
		return endpoints;
	}

}
