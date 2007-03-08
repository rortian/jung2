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
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.util.Caching;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

/**
 * A class that maintains many of the details necessary for creating 
 * visualizations of graphs.
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson
 * @author Danyel Fisher
 * @author Jason A Wrang
 */
@SuppressWarnings("serial")
public class BasicVisualizationServer<V, E> 
                implements //Transformer, LayoutTransformer, ViewTransformer, 
                ChangeListener, ChangeEventSupport, VisualizationServer<V, E>{

    protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);
    
//    protected Map<V,Point2D> locationMap = new HashMap<V,Point2D>();

    /**
     * holds the state of this View
     */
    protected VisualizationModel<V,E> model;

	/**
	 * handles the actual drawing of graph elements
	 */
	protected Renderer<V,E> renderer = new BasicRenderer<V,E>();
		
	/**
	 * holds the state of which vertices of the graph are
	 * currently 'picked'
	 */
	protected PickedState<V> pickedVertexState;
	
	/**
	 * holds the state of which edges of the graph are
	 * currently 'picked'
	 */
    protected PickedState<E> pickedEdgeState;
    
    /**
     * a listener used to cause pick events to result in
     * repaints, even if they come from another view
     */
    protected ItemListener pickEventListener;
	
//	/**
//	 * an offscreen image to render the graph
//	 * Used if doubleBuffered is set to true
//	 */
//	protected BufferedImage offscreen;
	
//	/**
//	 * graphics context for the offscreen image
//	 * Used if doubleBuffered is set to true
//	 */
//	protected Graphics2D offscreenG2d;
//	
//	/**
//	 * user-settable choice to use the offscreen image
//	 * or not. 'false' by default
//	 */
//	protected boolean doubleBuffered;
//	
	/**
	 * a collection of user-implementable functions to render under
	 * the topology (before the graph is rendered)
	 */
	protected List<Paintable> preRenderers = new ArrayList<Paintable>();
	
	/**
	 * a collection of user-implementable functions to render over the
	 * topology (after the graph is rendered)
	 */
	protected List<Paintable> postRenderers = new ArrayList<Paintable>();
	
    protected RenderContext<V,E> renderContext = new PluggableRenderContext<V,E>();
    
    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     */
	public BasicVisualizationServer(Layout<V,E> layout) {
	    this(new DefaultVisualizationModel<V,E>(layout));
	}
	
    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     * @param preferredSize the preferred size of this View
     */
	public BasicVisualizationServer(Layout<V,E> layout, Dimension preferredSize) {
	    this(new DefaultVisualizationModel<V,E>(layout, preferredSize));
	}
	
	public BasicVisualizationServer(VisualizationModel<V,E> model) {
		this(model, null);
	}
	
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 * @param preferredSize initial preferred size of the view
	 */
	@SuppressWarnings("unchecked")
    public BasicVisualizationServer(VisualizationModel<V,E> model, Dimension preferredSize) {
	    this.model = model;
	    if (preferredSize != null) this.model.getGraphLayout().setSize(preferredSize);
//        renderContext.setScreenDevice(this);
	    model.addChangeListener(this);
//	    setDoubleBuffered(false);
		

		setPickSupport(new ShapePickSupport<V,E>(this));
		setPickedVertexState(new MultiPickedState<V>());
		setPickedEdgeState(new MultiPickedState<E>());
        
        renderContext.setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<V,E>(getPickedEdgeState(), Color.black, Color.cyan));
        renderContext.setVertexFillPaintTransformer(new PickableVertexPaintTransformer<V>(getPickedVertexState(), 
                Color.red, Color.yellow));
		

        renderContext.getMultiLayerTransformer().addChangeListener(this);
	}
	
//	/* (non-Javadoc)
//     * @see edu.uci.ics.jung.visualization.VisualizationServer#setDoubleBuffered(boolean)
//     */
//	public void setDoubleBuffered(boolean doubleBuffered) {
//	    this.doubleBuffered = doubleBuffered;
//	}
//	
//	/* (non-Javadoc)
//     * @see edu.uci.ics.jung.visualization.VisualizationServer#isDoubleBuffered()
//     */
//	public boolean isDoubleBuffered() {
//	    return doubleBuffered;
//	}
	

