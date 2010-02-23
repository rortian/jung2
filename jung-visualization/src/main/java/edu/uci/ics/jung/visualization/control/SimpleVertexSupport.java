package edu.uci.ics.jung.visualization.control;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

/** 
 * sample implementation showing how to use the VertexSupport interface member of the
 * EditingGraphMousePlugin.
 * override midVertexCreate and endVertexCreate for more elaborate implementations
 * @author tanelso
 *
 * @param <V>
 */
public class SimpleVertexSupport<V,E> implements VertexSupport<V,E> {

	protected Factory<V> vertexFactory;
	
	public SimpleVertexSupport(Factory<V> vertexFactory) {
		this.vertexFactory = vertexFactory;
	}
	
	public void startVertexCreate(BasicVisualizationServer<V, E> vv,
			Point2D point) {
		V newVertex = vertexFactory.create();
		Layout<V,E> layout = vv.getGraphLayout();
		Graph<V,E> graph = layout.getGraph();
		graph.addVertex(newVertex);
		layout.setLocation(newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(point));
		vv.repaint();
	}

	public void midVertexCreate(BasicVisualizationServer<V, E> vv,
			Point2D point) {
		// noop
	}

	public void endVertexCreate(BasicVisualizationServer<V, E> vv,
			Point2D point) {
		
		//noop
	}

	public Factory<V> getVertexFactory() {
		return vertexFactory;
	}

	public void setVertexFactory(Factory<V> vertexFactory) {
		this.vertexFactory = vertexFactory;
	}

}
