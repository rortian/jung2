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
import edu.uci.ics.jung.graph.util.Pair;

public class HITSWithPriors<V, E> extends AbstractIterativeScorerWithPriors<V, E, Pair<Double>>
{
    protected double disappearing_hub;
    protected double disappearing_auth;

    public HITSWithPriors(Graph<V, E> g,
            Transformer<E, ? extends Number> edge_weights,
            Transformer<V, Pair<Double>> vertex_priors, double alpha)
    {
        super(g, edge_weights, vertex_priors, alpha);
    }

    public HITSWithPriors(Graph<V, E> g,
            Transformer<V, Pair<Double>> vertex_priors, double alpha)
    {
        super(g, vertex_priors, alpha);
    }

    
    public double update(V v)
    {
        collectDisappearingPotential(v);
        
        double auth = 0;
        for (E e : graph.getInEdges(v))
        {
            V w = graph.getOpposite(v, e);
            auth += (getHubScore(w) * getEdgeWeight(w, e).doubleValue());
        }
        
        double hub = 0;
        for (E e : graph.getOutEdges(v))
        {
            V x = graph.getOpposite(v,e);
            hub += (getAuthScore(x) * getEdgeWeight(x, e).doubleValue()); 
        }
        
        // modify total_input according to alpha
        auth = auth * (1 - alpha) + getAuthPrior(v) * alpha;
        hub = hub * (1 - alpha) + getHubPrior(v) * alpha;
        setOutputValue(v, new Pair<Double>(hub, auth));

        return Math.max(Math.abs(getHubScore(v) - hub), Math.abs(getAuthScore(v) - auth));
    }

    protected void afterStep()
    {
        if (disappearing_hub > 0 || disappearing_auth > 0)
        {
            for (V v : graph.getVertices())
            {
                double new_hub = getOutputValue(v).getFirst() + (1 - alpha) * (disappearing_hub * getHubPrior(v));
                double new_auth = getOutputValue(v).getSecond() + (1 - alpha) * (disappearing_hub * getAuthPrior(v));
                setOutputValue(v, new Pair<Double>(new_hub, new_auth));
            }
        }
        super.afterStep();
    }
    
    protected void collectDisappearingPotential(V v)
    {
        if (graph.outDegree(v) == 0)
        {
            if (isDisconnectedGraphOK())
                disappearing_hub += getAuthScore(v);
            else
                throw new IllegalArgumentException("Outdegree of " + v + " must be > 0");
        }
        if (graph.inDegree(v) == 0)
        {
            if (isDisconnectedGraphOK())
                disappearing_auth += getHubScore(v);
            else
                throw new IllegalArgumentException("Indegree of " + v + " must be > 0");
        }
    }

    
    protected double getHubScore(V v)
    {
        return getCurrentValue(v).getFirst();
    }
    
    protected double getAuthScore(V v)
    {
        return getCurrentValue(v).getSecond();
    }

    protected double getHubPrior(V v)
    {
        return getVertexPrior(v).getFirst();
    }
    
    protected double getAuthPrior(V v)
    {
        return getVertexPrior(v).getSecond();
    }
}
