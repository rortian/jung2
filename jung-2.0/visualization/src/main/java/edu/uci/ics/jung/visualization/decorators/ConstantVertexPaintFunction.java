/*
 * Created on Apr 5, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.decorators;

import java.awt.Paint;


/**
 * Provides the same <code>Paint</code>(s) for any specified vertex.
 * 
 * @author Tom Nelson - RABA Technologies
 * @author Joshua O'Madadhain
 */
public class ConstantVertexPaintFunction<V> implements VertexPaintFunction<V> {

    protected Paint draw_paint;
    protected Paint fill_paint;

    /**
     * Sets both draw and fill <code>Paint</code> instances to <code>paint</code>.
     * @param paint
     */
    public ConstantVertexPaintFunction(Paint paint) 
    {
        this.draw_paint = paint;
        this.fill_paint = paint;
    }

    /**
     * Sets the drawing <code>Paint</code> to <code>draw_paint</code> and
     * the filling <code>Paint</code> to <code>fill_paint</code>.
     * @param paint
     */
    public ConstantVertexPaintFunction(Paint draw_paint, Paint fill_paint) 
    {
        this.draw_paint = draw_paint;
        this.fill_paint = fill_paint;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.VertexPaintFunction#getDrawPaint(edu.uci.ics.jung.graph.Vertex)
     */
    public Paint getDrawPaint(V e) {
        return draw_paint;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.VertexPaintFunction#getFillPaint(edu.uci.ics.jung.graph.Vertex)
     */
    public Paint getFillPaint(V e) {
        return fill_paint;
    }
}

