/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.TruePredicate;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.graph.util.DefaultParallelEdgeIndexFunction;
import edu.uci.ics.graph.util.ParallelEdgeIndexFunction;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.PredicatedGraphCollections;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;


/**
 */
@SuppressWarnings("unchecked")
public class PluggableRenderContext<V, E> implements RenderContext<V, E> {
    
	protected float arrowPlacementTolerance = 1;
    protected Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate = TruePredicate.getInstance();
    protected Transformer<V,Stroke> vertexStrokeTransformer = 
    	new ConstantTransformer(new BasicStroke(1.0f));
    
    protected Transformer<V,Shape> vertexShapeTransformer = 
        		new ConstantTransformer(
        		new Ellipse2D.Float(-10,-10,20,20));

    protected Transformer<V,String> vertexStringer = new ConstantTransformer(null);
    protected Transformer<V,Icon> vertexIconFunction;
    protected Transformer<V,Font> vertexFontFunction = 
        new ConstantTransformer(new Font("Helvetica", Font.PLAIN, 12));
    
    protected Transformer<V,Paint> vertexDrawPaintFunction = new ConstantTransformer(Color.BLACK);
    protected Transformer<V,Paint> vertexFillPaintFunction = new ConstantTransformer(Color.RED);
    
    protected Transformer<E,String> edgeStringer = new ConstantTransformer(null);
    protected Transformer<E,Stroke> edgeStrokeFunction = new ConstantTransformer(new BasicStroke(1.0f));
    
    protected Transformer<Context<Graph<V,E>,E>,Shape> edgeArrowFunction = 
        new DirectionalEdgeArrowTransformer<V,E>(10, 8, 4);
    
    protected Predicate<Context<Graph<V,E>,E>> edgeArrowPredicate = new DirectedEdgeArrowPredicate<V,E>();
    protected Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate = TruePredicate.getInstance();
    protected Transformer<E,Font> edgeFontFunction =
        new ConstantTransformer(new Font("Helvetica", Font.PLAIN, 12));
    protected Transformer<Context<Graph<V,E>,E>,Number> edgeLabelClosenessFunction = 
        new ConstantDirectionalEdgeValueTransformer<V,E>(0.5, 0.65);
    protected Transformer<Context<Graph<V,E>,E>,Shape> edgeShapeFunction = 
        new EdgeShape.QuadCurve<V,E>();
    protected Transformer<E,Paint> edgeFillPaintFunction =
        new ConstantTransformer(null);
    protected Transformer<E,Paint> edgeDrawPaintFunction =
        new ConstantTransformer(Color.black);
    protected ParallelEdgeIndexFunction<V,E> parallelEdgeIndexFunction = 
        DefaultParallelEdgeIndexFunction.<V,E>getInstance();
//    protected MutableTransformer viewTransformer = new MutableAffineTransformer();
    
    protected BasicTransformer basicTransformer = new BasicTransformer();
    
	/**
	 * pluggable support for picking graph elements by
	 * finding them based on their coordinates.
	 */
	protected GraphElementAccessor<V, E> pickSupport;

    
    protected int labelOffset = LABEL_OFFSET;
    
    /**
     * the JComponent that this Renderer will display the graph on
     */
    protected JComponent screenDevice;
    
    protected PickedState<V> pickedVertexState;
    protected PickedState<E> pickedEdgeState;
    
    /**
     * The CellRendererPane is used here just as it is in JTree
     * and JTable, to allow a pluggable JLabel-based renderer for
     * Vertex and Edge label strings and icons.
     */
    protected CellRendererPane rendererPane = new CellRendererPane();
    
    /**
     * A default GraphLabelRenderer - picked Vertex labels are
     * blue, picked edge labels are cyan
     */
    protected VertexLabelRenderer vertexLabelRenderer = 
        new DefaultVertexLabelRenderer(Color.blue);
    
    protected EdgeLabelRenderer edgeLabelRenderer = new DefaultEdgeLabelRenderer(Color.cyan);
    
    protected GraphicsDecorator graphicsContext;
    
//    protected final static EdgePredicate self_loop = SelfLoopEdgePredicate.getInstance();
    
    PluggableRenderContext() {
        this.setEdgeShapeFunction(new EdgeShape.QuadCurve<V,E>());
    }

	/**
	 * @return the vertexShapeTransformer
	 */
	public Transformer<V, Shape> getVertexShapeTransformer() {
		return vertexShapeTransformer;
	}

