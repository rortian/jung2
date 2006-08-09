/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeAndLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;

/**
 * The default implementation of the Renderer used by the
 * VisualizationViewer. Default Vertex and Edge Renderers
 * are supplied, or the user may set custom values. The
 * Vertex and Edge renderers are used in the renderVertex
 * and renderEdge methods, which are called in the render
 * loop of the VisualizationViewer.
 * 
 * @author Tom Nelson
 */
public class BasicRenderer<V, E> implements Renderer<V, E> {
	
    Renderer.Vertex<V,E> vertexRenderer = new BasicVertexRenderer<V,E>();
    Renderer.Edge<V, E> edgeRenderer = new BasicEdgeAndLabelRenderer<V,E>();
    
    public void renderVertex(RenderContext<V,E> rc, V v, int x, int y) {
        vertexRenderer.paintVertex(rc, v, x, y);
    }
    
    public void renderEdge(RenderContext<V,E> rc, Graph<V, E> graph, E e, int x1, int y1, int x2, int y2) {
    	edgeRenderer.paintEdge(rc, graph, e, x1, y1, x2, y2);
    }
    
    public void setVertexRenderer(Renderer.Vertex<V,E> r) {
    	this.vertexRenderer = r;
    }

    public void setEdgeRenderer(Renderer.Edge<V,E> r) {
    	this.edgeRenderer = r;
    }

}