/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.visualization.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.MouseListenerTranslator;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

/**
 * Adds mouse behaviors and tooltips to the graph visualization base class
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson 
 * @author Danyel Fisher
 */
@SuppressWarnings("serial")
public class VisualizationComponent<V,E> extends JPanel 
	implements edu.uci.ics.jung.visualization.VisualizationViewer<V, E>, ChangeListener, ChangeEventSupport {
	
	protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);
	
	/**
	 * rendering hints used in drawing. Anti-aliasing is on
	 * by default
	 */
	protected Map renderingHints = new HashMap();
	
	
	protected BasicVisualizationServer<V,E> visualizationServer;
	protected Transformer<V,String> vertexToolTipTransformer;
	protected Transformer<E,String> edgeToolTipTransformer;
	protected Transformer<MouseEvent,String> mouseEventToolTipTransformer;
	
    /**
     * provides MouseListener, MouseMotionListener, and MouseWheelListener
     * events to the graph
     */
    protected GraphMouse graphMouse;
    
    protected MouseListener requestFocusListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			requestFocusInWindow();
		}
    };


    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     */
	public VisualizationComponent(Layout<V,E> layout) {
	    this(new DefaultVisualizationModel<V,E>(layout));
	}
	
    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     * @param preferredSize the preferred size of this View
     */
	public VisualizationComponent(Layout<V,E> layout, Dimension preferredSize) {
	    this(new DefaultVisualizationModel<V,E>(layout, preferredSize), preferredSize);
	}
	
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 */
	public VisualizationComponent(VisualizationModel<V,E> model) {
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
    public VisualizationComponent(VisualizationModel<V,E> model,
	        Dimension preferredSize) {
		visualizationServer = new BasicVisualizationServer<V, E>(model);
		setFocusable(true);
        addMouseListener(requestFocusListener);
        addComponentListener(new VisualizationListener(this));
        
        setPreferredSize(preferredSize);
        renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        visualizationServer.addChangeListener(this);
        
        visualizationServer.getRenderContext().setScreenDevice(new ScreenDevice(this));
	}
	
	/**
	 * Always sanity-check getSize so that we don't use a
	 * value that is improbable
	 * @see java.awt.Component#getSize()
	 */
	@Override
	public Dimension getSize() {
		Dimension d = super.getSize();
		if(d.width <= 0 || d.height <= 0) {
			d = getPreferredSize();
		}
		return d;
	}
	
	public Layout<V,E> getGraphLayout() {
		return visualizationServer.getGraphLayout();
	}
	
    public void setGraphLayout(Layout<V,E> layout) {
    	Dimension viewSize = getPreferredSize();
    	if(this.isShowing()) {
    		viewSize = getSize();
    	}
	    visualizationServer.setGraphLayout(layout, viewSize);
    }
    
    public RenderContext<V,E> getRenderContext() {
        return visualizationServer.getRenderContext();
    }
    
    public void scaleToLayout(ScalingControl scaler) {
    	Dimension vd = getPreferredSize();
    	if(this.isShowing()) {
    		vd = getSize();
    	}
		Dimension ld = visualizationServer.getGraphLayout().getSize();
		if(vd.equals(ld) == false) {
			scaler.scale(visualizationServer, (float)(vd.getWidth()/ld.getWidth()), new Point2D.Double());
		}
    }
    

	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		if(aFlag == true) {
			Dimension d = this.getSize();
			if(d.width <= 0 || d.height <= 0) {
				d = this.getPreferredSize();
			}
			visualizationServer.getModel().getGraphLayout().setSize(d);
		}
	}
	
	
	
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getRenderingHints()
     */
    public Map getRenderingHints() {
        return renderingHints;
    }
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setRenderingHints(java.util.Map)
     */
    public void setRenderingHints(Map renderingHints) {
        this.renderingHints = renderingHints;
    }
	
	
    
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHints(renderingHints);
		// no need to handle double buffering, let the JPanel take care of it
