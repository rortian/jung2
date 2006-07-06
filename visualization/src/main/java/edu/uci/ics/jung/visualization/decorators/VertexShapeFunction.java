/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Jul 19, 2004
 */
package edu.uci.ics.jung.visualization.decorators;

import java.awt.Shape;

/**
 * An interface for decorators that return a 
 * <code>Shape</code> for a specified vertex.
 *  
 * @author Joshua O'Madadhain
 */
public interface VertexShapeFunction<V>
{
    public Shape getShape(V v);
}
