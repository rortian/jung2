/*
 * Created on Jul 21, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;

public class StaticLayout<V, E> extends AbstractLayout<V,E> {
	
    public StaticLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer, Dimension size) {
        super(graph, initializer, size);
    }
    
    public void initialize() {}

	public void reset() {}

}
