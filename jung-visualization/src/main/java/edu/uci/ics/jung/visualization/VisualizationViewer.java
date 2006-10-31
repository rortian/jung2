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

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.ToolTipManager;

import edu.uci.ics.jung.visualization.decorators.ToolTipFunction;
import edu.uci.ics.jung.visualization.decorators.ToolTipFunctionAdapter;
import edu.uci.ics.jung.visualization.layout.Layout;

/**
 * Adds mouse behaviors and tooltips to the graph visualization base class
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson 
 * @author Danyel Fisher
 */
@SuppressWarnings("serial")
public class VisualizationViewer<V,E> extends BasicVisualizationServer<V,E> {

	/** should be set to user-defined class to provide
	 * tooltips on the graph elements
	 */
	protected ToolTipFunction toolTipFunction;
	
    /**
     * provides MouseListener, MouseMotionListener, and MouseWheelListener
     * events to the graph
     */
    protected GraphMouse graphMouse;

    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     */
	public VisualizationViewer(Layout<V,E> layout) {
	    this(new DefaultVisualizationModel<V,E>(layout));
	}
	
    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     * @param preferredSize the preferred size of this View
     */
	public VisualizationViewer(Layout<V,E> layout, Dimension preferredSize) {
	    this(new DefaultVisualizationModel<V,E>(layout, preferredSize), preferredSize);
	}
	
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 */
	public VisualizationViewer(VisualizationModel<V,E> model) {
	    this(model, new Dimension(600,600));
	}
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 * @param preferredSize initial preferred size of the view
	 */
	@SuppressWarnings("unchecked")
    public VisualizationViewer(VisualizationModel<V,E> model,
	        Dimension preferredSize) {
        super(model, preferredSize);
	}
	
	/**
	 * a setter for the GraphMouse. This will remove any
	 * previous GraphMouse (including the one that
	 * is added in the initMouseClicker method.
	 * @param graphMouse new value
	 */
	public void setGraphMouse(GraphMouse graphMouse) {
	    this.graphMouse = graphMouse;
	    MouseListener[] ml = getMouseListeners();
	    for(int i=0; i<ml.length; i++) {
	        if(ml[i] instanceof GraphMouse) {
	            removeMouseListener(ml[i]);
	        }
	    }
	    MouseMotionListener[] mml = getMouseMotionListeners();
	    for(int i=0; i<mml.length; i++) {
	        if(mml[i] instanceof GraphMouse) {
	            removeMouseMotionListener(mml[i]);
	        }
	    }
	    MouseWheelListener[] mwl = getMouseWheelListeners();
	    for(int i=0; i<mwl.length; i++) {
	        if(mwl[i] instanceof GraphMouse) {
	            removeMouseWheelListener(mwl[i]);
	        }
	    }
	    addMouseListener(graphMouse);
	    addMouseMotionListener(graphMouse);
	    addMouseWheelListener(graphMouse);
	}
	
	/**
	 * @return the current <code>GraphMouse</code>
	 */
	public GraphMouse getGraphMouse() {
	    return graphMouse;
	}

	/**
	 * This is the interface for adding a mouse listener. The GEL
	 * will be called back with mouse clicks on vertices.
	 * @param gel
	 */
	public void addGraphMouseListener( GraphMouseListener<V> gel ) {
		addMouseListener( new MouseListenerTranslator<V,E>( gel, this ));
	}
	
	/**
	 * sets the tooltip listener to the user's defined implementation
	 * of ToolTipListener
	 * @param listener the listener to ser
	 */
    public void setToolTipListener(ToolTipListener listener) {
        if(listener instanceof ToolTipFunction) {
            setToolTipFunction((ToolTipFunction)listener);
        } else {
            setToolTipFunction(new ToolTipListenerWrapper(listener));
        }
    }

    public void setToolTipFunction(ToolTipFunction toolTipFunction) {
        this.toolTipFunction = toolTipFunction;
        ToolTipManager.sharedInstance().registerComponent(this);
    }
    /**
     * called by the superclass to display tooltips
     */
    public String getToolTipText(MouseEvent event) {
        if(toolTipFunction != null) {
            if(toolTipFunction instanceof ToolTipListenerWrapper) {
                return toolTipFunction.getToolTipText(event);
            } 
            Layout<V,E> layout = getGraphLayout();
            Point2D p = inverseViewTransform(event.getPoint());
            Object vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
            if(vertex != null) {
                return toolTipFunction.getToolTipText(vertex);
            }
            Object edge = pickSupport.getEdge(layout, p.getX(), p.getY());
            if(edge != null) {
                return toolTipFunction.getToolTipText(edge);
            }
            return toolTipFunction.getToolTipText(event);
        }
        return super.getToolTipText(event);
    }

	/**
	 * The interface for the tool tip listener. Implement this
	 * interface to add custom tool tip to the graph elements.
	 * See sample code for examples
	 */
    public interface ToolTipListener {
        	String getToolTipText(MouseEvent event);
    }
    
    /**
     * used internally to wrap any legacy ToolTipListener
     * implementations so they can be used as a ToolTipFunction
     * @author Tom Nelson - RABA Technologies
     *
     *
     */
    protected static class ToolTipListenerWrapper extends ToolTipFunctionAdapter {
        ToolTipListener listener;
        public ToolTipListenerWrapper(ToolTipListener listener) {
            this.listener = listener;
        }
        public String getToolTipText(MouseEvent e) {
            return listener.getToolTipText(e);
        }
    }
    
    /**
     * a convenience type to represent a class that
     * processes all types of mouse events for the graph
     */
    public interface GraphMouse extends MouseListener, MouseMotionListener, MouseWheelListener {}
    
}
