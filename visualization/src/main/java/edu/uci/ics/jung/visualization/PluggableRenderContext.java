/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization;

import java.awt.Color;
import java.awt.Font;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.functors.TruePredicate;

import edu.uci.ics.graph.util.DefaultParallelEdgeIndexFunction;
import edu.uci.ics.graph.util.ParallelEdgeIndexFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValue;
import edu.uci.ics.jung.visualization.decorators.ConstantEdgeFontFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantEdgePaintFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantEdgeStringer;
import edu.uci.ics.jung.visualization.decorators.ConstantEdgeStrokeFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexFontFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexPaintFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexStringer;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexStrokeFunction;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeArrowFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeFontFunction;
import edu.uci.ics.jung.visualization.decorators.EdgePaintFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EdgeShapeFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeStringer;
import edu.uci.ics.jung.visualization.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue;
import edu.uci.ics.jung.visualization.decorators.VertexFontFunction;
import edu.uci.ics.jung.visualization.decorators.VertexIconFunction;
import edu.uci.ics.jung.visualization.decorators.VertexPaintFunction;
import edu.uci.ics.jung.visualization.decorators.VertexShapeFunction;
import edu.uci.ics.jung.visualization.decorators.VertexStringer;
import edu.uci.ics.jung.visualization.decorators.VertexStrokeFunction;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;


/**
 */
public class PluggableRenderContext<V, E> implements RenderContext<V, E> {
    
	protected float arrowPlacementTolerance = 1;
    protected Predicate<V> vertexIncludePredicate = TruePredicate.getInstance();
    protected VertexStrokeFunction<V> vertexStrokeFunction =
        new ConstantVertexStrokeFunction<V>(1.0f);
    protected VertexShapeFunction<V> vertexShapeFunction = 
        new EllipseVertexShapeFunction<V>(
                new ConstantVertexSizeFunction<V>(20),
                new ConstantVertexAspectRatioFunction<V>(1.0f));
    protected VertexStringer<V> vertexStringer = 
        new ConstantVertexStringer<V>(null);
    protected VertexIconFunction<V> vertexIconFunction;
    protected VertexFontFunction<V> vertexFontFunction = 
        new ConstantVertexFontFunction<V>(new Font("Helvetica", Font.PLAIN, 12));
    protected boolean centerVertexLabel = false;
    
    protected VertexPaintFunction<V> vertexPaintFunction =
        new ConstantVertexPaintFunction<V>(Color.BLACK, Color.RED);
    
