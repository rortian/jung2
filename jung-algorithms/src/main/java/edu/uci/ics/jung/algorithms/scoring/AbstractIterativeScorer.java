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

import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractIterativeScorer<V,E,T extends Number> implements IterativeContext
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
    protected Transformer<E, ? extends Number> edge_weights;
    
    /**
     * 
     */
//    protected Transformer<VEPair<V,E>, ? extends Number> vertex_edge_weights;
    
    /**
     * 
     */
    protected Map<V, T> output;
    
    /**
     * 
     */
    protected Map<V, T> current_values;
    
    
    /**
     * The largest change seen so far among all vertex scores.
     */
    protected double max_delta;
    
    public AbstractIterativeScorer(Graph<V,E> g, Transformer<E, ? extends Number> edge_weights)
    {
        this.graph = g;
        this.max_iterations = 100;
        this.tolerance = 0.001;
        this.edge_weights = edge_weights;
        initialize();
    }
    
    public AbstractIterativeScorer(Graph<V,E> g)
    {
        this(g, (Transformer<E, ? extends Number>)(g instanceof DirectedGraph ? new UniformOut<V,E>(g) : new UniformIncident<V,E>(g)));
    }
    
    protected void initialize()
    {
        this.total_iterations = 0;
        this.max_delta = Double.MIN_VALUE;
        this.current_values = new HashMap<V, T>();
        this.output = new HashMap<V, T>();
    }
    
    public void evaluate()
    {
        while (!done())
            step();
    }
    
    public boolean done()
    {
        return total_iterations > max_iterations || max_delta < tolerance;
    }

    public abstract void step();

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
    public Transformer<E, ? extends Number> getEdgeWeights()
    {
        return edge_weights;
    }

//    /**
//     * @param edge_weights the edge_weights to set
//     */
//    public void setEdgeWeights(Transformer<E, ? extends Number> edge_weights)
//    {
//        this.edge_weights = edge_weights;
//        this.vertex_edge_weights = null;
//        initialize();
//    }
//
//    /**
//     * @return the vertex_edge_weights
//     */
//    public Transformer<VEPair<V, E>, ? extends Number> getVertexEdgeWeights()
//    {
//        return vertex_edge_weights;
//    }
//
//    /**
//     * @param vertex_edge_weights the vertex_edge_weights to set
//     */
//    public void setVertexEdgeWeights(Transformer<VEPair<V, E>, ? extends Number> vertex_edge_weights)
//    {
//        this.vertex_edge_weights = vertex_edge_weights;
//        this.edge_weights = null;
//        initialize();
//    }



}
