/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Sep 10, 2004
 */
package edu.uci.ics.jung.visualization.decorators;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of <code>NumberEdgeValue</code> that stores the values
 * in the UserData repository.
 *  
 * @author Joshua O'Madadhain
 */
public class UserDatumNumberEdgeValue<E> implements NumberEdgeValue<E> {
    
    private Map<E, Number> map = new HashMap<E, Number>();
    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#getNumber(edu.uci.ics.jung.graph.ArchetypeEdge)
     */
    public Number getNumber(E e)
    {
        return map.get(e);
    }

    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#setNumber(edu.uci.ics.jung.graph.ArchetypeEdge, java.lang.Number)
     */
    public void setNumber(E e, Number n)
    {
        map.put(e, n);
    }
    
    /**
     * Removes this decoration from <code>g</code>.
     */
    public void clear() {
        map.clear();
    }

}
