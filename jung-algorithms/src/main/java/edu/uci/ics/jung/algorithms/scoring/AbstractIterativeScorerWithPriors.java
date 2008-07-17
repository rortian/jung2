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

public abstract class AbstractIterativeScorerWithPriors<V,E,S> extends
        AbstractIterativeScorer<V,E,S> implements VertexScorer<V,S>
{
    /**
     * 
     */
    protected Transformer<V,? extends S> vertex_priors;

    protected double alpha;
    
    public AbstractIterativeScorerWithPriors(Graph<V,E> g,
            Transformer<E,? extends Number> edge_weights, 
            Transformer<V,? extends S> vertex_priors, double alpha)
    {
        super(g, edge_weights);
        this.vertex_priors = vertex_priors;
        this.alpha = alpha;
        initialize();
    }

    public AbstractIterativeScorerWithPriors(Graph<V,E> g, 
    		Transformer<V,? extends S> vertex_priors, double alpha)
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
        // initialize output values to priors
        // (output and current are swapped before each step(), so current will
        // have priors when update()s start happening)
        for (V v : graph.getVertices())
            setOutputValue(v, getVertexPrior(v));
    }
    
    protected S getVertexPrior(V v)
    {
        return vertex_priors.transform(v);
    }

    /**
     * @return the vertex_priors
     */
    public Transformer<V, ? extends S> getVertexPriors()
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
