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

import edu.uci.ics.graph.Edge;

/**
 * An implementation of <code>EdgePaintFunction</code> that is appropriate for 
 * edge shapes that are not filled, such as lines or curves.
 * Provides an implementation of <code>getFillPaint()</code>
 * that returns null.
 * 
 * @author Joshua O'Madadhain
 */
public abstract class AbstractEdgePaintFunction<E extends Edge> implements EdgePaintFunction<E>
{
    /**
     * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getFillPaint(edu.uci.ics.jung.graph.Edge)
     */
    public Paint getFillPaint(E e)
    {
        return null;
    }

}
