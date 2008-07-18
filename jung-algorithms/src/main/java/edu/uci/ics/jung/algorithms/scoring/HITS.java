/*
 * Created on Jul 15, 2007
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

import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.Graph;

import org.apache.commons.collections15.Transformer;

/**
 * Assigns hub and authority scores to each vertex depending on the topology of
 * the network.  
 * 
 * <p>The classic HITS algorithm essentially proceeds as follows:
 * <pre>
 * assign equal initial hub and authority values to each vertex
 * repeat the following 
 *   for each vertex w:
 *     w.hub = sum over successors x of x.authority
 *     w.authority = sum over predecessors v of v.hub
 *   normalize hub and authority scores so that the sum of the squares of each = 1
 * until scores converge
 * </pre>
 * 
 * This is somewhat different from eigenvector-based algorithms such as PageRank in that 
 * <ul>
 * <li/>the edge weights are effectively all 1, i.e., they can't be interpreted
 * as transition probabilities
 * <li/>the scores cannot be interpreted as posterior probabilities (due to the different
 * normalization)
 * <li/>
 * </ul>
 * 
 * @param <V> the vertex type
 * @param <E> the edge type
 * 
 * @see "'Authoritative sources in a hyperlinked environment' by Jon Kleinberg, 1997"
 */
public class HITS<V,E> extends HITSWithPriors<V,E>
{

    /**
     * Creates an instance for the specified graph, edge weights, and 
     * @param g
     * @param edge_weights
     * @param alpha
     */
    public HITS(Graph<V,E> g, Transformer<E, Double> edge_weights, double alpha)
    {
        super(g, edge_weights, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
    }

    public HITS(Graph<V,E> g, double alpha)
    {
        super(g, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
    }

    public HITS(Graph<V,E> g)
    {
        this(g, 0.0);
    }
    

    public static class Scores
    {
    	public double hub;
    	public double authority;
    	
    	public Scores(double hub, double authority)
    	{
    		this.hub = hub;
    		this.authority = authority;
    	}
    	
    	@Override
        public String toString()
    	{
    		return String.format("[h:%.4f,a:%.4f]", this.hub, this.authority);
    	}
    }
}
