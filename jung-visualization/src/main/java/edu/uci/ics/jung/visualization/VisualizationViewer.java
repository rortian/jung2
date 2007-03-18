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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.cursor.Cursor;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;
import edu.uci.ics.jung.visualization.event.MouseMotionListener;
import edu.uci.ics.jung.visualization.event.MouseWheelListener;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * Adds mouse behaviors and tooltips to the graph visualization base class
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson 
 * @author Danyel Fisher
 * @author Jason A Wrang
 */
@SuppressWarnings("serial")
public interface VisualizationViewer<V,E> {
	Map<Object, VisualizationViewer> eventSourceToViewer
		= Collections.synchronizedMap( new HashMap<Object, VisualizationViewer>() );
	
	void setCursor(Cursor cursor);
	
	ScreenDevice getScreenDevice();
	VisualizationServer<V, E> getServer();
	
	void repaint();
	
	Color getBackground();
	void setBackground(Color c);
	
	Color getForeground();
	void setForeground(Color c);
	
	Rectangle getBounds();
	
	Dimension getSize();
	
	VisualizationModel<V, E> getModel();
	
	Renderer<V, E> getRenderer();
	
	void addChangeListener(ChangeListener l);
	
	Layout<V,E> getGraphLayout();
	
    void setGraphLayout(Layout<V,E> layout);
    
    RenderContext<V,E> getRenderContext();
    
    void scaleToLayout(ScalingControl scaler);
    
    
	void setVisible(boolean aFlag);
	
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getRenderingHints()
     */
    Map getRenderingHints();
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setRenderingHints(java.util.Map)
     */
    void setRenderingHints(Map renderingHints);
	
    
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getCenter()
     */
    Point2D getCenter();
    
	/**
	 * a setter for the GraphMouse. This will remove any
	 * previous GraphMouse (including the one that
	 * is added in the initMouseClicker method.
	 * @param graphMouse new value
	 */
	void setGraphMouse(GraphMouse graphMouse);
	
	/**
	 * @return the current <code>GraphMouse</code>
	 */
	GraphMouse getGraphMouse();

	/**
	 * This is the interface for adding a mouse listener. The GEL
	 * will be called back with mouse clicks on vertices.
	 * @param gel
	 */
	void addGraphMouseListener( GraphMouseListener<V> gel );
	
	String getToolTipText();
	void setToolTipText(String text);
	
	/**
	 * @param edgeToolTipTransformer the edgeToolTipTransformer to set
	 */
	void setEdgeToolTipTransformer(
			Transformer<E, String> edgeToolTipTransformer);

	/**
	 * @param mouseEventToolTipTransformer the mouseEventToolTipTransformer to set
	 */
	void setMouseEventToolTipTransformer(
			Transformer<MouseEvent, String> mouseEventToolTipTransformer);

	/**
	 * @param vertexToolTipTransformer the vertexToolTipTransformer to set
	 */
	void setVertexToolTipTransformer(
			Transformer<V, String> vertexToolTipTransformer);


    /**
     * a convenience type to represent a class that
     * processes all types of mouse events for the graph
     */
    interface GraphMouse extends MouseListener, MouseMotionListener, MouseWheelListener {}
}
