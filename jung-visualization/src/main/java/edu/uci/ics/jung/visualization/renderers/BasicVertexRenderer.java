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
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.JComponent;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class BasicVertexRenderer<V,E> implements Renderer.Vertex<V,E> {

    public void paintVertex(RenderContext<V,E> rc, Layout<V,E> layout, V v) {
    	Graph<V,E> graph = layout.getGraph();
        if (rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v))) {
        	paintIconForVertex(rc, v, layout);
        }
    }
    
    /**
     * Paint <code>v</code>'s icon on <code>g</code> at <code>(x,y)</code>.
     */
    protected void paintIconForVertex(RenderContext<V,E> rc, V v, Layout<V,E> layout) {
        GraphicsDecorator g = rc.getGraphicsContext();
        boolean vertexHit = true;
        // get the shape to be rendered
        Shape shape = rc.getVertexShapeTransformer().transform(v);
        
        Point2D p = layout.transform(v);
        p = rc.getBasicTransformer().transform(Layer.LAYOUT, p);
        float x = (float)p.getX();
        float y = (float)p.getY();
        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
        // transform the vertex shape with xtransform
        shape = xform.createTransformedShape(shape);
        
        vertexHit = vertexHit(rc, shape);
            //rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

        if (vertexHit) {
        	if(rc.getVertexIconTransformer() != null) {
        		Icon icon = rc.getVertexIconTransformer().transform(v);
        		if(icon != null) {
        		
        			int xLoc = (int) (x - icon.getIconWidth()/2);
        			int yLoc = (int) (y - icon.getIconHeight()/2);
        			icon.paintIcon(rc.getScreenDevice(), g.getDelegate(), xLoc, yLoc);
        		} else {
        			paintShapeForVertex(rc, v, shape);
        		}
        	} else {
        		paintShapeForVertex(rc, v, shape);
        	}
//    		labelVertex(rc, v, rc.getVertexStringer().transform(v), x, y);
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
//        System.err.println("shape bounds "+rc.getBasicTransformer().getViewTransformer().transform(s).getBounds());
//        System.err.println("deviceRect "+deviceRectangle);
        MutableTransformer vt = rc.getBasicTransformer().getTransformer(Layer.VIEW);
        if(vt instanceof LensTransformer) {
        	vt = ((LensTransformer)vt).getDelegate();
        }
        return vt.transform(s).intersects(deviceRectangle);
    }

    protected void paintShapeForVertex(RenderContext<V,E> rc, V v, Shape shape) {
        GraphicsDecorator g = rc.getGraphicsContext();
        Paint oldPaint = g.getPaint();
        Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
        if(fillPaint != null) {
            g.setPaint(fillPaint);
            g.fill(shape);
            g.setPaint(oldPaint);
        }
        Paint drawPaint = rc.getVertexDrawPaintTransformer().transform(v);
        if(drawPaint != null) {
            g.setPaint(drawPaint);
        }
        Stroke oldStroke = g.getStroke();
        Stroke stroke = rc.getVertexStrokeTransformer().transform(v);
        if(stroke != null) {
            g.setStroke(stroke);
        }
        g.draw(shape);
        g.setPaint(oldPaint);
        g.setStroke(oldStroke);
    }

//	public Component prepareRenderer(RenderContext<V,E> rc, VertexLabelRenderer graphLabelRenderer, Object value, 
//			boolean isSelected, V vertex) {
//		return rc.getVertexLabelRenderer().<V>getVertexLabelRendererComponent(rc.getScreenDevice(), value, 
//				rc.getVertexFontFunction().transform(vertex), isSelected, vertex);
//	}
//
//	/**
//	 * Labels the specified vertex with the specified label.  
//	 * Uses the font specified by this instance's 
//	 * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
//	 * font for the graphics context is used.)  If vertex label centering
//	 * is active, the label is centered on the position of the vertex; otherwise
//     * the label is offset slightly.
//     */
//    protected void labelVertex(RenderContext<V,E> rc, V v, String label, int x, int y) {
//        Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), label,
//        		rc.getPickedVertexState().isPicked(v), v);
//
//        GraphicsDecorator g = rc.getGraphicsContext();
//        Dimension d = component.getPreferredSize();
//        
//        int h_offset;
//        int v_offset;
//        if (rc.isCenterVertexLabel()) {
//            h_offset = -d.width / 2;
//            v_offset = -d.height / 2;
//
//        } else {
//            Rectangle2D bounds = rc.getVertexShapeFunction().transform(v).getBounds2D();
//            h_offset = (int)(bounds.getWidth() / 2) + 5;
//            v_offset = (int)(bounds.getHeight() / 2) + 5 -d.height;
//        }
//        
//        rc.getRendererPane().paintComponent(g.getDelegate(), component, rc.getScreenDevice(), x+h_offset, y+v_offset,
//                d.width, d.height, true);
//        
//    }

}
