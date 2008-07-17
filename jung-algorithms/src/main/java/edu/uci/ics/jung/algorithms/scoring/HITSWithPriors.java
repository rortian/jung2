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
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.graph.Graph;

public class HITSWithPriors<V, E> 
	extends AbstractIterativeScorerWithPriors<V,E,HITS.Scores>
{
    protected double disappearing_hub;
    protected double disappearing_auth;

    public HITSWithPriors(Graph<V,E> g,
            Transformer<E, ? extends Number> edge_weights,
            Transformer<V, HITS.Scores> vertex_priors, double alpha)
    {
        super(g, edge_weights, vertex_priors, alpha);
    }

    @SuppressWarnings("unchecked")
    public HITSWithPriors(Graph<V,E> g, 
          Transformer<V, HITS.Scores> vertex_priors, double alpha)
    {
    	super(g, new ConstantTransformer(1.0), vertex_priors, alpha);
    }

    
//    public HITSWithPriors(DirectedGraph<V,E> g,
//            Transformer<V, Pair<Double>> vertex_priors, double alpha)
//    {
//    	super(g, vertex_priors, alpha);
//    	this.edge_weights = new UniformInOut<V,E>(g);
//    }
//
//    public HITSWithPriors(UndirectedGraph<V,E> g, 
//            Transformer<V, Pair<Double>> vertex_priors, double alpha)
//    {
//        super(g, vertex_priors, alpha);
//        this.edge_weights = new UniformIncidentPair<V,E>(g);
//    }
    
    @Override
    public double update(V v)
    {
        collectDisappearingPotential(v);
        
        double auth = 0;
        for (E e : graph.getInEdges(v))
        {
            V w = graph.getOpposite(v, e);
            auth += (getHubScore(w) * getEdgeWeight(w, e).doubleValue()); //.getSecond().doubleValue());
        }
        
        double hub = 0;
        for (E e : graph.getOutEdges(v))
        {
            V x = graph.getOpposite(v,e);
            hub += (getAuthScore(x) * getEdgeWeight(x, e).doubleValue()); // .getFirst().doubleValue()); 
        }
        
        // modify total_input according to alpha
        auth = auth * (1 - alpha) + getAuthPrior(v) * alpha;
        hub = hub * (1 - alpha) + getHubPrior(v) * alpha;
        setOutputValue(v, new HITS.Scores(hub, auth));

        return Math.max(Math.abs(getHubScore(v) - hub), Math.abs(getAuthScore(v) - auth));
    }

    @Override
    protected void afterStep()
    {
//        if (disappearing_hub > 0 || disappearing_auth > 0)
//        {
//            for (V v : graph.getVertices())
//            {
//                double new_hub = getOutputValue(v).getFirst().doubleValue() + 
//                    (1 - alpha) * (disappearing_hub * getHubPrior(v));
//                double new_auth = getOutputValue(v).getSecond().doubleValue() + 
//                    (1 - alpha) * (disappearing_hub * getAuthPrior(v));
//                setOutputValue(v, new Pair<Double>(new_hub, new_auth));
//            }
//            disappearing_hub = 0;
//            disappearing_auth = 0;
//        }
    	disappearing_hub = 0;
    	disappearing_auth = 0; 
        
    	normalizeScores();

    	
        super.afterStep();
    }

	/**
	 * Normalizes scores so that sum of their squares = 1.
	 */
	protected void normalizeScores() {
    	double hub_ssum = 0;
    	double auth_ssum = 0;
    	for (V v : graph.getVertices())
    	{
    		double hub_val = getOutputValue(v).hub;
    		double auth_val = getOutputValue(v).authority;
    		hub_ssum += (hub_val * hub_val);
    		auth_ssum += (auth_val * auth_val);
    	}

    	hub_ssum = Math.sqrt(hub_ssum);
    	auth_ssum = Math.sqrt(auth_ssum);
    	
    	for (V v : graph.getVertices())
    	{
    		HITS.Scores values = getOutputValue(v);
    		setOutputValue(v, new HITS.Scores(
    				values.hub / hub_ssum,
    				values.authority / auth_ssum));
    	}
	}
    
    @Override
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

    
    public double getHubScore(V v)
    {
        return getCurrentValue(v).hub;
    }
    
    public double getAuthScore(V v)
    {
        return getCurrentValue(v).authority;
    }

    protected double getHubPrior(V v)
    {
        return getVertexPrior(v).hub;
    }
    
    protected double getAuthPrior(V v)
    {
        return getVertexPrior(v).authority;
    }
    
}
