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

import edu.uci.ics.jung.graph.Graph;

public class HITS<V,E> extends HITSWithPriors<V,E>
{

    public HITS(Graph<V,E> g, Transformer<E, ? extends Number> edge_weights, double alpha)
    {
        super(g, edge_weights, ScoringUtils.getTwoValueUniformRootPrior(g.getVertices()), alpha);
    }

    public HITS(Graph<V,E> g, double alpha)
    {
        super(g, ScoringUtils.getTwoValueUniformRootPrior(g.getVertices()), alpha);
    }

    public HITS(Graph<V,E> g)
    {
        this(g, 0.0);
    }

}
