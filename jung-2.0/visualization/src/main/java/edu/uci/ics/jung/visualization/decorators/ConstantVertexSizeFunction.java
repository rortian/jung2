/*
 * Created on Jul 16, 2004
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



public class ConstantVertexSizeFunction<V> implements VertexSizeFunction<V>
{
    private int size;
    
    public ConstantVertexSizeFunction(int size)
    {
        this.size = size;
    }
    
    public int getSize(V v)
    {
        return size;
    }
}