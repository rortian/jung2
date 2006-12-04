/*
 * Created on Apr 8, 2005
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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.transform.Transformer;

/**
 * Creates <code>GradientPaint</code> instances which can be used
 * to paint an <code>Edge</code>.  For <code>DirectedEdge</code>s, 
 * the color will blend from <code>c1</code> (source) to 
 * <code>c2</code> (destination); for <code>UndirectedEdge</code>s,
 * the color will be <code>c1</code> at each end and <code>c2</code>
 * in the middle.
 * 
 * @author Joshua O'Madadhain
 */
public class GradientEdgePaintTransformer<V, E> 
	implements org.apache.commons.collections15.Transformer<E,Paint>
{
    protected Color c1;
    protected Color c2;
    protected VisualizationViewer<V,E> vv;
    protected Transformer transformer;
    
    public GradientEdgePaintTransformer(Color c1, Color c2, 
            VisualizationViewer<V,E> vv)
    {
        this.c1 = c1;
        this.c2 = c2;
        this.vv = vv;
        this.transformer = vv.getLayoutTransformer();
    }
    
    public Paint transform(E e)
    {
        Layout<V, E> layout = vv.getGraphLayout();
        Pair<V> p = layout.getGraph().getEndpoints(e);
        V b = p.getFirst();
        V f = p.getSecond();
        Point2D pb = transformer.transform(layout.transform(b));
        Point2D pf = transformer.transform(layout.transform(f));
        float xB = (float) pb.getX();
        float yB = (float) pb.getY();
        float xF = (float) pf.getX();
        float yF = (float) pf.getY();
        if ((layout.getGraph().isDirected(e)) == false) 
        {
            xF = (xF + xB) / 2;
            yF = (yF + yB) / 2;
        }

        return new GradientPaint(xB, yB, getColor1(e), xF, yF, getColor2(e), true);
    }
    
    /**
     * Returns <code>c1</code>.  Subclasses may override
     * this method to enable more complex behavior (e.g., for
     * picked edges).
     */
    protected Color getColor1(E e)
    {
        return c1;
    }

    /**
     * Returns <code>c2</code>.  Subclasses may override
     * this method to enable more complex behavior (e.g., for
     * picked edges).
     */
    protected Color getColor2(E e)
    {
        return c2;
    }
}
