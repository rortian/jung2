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


import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns the values specified by a <code>NumberEdgeValue</code>
 * instance as <code>String</code>s.
 * 
 * @author Joshua O'Madadhain
 */
public class NumberEdgeValueStringer<E> implements EdgeStringer<E>
{
//    protected NumberEdgeValue<E> nev;
    protected Map<E,Number> nev = new HashMap<E,Number>();
    protected final static NumberFormat nf = NumberFormat.getInstance();
    
    public NumberEdgeValueStringer(Map<E,Number> nev)
    {
        this.nev = nev;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.EdgeStringer#getLabel(ArchetypeEdge)
     */
    public String getLabel(E e)
    {
        return nf.format(nev.get(e));
    }

}
