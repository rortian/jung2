/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.visualization.swt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.MouseListenerTranslator;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.cursor.Cursor;
import edu.uci.ics.jung.visualization.event.KeyListener;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;
import edu.uci.ics.jung.visualization.event.MouseMotionListener;
import edu.uci.ics.jung.visualization.event.MouseWheelListener;
import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.swt.graphics.GCGraphicsContext;
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
public class VisualizationComposite<V,E>
	implements edu.uci.ics.jung.visualization.VisualizationViewer<V, E>, ChangeListener, ChangeEventSupport {
	
	Composite dest;
	public Composite getComposite() {
		return dest;
	}
	
//	Image offscreen;
//	GC offscreenGC;
//	private void checkOffscreenImage(Device dev, Point p) {
//		if (offscreen == null 
//				|| offscreen.getBounds().width != p.x
//				|| offscreen.getBounds().height != p.y) {
//			if (offscreenGC != null) offscreenGC.dispose();
//			if (offscreen != null) offscreen.dispose();
//			
//			offscreen = new Image(dev, p.x, p.y);
//			offscreenGC = new GC(offscreen);
//		}
//	}
	
	
	protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);
	
	/**
	 * rendering hints used in drawing. Anti-aliasing is on
	 * by default
	 */
	protected Map renderingHints = new HashMap();
	
	// i could use Component, JPanel, whatever as the generic tag here:
	protected ScreenDevice<Composite> screenDevice;
	protected BasicVisualizationServer<V,E> visualizationServer;
	protected Transformer<V,String> vertexToolTipTransformer;
	protected Transformer<E,String> edgeToolTipTransformer;
	protected Transformer<MouseEvent,String> mouseEventToolTipTransformer;
	
	Dimension preferredSize;
	/**
	 * @return the preferredSize
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	/**
	 * @param preferredSize the preferredSize to set
	 */
	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}
	
	
    /**
     * provides MouseListener, MouseMotionListener, and MouseWheelListener
     * events to the graph
     */
    protected GraphMouse graphMouse;
    
    protected org.eclipse.swt.events.MouseListener requestFocusListener = new MouseAdapter() {
		public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
			dest.setFocus();
		}
    };


    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     */
	public VisualizationComposite(Composite parent, int style, Layout<V,E> layout) {
	    this(parent, style, new DefaultVisualizationModel<V,E>(layout));
	}
	
    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     * @param preferredSize the preferred size of this View
     */
	public VisualizationComposite(Composite parent, int style, Layout<V,E> layout, Dimension preferredSize) {
	    this(parent, style, new DefaultVisualizationModel<V,E>(layout, preferredSize), preferredSize);
	}
	
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 */
	public VisualizationComposite(Composite parent, int style, VisualizationModel<V,E> model) {
	    this(parent, style, model, new Dimension(600,600));
	}
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 * @param preferredSize initial preferred size of the view
	 */
	@SuppressWarnings("unchecked")
    public VisualizationComposite(Composite parent, int style, VisualizationModel<V,E> model,
	        Dimension preferredSize) {
		dest = new Composite(parent, style | SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		
		VisualizationViewer.eventSourceToViewer.put(dest, this);
		
		screenDevice = new ScreenDevice<Composite>(dest);
		visualizationServer = new BasicVisualizationServer<V, E>(model);
		dest.addMouseListener(requestFocusListener);
		dest.addControlListener(new VisualizationListener(this));
        
        setPreferredSize(preferredSize);
        renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        dest.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {				
				Transform transform = new Transform(e.gc.getDevice());
				e.gc.getTransform(transform);
				Font font = e.gc.getFont();
				org.eclipse.swt.graphics.Color fg = e.gc.getForeground();
				org.eclipse.swt.graphics.Color bg = e.gc.getBackground();
				int a = e.gc.getAntialias();
				int ta = e.gc.getTextAntialias();
				int alpha = e.gc.getAlpha();
				try {
					e.gc.setAntialias(SWT.ON);
					e.gc.setTextAntialias(SWT.ON);
					
					GCGraphicsContext graphicsContext = new GCGraphicsContext(e.gc);
					screenDevice.setGraphicsContext(graphicsContext);
					visualizationServer.getRenderContext().setScreenDevice(screenDevice);
					visualizationServer.renderGraph(screenDevice, graphicsContext);
					graphicsContext.dispose();
				} finally {
					e.gc.setAlpha(alpha);
					e.gc.setTextAntialias(ta);
					e.gc.setAntialias(a);
					e.gc.setBackground(bg);
					e.gc.setForeground(fg);
					e.gc.setFont(font);
					e.gc.setTransform(transform);
					transform.dispose();
					transform = null;
				}
			}
        });
        
        visualizationServer.addChangeListener(this);
	}
	
	/**
	 * Always sanity-check getSize so that we don't use a
	 * value that is improbable
	 * @see java.awt.Component#getSize()
	 */
	public Dimension getSize() {
		Point p = dest.getSize();
		Dimension d = new Dimension(p.x, p.y);
		if(d.width <= 0 || d.height <= 0) {
			d = getPreferredSize();
		}
		return d;
	}
	
	public Layout<V,E> getGraphLayout() {
		return visualizationServer.getGraphLayout();
	}
	
    public void setGraphLayout(Layout<V,E> layout) {
    	Dimension viewSize = getSize();
//    	if(this.isShowing()) {
//    		viewSize = getSize();
//    	}
	    visualizationServer.setGraphLayout(layout, viewSize);
    }
    
    public ScreenDevice getScreenDevice() {
    	return screenDevice;
    }
    
    public RenderContext<V,E> getRenderContext() {
        return visualizationServer.getRenderContext();
    }
    
    public void scaleToLayout(ScalingControl scaler) {
    	Dimension vd = getSize();
//    	if(this.isShowing()) {
//    		vd = getSize();
//    	}
		Dimension ld = visualizationServer.getGraphLayout().getSize();
		if(vd.equals(ld) == false) {
			scaler.scale(visualizationServer, (float)(vd.getWidth()/ld.getWidth()), new Point2D.Double());
		}
    }
    

	public void setVisible(boolean aFlag) {
		dest.setVisible(aFlag);
		if(aFlag == true) {
			Dimension d = this.getSize();
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
	
	
    
//	protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//		Graphics2D g2d = (Graphics2D)g;
//		g2d.setRenderingHints(renderingHints);
//		// no need to handle double buffering, let the JPanel take care of it
////		if(doubleBuffered) {
////		    checkOffscreenImage(getSize());
////			renderGraph(offscreenG2d);
////		    g2d.drawImage(offscreen, null, 0, 0);
////		} else {
//		visualizationServer.getRenderContext().setScreenDevice(screenDevice);
//		visualizationServer.renderGraph(screenDevice, g2d);
////		}
//	}
    
    
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
	    MouseListener[] ml = screenDevice.getMouseListeners();
	    for(int i=0; i<ml.length; i++) {
	        if(ml[i] instanceof GraphMouse) {
	        	screenDevice.removeMouseListener(ml[i]);
	        }
	    }
	    MouseMotionListener[] mml = screenDevice.getMouseMotionListeners();
	    for(int i=0; i<mml.length; i++) {
	        if(mml[i] instanceof GraphMouse) {
	        	screenDevice.removeMouseMotionListener(mml[i]);
	        }
	    }
	    MouseWheelListener[] mwl = screenDevice.getMouseWheelListeners();
	    for(int i=0; i<mwl.length; i++) {
	        if(mwl[i] instanceof GraphMouse) {
	        	screenDevice.removeMouseWheelListener(mwl[i]);
	        }
	    }
	    screenDevice.addMouseListener(graphMouse);
	    screenDevice.addMouseMotionListener(graphMouse);
	    screenDevice.addMouseWheelListener(graphMouse);
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
		screenDevice.addMouseListener( new MouseListenerTranslator<V,E>( gel, visualizationServer ));
	}
	
	/** 
	 * A convienence method to add the key listener to the screen
	 * device.
	 */
	public synchronized void addKeyListener(KeyListener l) {
		screenDevice.addKeyListener(l);
//		super.addKeyListener(l);
////		setFocusable(true);
////		addMouseListener(requestFocusListener);
	}
	
	/**
	 * @param edgeToolTipTransformer the edgeToolTipTransformer to set
	 */
	public void setEdgeToolTipTransformer(
			Transformer<E, String> edgeToolTipTransformer) {
		this.edgeToolTipTransformer = edgeToolTipTransformer;
//		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * @param mouseEventToolTipTransformer the mouseEventToolTipTransformer to set
	 */
	public void setMouseEventToolTipTransformer(
			Transformer<MouseEvent, String> mouseEventToolTipTransformer) {
		this.mouseEventToolTipTransformer = mouseEventToolTipTransformer;
//		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * @param vertexToolTipTransformer the vertexToolTipTransformer to set
	 */
	public void setVertexToolTipTransformer(
			Transformer<V, String> vertexToolTipTransformer) {
		this.vertexToolTipTransformer = vertexToolTipTransformer;
//		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	
	public VisualizationServer<V, E> getServer() {
		return visualizationServer;
	}

//	/**
//     * called by the superclass to display tooltips
//     */
//    public String getToolTipText(java.awt.event.MouseEvent event) {
//        Layout<V,E> layout = visualizationServer.getGraphLayout();
//        Point2D p = null;
//        if(vertexToolTipTransformer != null) {
//            p = event.getPoint();
//            	//renderContext.getBasicTransformer().inverseViewTransform(event.getPoint());
//            V vertex = visualizationServer.getPickSupport().getVertex(layout, p.getX(), p.getY());
//            if(vertex != null) {
//            	return vertexToolTipTransformer.transform(vertex);
//            }
//        }
//        if(edgeToolTipTransformer != null) {
//        	if(p == null) p = visualizationServer.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, event.getPoint());
//            E edge = visualizationServer.getPickSupport().getEdge(layout, p.getX(), p.getY());
//            if(edge != null) {
//            	return edgeToolTipTransformer.transform(edge);
//            }
//        }
//        if(mouseEventToolTipTransformer != null) {
//        	return mouseEventToolTipTransformer.transform(ScreenDevice.createEvent(event));
//        }
//        
//        return super.getToolTipText(event);
//    }
    
    
	/**
	 * VisualizationListener reacts to changes in the size of the
	 * VisualizationViewer. When the size changes, it ensures
	 * that the offscreen image is sized properly. 
	 * If the layout is locked to this view size, then the layout
	 * is also resized to be the same as the view size.
	 *
	 *
	 */
	protected class VisualizationListener extends ControlAdapter {
		protected VisualizationComposite<V,E> vv;
		public VisualizationListener(VisualizationComposite<V,E> vv) {
			this.vv = vv;
		}

		/**
		 * create a new offscreen image for the graph
		 * whenever the window is resied
		 */
		public void controlResized(ControlEvent e) {
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

	public void setCursor(Cursor cursor) {
		dest.setCursor( CursorUtils.getCursor(cursor) );
	}

	public void repaint() {
		dest.redraw();
	}

	public void setBackground(Color c) {
		org.eclipse.swt.graphics.Color color 
		= new org.eclipse.swt.graphics.Color(dest.getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
		dest.setBackground(color);
	}

	public void setForeground(Color c) {
		org.eclipse.swt.graphics.Color color 
		= new org.eclipse.swt.graphics.Color(dest.getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
		dest.setForeground(color);
	}

	public Color getBackground() {
		org.eclipse.swt.graphics.Color color = dest.getBackground();
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	public Rectangle getBounds() {
		org.eclipse.swt.graphics.Rectangle r = dest.getBounds();
		return new Rectangle(r.x, r.y, r.width, r.height);
	}

	public Color getForeground() {
		org.eclipse.swt.graphics.Color color = dest.getForeground();
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	public String getToolTipText() {
		return null;
	}

	public void setToolTipText(String text) {
		
	}
}
