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

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.SelfLoopEdgePredicate;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * Creates <code>GradientPaint</code> instances which can be used
 * to paint an <code>Edge</code>.  For <code>DirectedEdge</code>s, 
 * the color will blend from <code>c1</code> (source) to 
 * <code>c2</code> (destination); for <code>UndirectedEdge</code>s,
 * the color will be <code>c1</code> at each end and <code>c2</code>
 * in the middle.
 * 
 * @author Tom Nelson
 */
public class GradientEdgePaintRenderer<V, E> extends BasicEdgeRenderer<V,E>
	implements  Renderer.Edge<V,E>, Transformer<E,Paint> {
    protected Color c1;
    protected Color c2;
    protected VisualizationViewer<V,E> vv;
    protected Predicate<Context<Graph<V,E>,E>> selfLoop = new SelfLoopEdgePredicate<V,E>();

    protected Graph<V,E> graph;
    protected float xB,yB,xF,yF;
    
    public GradientEdgePaintRenderer(Color c1, Color c2, 
            VisualizationViewer<V,E> vv)
    {
        this.c1 = c1;
        this.c2 = c2;
        this.vv = vv;
//        this.transformer = vv.getLayoutTransformer();
    }
    
    public Paint transform(E e) {
    	return getGradientPaint(e, graph, xB, yB, xF, yF);
    }
    
    private Paint getGradientPaint(E e, Graph<V,E> graph, float xB, float yB, float xF, float yF) {

        if ((graph.getEdgeType(e)) == EdgeType.UNDIRECTED)  {
            xF = (xF + xB) / 2;
            yF = (yF + yB) / 2;
        } 
        if(selfLoop.evaluate(Context.<Graph<V,E>,E>getInstance(graph, e))) {
        	yF += 50;
        	xF += 50;
        }

        return new GradientPaint(xB, yB, c1, xF, yF, c2, true);
    }
    
//    /**
//     * Returns <code>c1</code>.  Subclasses may override
//     * this method to enable more complex behavior (e.g., for
//     * picked edges).
//     */
//    protected Color getColor1(E e)
//    {
//        return c1;
//    }
//
//    /**
//     * Returns <code>c2</code>.  Subclasses may override
//     * this method to enable more complex behavior (e.g., for
//     * picked edges).
//     */
//    protected Color getColor2(E e)
//    {
//        return c2;
//    }

	public void paintEdge(RenderContext<V, E> rc, Layout<V, E> layout, E e) {
        Graph<V,E> graph = layout.getGraph();
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        Point2D p1 = layout.transform(v1);
        Point2D p2 = layout.transform(v2);
        p1 = rc.getBasicTransformer().layoutTransform(p1);
        p2 = rc.getBasicTransformer().layoutTransform(p2);
        float x1 = (float) p1.getX();
        float y1 = (float) p1.getY();
        float x2 = (float) p2.getX();
        float y2 = (float) p2.getY();

		xB=x1;
		yB=y1;
		xF=x2;
		yF=y2;
		this.graph = graph;
		Transformer<E,Paint> oldEdgeDrawPaintTransformer = rc.getEdgeDrawPaintFunction();
		Transformer<E,Paint> oldEdgeFillPaintTransformer = rc.getEdgeFillPaintFunction();
		rc.setEdgeDrawPaintFunction(this);
		rc.setEdgeFillPaintFunction(this);
		super.drawSimpleEdge(rc, layout, e);
		rc.setEdgeDrawPaintFunction(oldEdgeDrawPaintTransformer);
		rc.setEdgeFillPaintFunction(oldEdgeFillPaintTransformer);
	}
}
