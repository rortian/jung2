/*
 * Created on Jul 10, 2007
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

public class BetweennessCentralityScorer<V,E> implements VertexScorer<V,Double>, EdgeScorer<E, Double>
{
    /**
     * Specifies whether this instance generates scores for vertices.
     */
    protected boolean score_vertices;
    
    /**
     * Specifies whether this instance generates scores for edges.
     */
    protected boolean score_edges;

    /**
     * Specifies the weight for each edge (if non-null).
     */
    protected Transformer<E, ? extends Number> edge_weights;
    
    
    public BetweennessCentralityScorer(Graph<V,E> graph, Transformer<E,? extends Number> edge_weights, boolean score_vertices,
            boolean score_edges)
    {
        
    }
    
    
    public Transformer<V, Double> getVertexScores()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public Transformer<E, Double> getEdgeScores()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public void evaluate()
    {
        // TODO Auto-generated method stub
        
    }

}