    protected EdgeStringer<E> edgeStringer = 
        new ConstantEdgeStringer<E>(null);
    protected EdgeStrokeFunction<E> edgeStrokeFunction = 
        new ConstantEdgeStrokeFunction<E>(1.0f);
    protected EdgeArrowFunction<V,E> edgeArrowFunction = 
        new DirectionalEdgeArrowFunction<V,E>(10, 8, 4);    
//    protected Predicate edgeArrowPredicate = InstanceofPredicate.getInstance(DirectedEdge.class);
    protected Predicate<E> edgeIncludePredicate = TruePredicate.getInstance();
    protected EdgeFontFunction<E> edgeFontFunction =
        new ConstantEdgeFontFunction<E>(new Font("Helvetica", Font.PLAIN, 12));
    protected NumberDirectionalEdgeValue<V,E> edgeLabelClosenessFunction = 
        new ConstantDirectionalEdgeValue<V,E>(0.5, 0.65);
    protected EdgeShapeFunction<V,E> edgeShapeFunction = 
        new EdgeShape.QuadCurve<V,E>();
    protected EdgePaintFunction<E> edgePaintFunction =
        new ConstantEdgePaintFunction<E>(Color.black, null);
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
    public EdgeArrowFunction<V, E> getEdgeArrowFunction() {
        return edgeArrowFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeArrowFunction(edu.uci.ics.jung.visualization.decorators.EdgeArrowFunction)
     */
    public void setEdgeArrowFunction(EdgeArrowFunction<V, E> edgeArrowFunction) {
        this.edgeArrowFunction = edgeArrowFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeFontFunction()
     */
    public EdgeFontFunction<E> getEdgeFontFunction() {
        return edgeFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeFontFunction(edu.uci.ics.jung.visualization.decorators.EdgeFontFunction)
     */
    public void setEdgeFontFunction(EdgeFontFunction<E> edgeFontFunction) {
        this.edgeFontFunction = edgeFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeIncludePredicate()
     */
    public Predicate<E> getEdgeIncludePredicate() {
        return edgeIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeIncludePredicate(org.apache.commons.collections15.Predicate)
     */
    public void setEdgeIncludePredicate(Predicate<E> edgeIncludePredicate) {
        this.edgeIncludePredicate = edgeIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelClosenessFunction()
     */
    public NumberDirectionalEdgeValue<V, E> getEdgeLabelClosenessFunction() {
        return edgeLabelClosenessFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelClosenessFunction(edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue)
     */
    public void setEdgeLabelClosenessFunction(
            NumberDirectionalEdgeValue<V, E> edgeLabelClosenessFunction) {
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
    public EdgePaintFunction<E> getEdgePaintFunction() {
        return edgePaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgePaintFunction(edu.uci.ics.jung.visualization.decorators.EdgePaintFunction)
     */
    public void setEdgePaintFunction(EdgePaintFunction<E> edgePaintFunction) {
        this.edgePaintFunction = edgePaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeShapeFunction()
     */
    public EdgeShapeFunction<V, E> getEdgeShapeFunction() {
        return edgeShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeShapeFunction(edu.uci.ics.jung.visualization.decorators.EdgeShapeFunction)
     */
    public void setEdgeShapeFunction(EdgeShapeFunction<V, E> edgeShapeFunction) {
        this.edgeShapeFunction = edgeShapeFunction;
        if(edgeShapeFunction instanceof EdgeShape.ParallelRendering) {
            ((EdgeShape.ParallelRendering<V,E>)edgeShapeFunction).setParallelEdgeIndexFunction(this.parallelEdgeIndexFunction);
        }
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeStringer()
     */
    public EdgeStringer<E> getEdgeStringer() {
        return edgeStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeStringer(edu.uci.ics.jung.visualization.decorators.EdgeStringer)
     */
    public void setEdgeStringer(EdgeStringer<E> edgeStringer) {
        this.edgeStringer = edgeStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeStrokeFunction()
     */
    public EdgeStrokeFunction<E> getEdgeStrokeFunction() {
        return edgeStrokeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeStrokeFunction(edu.uci.ics.jung.visualization.decorators.EdgeStrokeFunction)
     */
    public void setEdgeStrokeFunction(EdgeStrokeFunction<E> edgeStrokeFunction) {
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
    public VertexFontFunction<V> getVertexFontFunction() {
        return vertexFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexFontFunction(edu.uci.ics.jung.visualization.decorators.VertexFontFunction)
     */
    public void setVertexFontFunction(VertexFontFunction<V> vertexFontFunction) {
        this.vertexFontFunction = vertexFontFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexIconFunction()
     */
    public VertexIconFunction<V> getVertexIconFunction() {
        return vertexIconFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIconFunction(edu.uci.ics.jung.visualization.decorators.VertexIconFunction)
     */
    public void setVertexIconFunction(VertexIconFunction<V> vertexIconFunction) {
        this.vertexIconFunction = vertexIconFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexIncludePredicate()
     */
    public Predicate<V> getVertexIncludePredicate() {
        return vertexIncludePredicate;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIncludePredicate(org.apache.commons.collections15.Predicate)
     */
    public void setVertexIncludePredicate(Predicate<V> vertexIncludePredicate) {
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
    public VertexPaintFunction<V> getVertexPaintFunction() {
        return vertexPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexPaintFunction(edu.uci.ics.jung.visualization.decorators.VertexPaintFunction)
     */
    public void setVertexPaintFunction(VertexPaintFunction<V> vertexPaintFunction) {
        this.vertexPaintFunction = vertexPaintFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexShapeFunction()
     */
    public VertexShapeFunction<V> getVertexShapeFunction() {
        return vertexShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexShapeFunction(edu.uci.ics.jung.visualization.decorators.VertexShapeFunction)
     */
    public void setVertexShapeFunction(VertexShapeFunction<V> vertexShapeFunction) {
        this.vertexShapeFunction = vertexShapeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexStringer()
     */
    public VertexStringer<V> getVertexStringer() {
        return vertexStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexStringer(edu.uci.ics.jung.visualization.decorators.VertexStringer)
     */
    public void setVertexStringer(VertexStringer<V> vertexStringer) {
        this.vertexStringer = vertexStringer;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#getVertexStrokeFunction()
     */
    public VertexStrokeFunction<V> getVertexStrokeFunction() {
        return vertexStrokeFunction;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.RenderContext#setVertexStrokeFunction(edu.uci.ics.jung.visualization.decorators.VertexStrokeFunction)
     */
    public void setVertexStrokeFunction(VertexStrokeFunction<V> vertexStrokeFunction) {
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