//		if(doubleBuffered) {
//		    checkOffscreenImage(getSize());
//			renderGraph(offscreenG2d);
//		    g2d.drawImage(offscreen, null, 0, 0);
//		} else {
		    visualizationServer.renderGraph(g2d);
//		}
	}
    
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getCenter()
     */
    public Point2D getCenter() {
        Dimension d = getSize();
        return new Point2D.Float(d.width/2, d.height/2);
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
		addMouseListener( new MouseListenerTranslator<V,E>( gel, visualizationServer ));
	}
	
	/** 
	 * Override to request focus on mouse enter, if a key listener is added
	 * @see java.awt.Component#addKeyListener(java.awt.event.KeyListener)
	 */
	@Override
	public synchronized void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
//		setFocusable(true);
//		addMouseListener(requestFocusListener);
	}
	
	/**
	 * @param edgeToolTipTransformer the edgeToolTipTransformer to set
	 */
	public void setEdgeToolTipTransformer(
			Transformer<E, String> edgeToolTipTransformer) {
		this.edgeToolTipTransformer = edgeToolTipTransformer;
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * @param mouseEventToolTipTransformer the mouseEventToolTipTransformer to set
	 */
	public void setMouseEventToolTipTransformer(
			Transformer<MouseEvent, String> mouseEventToolTipTransformer) {
		this.mouseEventToolTipTransformer = mouseEventToolTipTransformer;
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * @param vertexToolTipTransformer the vertexToolTipTransformer to set
	 */
	public void setVertexToolTipTransformer(
			Transformer<V, String> vertexToolTipTransformer) {
		this.vertexToolTipTransformer = vertexToolTipTransformer;
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	
	public VisualizationServer<V, E> getServer() {
		return visualizationServer;
	}

	/**
     * called by the superclass to display tooltips
     */
    public String getToolTipText(MouseEvent event) {
        Layout<V,E> layout = visualizationServer.getGraphLayout();
        Point2D p = null;
        if(vertexToolTipTransformer != null) {
            p = event.getPoint();
            	//renderContext.getBasicTransformer().inverseViewTransform(event.getPoint());
            V vertex = visualizationServer.getPickSupport().getVertex(layout, p.getX(), p.getY());
            if(vertex != null) {
            	return vertexToolTipTransformer.transform(vertex);
            }
        }
        if(edgeToolTipTransformer != null) {
        	if(p == null) p = visualizationServer.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, event.getPoint());
            E edge = visualizationServer.getPickSupport().getEdge(layout, p.getX(), p.getY());
            if(edge != null) {
            	return edgeToolTipTransformer.transform(edge);
            }
        }
        if(mouseEventToolTipTransformer != null) {
        	return mouseEventToolTipTransformer.transform(event);
        }
        return super.getToolTipText(event);
    }
    
    
	/**
	 * VisualizationListener reacts to changes in the size of the
	 * VisualizationViewer. When the size changes, it ensures
	 * that the offscreen image is sized properly. 
	 * If the layout is locked to this view size, then the layout
	 * is also resized to be the same as the view size.
	 *
	 *
	 */
	protected class VisualizationListener extends ComponentAdapter {
		protected VisualizationComponent<V,E> vv;
		public VisualizationListener(VisualizationComponent<V,E> vv) {
			this.vv = vv;
		}

		/**
		 * create a new offscreen image for the graph
		 * whenever the window is resied
		 */
		public void componentResized(ComponentEvent e) {
		    Dimension d = vv.getSize();
		    if(d.width <= 0 || d.height <= 0) return;
		    repaint();
		}
	}
	
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#fireStateChanged()
     */
    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }   
	
	public void stateChanged(ChangeEvent e) {
	    repaint();
	    fireStateChanged();
	}
	
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getChangeListeners()
     */
    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

	public VisualizationModel<V, E> getModel() {
		return getServer().getModel();
	}

    
	public Renderer<V, E> getRenderer() {
		return getServer().getRenderer();
	}
}
