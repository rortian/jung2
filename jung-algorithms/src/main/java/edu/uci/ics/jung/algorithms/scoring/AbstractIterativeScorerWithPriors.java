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

public abstract class AbstractIterativeScorerWithPriors<V, E, S> extends
        AbstractIterativeScorer<V, E, S> implements VertexScorer<V, S>
{
    /**
     * 
     */
    protected Transformer<V, S> vertex_priors;

    protected double alpha;

    private boolean accept_disconnected_graph;
    
    public AbstractIterativeScorerWithPriors(Graph<V, E> g,
            Transformer<E, ? extends Number> edge_weights, Transformer<V, S> vertex_priors, double alpha)
    {
        super(g, edge_weights);
        this.vertex_priors = vertex_priors;
        this.alpha = alpha;
        initialize();
    }

    public AbstractIterativeScorerWithPriors(Graph<V, E> g, Transformer<V, S> vertex_priors, double alpha)
    {
        super(g);
        this.vertex_priors = vertex_priors;
        this.alpha = alpha;
        initialize();
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.accept_disconnected_graph = true;
        // initialize current values to priors
        for (V v : graph.getVertices())
            setCurrentValue(v, getVertexPrior(v));
    }
    
    protected abstract void collectDisappearingPotential(V v);

    public void acceptDisconnectedGraph(boolean accept)
    {
        this.accept_disconnected_graph = accept;
    }
    
    public boolean isDisconnectedGraphOK()
    {
        return this.accept_disconnected_graph;
    }
    
    protected S getVertexPrior(V v)
    {
        return vertex_priors.transform(v);
    }

    /**
     * @return the vertex_priors
     */
    public Transformer<V, S> getVertexPriors()
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
