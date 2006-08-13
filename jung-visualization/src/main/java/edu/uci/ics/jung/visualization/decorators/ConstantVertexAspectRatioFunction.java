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



public class ConstantVertexAspectRatioFunction<V>
    implements VertexAspectRatioFunction<V>
{
    private float ratio;
    
    public ConstantVertexAspectRatioFunction(float ratio)
    {
        this.ratio = ratio;
    }
    
    public float getAspectRatio(V v) 
    {
        return ratio;
    }
}