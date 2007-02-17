/*
 * Created on Oct 21, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.decorators;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.util.Context;


/**
 * Returns the constructor-specified value for each edge type.
 * 
 * @author Joshua O'Madadhain
 */
public class ConstantDirectionalEdgeValueTransformer<V,E> implements Transformer<Context<Graph<V,E>,E>,Number>
{
    protected Double undirected_closeness;
    protected Double directed_closeness;

    /**
     * 
     * @param undirected
     * @param directed
     */
    public ConstantDirectionalEdgeValueTransformer(double undirected, double directed)
    {
        this.undirected_closeness = new Double(undirected);
        this.directed_closeness = new Double(directed);
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#getNumber(ArchetypeEdge)
     */
    public Number transform(Context<Graph<V,E>,E> context) {
    	Graph<V,E> graph = context.graph;
    	E e = context.element;
        if (graph.getEdgeType(e) == EdgeType.DIRECTED)
            return directed_closeness;
        else 
            return undirected_closeness;
    }
}
