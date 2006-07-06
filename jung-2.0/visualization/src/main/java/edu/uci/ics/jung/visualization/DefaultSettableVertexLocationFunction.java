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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A <code>Map</code>-based implementation of 
 * <code>SettableVertexLocationFunction</code>.
 * 
 * @author Joshua O'Madadhain
 */
public class DefaultSettableVertexLocationFunction<V> implements
        SettableVertexLocationFunction<V>
{
    protected Map<V, Point2D> v_locations;
    protected boolean normalized;
    
    public DefaultSettableVertexLocationFunction()
    {
        v_locations = new HashMap<V, Point2D>();
    }
    
    public DefaultSettableVertexLocationFunction(VertexLocationFunction<V> vlf) {
        v_locations = new HashMap<V, Point2D>();
        for(V v : vlf.getVertices()) {
//        for(Iterator iterator=vlf.getVertexIterator(); iterator.hasNext(); ) {
//            V v = (ArchetypeVertex)iterator.next();
            v_locations.put(v, vlf.getLocation(v));
        }
    }
    
    public void setLocation(V v, Point2D location)
    {
        v_locations.put(v, location);
    }
    
    public Point2D getLocation(V v)
    {
        return (Point2D)v_locations.get(v);
    }

    public void reset()
    {
        v_locations.clear();
    }
    
    public Iterator getVertexIterator()
    {
        return v_locations.keySet().iterator();
    }
    
    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(v_locations.keySet());
    }
}
