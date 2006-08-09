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
import java.awt.geom.Rectangle2D;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;


public class BasicVertexLabelRenderer<V,E> implements Renderer.Vertex<V,E> {

	public void paintVertex(RenderContext<V,E> rc, V v, int x, int y) {
		labelVertex(rc, v, rc.getVertexStringer().getLabel(v), x, y);
	}

	public Component prepareRenderer(RenderContext<V,E> rc, VertexLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, V vertex) {
		return rc.getVertexLabelRenderer().<V>getVertexLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getVertexFontFunction().getFont(vertex), isSelected, vertex);
	}

	/**
	 * Labels the specified vertex with the specified label.  
	 * Uses the font specified by this instance's 
	 * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
	 * font for the graphics context is used.)  If vertex label centering
	 * is active, the label is centered on the position of the vertex; otherwise
     * the label is offset slightly.
     */
    protected void labelVertex(RenderContext<V,E> rc, V v, String label, int x, int y) {
        Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), label,
        		rc.getPickedVertexState().isPicked(v), v);
        GraphicsDecorator g = rc.getGraphicsContext();
        Dimension d = component.getPreferredSize();
        
        int h_offset;
        int v_offset;
        if (rc.isCenterVertexLabel()) {
            h_offset = -d.width / 2;
            v_offset = -d.height / 2;

        } else {
            Rectangle2D bounds = rc.getVertexShapeFunction().getShape(v).getBounds2D();
            h_offset = (int)(bounds.getWidth() / 2) + 5;
            v_offset = (int)(bounds.getHeight() / 2) + 5 -d.height;
        }
        
        rc.getRendererPane().paintComponent(g.getDelegate(), component, rc.getScreenDevice(), x+h_offset, y+v_offset,
                d.width, d.height, true);
        
    }
}
