/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
/*
 * Created on Jul 27, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.uci.ics.jung.visualization;

import java.awt.Graphics;

import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;

/**
 * This abstract class structures much of the annoying
 * bits of Renderers, allowing the user to simply override
 * the important methods and move on.
 * 
 * @author danyelf
 */
public abstract class AbstractRenderer<V, E extends Edge<V>> implements Renderer<V, E> {

//	private PickedState pickedState;

	abstract public void paintEdge(
		Graphics g,
		Graph<V,E> graph,
		E e,
		int x1,
		int y1,
		int x2,
		int y2);

	abstract public void paintVertex(Graphics g, V v, int x, int y);

//	public void setPickedState(PickedState pk) {
//		this.pickedState = pk;
//	}
//
//    public PickedState getPickedState()
//    {
//        return pickedState;
//    }
//    
//    
//	public boolean isPicked(Object v) {
//		return pickedState.isPicked(v);
//	}

//	public int wiggleRoomX() {
//		return 0;
//	}
//
//	public int wiggleRoomY() {
//		return 0;
//	}

}
