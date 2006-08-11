package edu.uci.ics.jung.visualization;

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.graph.util.ParallelEdgeIndexFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeArrowFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeFontFunction;
import edu.uci.ics.jung.visualization.decorators.EdgePaintFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeShapeFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeStringer;
import edu.uci.ics.jung.visualization.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue;
import edu.uci.ics.jung.visualization.decorators.VertexFontFunction;
import edu.uci.ics.jung.visualization.decorators.VertexIconFunction;
import edu.uci.ics.jung.visualization.decorators.VertexPaintFunction;
import edu.uci.ics.jung.visualization.decorators.VertexShapeFunction;
import edu.uci.ics.jung.visualization.decorators.VertexStringer;
import edu.uci.ics.jung.visualization.decorators.VertexStrokeFunction;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public interface RenderContext<V, E> {

    float[] dotting = {1.0f, 3.0f};
    float[] dashing = {5.0f};

    /**
     * A stroke for a dotted line: 1 pixel width, round caps, round joins, and an 
     * array of {1.0f, 3.0f}.
     */
    Stroke DOTTED = new BasicStroke(1.0f,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, dotting, 0f);

    /**
     * A stroke for a dashed line: 1 pixel width, square caps, beveled joins, and an
     * array of {5.0f}.
     */
    Stroke DASHED = new BasicStroke(1.0f,
            BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, dashing, 0f);

    /**
     * Specifies the offset for the edge labels.
     */
    public static final int LABEL_OFFSET = 10;

    int getLabelOffset();
    
    void setLabelOffset(int labelOffset);
    
    float getArrowPlacementTolerance();

    void setArrowPlacementTolerance(float arrow_placement_tolerance);

    boolean isCenterVertexLabel();

    void setCenterVertexLabel(boolean centerVertexLabel);

    EdgeArrowFunction<V, E> getEdgeArrowFunction();

    void setEdgeArrowFunction(EdgeArrowFunction<V, E> edgeArrowFunction);

    Predicate<E> getEdgeArrowPredicate() ;

    void setEdgeArrowPredicate(Predicate<E> edgeArrowPredicate);

    EdgeFontFunction<E> getEdgeFontFunction();

    void setEdgeFontFunction(EdgeFontFunction<E> edgeFontFunction);

    Predicate<E> getEdgeIncludePredicate();

    void setEdgeIncludePredicate(Predicate<E> edgeIncludePredicate);

    NumberDirectionalEdgeValue<V, E> getEdgeLabelClosenessFunction();

    void setEdgeLabelClosenessFunction(
            NumberDirectionalEdgeValue<V, E> edgeLabelClosenessFunction);

    EdgeLabelRenderer getEdgeLabelRenderer();

    void setEdgeLabelRenderer(EdgeLabelRenderer edgeLabelRenderer);

    EdgePaintFunction<E> getEdgePaintFunction();

    void setEdgePaintFunction(EdgePaintFunction<E> edgePaintFunction);

    EdgeShapeFunction<V, E> getEdgeShapeFunction();

    void setEdgeShapeFunction(EdgeShapeFunction<V, E> edgeShapeFunction);

    EdgeStringer<E> getEdgeStringer();

    void setEdgeStringer(EdgeStringer<E> edgeStringer);

    EdgeStrokeFunction<E> getEdgeStrokeFunction();

    void setEdgeStrokeFunction(EdgeStrokeFunction<E> edgeStrokeFunction);
    
    GraphicsDecorator getGraphicsContext();
    
    void setGraphicsContext(GraphicsDecorator graphicsContext);

    ParallelEdgeIndexFunction<V, E> getParallelEdgeIndexFunction();

    void setParallelEdgeIndexFunction(
            ParallelEdgeIndexFunction<V, E> parallelEdgeIndexFunction);

    PickedState<E> getPickedEdgeState();

    void setPickedEdgeState(PickedState<E> pickedEdgeState);

    PickedState<V> getPickedVertexState();

    void setPickedVertexState(PickedState<V> pickedVertexState);

    CellRendererPane getRendererPane();

    void setRendererPane(CellRendererPane rendererPane);

    JComponent getScreenDevice();

    void setScreenDevice(JComponent screenDevice);

    VertexFontFunction<V> getVertexFontFunction();

    void setVertexFontFunction(VertexFontFunction<V> vertexFontFunction);

    VertexIconFunction<V> getVertexIconFunction();

    void setVertexIconFunction(VertexIconFunction<V> vertexIconFunction);

    Predicate<V> getVertexIncludePredicate();

    void setVertexIncludePredicate(Predicate<V> vertexIncludePredicate);

    VertexLabelRenderer getVertexLabelRenderer();

    void setVertexLabelRenderer(VertexLabelRenderer vertexLabelRenderer);

    VertexPaintFunction<V> getVertexPaintFunction();

    void setVertexPaintFunction(VertexPaintFunction<V> vertexPaintFunction);

    VertexShapeFunction<V> getVertexShapeFunction();

    void setVertexShapeFunction(VertexShapeFunction<V> vertexShapeFunction);

    VertexStringer<V> getVertexStringer();

    void setVertexStringer(VertexStringer<V> vertexStringer);

    VertexStrokeFunction<V> getVertexStrokeFunction();

    void setVertexStrokeFunction(VertexStrokeFunction<V> vertexStrokeFunction);

    MutableTransformer getViewTransformer();

    void setViewTransformer(MutableTransformer viewTransformer);

}