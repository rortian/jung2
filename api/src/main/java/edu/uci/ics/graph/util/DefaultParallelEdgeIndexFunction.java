/*
 * Created on Sep 24, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.graph.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;

/**
 * A class which creates and maintains indices for parallel edges.
 * Parallel edges are defined here to be those edges of type <code>Edge</code>
 * that are returned by <code>v.findEdgeSet(w)</code> for some 
 * <code>v</code> and <code>w</code>.
 * 
 * <p>At this time, users are responsible for resetting the indices if changes to the
 * graph make it appropriate.</p>
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson
 *
 */
public class DefaultParallelEdgeIndexFunction<V,E extends Edge<V>> implements ParallelEdgeIndexFunction<V,E>
{
    protected Map<E, Integer> edge_index = new HashMap<E, Integer>();
    
    private DefaultParallelEdgeIndexFunction() {
    }
    
    public static <V,E extends Edge<V>> DefaultParallelEdgeIndexFunction<V,E> getInstance() {
        return new DefaultParallelEdgeIndexFunction<V,E>();
    }
    /**
     * Returns the index for the specified edge.
     * Calculates the indices for <code>e</code> and for all edges parallel
     * to <code>e</code>.
     */
    public int getIndex(Graph<V,E> graph, E e)
    {
        Integer index = edge_index.get(e);
        if(index == null) 
            index = getIndex_internal(graph, e);
        return index.intValue();
    }

    protected Integer getIndex_internal(Graph<V,E> graph, E e)
    {
        Pair<V> endpoints = e.getEndpoints();
        V u = endpoints.getFirst();
        V v = endpoints.getSecond();
        Collection<E> commonEdgeSet = new HashSet<E>(graph.getIncidentEdges(u));
        commonEdgeSet.retainAll(graph.getIncidentEdges(v));
        int count = 0;
        for(E other : commonEdgeSet) {
            if (e.equals(other) == false) {
                edge_index.put(other, new Integer(count));
                count++;
            }
        }
        Integer index = new Integer(count);
        edge_index.put(e, index);
        
        return index;
    }
    
    /**
     * Resets the indices for this edge and its parallel edges.
     * Should be invoked when an edge parallel to <code>e</code>
     * has been added or removed.
     * @param e
     */
    public void reset(Graph<V,E> graph, E e)
    {
        getIndex_internal(graph, e);
    }
    
    /**
     * Clears all edge indices for all edges in all graphs.
     * Does not recalculate the indices.
     */
    public void reset()
    {
        edge_index.clear();
    }
}
