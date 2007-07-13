/*
 * Created on Jul 12, 2007
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

/**
 * 
 * @author Joshua O'Madadhain
 */
public class EigenvectorCentrality<V,E> extends PageRank<V,E>
{

    /**
     * @param graph
     * @param output_map
     * @param alpha
     */
    public EigenvectorCentrality(Graph<V,E> graph, Transformer<?, ? extends Number> edge_weights)
    {
        super(graph, edge_weights, 0);
        setAcceptSinks(false);
    }

    /**
     * @param graph
     * @param alpha
     */
    public EigenvectorCentrality(Graph<V,E> graph)
    {
        super(graph, 0);
        setAcceptSinks(false);
    }

}
