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

/**
 * Returns the values specified by a <code>NumberVertexValue</code>
 * instance as <code>String</code>s.
 * 
 * @author Joshua O'Madadhain
 */
public class NumberVertexValueStringer<V> implements VertexStringer<V>
{
    protected NumberVertexValue<V> nvv;
    protected final static NumberFormat nf = NumberFormat.getInstance();
    
    public NumberVertexValueStringer(NumberVertexValue<V> nev)
    {
        this.nvv = nev;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.EdgeStringer#getLabel(ArchetypeEdge)
     */
    public String getLabel(V v)
    {
        return nf.format(nvv.getNumber(v));
    }
}
