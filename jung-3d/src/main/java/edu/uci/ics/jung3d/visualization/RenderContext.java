package edu.uci.ics.jung3d.visualization;

import javax.media.j3d.Appearance;
import javax.media.j3d.Node;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.jung.visualization.picking.PickedState;

public interface RenderContext<V, E> {

    Transformer<E,Appearance> getEdgeAppearanceTransformer();

    void setEdgeAppearanceTransformer(Transformer<E,Appearance> edgeAppearanceTransformer);

    Transformer<Context<Graph<V,E>,E>,Node> getEdgeShapeTransformer();

    void setEdgeShapeTransformer(Transformer<Context<Graph<V,E>,E>,Node> edgeShapeTransformer);

    PickedState<E> getPickedEdgeState();

    void setPickedEdgeState(PickedState<E> pickedEdgeState);

    PickedState<V> getPickedVertexState();

    void setPickedVertexState(PickedState<V> pickedVertexState);

    Transformer<V,Appearance> getVertexAppearanceTransformer();

    void setVertexAppearanceTransformer(Transformer<V,Appearance> vertexAppearanceTransformer);

    Transformer<V,Node> getVertexShapeTransformer();

    void setVertexShapeTransformer(Transformer<V,Node> vertexShapeTransformer);

    Transformer<V,String> getVertexStringer();

    void setVertexStringer(Transformer<V,String> vertexStringer);

}