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
package edu.uci.ics.jung.visualization;

import java.awt.geom.Point2D;

/**
 * An extension of <code>VertexLocationFunction</code> that
 * provides a means to alter the location for a <code>ArchetypeVertex</code>.
 * 
 * @author Joshua O'Madadhain
 */
public interface SettableVertexLocationFunction<V> extends VertexLocationFunction<V>
{
    public void setLocation(V v, Point2D location);
}
