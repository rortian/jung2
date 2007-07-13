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
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.graph.Graph;

public class PageRankWithPriors<V, E> extends AbstractIterativeScorer<V, E, Double> implements VertexScorer<V, Double>
{
    /**
     * 
     */
    protected double alpha;
    
    /**
     * 
     */
    protected Transformer<V, Double> vertex_priors;
    
    /**
     * True if this instance permits 'sink' vertices (those with outdegree 0) and adjusts the results 
     * accordingly (by distributing incoming potential according to the specified vertex priors, i.e., 
     * uniformly by default), and false if this instance rejects sink vertices and throws an exception if 
     * one is encountered.
     */
    protected boolean accept_sinks;

    public PageRankWithPriors(Graph<V,E> graph, Transformer<E, ? extends Number> edge_weights, 
            Transformer<V, Double> vertex_priors, double alpha)
    {
        super(graph, edge_weights);
    }
    
    /**
     * 
     * @param graph
     * @param output_map
     * @param alpha
     */
    public PageRankWithPriors(Graph<V,E> graph, Transformer<V, Double> vertex_priors, double alpha)
    {
        super(graph);
        this.alpha = alpha;
        this.vertex_priors = vertex_priors;
        
        // initialize output to priors
//        this.user_output = output_map;
        for (V v : graph.getVertices())
            output.put(v, vertex_priors.transform(v));

        
        this.accept_sinks = true;
    }
    
    /**
     * 
     * @param graph
     * @param alpha
     */
    public PageRankWithPriors(Graph<V,E> graph, Transformer<V, Double> vertex_priors)
    {
        this(graph, vertex_priors, 0);
    }
    
    /**
     * 
     */
    public void step()
    {
        double disappearing_potential = 0;
        
        for (V v : graph.getVertices())
        {
            if (graph.outDegree(v) == 0)
            {
                if (accept_sinks)
                    disappearing_potential += output.get(v).doubleValue();
                else
                    throw new IllegalArgumentException("Input graph vertices must all have outdegree > 0");
            }
            
            double total_input = 0;
            for (E e : graph.getInEdges(v))
            {
                V w = graph.getOpposite(v, e);
                double value;
                if (edge_weights instanceof VertexEdgeWeight)
                    value = ((VertexEdgeWeight<V,E,? extends Number>)edge_weights).transform(new VEPair<V,E>(w,e)).doubleValue();
                else
                    value = edge_weights.transform(e).doubleValue();
                total_input += (output.get(w).doubleValue() * value);
            }
            
            // modify total_input according to alpha
            double new_value = total_input * (1 - alpha) + vertex_priors.transform(v).doubleValue() * alpha;
            current_values.put(v, new_value);
            
            // update max_delta as appropriate
            this.max_delta = Math.max(this.max_delta, Math.abs(output.get(v) - new_value));
        }
        
        // distribute disappearing potential according to priors
        if (disappearing_potential > 0)
        {
//            double to_add = (1 - alpha) * (disappearing_potential / graph.getVertexCount());
            for (V v : graph.getVertices())
            {
                double value = current_values.get(v).doubleValue();
                double to_add = (1 - alpha) * (disappearing_potential * vertex_priors.transform(v));
                current_values.put(v, value + to_add);
            }
        }
        
        // swap output and current values
        Map<V, Double> tmp = output;
        output = current_values;
        current_values = tmp;
        total_iterations++;
    }

    public void setAcceptSinks(boolean accept)
    {
        this.accept_sinks = accept;
    }
    
    public boolean getAcceptSinks()
    {
        return this.accept_sinks;
    }
    
    public Transformer<V, Double> getVertexScores()
    {
//        if (user_output != output)
//        {
//            // copy output into user-specified storage if output is currently in the storage
//            // allocated to current values (current values and output swap back and forth over the
//            // course of the algorithm to save space and reallocation time)
//            for (Map.Entry<V, Double> entry : output.entrySet())
//            {
//                user_output.put(entry.getKey(), entry.getValue());
//            }
//        }
        return MapTransformer.getInstance(output);
    }

    /**
     * @return the vertex_priors
     */
    public Transformer<V, Double> getVertexPriors()
    {
        return vertex_priors;
    }

    /**
     * @return the alpha
     */
    public double getAlpha()
    {
        return alpha;
    }

}
