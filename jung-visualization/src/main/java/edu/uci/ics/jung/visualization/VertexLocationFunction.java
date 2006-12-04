/*
 * Created on Jul 19, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization;

import java.awt.geom.Point2D;

/**
 * An interface for classes that return a location for
 * a vertex.
 * 
 * @author Joshua O'Madadhain
 */
public interface VertexLocationFunction<V> {
    
    public Point2D getLocation(V v);
    
//    Collection<V> getVertices();

}
