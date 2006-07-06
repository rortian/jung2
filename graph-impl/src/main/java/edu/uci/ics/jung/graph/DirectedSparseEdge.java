package edu.uci.ics.jung.graph;

import edu.uci.ics.graph.DirectedEdge;
import edu.uci.ics.graph.util.Pair;

public class DirectedSparseEdge<V> implements DirectedEdge<V> {
	
	Pair<V> endpoints;
	
	public DirectedSparseEdge(V one, V two) {
		endpoints = new Pair<V>(one, two);
	}
	public Pair<V> getEndpoints() {
		// TODO Auto-generated method stub
		return endpoints;
	}

}
