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

import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class HITSWithPriors<V, E> extends AbstractIterativeScorerWithPriors<V, E, Pair<Double>>
{

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

    
    public void step()
    {
        double disappearing_hub = 0;
        double disappearing_auth = 0;
        
        for (V v : graph.getVertices())
        {
            Pair<Double> disappearing_potential = getDisappearingPotential(v);
            disappearing_hub += disappearing_potential.getFirst();
            disappearing_auth += disappearing_potential.getSecond();
            
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
            auth = auth * (1 - alpha) + vertex_priors.transform(v).getSecond() * alpha;
            hub = hub * (1 - alpha) + vertex_priors.transform(v).getFirst() * alpha;
            
            // update max_delta as appropriate
            this.max_delta = Math.max(this.max_delta, Math.abs(getHubScore(v) - hub));
            this.max_delta = Math.max(this.max_delta, Math.abs(getAuthScore(v) - auth));

            setScore(v, hub, auth);
        }

        if (disappearing_hub > 0 || disappearing_auth > 0)
        {
            for (V v : graph.getVertices())
            {
                setScore(v, 
                        getHubScore(v) + (1 - alpha) * (disappearing_hub * vertex_priors.transform(v).getFirst()),
                        getAuthScore(v) + (1 - alpha) * (disappearing_hub * vertex_priors.transform(v).getSecond()));
            }
        }
        
        // swap output and current values
        Map<V, Pair<Double>> tmp = output;
        output = current_values;
        current_values = tmp;
        
        total_iterations++;
    }
    
    protected Pair<Double> getDisappearingPotential(V v)
    {
        double hub = 0;
        double auth = 0;
        if (graph.outDegree(v) == 0)
        {
            if (accept_disconnected_graph)
                hub = getAuthScore(v);
            else
                throw new IllegalArgumentException("Outdegree of " + v + " must be > 0");
        }
        if (graph.inDegree(v) == 0)
        {
            if (accept_disconnected_graph)
                auth = getHubScore(v);
            else
                throw new IllegalArgumentException("Indegree of " + v + " must be > 0");
        }
        
        return new Pair<Double>(hub, auth);
    }

    
    protected double getHubScore(V v)
    {
        return output.get(v).getFirst();
    }
    
    protected double getAuthScore(V v)
    {
        return output.get(v).getSecond();
    }

    protected void setScore(V v, double hub, double authority)
    {
        current_values.put(v, new Pair<Double>(hub, authority));
    }
}
