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
 * Created on Feb 17, 2004
 */
package edu.uci.ics.jung.visualization;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;

/**
 * This class translates mouse clicks into vertex clicks
 * 
 * @author danyelf
 */
public class MouseListenerTranslator<V, E> implements MouseListener{

	private VisualizationServer<V,E> vs;
	private GraphMouseListener<V> gel;

	/**
	 * @param gel
	 * @param vv
	 */
	public MouseListenerTranslator(GraphMouseListener<V> gel, VisualizationServer<V,E> vs) {
		this.gel = gel;
		this.vs = vs;
	}
	
	/**
	 * Transform the point to the coordinate system in the
	 * VisualizationViewer, then use either PickSuuport
	 * (if available) or Layout to find a Vertex
	 * @param point
	 * @return
	 */
	private V getVertex(Point2D point) {
	    // adjust for scale and offset in the VisualizationViewer
	    Point2D p = point;
	    	//vv.getRenderContext().getBasicTransformer().inverseViewTransform(point);
	    GraphElementAccessor<V,E> pickSupport = vs.getPickSupport();
        Layout<V,E> layout = vs.getGraphLayout();
	    V v = null;
	    if(pickSupport != null) {
	        v = pickSupport.getVertex(layout, p.getX(), p.getY());
	    } 
	    return v;
	}

	public void mouseClicked(MouseEvent e) {
	    V v = getVertex(e.getPoint());
		if ( v != null ) {
			gel.graphClicked(v, e );
		}
	}


	public void mousePressed(MouseEvent e) {
		V v = getVertex(e.getPoint());
		if ( v != null ) {
			gel.graphPressed(v, e );
		}
	}


	public void mouseReleased(MouseEvent e) {
		V v = getVertex(e.getPoint());
		if ( v != null ) {
			gel.graphReleased(v, e );
		}
	}

	public void mouseDoubleClicked(MouseEvent mouseEvent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent mouseEvent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent mouseEvent) {
		// TODO Auto-generated method stub
		
	}
}
