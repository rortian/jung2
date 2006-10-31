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

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.graph.predicates.GraphPredicate;
import edu.uci.ics.graph.predicates.TrueGraphPredicate;
import edu.uci.ics.graph.util.DefaultParallelEdgeIndexFunction;
import edu.uci.ics.graph.util.ParallelEdgeIndexFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValue;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeContext;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;


/**
 */
public class PluggableRenderContext<V, E> implements RenderContext<V, E> {
    
	protected float arrowPlacementTolerance = 1;
    protected GraphPredicate<V,E> vertexIncludePredicate = new TrueGraphPredicate<V,E>();
    protected Transformer<V,Stroke> vertexStrokeFunction = 
    	new ConstantTransformer(new BasicStroke(1.0f));
    
    protected Transformer<V,Shape> vertexShapeFunction = 
//        new ChainedTransformer(new Transformer[]{
        		new ConstantTransformer(
//        }
        		new Ellipse2D.Float(-10,-10,20,20));
//    , 
//        		new Transformer<Shape,Shape>(){
//
//					public Shape transform(Shape input) {
//						// scale to constant size
//						return input;
//					}}, 
//        		new Transformer<Shape,Shape>() {
//
//					public Shape transform(Shape input) {
//						// scale with aspect ratio
//						return input;
//					}}});
//                new ConstantVertexSizeFunction<V>(20),
//                new ConstantVertexAspectRatioFunction<V>(1.0f));
    protected Transformer<V,String> vertexStringer = new ConstantTransformer(null);
    protected Transformer<V,Icon> vertexIconFunction;
    protected Transformer<V,Font> vertexFontFunction = 
        new ConstantTransformer(new Font("Helvetica", Font.PLAIN, 12));
    protected boolean centerVertexLabel = false;
    
    protected Transformer<V,Paint> vertexDrawPaintFunction = new ConstantTransformer(Color.BLACK);
    protected Transformer<V,Paint> vertexFillPaintFunction = new ConstantTransformer(Color.RED);
    
    protected Transformer<E,String> edgeStringer = new ConstantTransformer(null);
    protected Transformer<E,Stroke> edgeStrokeFunction = new ConstantTransformer(new BasicStroke(1.0f));
    
    protected Transformer<EdgeContext<V,E>,Shape> edgeArrowFunction = 
        new DirectionalEdgeArrowFunction<V,E>(10, 8, 4);
    
    protected GraphPredicate<V,E> edgeArrowPredicate = new DirectedEdgeArrowPredicate<V,E>();
    protected GraphPredicate<V,E> edgeIncludePredicate = new TrueGraphPredicate<V,E>();
    protected Transformer<E,Font> edgeFontFunction =
        new ConstantTransformer(new Font("Helvetica", Font.PLAIN, 12));
    protected Transformer<EdgeContext<V,E>,Number> edgeLabelClosenessFunction = 
        new ConstantDirectionalEdgeValue<V,E>(0.5, 0.65);
    protected Transformer<EdgeContext<V,E>,Shape> edgeShapeFunction = 
        new EdgeShape.QuadCurve<V,E>();
    protected Transformer<E,Paint> edgeFillPaintFunction =
        new ConstantTransformer(null);
    protected Transformer<E,Paint> edgeDrawPaintFunction =
        new ConstantTransformer(Color.black);
    protected ParallelEdgeIndexFunction<V,E> parallelEdgeIndexFunction = 
        DefaultParallelEdgeIndexFunction.<V,E>getInstance();
    protected MutableTransformer viewTransformer = new MutableAffineTransformer();
    
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
     * @see edu.uci.ics.jung.visualization.RenderContext#isCenterVertexLabel()
     */
    public boolean isCenterVertexLabel() {
        return centerVertexLabel;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setCenterVertexLabel(boolean)
     */
    public void setCenterVertexLabel(boolean centerVertexLabel) {
        this.centerVertexLabel = centerVertexLabel;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeArrowFunction()
     */
    public Transformer<EdgeContext<V,E>,Shape> getEdgeArrowFunction() {
        return edgeArrowFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeArrowFunction(edu.uci.ics.jung.visualization.decorators.EdgeArrowFunction)
     */
    public void setEdgeArrowFunction(Transformer<EdgeContext<V,E>,Shape> edgeArrowFunction) {
        this.edgeArrowFunction = edgeArrowFunction;
    }

    public GraphPredicate<V,E> getEdgeArrowPredicate() {
        return edgeArrowPredicate;
    }

    public void setEdgeArrowPredicate(GraphPredicate<V,E> edgeArrowPredicate) {
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
    public GraphPredicate<V,E> getEdgeIncludePredicate() {
        return edgeIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeIncludePredicate(org.apache.commons.collections15.Predicate)
     */
    public void setEdgeIncludePredicate(GraphPredicate<V,E> edgeIncludePredicate) {
        this.edgeIncludePredicate = edgeIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelClosenessFunction()
     */
    public Transformer<EdgeContext<V,E>,Number> getEdgeLabelClosenessFunction() {
        return edgeLabelClosenessFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelClosenessFunction(edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue)
     */
    public void setEdgeLabelClosenessFunction(
    		Transformer<EdgeContext<V,E>,Number> edgeLabelClosenessFunction) {
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
    public Transformer<EdgeContext<V,E>,Shape> getEdgeShapeFunction() {
        return edgeShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeShapeFunction(edu.uci.ics.jung.visualization.decorators.EdgeShapeFunction)
     */
    public void setEdgeShapeFunction(Transformer<EdgeContext<V,E>,Shape> edgeShapeFunction) {
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
    public GraphPredicate<V,E> getVertexIncludePredicate() {
        return vertexIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIncludePredicate(org.apache.commons.collections15.Predicate)
     */
    public void setVertexIncludePredicate(GraphPredicate<V,E> vertexIncludePredicate) {
        this.vertexIncludePredicate = vertexIncludePredicate;
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
        return vertexShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexShapeFunction(edu.uci.ics.jung.visualization.decorators.VertexShapeFunction)
     */
    public void setVertexShapeFunction(Transformer<V,Shape> vertexShapeFunction) {
        this.vertexShapeFunction = vertexShapeFunction;
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
        return vertexStrokeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexStrokeFunction(edu.uci.ics.jung.visualization.decorators.VertexStrokeFunction)
     */
    public void setVertexStrokeFunction(Transformer<V,Stroke> vertexStrokeFunction) {
        this.vertexStrokeFunction = vertexStrokeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getViewTransformer()
     */
    public MutableTransformer getViewTransformer() {
        return viewTransformer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setViewTransformer(edu.uci.ics.jung.visualization.transform.MutableTransformer)
     */
    public void setViewTransformer(MutableTransformer viewTransformer) {
        this.viewTransformer = viewTransformer;
    }
}


