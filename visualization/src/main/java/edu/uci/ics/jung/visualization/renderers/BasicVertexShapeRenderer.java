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

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class BasicVertexShapeRenderer<V,E> implements Renderer.Vertex<V,E> {
	
    public void paintVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y) {
        if (rc.getVertexIncludePredicate().evaluateVertex(graph, v)) {
        	paintShapeForVertex(rc, v, x, y);
        }
    }
    
    protected void paintShapeForVertex(RenderContext<V,E> rc, V v, int x, int y) {
    	
        GraphicsDecorator g2d = rc.getGraphicsContext();
        JComponent vv = rc.getScreenDevice();
        boolean vertexHit = true;
        Rectangle deviceRectangle = null;

        if(vv != null) {
            Dimension d = vv.getSize();
            if(d.width <= 0 || d.height <= 0) {
                d = vv.getPreferredSize();
            }
            deviceRectangle = new Rectangle(
                    0,0,
                    d.width,d.height);
        }
        // get the shape to be rendered
        Shape shape = rc.getVertexShapeFunction().getShape(v);
        
        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
        // transform the vertex shape with xtransform
        shape = xform.createTransformedShape(shape);
        
        vertexHit = rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

        if (vertexHit) {

        	Stroke old_stroke = g2d.getStroke();
        	Stroke new_stroke = rc.getVertexStrokeFunction().getStroke(v);
        	if (new_stroke != null) {
        		g2d.setStroke(new_stroke);
        	}
        	
        	paintShapeForVertex(rc, v, shape);
        	if (new_stroke != null) {
        		g2d.setStroke(old_stroke);
        	}
        }
    }
    
    protected void paintShapeForVertex(RenderContext<V,E> rc, V v, Shape shape) {
        GraphicsDecorator g2d = rc.getGraphicsContext();
        Paint oldPaint = g2d.getPaint();
        Paint fillPaint = rc.getVertexPaintFunction().getFillPaint(v);
        if(fillPaint != null) {
            g2d.setPaint(fillPaint);
            g2d.fill(shape);
            g2d.setPaint(oldPaint);
        }
        Paint drawPaint = rc.getVertexPaintFunction().getDrawPaint(v);
        if(drawPaint != null) {
            g2d.setPaint(drawPaint);
            g2d.draw(shape);
            g2d.setPaint(oldPaint);
        }
    }
}
