/*
 * Created on Jul 14, 2007
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

public abstract class AbstractIterativeScorerWithPriors<V, E, T> extends
        AbstractIterativeScorer<V, E, T> implements VertexScorer<V, T>
{
    /**
     * 
     */
    protected Transformer<V, T> vertex_priors;

    protected double alpha;

    protected boolean accept_disconnected_graph;
    
    public AbstractIterativeScorerWithPriors(Graph<V, E> g,
            Transformer<E, ? extends Number> edge_weights, Transformer<V, T> vertex_priors, double alpha)
    {
        super(g, edge_weights);
        this.vertex_priors = vertex_priors;
        this.accept_disconnected_graph = true;
    }

    public AbstractIterativeScorerWithPriors(Graph<V, E> g, Transformer<V, T> vertex_priors, double alpha)
    {
        super(g);
        this.vertex_priors = vertex_priors;
        this.accept_disconnected_graph = true;
    }

    public void initialize()
    {
        super.initialize();
        // initialize output to priors
        for (V v : graph.getVertices())
            output.put(v, vertex_priors.transform(v));

    }
    
    protected abstract T getDisappearingPotential(V v);

    public void acceptDisconnectedGraph(boolean accept)
    {
        this.accept_disconnected_graph = accept;
    }
    
    public boolean isDisconnectedGraphOK()
    {
        return this.accept_disconnected_graph;
    }
    
    protected T getVertexScore(V v)
    {
        return output.get(v);
    }
    
    protected void setVertexScore(V v, T t)
    {
        output.put(v, t);
    }

    /**
     * @return the vertex_priors
     */
    public Transformer<V, T> getVertexPriors()
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
