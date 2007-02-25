package edu.uci.ics.jung.graph;

import java.util.Collection;

public interface Forest<V,E> extends DirectedGraph<V,E> {
	
	Collection<Tree<V,E>> getTrees();

}
