/*
 * Created on Jul 18, 2004
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

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class ConstantVertexStrokeFunction<V> implements VertexStrokeFunction<V>
{
    protected Stroke stroke;
    
    public ConstantVertexStrokeFunction(float thickness)
    {
        this.stroke = new BasicStroke(thickness);
    }

    public ConstantVertexStrokeFunction(Stroke s)
    {
        this.stroke = s;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.VertexStrokeFunction#getStroke(edu.uci.ics.jung.graph.Vertex)
     */
    public Stroke getStroke(V v)
    {
        return stroke;
    }

}
