/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */
package edu.uci.ics.jung.visualization.renderers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class BasicEdgeLabelRenderer<V,E> implements Renderer.EdgeLabel<V,E> {
	
//	public void paintEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, int x1, int x2, int y1, int y2) {
//        // don't draw edge label if either incident vertex is not drawn
//        Pair<V> endpoints = graph.getEndpoints(e);
//        V v1 = endpoints.getFirst();
//        V v2 = endpoints.getSecond();
//        if (!rc.getVertexIncludePredicate().evaluateVertex(graph, v1) || 
//            !rc.getVertexIncludePredicate().evaluateVertex(graph, v2))
//            return;
//
//		labelEdge(rc, graph, e, rc.getEdgeStringer().transform(e), x1, y1, x2, y2);
//	}
	
	public Component prepareRenderer(RenderContext<V,E> rc, EdgeLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, E edge) {
		return rc.getEdgeLabelRenderer().<E>getEdgeLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getEdgeFontFunction().transform(edge), isSelected, edge);
	}
    
    /**
     * Labels the specified non-self-loop edge with the specified label.
     * Uses the font specified by this instance's 
     * <code>EdgeFontFunction</code>.  (If the font is unspecified, the existing
     * font for the graphics context is used.)  Positions the 
     * label between the endpoints according to the coefficient returned
     * by this instance's edge label closeness function.
     */
//    public void labelEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, String label, int x1, int x2, int y1, int y2) {
//        int distX = x2 - x1;
//        int distY = y2 - y1;
//        double totalLength = Math.sqrt(distX * distX + distY * distY);
//        GraphicsDecorator g2d = rc.getGraphicsContext();
//        double closeness = rc.getEdgeLabelClosenessFunction().transform(Context.<V,E>(graph, e)).doubleValue();
//
//        int posX = (int) (x1 + (closeness) * distX);
//        int posY = (int) (y1 + (closeness) * distY);
//
//        int xDisplacement = (int) (rc.getLabelOffset() * (distY / totalLength));
//        int yDisplacement = (int) (rc.getLabelOffset() * (-distX / totalLength));
//        
//        Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, rc.getPickedEdgeState().isPicked(e), e);
//        
//        Dimension d = component.getPreferredSize();
//
//        Shape edgeShape = rc.getEdgeShapeFunction().transform(Context.<V,E>(graph, e));
//        
//        double parallelOffset = 1;
//
//        parallelOffset += rc.getParallelEdgeIndexFunction().getIndex(graph, e);
//
//        if(edgeShape instanceof Ellipse2D) {
//            parallelOffset += edgeShape.getBounds().getHeight();
//            parallelOffset = -parallelOffset;
//        }
//        
//        parallelOffset *= d.height;
//        
//        AffineTransform old = g2d.getTransform();
//        AffineTransform xform = new AffineTransform(old);
//        xform.translate(posX+xDisplacement, posY+yDisplacement);
//        double dx = x2 - x1;
//        double dy = y2 - y1;
//        if(rc.getEdgeLabelRenderer().isRotateEdgeLabels()) {
//            double theta = Math.atan2(dy, dx);
//            if(dx < 0) {
//                theta += Math.PI;
//            }
//            xform.rotate(theta);
//        }
//        if(dx < 0) {
//            parallelOffset = -parallelOffset;
//        }
//        
//        xform.translate(-d.width/2, -(d.height/2-parallelOffset));
//        g2d.setTransform(xform);
//        rc.getRendererPane().paintComponent(g2d.getDelegate(), component, rc.getScreenDevice(), 
//                0, 0,
//                d.width, d.height, true);
//        g2d.setTransform(old);
//    }
    public void labelEdge(RenderContext<V,E> rc, Layout<V,E> layout, E e, String label) {
    	if(label == null || label.length() == 0) return;
    	
    	Graph<V,E> graph = layout.getGraph();
        // don't draw edge if either incident vertex is not drawn
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        if (!rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v1)) || 
            !rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v2)))
            return;

        Point2D p1 = layout.transform(v1);
        Point2D p2 = layout.transform(v2);
        p1 = rc.getBasicTransformer().layoutTransform(p1);
        p2 = rc.getBasicTransformer().layoutTransform(p2);
        float x1 = (float) p1.getX();
        float y1 = (float) p1.getY();
        float x2 = (float) p2.getX();
        float y2 = (float) p2.getY();

        GraphicsDecorator g = rc.getGraphicsContext();
        float distX = x2 - x1;
        float distY = y2 - y1;
        double totalLength = Math.sqrt(distX * distX + distY * distY);

        double closeness = rc.getEdgeLabelClosenessFunction().transform(Context.<Graph<V,E>,E>getInstance(graph, e)).doubleValue();

        int posX = (int) (x1 + (closeness) * distX);
        int posY = (int) (y1 + (closeness) * distY);

        int xDisplacement = (int) (rc.getLabelOffset() * (distY / totalLength));
        int yDisplacement = (int) (rc.getLabelOffset() * (-distX / totalLength));
        
        Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, 
                rc.getPickedEdgeState().isPicked(e), e);
        
        Dimension d = component.getPreferredSize();

        Shape edgeShape = rc.getEdgeShapeFunction().transform(Context.<Graph<V,E>,E>getInstance(graph, e));
        
        double parallelOffset = 1;

        parallelOffset += rc.getParallelEdgeIndexFunction().getIndex(graph, e);

        if(edgeShape instanceof Ellipse2D) {
            parallelOffset += edgeShape.getBounds().getHeight();
            parallelOffset = -parallelOffset;
        }
        
        parallelOffset *= d.height;
        
        AffineTransform old = g.getTransform();
        AffineTransform xform = new AffineTransform(old);
        xform.translate(posX+xDisplacement, posY+yDisplacement);
        double dx = x2 - x1;
        double dy = y2 - y1;
        if(rc.getEdgeLabelRenderer().isRotateEdgeLabels()) {
            double theta = Math.atan2(dy, dx);
            if(dx < 0) {
                theta += Math.PI;
            }
            xform.rotate(theta);
        }
        if(dx < 0) {
            parallelOffset = -parallelOffset;
        }
        
        xform.translate(-d.width/2, -(d.height/2-parallelOffset));
        g.setTransform(xform);
        rc.getRendererPane().paintComponent(g.getDelegate(), component, rc.getScreenDevice(), 
                0, 0,
                d.width, d.height, true);
        g.setTransform(old);
    }

}
