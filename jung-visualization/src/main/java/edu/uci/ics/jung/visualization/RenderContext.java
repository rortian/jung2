package edu.uci.ics.jung.visualization;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.predicates.AbstractGraphPredicate;
import edu.uci.ics.graph.predicates.GraphPredicate;
import edu.uci.ics.graph.util.ParallelEdgeIndexFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeContext;
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

    Transformer<EdgeContext<V,E>,Shape> getEdgeArrowFunction();

    void setEdgeArrowFunction(Transformer<EdgeContext<V,E>,Shape> edgeArrowFunction);

    GraphPredicate<V,E> getEdgeArrowPredicate() ;

    void setEdgeArrowPredicate(GraphPredicate<V,E> edgeArrowPredicate);

    Transformer<E,Font> getEdgeFontFunction();

    void setEdgeFontFunction(Transformer<E,Font> edgeFontFunction);

    GraphPredicate<V,E> getEdgeIncludePredicate();

    void setEdgeIncludePredicate(GraphPredicate<V,E> edgeIncludePredicate);

    Transformer<EdgeContext<V,E>,Number> getEdgeLabelClosenessFunction();

    void setEdgeLabelClosenessFunction(
    		Transformer<EdgeContext<V,E>,Number> edgeLabelClosenessFunction);

    EdgeLabelRenderer getEdgeLabelRenderer();

    void setEdgeLabelRenderer(EdgeLabelRenderer edgeLabelRenderer);

    Transformer<E,Paint> getEdgeFillPaintFunction();

    void setEdgeFillPaintFunction(Transformer<E,Paint> edgePaintFunction);

    Transformer<E,Paint> getEdgeDrawPaintFunction();

    void setEdgeDrawPaintFunction(Transformer<E,Paint> edgeDrawPaintFunction);

    Transformer<EdgeContext<V,E>,Shape> getEdgeShapeFunction();

    void setEdgeShapeFunction(Transformer<EdgeContext<V,E>,Shape> edgeShapeFunction);

    Transformer<E,String> getEdgeStringer();

    void setEdgeStringer(Transformer<E,String> edgeStringer);

    Transformer<E,Stroke> getEdgeStrokeFunction();

    void setEdgeStrokeFunction(Transformer<E,Stroke> edgeStrokeFunction);
    
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

    Transformer<V,Font> getVertexFontFunction();

    void setVertexFontFunction(Transformer<V,Font> vertexFontFunction);

    Transformer<V,Icon> getVertexIconFunction();

    void setVertexIconFunction(Transformer<V,Icon> vertexIconFunction);

    GraphPredicate<V,E> getVertexIncludePredicate();

    void setVertexIncludePredicate(GraphPredicate<V,E> vertexIncludePredicate);

    VertexLabelRenderer getVertexLabelRenderer();

    void setVertexLabelRenderer(VertexLabelRenderer vertexLabelRenderer);

    Transformer<V,Paint> getVertexFillPaintFunction();

    void setVertexFillPaintFunction(Transformer<V,Paint> vertexFillPaintFunction);

    Transformer<V,Paint> getVertexDrawPaintFunction();

    void setVertexDrawPaintFunction(Transformer<V,Paint> vertexDrawPaintFunction);

    Transformer<V,Shape> getVertexShapeFunction();

    void setVertexShapeFunction(Transformer<V,Shape> vertexShapeFunction);

    Transformer<V,String> getVertexStringer();

    void setVertexStringer(Transformer<V,String> vertexStringer);

    Transformer<V,Stroke> getVertexStrokeFunction();

    void setVertexStrokeFunction(Transformer<V,Stroke> vertexStrokeFunction);

    MutableTransformer getViewTransformer();

    void setViewTransformer(MutableTransformer viewTransformer);
    
    class DirectedEdgeArrowPredicate<V,E> extends AbstractGraphPredicate<V,E> {

        @Override
        public boolean evaluateEdge(Graph<V, E> graph, E edge) {
            return graph.isDirected(edge);
        }
        
    }
    
    class UndirectedEdgeArrowPredicate<V,E> extends AbstractGraphPredicate<V,E> {
        @Override
        public boolean evaluateEdge(Graph<V, E> graph, E edge) {
            return graph.isDirected(edge) == false;
        }
        
    }

}