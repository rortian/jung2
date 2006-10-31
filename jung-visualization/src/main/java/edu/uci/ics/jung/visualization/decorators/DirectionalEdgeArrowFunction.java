/*
 * Created on Jul 18, 2004
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

import java.awt.Shape;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.ArrowFactory;

/**
 * Returns wedge arrows for undirected edges and notched arrows
 * for directed edges, of the specified dimensions.
 * 
 * @author Joshua O'Madadhain
 */
public class DirectionalEdgeArrowFunction<V,E> implements Transformer<EdgeContext<V,E>,Shape> {
    protected Shape undirected_arrow;
    protected Shape directed_arrow;
    
    public DirectionalEdgeArrowFunction(int length, int width, int notch_depth)
    {
        directed_arrow = ArrowFactory.getNotchedArrow(width, length, notch_depth);
        undirected_arrow = ArrowFactory.getWedgeArrow(width, length);
    }
    
    /**
     * 
     */
    public Shape transform(EdgeContext<V,E> context)
    {
        if (context.graph.isDirected(context.edge))
            return directed_arrow;
        else 
            return undirected_arrow;
    }

}
