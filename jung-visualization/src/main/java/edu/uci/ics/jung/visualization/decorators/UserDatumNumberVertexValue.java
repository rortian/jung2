/*
 * Created on Nov 7, 2004
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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class UserDatumNumberVertexValue<V> implements NumberVertexValue<V> {
    Map<V, Number> map = new HashMap<V, Number>();
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberVertexValue#getNumber(edu.uci.ics.jung.graph.ArchetypeVertex)
     */
    public Number getNumber(V v) {
        return map.get(v);
    }

    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberVertexValue#setNumber(edu.uci.ics.jung.graph.ArchetypeVertex, java.lang.Number)
     */
    public void setNumber(V v, Number n) {
        map.put(v, n);
    }

    /**
     * Removes this decoration from <code>g</code>.
     */
    public void clear() {
        map.clear();
    }
}
