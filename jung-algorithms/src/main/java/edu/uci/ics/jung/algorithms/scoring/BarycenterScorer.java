/*
 * Created on Jul 12, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to each vertex according to the sum of the distances to all other vertices.
 * 
 * @author Joshua O'Madadhain
 */
public class BarycenterScorer<V,E> extends DistanceCentralityScorer<V, E>
{
    /**
     * 
     * @param graph
     * @param distance
     */
    public BarycenterScorer(Hypergraph<V,E> graph, Distance<V> distance)
    {
        super(graph, distance, false);
    }
    
    /**
     * 
     * @param graph
     * @param edge_weights
     */
    public BarycenterScorer(Hypergraph<V,E> graph, Transformer<E, ? extends Number> edge_weights)
    {
        super(graph, edge_weights, false);
    }
    
    public BarycenterScorer(Graph<V,E> graph)
    {
        super(graph, false);
    }
}
