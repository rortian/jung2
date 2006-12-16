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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
// not used
public class BasicVertexIconRenderer<V,E> extends BasicVertexShapeRenderer<V,E> implements Renderer.Vertex<V,E> {

	Transformer<V,Icon> vertexIconFunction;
	
    public void paintVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y) {
        if (rc.getVertexIncludePredicate().evaluateVertex(graph, v)) {
        	paintIconForVertex(rc, v, x, y);
        }
    }
    
    /**
     * Paint <code>v</code>'s icon on <code>g</code> at <code>(x,y)</code>.
     */
    protected void paintIconForVertex(RenderContext<V,E> rc, V v, int x, int y) {
        GraphicsDecorator g = rc.getGraphicsContext();
        boolean vertexHit = true;
        Rectangle deviceRectangle = null;

        JComponent vv = rc.getScreenDevice();
        if(vv != null) {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(
                    0,0,
                    d.width,d.height);
        }
        // get the shape to be rendered
        Shape shape = rc.getVertexShapeFunction().transform(v);
        
        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
        // transform the vertex shape with xtransform
        shape = xform.createTransformedShape(shape);
        
        vertexHit = rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

        if (vertexHit) {
        	if(vertexIconFunction != null) {
        		Icon icon = vertexIconFunction.transform(v);
        		if(icon != null) {
        		
        			int xLoc = x - icon.getIconWidth()/2;
        			int yLoc = y - icon.getIconHeight()/2;
        			icon.paintIcon(vv, g.getDelegate(), xLoc, yLoc);
        		} else {
        			paintShapeForVertex(rc, v, shape);
        		}
        	} else {
        		paintShapeForVertex(rc, v, shape);
        	}
        }
    }
}
