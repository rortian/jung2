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
import java.util.Map;

import org.apache.commons.collections15.Transformer;

/**
 * Returns the values specified by a <code>NumberVertexValue</code>
 * instance as <code>String</code>s.
 * 
 * @author Joshua O'Madadhain
 */
public class NumberVertexValueStringer<V> implements Transformer<V,String>
{
    protected Map<V,Number> nvv;
    protected final static NumberFormat nf = NumberFormat.getInstance();
    
    public NumberVertexValueStringer(Map<V,Number> nev)
    {
        this.nvv = nev;
    }
    
    /**
     * 
     */
    public String transform(V v)
    {
        return nf.format(nvv.get(v));
    }
}
