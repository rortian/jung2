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

import java.awt.Graphics;

import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;

/**
 * Draws individual vertices and
 * edges on a display. Given a <tt>Graphics</tt> context, it paints
 * a <tt>Vertex</tt> or an <tt>Edge</tt> appropriately.
 * <p>
 * Users must provide an appropriate Renderer, if they are rendering
 * to AWT / Swing. (Presumably, a similar mechanism might be built
 * for other Graphics types; however, this class and its implementations
 * are all Swing specific).
 * <p>
 * The <tt>{@link edu.uci.ics.jung.visualization.PluggableRenderer PluggableRenderer}</tt>
 * is a good starting <code>Renderer</code> for off-the shelf use.
 * <p>
 * In general, one can expect that <code>paintVertex</code> and <code>paintEdge</code> will
 * only be called with visible edges and visible vertices.
 * 
 * @author danyelf
 */
public interface Renderer<V, E extends Edge<V>> {

	void paintVertex(Graphics g, V v, int x, int y);
	void paintEdge(Graphics g, Graph<V,E> graph, E e, int x1, int y1, int x2, int y2);
	void setPickedVertexState(PickedState<V> pickedState);
	void setPickedEdgeState(PickedState<E> pickedState);
}
