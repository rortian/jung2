/*
 * Created on May 9, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.decorators;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of <code>NumberVertexValue</code> backed by a 
 * <code>Map</code>.
 * 
 * @author Joshua O'Madadhain
 */
public class MapNumberVertexValue<V> implements NumberVertexValue<V>
{
    protected Map<V, Number> map;
    
    public MapNumberVertexValue()
    {
        this.map = new HashMap<V, Number>();
    }
    
    public Number getNumber(V v)
    {
        return map.get(v);
    }

    public void setNumber(V v, Number n)
    {
        map.put(v, n);
    }
}
