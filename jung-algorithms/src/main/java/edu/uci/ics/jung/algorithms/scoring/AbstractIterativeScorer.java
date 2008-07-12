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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.scoring.util.DelegateToEdgeTransformer;
import edu.uci.ics.jung.algorithms.scoring.util.UniformIncident;
import edu.uci.ics.jung.algorithms.scoring.util.UniformOut;
import edu.uci.ics.jung.algorithms.scoring.util.VEPair;
import edu.uci.ics.jung.algorithms.scoring.util.VertexEdgeWeight;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractIterativeScorer<V,E,T,W> implements IterativeContext, VertexScorer<V,T>
{
    /**
     * Maximum number of iterations to use before terminating.  Defaults to 100.
     */
    protected int max_iterations;
    
    /**
     * Minimum change from one step to the next; if all changes are <= tolerance, no further updates will occur.
     * Defaults to 0.001.
     */
    protected double tolerance;
    
    /**
     * The graph on which the calculations are to be made.
     */
    protected Graph<V,E> graph;
    
    /**
     * The total number of iterations used so far.
     */
    protected int total_iterations;
    
    /**
     * 
     */
    private Transformer<VEPair<V,E>, W> edge_weights;
    
    /**
     * 
     */
    private Map<V, T> output;
    
    /**
     * 
     */
    private Map<V, T> current_values;
    
    protected void setOutputValue(V v, T value)
    {
        output.put(v, value);
    }
    
    protected T getOutputValue(V v)
    {
        return output.get(v);
    }
    
    protected T getCurrentValue(V v)
    {
        return current_values.get(v);
    }
    
    protected void setCurrentValue(V v, T value)
    {
        current_values.put(v, value);
    }
    
    /**
     * The largest change seen so far among all vertex scores.
     */
    protected double max_delta;
    
    public AbstractIterativeScorer(Graph<V,E> g, Transformer<E, W> edge_weights)
    {
        this.graph = g;
        this.max_iterations = 100;
        this.tolerance = 0.001;
        setEdgeWeights(edge_weights);
    }
    
//    public AbstractIterativeScorer(Graph<V,E> g)
//    {
//        this(g, (Transformer<E, ? extends Number>)
//                (g instanceof DirectedGraph ? 
//                        new UniformOut<V,E>(g) : 
//                        new UniformIncident<V,E>(g)));
//    }
    
    protected void initialize()
    {
        this.total_iterations = 0;
        this.max_delta = Double.MIN_VALUE;
        this.current_values = new HashMap<V, T>();
        this.output = new HashMap<V, T>();
    }
    
    public void evaluate()
    {
        do
            step();
        while (!done());
    }
    
    public boolean done()
    {
        return total_iterations > max_iterations || max_delta < tolerance;
    }

    public void step()
    {
        for (V v : graph.getVertices())
        {
            double diff = update(v);
            updateMaxDelta(v, diff);
        }
        afterStep();
        total_iterations++;
    }

    protected abstract double update(V v);

    protected void updateMaxDelta(V v, double diff)
    {
        max_delta = Math.max(max_delta, diff);
    }
    
    protected void afterStep()
    {
        Map<V, T> tmp = output;
        output = current_values;
        current_values = tmp;
        
        total_iterations++;
    }
    
    public Transformer<V, T> getVertexScores()
    {
        return MapTransformer.getInstance(output);
    }

    
    public int getMaxIterations()
    {
        return max_iterations;
    }

    public void setMaxIterations(int max_iterations)
    {
        this.max_iterations = max_iterations;
    }

    public double getTolerance()
    {
        return tolerance;
    }

    public void setTolerance(double tolerance)
    {
        this.tolerance = tolerance;
    }
    
    /**
     * @return the edge_weights
     */
    public Transformer<VEPair<V,E>, W> getEdgeWeights()
    {
        return edge_weights;
    }

    /**
     * @param edge_weights the edge_weights to set
     */
    public void setEdgeWeights(Transformer<E, W> edge_weights)
    {
        this.edge_weights = new DelegateToEdgeTransformer<V,E,W>(edge_weights);
        initialize();
    }
    
    protected W getEdgeWeight(V v, E e)
    {
        return edge_weights.transform(new VEPair<V,E>(v,e));
//        if (edge_weights instanceof VertexEdgeWeight)
//            return ((VertexEdgeWeight<V,E,? extends Number>)edge_weights).transform(new VEPair<V,E>(v,e));
//        else
//            return edge_weights.transform(e);
    }
}
