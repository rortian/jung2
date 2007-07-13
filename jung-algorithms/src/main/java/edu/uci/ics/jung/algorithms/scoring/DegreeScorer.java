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

public class DegreeScorer<V> implements VertexScorer<V,Double>
{
    protected Graph<V,?> graph;
    
    protected Transformer<V, Double> scores;
    
    public DegreeScorer(Graph<V,?> graph)
    {
        this.graph = graph;
    }
    
    public void evaluate() 
    {
        if (scores == null)
        {
            scores = new Transformer<V,Double>()
            {
                public Double transform(V vertex)
                {
                    return new Double(graph.degree(vertex));
                }
            };
        }
    }

    public Transformer<V, Double> getVertexScores() 
    {
        return scores; 
    }

}