	/**
	 * @param vertexShapeTransformer the vertexShapeTransformer to set
	 */
	public void setVertexShapeTransformer(
			Transformer<V, Shape> vertexShapeTransformer) {
		this.vertexShapeTransformer = vertexShapeTransformer;
	}

	/**
	 * @return the vertexStrokeTransformer
	 */
	public Transformer<V, Stroke> getVertexStrokeTransformer() {
		return vertexStrokeTransformer;
	}

	/**
	 * @param vertexStrokeTransformer the vertexStrokeTransformer to set
	 */
	public void setVertexStrokeTransformer(
			Transformer<V, Stroke> vertexStrokeTransformer) {
		this.vertexStrokeTransformer = vertexStrokeTransformer;
	}

	public static float[] getDashing() {
        return dashing;
    }

    public static float[] getDotting() {
        return dotting;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getArrow_placement_tolerance()
     */
    public float getArrowPlacementTolerance() {
        return arrowPlacementTolerance;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setArrow_placement_tolerance(float)
     */
    public void setArrowPlacementTolerance(float arrow_placement_tolerance) {
        this.arrowPlacementTolerance = arrow_placement_tolerance;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeArrowFunction()
     */
    public Transformer<Context<Graph<V,E>,E>,Shape> getEdgeArrowFunction() {
        return edgeArrowFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeArrowFunction(edu.uci.ics.jung.visualization.decorators.EdgeArrowFunction)
     */
    public void setEdgeArrowFunction(Transformer<Context<Graph<V,E>,E>,Shape> edgeArrowFunction) {
        this.edgeArrowFunction = edgeArrowFunction;
    }

    public Predicate<Context<Graph<V,E>,E>> getEdgeArrowPredicate() {
        return edgeArrowPredicate;
    }

    public void setEdgeArrowPredicate(Predicate<Context<Graph<V,E>,E>> edgeArrowPredicate) {
        this.edgeArrowPredicate = edgeArrowPredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeFontFunction()
     */
    public Transformer<E,Font> getEdgeFontFunction() {
        return edgeFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeFontFunction(edu.uci.ics.jung.visualization.decorators.EdgeFontFunction)
     */
    public void setEdgeFontFunction(Transformer<E,Font> edgeFontFunction) {
        this.edgeFontFunction = edgeFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeIncludePredicate()
     */
    public Predicate<Context<Graph<V,E>,E>> getEdgeIncludePredicate() {
        return edgeIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeIncludePredicate(org.apache.commons.collections15.Predicate)
     */
    public void setEdgeIncludePredicate(Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate) {
        this.edgeIncludePredicate = edgeIncludePredicate;
        updateState(pickSupport, vertexIncludePredicate, edgeIncludePredicate);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelClosenessFunction()
     */
    public Transformer<Context<Graph<V,E>,E>,Number> getEdgeLabelClosenessFunction() {
        return edgeLabelClosenessFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelClosenessFunction(edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue)
     */
    public void setEdgeLabelClosenessFunction(
    		Transformer<Context<Graph<V,E>,E>,Number> edgeLabelClosenessFunction) {
        this.edgeLabelClosenessFunction = edgeLabelClosenessFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelRenderer()
     */
    public EdgeLabelRenderer getEdgeLabelRenderer() {
        return edgeLabelRenderer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelRenderer(edu.uci.ics.jung.visualization.EdgeLabelRenderer)
     */
    public void setEdgeLabelRenderer(EdgeLabelRenderer edgeLabelRenderer) {
        this.edgeLabelRenderer = edgeLabelRenderer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgePaintFunction()
     */
    public Transformer<E,Paint> getEdgeFillPaintFunction() {
        return edgeFillPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgePaintFunction(edu.uci.ics.jung.visualization.decorators.EdgePaintFunction)
     */
    public void setEdgeDrawPaintFunction(Transformer<E,Paint> edgeDrawPaintFunction) {
        this.edgeDrawPaintFunction = edgeDrawPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgePaintFunction()
     */
    public Transformer<E,Paint> getEdgeDrawPaintFunction() {
        return edgeDrawPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgePaintFunction(edu.uci.ics.jung.visualization.decorators.EdgePaintFunction)
     */
    public void setEdgeFillPaintFunction(Transformer<E,Paint> edgeFillPaintFunction) {
        this.edgeFillPaintFunction = edgeFillPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeShapeFunction()
     */
    public Transformer<Context<Graph<V,E>,E>,Shape> getEdgeShapeFunction() {
        return edgeShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeShapeFunction(edu.uci.ics.jung.visualization.decorators.EdgeShapeFunction)
     */
    public void setEdgeShapeFunction(Transformer<Context<Graph<V,E>,E>,Shape> edgeShapeFunction) {
        this.edgeShapeFunction = edgeShapeFunction;
        if(edgeShapeFunction instanceof EdgeShape.ParallelRendering) {
            ((EdgeShape.ParallelRendering<V,E>)edgeShapeFunction).setParallelEdgeIndexFunction(this.parallelEdgeIndexFunction);
        }
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeStringer()
     */
    public Transformer<E,String> getEdgeStringer() {
        return edgeStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeStringer(edu.uci.ics.jung.visualization.decorators.EdgeStringer)
     */
    public void setEdgeStringer(Transformer<E,String> edgeStringer) {
        this.edgeStringer = edgeStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeStrokeFunction()
     */
    public Transformer<E,Stroke> getEdgeStrokeFunction() {
        return edgeStrokeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeStrokeFunction(edu.uci.ics.jung.visualization.decorators.EdgeStrokeFunction)
     */
    public void setEdgeStrokeFunction(Transformer<E,Stroke> edgeStrokeFunction) {
        this.edgeStrokeFunction = edgeStrokeFunction;
    }

    public GraphicsDecorator getGraphicsContext() {
        return graphicsContext;
    }

    public void setGraphicsContext(GraphicsDecorator graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public int getLabelOffset() {
        return labelOffset;
    }

    public void setLabelOffset(int labelOffset) {
        this.labelOffset = labelOffset;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getParallelEdgeIndexFunction()
     */
    public ParallelEdgeIndexFunction<V, E> getParallelEdgeIndexFunction() {
        return parallelEdgeIndexFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setParallelEdgeIndexFunction(edu.uci.ics.graph.util.ParallelEdgeIndexFunction)
     */
    public void setParallelEdgeIndexFunction(
            ParallelEdgeIndexFunction<V, E> parallelEdgeIndexFunction) {
        this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getPickedEdgeState()
     */
    public PickedState<E> getPickedEdgeState() {
        return pickedEdgeState;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setPickedEdgeState(edu.uci.ics.jung.visualization.picking.PickedState)
     */
    public void setPickedEdgeState(PickedState<E> pickedEdgeState) {
        this.pickedEdgeState = pickedEdgeState;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getPickedVertexState()
     */
    public PickedState<V> getPickedVertexState() {
        return pickedVertexState;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setPickedVertexState(edu.uci.ics.jung.visualization.picking.PickedState)
     */
    public void setPickedVertexState(PickedState<V> pickedVertexState) {
        this.pickedVertexState = pickedVertexState;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getRendererPane()
     */
    public CellRendererPane getRendererPane() {
        return rendererPane;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setRendererPane(javax.swing.CellRendererPane)
     */
    public void setRendererPane(CellRendererPane rendererPane) {
        this.rendererPane = rendererPane;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getScreenDevice()
     */
    public JComponent getScreenDevice() {
        return screenDevice;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setScreenDevice(edu.uci.ics.jung.visualization.VisualizationViewer)
     */
    public void setScreenDevice(JComponent screenDevice) {
        this.screenDevice = screenDevice;
        screenDevice.add(rendererPane);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexFontFunction()
     */
    public Transformer<V,Font> getVertexFontFunction() {
        return vertexFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexFontFunction(edu.uci.ics.jung.visualization.decorators.VertexFontFunction)
     */
    public void setVertexFontFunction(Transformer<V,Font> vertexFontFunction) {
        this.vertexFontFunction = vertexFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexIconFunction()
     */
    public Transformer<V,Icon> getVertexIconFunction() {
        return vertexIconFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIconFunction(edu.uci.ics.jung.visualization.decorators.VertexIconFunction)
     */
    public void setVertexIconFunction(Transformer<V,Icon> vertexIconFunction) {
        this.vertexIconFunction = vertexIconFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexIncludePredicate()
     */
    public Predicate<Context<Graph<V,E>,V>> getVertexIncludePredicate() {
        return vertexIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIncludePredicate(org.apache.commons.collections15.Predicate)
     */
    public void setVertexIncludePredicate(Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate) {
        this.vertexIncludePredicate = vertexIncludePredicate;
        updateState(pickSupport, vertexIncludePredicate, edgeIncludePredicate);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexLabelRenderer()
     */
    public VertexLabelRenderer getVertexLabelRenderer() {
        return vertexLabelRenderer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexLabelRenderer(edu.uci.ics.jung.visualization.VertexLabelRenderer)
     */
    public void setVertexLabelRenderer(VertexLabelRenderer vertexLabelRenderer) {
        this.vertexLabelRenderer = vertexLabelRenderer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexPaintFunction()
     */
    public Transformer<V,Paint> getVertexFillPaintFunction() {
        return vertexFillPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexPaintFunction(edu.uci.ics.jung.visualization.decorators.VertexPaintFunction)
     */
    public void setVertexFillPaintFunction(Transformer<V,Paint> vertexFillPaintFunction) {
        this.vertexFillPaintFunction = vertexFillPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexPaintFunction()
     */
    public Transformer<V,Paint> getVertexDrawPaintFunction() {
        return vertexDrawPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexPaintFunction(edu.uci.ics.jung.visualization.decorators.VertexPaintFunction)
     */
    public void setVertexDrawPaintFunction(Transformer<V,Paint> vertexDrawPaintFunction) {
        this.vertexDrawPaintFunction = vertexDrawPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexShapeFunction()
     */
    public Transformer<V,Shape> getVertexShapeFunction() {
        return vertexShapeTransformer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexShapeFunction(edu.uci.ics.jung.visualization.decorators.VertexShapeFunction)
     */
    public void setVertexShapeFunction(Transformer<V,Shape> vertexShapeFunction) {
        this.vertexShapeTransformer = vertexShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexStringer()
     */
    public Transformer<V,String> getVertexStringer() {
        return vertexStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexStringer(edu.uci.ics.jung.visualization.decorators.VertexStringer)
     */
    public void setVertexStringer(Transformer<V,String> vertexStringer) {
        this.vertexStringer = vertexStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexStrokeFunction()
     */
    public Transformer<V,Stroke> getVertexStrokeFunction() {
        return vertexStrokeTransformer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexStrokeFunction(edu.uci.ics.jung.visualization.decorators.VertexStrokeFunction)
     */
    public void setVertexStrokeFunction(Transformer<V,Stroke> vertexStrokeFunction) {
        this.vertexStrokeTransformer = vertexStrokeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getViewTransformer()
     */
//    public MutableTransformer getViewTransformer() {
//        return viewTransformer;
//    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setViewTransformer(edu.uci.ics.jung.visualization.transform.MutableTransformer)
     */
//    public void setViewTransformer(MutableTransformer viewTransformer) {
//        this.viewTransformer = viewTransformer;
//    }

	/**
	 * @return the pickSupport
	 */
	public GraphElementAccessor<V, E> getPickSupport() {
		return pickSupport;
	}

	/**
	 * @param pickSupport the pickSupport to set
	 */
	public void setPickSupport(GraphElementAccessor<V, E> pickSupport) {
		this.pickSupport = pickSupport;
		updateState(pickSupport, vertexIncludePredicate, edgeIncludePredicate);
	}
	
	private void updateState(GraphElementAccessor gae, 
			Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate,
			Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate) {
		
		if(gae instanceof PredicatedGraphCollections) {
			PredicatedGraphCollections fgea =
				(PredicatedGraphCollections)gae;
			fgea.setVertexIncludePredicate(vertexIncludePredicate);
			fgea.setEdgeIncludePredicate(edgeIncludePredicate);
		}
		
	}

	/**
	 * @return the basicTransformer
	 */
	public BasicTransformer getBasicTransformer() {
		return basicTransformer;
	}

	/**
	 * @param basicTransformer the basicTransformer to set
	 */
	public void setBasicTransformer(BasicTransformer basicTransformer) {
		this.basicTransformer = basicTransformer;
	}
	
//	public GraphElementAccessor<V,E> createPredicatedShapePickSupport(VisualizationServer<V,E> vv) {
//		return new PredicatedShapePickSupport(vv);
//	}
	
//	protected class PredicatedShapePickSupport extends ShapePickSupport<V,E> {
//	
//
//	    public PredicatedShapePickSupport(VisualizationServer<V,E> vv, float pickSize) {
//	    	super(vv, pickSize);
//	    }
//	    
//	    public PredicatedShapePickSupport(float pickSize) {
//	        super(pickSize);
//	    }
//	            
//	    /**
//	     * Create an instance.
//	     * The pickSize footprint defaults to 2.
//	     */
//	    public PredicatedShapePickSupport(VisualizationServer<V,E> vv) {
//	        this(vv, 2);
//	    }
//	    
//	    /**
//	     * Create an instance.
//	     * The pickSize footprint defaults to 2.
//	     */
//	    public PredicatedShapePickSupport() {
//	        this(2);
//	    }
//
//	    
//	    protected Collection<V> getVertices(Layout<V,E> layout) {
//	    	Collection<V> unfiltered = layout.getGraph().getVertices();
//	    	Collection<V> filtered = new HashSet<V>();
//	    	for(V v : unfiltered) {
//	    		if(isRendered(new Context<V,E>(layout.getGraph(),v))) {
//	    			filtered.add(v);
//	    		}
//	    	}
//	    	return filtered;
//	    }
//	    
//	    protected Collection<E> getEdges(Layout<V,E> layout) {
//	    	Collection<E> unfiltered = layout.getGraph().getEdges();
//	    	Collection<E> filtered = new HashSet<E>();
//	    	for(E e : unfiltered) {
//	    		if(isRendered(new Context<V,E>(layout.getGraph(),e))) {
//	    			filtered.add(e);
//	    		}
//	    	}
//	    	return filtered;
//	    }
//	    
//		private boolean isRendered(Context<V,E> context) {
//			return vertexIncludePredicate == null || vertexIncludePredicate.evaluate(context);
//		}
//		
//		private boolean isRendered(Context<V,E> context) {
//			Graph<V,E> g = context.graph;
//			E e = context.edge;
//			boolean edgeTest = edgeIncludePredicate == null || edgeIncludePredicate.evaluate(context);
//			Pair<V> endpoints = g.getEndpoints(e);
//			V v1 = endpoints.getFirst();
//			V v2 = endpoints.getSecond();
//			boolean endpointsTest = vertexIncludePredicate == null ||
//				(vertexIncludePredicate.evaluate(new Context<V,E>(g,v1)) && 
//						vertexIncludePredicate.evaluate(new Context<V,E>(g,v2)));
//			return edgeTest && endpointsTest;
//		}
//
//
//	}

//    class PickSupportDecorator implements GraphElementAccessor<V,E> {
//    	
//		/**
//		 * @param layout
//		 * @param x
//		 * @param y
//		 * @return
//		 * @see edu.uci.ics.jung.algorithms.layout.GraphElementAccessor#getEdge(edu.uci.ics.jung.algorithms.layout.Layout, double, double)
//		 */
//		public E getEdge(Layout<V, E> layout, double x, double y) {
//			E e =  pickSupport.getEdge(layout, x, y);
//			// ensure e is being rendered:
//			return isRendered(new Context<V,E>(layout.getGraph(),e)) ?
//					e : null;
//		}
//
//		/**
//		 * @param layout
//		 * @param x
//		 * @param y
//		 * @return
//		 * @see edu.uci.ics.jung.algorithms.layout.GraphElementAccessor#getVertex(edu.uci.ics.jung.algorithms.layout.Layout, double, double)
//		 */
//		public V getVertex(Layout<V, E> layout, double x, double y) {
//			V v = pickSupport.getVertex(layout, x, y);
//			return isRendered(new Context<V,E>(layout.getGraph(),v)) ?
//					v : null;
//		}
//		
//		private boolean isRendered(Context<V,E> context) {
//			return vertexIncludePredicate == null || vertexIncludePredicate.evaluate(context);
//		}
//		
//		private boolean isRendered(Context<V,E> context) {
//			Graph<V,E> g = context.graph;
//			E e = context.edge;
//			boolean edgeTest = edgeIncludePredicate == null || edgeIncludePredicate.evaluate(context);
//			Pair<V> endpoints = g.getEndpoints(e);
//			V v1 = endpoints.getFirst();
//			V v2 = endpoints.getSecond();
//			boolean endpointsTest = vertexIncludePredicate == null ||
//				(vertexIncludePredicate.evaluate(new Context<V,E>(g,v1)) && 
//						vertexIncludePredicate.evaluate(new Context<V,E>(g,v2)));
//			return edgeTest && endpointsTest;
//		}
//    }
//
}


