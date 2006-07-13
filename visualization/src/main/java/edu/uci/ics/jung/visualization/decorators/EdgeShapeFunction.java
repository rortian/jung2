/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on March 10, 2005
 */
package edu.uci.ics.jung.visualization.decorators;

import java.awt.Shape;

import edu.uci.ics.graph.Graph;

/**
 * An interface for decorators that return a 
 * <code>Shape</code> for a specified edge.
 *  
 * @author Tom Nelson
 */
public interface EdgeShapeFunction<V,E> {

    /**
     * Returns the <code>Shape</code> associated with <code>e</code>.
     */
    Shape getShape(Graph<V,E> graph, E e);
 }