//	/**
//	 * Ensure that, if doubleBuffering is enabled, the offscreen
//	 * image buffer exists and is the correct size.
//	 * @param d
//	 */
//	protected void checkOffscreenImage(Dimension d) {
//	    if(doubleBuffered) {
//	        if(offscreen == null || offscreen.getWidth() != d.width || offscreen.getHeight() != d.height) {
//	            offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
//	            offscreenG2d = offscreen.createGraphics();
//	        }
//	    }
//	}
	
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getModel()
     */
    public VisualizationModel<V,E> getModel() {
        return model;
    }
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setModel(edu.uci.ics.jung.visualization.VisualizationModel)
     */
    public void setModel(VisualizationModel<V,E> model) {
        this.model = model;
    }
	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#stateChanged(javax.swing.event.ChangeEvent)
     */
	public void stateChanged(ChangeEvent e) {
	    fireStateChanged();
	}

	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setRenderer(edu.uci.ics.jung.visualization.Renderer)
     */
	public void setRenderer(Renderer<V,E> r) {
	    this.renderer = r;
	}
	
	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getRenderer()
     */
	public Renderer<V,E> getRenderer() {
	    return renderer;
	}

	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setGraphLayout(edu.uci.ics.jung.visualization.layout.Layout)
     */
    public void setGraphLayout(Layout<V,E> layout) {
    	Layout<V, E> curLayout = model.getGraphLayout();
    	Dimension viewSize = curLayout == null?null:curLayout.getSize();
    	setGraphLayout(layout, viewSize);
    }
    
    public void setGraphLayout(Layout<V,E> layout, Dimension viewSize) {
	    if (viewSize == null)
	    	model.setGraphLayout(layout);
	    else
	    	model.setGraphLayout(layout, viewSize);
    }
    
	
	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getGraphLayout()
     */
	public Layout<V,E> getGraphLayout() {
		return model.getGraphLayout();
	}
	

    

	
	public void renderGraph(Graphics2D g2d) {
	    if(renderContext.getGraphicsContext() == null) {
	        renderContext.setGraphicsContext(new GraphicsDecorator(g2d));
        } else {
        	renderContext.getGraphicsContext().setDelegate(g2d);
        }
	    Layout<V,E> layout = model.getGraphLayout();

		// the size of the VisualizationViewer
		Dimension d = renderContext.getScreenDevice().getSize();
		
		// clear the offscreen image
		g2d.setColor(renderContext.getScreenDevice().getBackground());
		g2d.fillRect(0,0,d.width,d.height);

		AffineTransform oldXform = g2d.getTransform();
        AffineTransform newXform = new AffineTransform(oldXform);
        newXform.concatenate(
        		renderContext.getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform());
//        		viewTransformer.getTransform());
		
        g2d.setTransform(newXform);

		// if there are  preRenderers set, paint them
		for(Paintable paintable : preRenderers) {

		    if(paintable.useTransform()) {
		        paintable.paint(g2d);
		    } else {
		        g2d.setTransform(oldXform);
		        paintable.paint(g2d);
                g2d.setTransform(newXform);
		    }
		}
		
        if(layout instanceof Caching) {
        	((Caching)layout).clear();
        }
        
        renderer.render(renderContext, layout);
		
		// if there are postRenderers set, do it
		for(Paintable paintable : postRenderers) {

		    if(paintable.useTransform()) {
		        paintable.paint(g2d);
		    } else {
		        g2d.setTransform(oldXform);
		        paintable.paint(g2d);
                g2d.setTransform(newXform);
		    }
		}
		g2d.setTransform(oldXform);
	}



    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#addPreRenderPaintable(edu.uci.ics.jung.visualization.BasicPaintable)
     */
    public void addPreRenderPaintable(Paintable paintable) {
        if(preRenderers == null) {
            preRenderers = new ArrayList<Paintable>();
        }
        preRenderers.add(paintable);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#addPreRenderPaintable(edu.uci.ics.jung.visualization.BasicPaintable)
     */
    public void prependPreRenderPaintable(Paintable paintable) {
        if(preRenderers == null) {
            preRenderers = new ArrayList<Paintable>();
        }
        preRenderers.add(0,paintable);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#removePreRenderPaintable(edu.uci.ics.jung.visualization.BasicPaintable)
     */
    public void removePreRenderPaintable(Paintable paintable) {
        if(preRenderers != null) {
            preRenderers.remove(paintable);
        }
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#addPostRenderPaintable(edu.uci.ics.jung.visualization.BasicVisualizationServer.Paintable)
     */
    public void addPostRenderPaintable(Paintable paintable) {
        if(postRenderers == null) {
            postRenderers = new ArrayList<Paintable>();
        }
        postRenderers.add(paintable);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#addPostRenderPaintable(edu.uci.ics.jung.visualization.BasicVisualizationServer.Paintable)
     */
    public void prependPostRenderPaintable(Paintable paintable) {
        if(postRenderers == null) {
            postRenderers = new ArrayList<Paintable>();
        }
        postRenderers.add(0,paintable);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#removePostRenderPaintable(edu.uci.ics.jung.visualization.BasicVisualizationServer.Paintable)
     */
   public void removePostRenderPaintable(Paintable paintable) {
        if(postRenderers != null) {
            postRenderers.remove(paintable);
        }
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

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#fireStateChanged()
     */
    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }   
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getPickedVertexState()
     */
    public PickedState<V> getPickedVertexState() {
        return pickedVertexState;
    }
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getPickedEdgeState()
     */
    public PickedState<E> getPickedEdgeState() {
        return pickedEdgeState;
    }
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setPickedVertexState(edu.uci.ics.jung.visualization.picking.PickedState)
     */
    public void setPickedVertexState(PickedState<V> pickedVertexState) {
        if(pickEventListener != null && this.pickedVertexState != null) {
            this.pickedVertexState.removeItemListener(pickEventListener);
        }
        this.pickedVertexState = pickedVertexState;
        this.renderContext.setPickedVertexState(pickedVertexState);
        if(pickEventListener == null) {
            pickEventListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    fireStateChanged();
                }
            };
        }
        pickedVertexState.addItemListener(pickEventListener);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setPickedEdgeState(edu.uci.ics.jung.visualization.picking.PickedState)
     */
    public void setPickedEdgeState(PickedState<E> pickedEdgeState) {
        if(pickEventListener != null && this.pickedEdgeState != null) {
            this.pickedEdgeState.removeItemListener(pickEventListener);
        }
        this.pickedEdgeState = pickedEdgeState;
        this.renderContext.setPickedEdgeState(pickedEdgeState);
        if(pickEventListener == null) {
            pickEventListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    fireStateChanged();
                }
            };
        }
        pickedEdgeState.addItemListener(pickEventListener);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getPickSupport()
     */
    public GraphElementAccessor<V,E> getPickSupport() {
        return renderContext.getPickSupport();
    }
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setPickSupport(edu.uci.ics.jung.visualization.GraphElementAccessor)
     */
    public void setPickSupport(GraphElementAccessor<V,E> pickSupport) {
        renderContext.setPickSupport(pickSupport);
    }
    
    public List<Paintable> getPreRenderPaintables() {
    	return preRenderers;
    }
    
    public List<Paintable> getPostRenderPaintables() {
    	return postRenderers;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getRenderContext()
     */
    public RenderContext<V,E> getRenderContext() {
        return renderContext;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setRenderContext(edu.uci.ics.jung.visualization.RenderContext)
     */
    public void setRenderContext(RenderContext<V,E> renderContext) {
        this.renderContext = renderContext;
    }
}
