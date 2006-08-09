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

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class BasicEdgeAndLabelRenderer<V,E> 
	extends BasicEdgeRenderer<V,E> implements Renderer.Edge<V,E> {

    public void paintEdge(RenderContext<V,E> rc, Graph<V, E> graph, E e, int x1, int y1, int x2, int y2) {
    	super.paintEdge(rc, graph, e, x1, y1, x2, y2);
    	labelEdge(rc, graph, e, rc.getEdgeStringer().getLabel(e), x1, x2, y1, y2);
    }
	public Component prepareRenderer(RenderContext<V,E> rc, EdgeLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, E edge) {
		return rc.getEdgeLabelRenderer().<E>getEdgeLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getEdgeFontFunction().getFont(edge), isSelected, edge);
	}
    
    /**
     * Labels the specified non-self-loop edge with the specified label.
     * Uses the font specified by this instance's 
     * <code>EdgeFontFunction</code>.  (If the font is unspecified, the existing
     * font for the graphics context is used.)  Positions the 
     * label between the endpoints according to the coefficient returned
     * by this instance's edge label closeness function.
     */
    protected void labelEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, String label, int x1, int x2, int y1, int y2) {
        GraphicsDecorator g = rc.getGraphicsContext();
        int distX = x2 - x1;
        int distY = y2 - y1;
        double totalLength = Math.sqrt(distX * distX + distY * distY);

        double closeness = rc.getEdgeLabelClosenessFunction().getNumber(graph, e).doubleValue();

        int posX = (int) (x1 + (closeness) * distX);
        int posY = (int) (y1 + (closeness) * distY);

        int xDisplacement = (int) (rc.getLabelOffset() * (distY / totalLength));
        int yDisplacement = (int) (rc.getLabelOffset() * (-distX / totalLength));
        
        Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, 
                rc.getPickedEdgeState().isPicked(e), e);
        
        Dimension d = component.getPreferredSize();

        Shape edgeShape = rc.getEdgeShapeFunction().getShape(graph, e);
        
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
