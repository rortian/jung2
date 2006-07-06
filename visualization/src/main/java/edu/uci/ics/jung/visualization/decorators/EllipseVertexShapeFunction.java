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

import java.awt.Shape;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class EllipseVertexShapeFunction<V> extends AbstractVertexShapeFunction<V>
{
    public EllipseVertexShapeFunction() 
    {
    }
    public EllipseVertexShapeFunction(VertexSizeFunction<V> vsf, VertexAspectRatioFunction<V> varf)
    {
        super(vsf, varf);
    }
    
    public Shape getShape(V v)
    {
        return factory.getEllipse(v);
    }
}
