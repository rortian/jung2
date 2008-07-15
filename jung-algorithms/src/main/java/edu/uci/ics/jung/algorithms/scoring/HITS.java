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

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class HITS<V,E> extends HITSWithPriors<V,E>
{

    public HITS(Graph<V,E> g, Transformer<E, Double> edge_weights, double alpha)
    {
        super(g, edge_weights, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
    }

    public HITS(DirectedGraph<V,E> g, double alpha)
    {
        super(g, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
    }

    public HITS(UndirectedGraph<V,E> g, double alpha)
    {
        super(g, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
    }
    
    public HITS(DirectedGraph<V,E> g)
    {
        this(g, 0.0);
    }

    public HITS(UndirectedGraph<V,E> g)
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
    	
    	public String toString()
    	{
    		return String.format("[h:%.4f,a:%.4f]", this.hub, this.authority);
    	}
    }
}
