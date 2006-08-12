/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.visualization;

import edu.uci.ics.graph.Graph;

/**
 */
public interface Renderer<V, E> {

	void renderVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y);
	void renderEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, int x1, int y1, int x2, int y2);
    void setVertexRenderer(Renderer.Vertex<V,E> r);
    void setEdgeRenderer(Renderer.Edge<V,E> r);

	interface Vertex<V,E> {
		void paintVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y);
	}
    
	interface Edge<V,E> {
		void paintEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, int x1, int y1, int x2, int y2);
	}
}
