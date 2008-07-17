/*
 * Created on Jul 6, 2007
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

import edu.uci.ics.jung.graph.Graph;

/**
 * Assigns a score to each vertex equal to its degree.
 *
 * @param <V> the vertex type
 */
public class DegreeScorer<V> implements VertexScorer<V,Integer>
{
	/**
	 * The graph for which scores are to be generated.
	 */
    protected Graph<V,?> graph;
    
    /**
     * The transformer which assigns scores to vertices.
     */
    protected Transformer<V, Integer> scores;
    
    /**
     * Creates an instance for the specified graph.
     * @param graph the input graph
     */
    public DegreeScorer(Graph<V,?> graph)
    {
        this.graph = graph;
    }
    
    /**
     * Assigns a score to each vertex, if one has not already been assigned.
     */
    public void evaluate() 
    {
        if (scores == null)
        {
            scores = new Transformer<V,Integer>()
            {
                public Integer transform(V vertex)
                {
                    return graph.degree(vertex);
                }
            };
        }
    }

    /**
     * Returns the transformer which assigns scores to each vertex.
     * @return the transformer assigning scores to each vertex
     */
    public Transformer<V, Integer> getVertexScores() 
    {
        return scores; 
    }

}
