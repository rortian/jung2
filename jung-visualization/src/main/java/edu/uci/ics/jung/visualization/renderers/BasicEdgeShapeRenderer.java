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
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
// not used
public class BasicEdgeShapeRenderer<V,E> implements Renderer.Edge<V,E> {
	
	
    public void paintEdge(RenderContext<V,E> rc, Graph<V, E> graph, E e, int x1, int y1, int x2, int y2) {
        if (!rc.getEdgeIncludePredicate().evaluate(Context.<Graph<V,E>,E>getInstance(graph, e)))
            return;
        
        // don't draw edge if either incident vertex is not drawn
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        if (!rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v1)) || 
            !rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v2)))
            return;
        
        GraphicsDecorator g2d = rc.getGraphicsContext();

        Stroke new_stroke = rc.getEdgeStrokeFunction().transform(e);
        Stroke old_stroke = g2d.getStroke();
        if (new_stroke != null)
            g2d.setStroke(new_stroke);
        
        drawSimpleEdge(rc, graph, e, x1, y1, x2, y2);

        // restore paint and stroke
        if (new_stroke != null)
            g2d.setStroke(old_stroke);

    }

    /**
     * Draws the edge <code>e</code>, whose endpoints are at <code>(x1,y1)</code>
     * and <code>(x2,y2)</code>, on the graphics context <code>g</code>.
     * The <code>Shape</code> provided by the <code>EdgeShapeFunction</code> instance
     * is scaled in the x-direction so that its width is equal to the distance between
     * <code>(x1,y1)</code> and <code>(x2,y2)</code>.
     */
    protected void drawSimpleEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, 
    		int x1, int y1, int x2, int y2)
    {
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        boolean isLoop = v1.equals(v2);
        Shape s2 = rc.getVertexShapeFunction().transform(v2);
        Shape edgeShape = rc.getEdgeShapeFunction().transform(Context.<Graph<V,E>,E>getInstance(graph, e));
        
        boolean edgeHit = true;
        Rectangle deviceRectangle = null;
        JComponent vv = rc.getScreenDevice();
        if(vv != null) {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(0,0,d.width,d.height);
        }

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
        
        if(isLoop) {
            // this is a self-loop. scale it is larger than the vertex
            // it decorates and translate it so that its nadir is
            // at the center of the vertex.
            Rectangle2D s2Bounds = s2.getBounds2D();
            xform.scale(s2Bounds.getWidth(),s2Bounds.getHeight());
            xform.translate(0, -edgeShape.getBounds2D().getWidth()/2);
        } else {
            // this is a normal edge. Rotate it to the angle between
            // vertex endpoints, then scale it to the distance between
            // the vertices
            float dx = x2-x1;
            float dy = y2-y1;
            float thetaRadians = (float) Math.atan2(dy, dx);
            xform.rotate(thetaRadians);
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            xform.scale(dist, 1.0);
        }
        
        edgeShape = xform.createTransformedShape(edgeShape);
        
        edgeHit = rc.getViewTransformer().transform(edgeShape).intersects(deviceRectangle);
        GraphicsDecorator g = rc.getGraphicsContext();
        if(edgeHit == true) {
            
            Paint oldPaint = g.getPaint();
            
            // get Paints for filling and drawing
            // (filling is done first so that drawing and label use same Paint)
            Paint fill_paint = rc.getEdgeFillPaintFunction().transform(e); 
            if (fill_paint != null)
            {
                g.setPaint(fill_paint);
                g.fill(edgeShape);
            }
            Paint draw_paint = rc.getEdgeDrawPaintFunction().transform(e);
            if (draw_paint != null)
            {
                g.setPaint(draw_paint);
                g.draw(edgeShape);
            }
            
            float scalex = (float)g.getTransform().getScaleX();
            float scaley = (float)g.getTransform().getScaleY();
            // see if arrows are too small to bother drawing
            if(scalex < .3 || scaley < .3) return;
            
            if (graph.getEdgeType(e) == EdgeType.DIRECTED) {
                
                Shape destVertexShape = 
                    rc.getVertexShapeFunction().transform(graph.getEndpoints(e).getSecond());
                AffineTransform xf = AffineTransform.getTranslateInstance(x2, y2);
                destVertexShape = xf.createTransformedShape(destVertexShape);
            }
            // restore old paint
            g.setPaint(oldPaint);
        }
    }

}
