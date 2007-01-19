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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * A renderer that will fill vertex shapes with a GradientPaint
 * @author Tom Nelson
 *
 * @param <V>
 * @param <E>
 */
public class GradientVertexRenderer<V,E> implements Renderer.Vertex<V,E> {
	
	Color colorOne;
	Color colorTwo;
	Color pickedColorOne;
	Color pickedColorTwo;
	PickedState<V> pickedState;
	boolean cyclic;
	

    public GradientVertexRenderer(Color colorOne, Color colorTwo, boolean cyclic) {
		this.colorOne = colorOne;
		this.colorTwo = colorTwo;
		this.cyclic = cyclic;
	}


	public GradientVertexRenderer(Color colorOne, Color colorTwo, Color pickedColorOne, Color pickedColorTwo, PickedState<V> pickedState, boolean cyclic) {
		this.colorOne = colorOne;
		this.colorTwo = colorTwo;
		this.pickedColorOne = pickedColorOne;
		this.pickedColorTwo = pickedColorTwo;
		this.pickedState = pickedState;
		this.cyclic = cyclic;
	}


	public void paintVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y) {
        if (rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v))) {
            boolean vertexHit = true;
            // get the shape to be rendered
            Shape shape = rc.getVertexShapeFunction().transform(v);
            
            // create a transform that translates to the location of
            // the vertex to be rendered
            AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
            // transform the vertex shape with xtransform
            shape = xform.createTransformedShape(shape);
            
            vertexHit = vertexHit(rc, shape);
                //rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

            if (vertexHit) {
            	paintShapeForVertex(rc, v, shape);
            }
        }
    }
    
    protected boolean vertexHit(RenderContext<V,E> rc, Shape s) {
        JComponent vv = rc.getScreenDevice();
        Rectangle deviceRectangle = null;
        if(vv != null) {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(
                    0,0,
                    d.width,d.height);
        }
        return rc.getViewTransformer().transform(s).intersects(deviceRectangle);
    }

    protected void paintShapeForVertex(RenderContext<V,E> rc, V v, Shape shape) {
        GraphicsDecorator g = rc.getGraphicsContext();
        Paint oldPaint = g.getPaint();
        Rectangle r = shape.getBounds();
        float y2 = (float)r.getMaxY();
        if(cyclic) {
        	y2 = (float)(r.getMinY()+r.getHeight()/2);
        }
        
        Paint fillPaint = null;
        if(pickedState != null && pickedState.isPicked(v)) {
        	fillPaint = new GradientPaint((float)r.getMinX(), (float)r.getMinY(), pickedColorOne,
            		(float)r.getMinX(), y2, pickedColorTwo, cyclic);
        } else {
        	fillPaint = new GradientPaint((float)r.getMinX(), (float)r.getMinY(), colorOne,
        		(float)r.getMinX(), y2, colorTwo, cyclic);
        }
        if(fillPaint != null) {
            g.setPaint(fillPaint);
            g.fill(shape);
            g.setPaint(oldPaint);
        }
        Paint drawPaint = rc.getVertexDrawPaintFunction().transform(v);
        if(drawPaint != null) {
            g.setPaint(drawPaint);
        }
        Stroke oldStroke = g.getStroke();
        Stroke stroke = rc.getVertexStrokeFunction().transform(v);
        if(stroke != null) {
            g.setStroke(stroke);
        }
        g.draw(shape);
        g.setPaint(oldPaint);
        g.setStroke(oldStroke);
    }
}
