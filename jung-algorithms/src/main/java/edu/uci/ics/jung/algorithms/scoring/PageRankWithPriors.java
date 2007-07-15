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

import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

public class PageRankWithPriors<V, E> extends AbstractIterativeScorerWithPriors<V, E, Double>
{
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
    public void step()
    {
        double disappearing_potential = 0;
        
        for (V v : graph.getVertices())
        {
            disappearing_potential += getDisappearingPotential(v);
            
            double total_input = 0;
            for (E e : graph.getInEdges(v))
            {
                V w = graph.getOpposite(v, e);
                total_input += (getVertexScore(w) * getEdgeWeight(w,e).doubleValue());
            }
            
            // modify total_input according to alpha
            double new_value = total_input * (1 - alpha) + getVertexPrior(v) * alpha;
            current_values.put(v, new_value);
            
            // update max_delta as appropriate
            this.max_delta = Math.max(this.max_delta, Math.abs(output.get(v) - new_value));
        }
        
        // distribute disappearing potential according to priors
        if (disappearing_potential > 0)
        {
            for (V v : graph.getVertices())
            {
                setVertexScore(v, 
                        getVertexScore(v) + 
                        (1 - alpha) * (disappearing_potential * getVertexPrior(v)));
            }
        }
        
        // swap output and current values
        Map<V, Double> tmp = output;
        output = current_values;
        current_values = tmp;
        total_iterations++;
    }

    protected Double getDisappearingPotential(V v)
    {
        if (graph.outDegree(v) == 0)
        {
            if (accept_disconnected_graph)
                return output.get(v);
            else
                throw new IllegalArgumentException("Outdegree of " + v + " must be > 0");
        }
        return 0.0;
    }
}
