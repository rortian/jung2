package edu.uci.ics.jung3d.visualization;

import javax.media.j3d.Appearance;
import javax.media.j3d.Node;

import org.apache.commons.collections15.Transformer;

import com.sun.j3d.utils.geometry.Primitive;

import edu.uci.ics.jung.visualization.decorators.EdgeContext;
import edu.uci.ics.jung.visualization.picking.PickedState;

public interface RenderContext<V, E> {

    Transformer<E,Appearance> getEdgeAppearanceTransformer();

    void setEdgeAppearanceTransformer(Transformer<E,Appearance> edgeAppearanceTransformer);

    Transformer<EdgeContext<V,E>,Node> getEdgeShapeTransformer();

    void setEdgeShapeTransformer(Transformer<EdgeContext<V,E>,Node> edgeShapeTransformer);

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