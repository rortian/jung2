/*
 * Created on Jul 8, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring;

public class VEPair<V, E>
{
    private V v;
    private E e;
    
    public VEPair(V v, E e)
    {
        if (v == null || e == null)
            throw new IllegalArgumentException("elements must be non-null");
        
        this.v = v;
        this.e = e;
    }
    
    public V getV()
    {
        return v;
    }
    
    public E getE()
    {
        return e;
    }
}
