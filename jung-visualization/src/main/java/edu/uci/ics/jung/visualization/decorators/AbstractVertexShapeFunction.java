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

import edu.uci.ics.jung.visualization.VertexShapeFactory;



/**
 * 
 * @author Joshua O'Madadhain
 */
public abstract class AbstractVertexShapeFunction<V> implements SettableVertexShapeFunction<V>
{
    protected VertexSizeFunction<V> vsf;
    protected VertexAspectRatioFunction<V> varf;
    protected VertexShapeFactory<V> factory;
    public final static int DEFAULT_SIZE = 8;
    public final static float DEFAULT_ASPECT_RATIO = 1.0f;
    
    public AbstractVertexShapeFunction(VertexSizeFunction<V> vsf, VertexAspectRatioFunction<V> varf)
    {
        this.vsf = vsf;
        this.varf = varf;
        factory = new VertexShapeFactory<V>(vsf, varf);
    }

    public AbstractVertexShapeFunction()
    {
        this(new ConstantVertexSizeFunction<V>(DEFAULT_SIZE), 
                new ConstantVertexAspectRatioFunction<V>(DEFAULT_ASPECT_RATIO));
    }
    
    public void setSizeFunction(VertexSizeFunction<V> vsf)
    {
        this.vsf = vsf;
        factory = new VertexShapeFactory<V>(vsf, varf);
    }
    
    public void setAspectRatioFunction(VertexAspectRatioFunction<V> varf)
    {
        this.varf = varf;
        factory = new VertexShapeFactory<V>(vsf, varf);
    }
}
