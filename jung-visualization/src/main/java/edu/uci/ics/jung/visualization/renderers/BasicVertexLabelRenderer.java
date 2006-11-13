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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.Transformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.TransformingGraphics;


public class BasicVertexLabelRenderer<V,E> implements Renderer.VertexLabel<V,E> {

	public void paintVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y) {
		labelVertex(rc, v, rc.getVertexStringer().transform(v), x, y);
	}

	public Component prepareRenderer(RenderContext<V,E> rc, VertexLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, V vertex) {
		return rc.getVertexLabelRenderer().<V>getVertexLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getVertexFontFunction().transform(vertex), isSelected, vertex);
	}

	/**
	 * Labels the specified vertex with the specified label.  
	 * Uses the font specified by this instance's 
	 * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
	 * font for the graphics context is used.)  If vertex label centering
	 * is active, the label is centered on the position of the vertex; otherwise
     * the label is offset slightly.
     */
    public void labelVertex(RenderContext<V,E> rc, V v, String label, int x, int y) {
        Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), label,
        		rc.getPickedVertexState().isPicked(v), v);
        GraphicsDecorator g = rc.getGraphicsContext();
        Dimension d = component.getPreferredSize();
        AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
        
        if (rc.isCenterVertexLabel()) {
            x += -d.width / 2;
            y += -d.height / 2;

        } else {
        	Shape shape = rc.getVertexShapeFunction().transform(v);
        	shape = xform.createTransformedShape(shape);
        	Rectangle2D bounds = shape.getBounds2D();
    		x += (int)(bounds.getWidth() / 2) + 5;
    		y += (int)(bounds.getHeight() / 2) + 5 -d.height;

        	if(rc.getGraphicsContext() instanceof TransformingGraphics) {
        		Transformer transformer = ((TransformingGraphics)rc.getGraphicsContext()).getTransformer();
        		if(transformer instanceof ShapeTransformer) {
        			ShapeTransformer shapeTransformer = (ShapeTransformer)transformer;
        			shape = shapeTransformer.transform(shape);
        			bounds = shape.getBounds2D();
                    x = (int)(bounds.getMaxX()) + 5;
                    y = (int)(bounds.getMaxY()) + 5 -d.height;
        		}
        	}
        }
        
        rc.getRendererPane().paintComponent(g.getDelegate(), component, rc.getScreenDevice(), x, y,
                d.width, d.height, true);
        
    }
}
