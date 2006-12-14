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
 * The interface for drawing vertices, edges, and their labels.
 * Implementations of this class can set specific renderers for
 * each element, allowing custom control of each.
 */
public interface Renderer<V,E> {

	void renderVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y);
	void renderVertexLabel(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y);
	void renderEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, int x1, int y1, int x2, int y2);
	void renderEdgeLabel(RenderContext<V,E> rc, Graph<V,E> graph, E e, int x1, int y1, int x2, int y2);
    void setVertexRenderer(Renderer.Vertex<V,E> r);
    void setEdgeRenderer(Renderer.Edge<V,E> r);
    void setVertexLabelRenderer(Renderer.VertexLabel<V,E> r);
    void setEdgeLabelRenderer(Renderer.EdgeLabel<V,E> r);
    Renderer.VertexLabel getVertexLabelRenderer();
    Renderer.Vertex getVertexRenderer();
    Renderer.Edge getEdgeRenderer();
    Renderer.EdgeLabel getEdgeLabelRenderer();

	interface Vertex<V,E> {
		void paintVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, int x, int y);
		class NOOP implements Vertex {
			public void paintVertex(RenderContext rc, Graph graph, Object v, int x, int y) {}
		};
	}
    
	interface Edge<V,E> {
		void paintEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, int x1, int y1, int x2, int y2);
		class NOOP implements Edge {
			public void paintEdge(RenderContext rc, Graph graph, Object e, int x1, int y1, int x2, int y2) {}
		}
	}
	
	interface VertexLabel<V,E> {
		void labelVertex(RenderContext<V,E> rc, Graph<V,E> graph, V v, String label, int x, int y);
		Position getPosition();
		void setPosition(Position position);
		class NOOP implements VertexLabel {
			public void labelVertex(RenderContext rc, Graph graph, Object v, String label, int x, int y) {}
			public Position getPosition() { return Position.C; }
			public void setPosition(Position position) {}
		}
		enum Position { N, NE, E, SE, S, SW, W, NW, C, AUTO }
	}
	
	interface EdgeLabel<V,E> {
		void labelEdge(RenderContext<V,E> rc, Graph<V,E> graph, E e, String label, int x1, int x2, int y1, int y2);
		class NOOP implements EdgeLabel {
			public void labelEdge(RenderContext rc, Graph graph, Object e, String label, int x1, int x2, int y1, int y2) {}
		}
	}
}
