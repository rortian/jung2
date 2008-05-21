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

public class PageRankWithPriors<V, E> extends AbstractIterativeScorerWithPriors<V, E, Double>
{
    protected double disappearing_potential_total;
    
    public PageRankWithPriors(Graph<V,E> graph, Transformer<E, ? extends Number> edge_weights, 
            Transformer<V, Double> vertex_priors, double alpha)
    {
        super(graph, edge_weights, vertex_priors, alpha);
    }
    
    /**
     * 
     * @param graph
     * @param output_map
     * @param alpha
     */
    public PageRankWithPriors(Graph<V,E> graph, Transformer<V, Double> vertex_priors, double alpha)
    {
        super(graph, vertex_priors, alpha);
    }
    
    /**
     * 
     */
    @Override
    public double update(V v)
    {
        collectDisappearingPotential(v);
        
        double total_input = 0;
        for (E e : graph.getInEdges(v))
        {
            V w = graph.getOpposite(v, e);
            total_input += (getCurrentValue(w) * getEdgeWeight(w,e).doubleValue());
        }
        
        // modify total_input according to alpha
        double new_value = total_input * (1 - alpha) + getVertexPrior(v) * alpha;
        setOutputValue(v, new_value);
        
        return Math.abs(getCurrentValue(v) - new_value);
    }

    @Override
    protected void afterStep()
    {
        // distribute disappearing potential according to priors
        if (disappearing_potential_total > 0)
        {
            for (V v : graph.getVertices())
            {
                setOutputValue(v, getOutputValue(v) + 
                        (1 - alpha) * (disappearing_potential_total * getVertexPrior(v)));
            }
        }
        
        super.afterStep();
    }
    
    @Override
    protected void collectDisappearingPotential(V v)
    {
        if (graph.outDegree(v) == 0)
        {
            if (isDisconnectedGraphOK())
                disappearing_potential_total += getCurrentValue(v);
            else
                throw new IllegalArgumentException("Outdegree of " + v + " must be > 0");
        }
    }
}
