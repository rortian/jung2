package edu.uci.ics.jung.graph;

import java.util.Collection;

/**
 * An interface for a graph which consists of a collection of rooted 
 * directed acyclic graphs.
 * 
 * @author Joshua O'Madadhain
 */
public interface Forest<V,E> extends DirectedGraph<V,E> {
	
    /**
     * Returns a view of this graph as a collection of <code>Tree</code> instances.
     * @return
     */
	Collection<Tree<V,E>> getTrees();

}
