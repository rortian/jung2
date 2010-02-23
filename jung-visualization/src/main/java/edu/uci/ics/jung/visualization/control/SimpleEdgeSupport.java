package edu.uci.ics.jung.visualization.control;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class SimpleEdgeSupport<V,E> implements EdgeSupport<V,E> {

	protected Point2D down;
	protected EdgeEffects<V,E> edgeEffects;
	protected EdgeType edgeType;
	protected Factory<E> edgeFactory;
	protected V startVertex;
	
	public SimpleEdgeSupport(Factory<E> edgeFactory) {
		this.edgeFactory = edgeFactory;
		this.edgeEffects = new CubicCurveEdgeEffects<V,E>();
	}
	
	public void startEdgeCreate(BasicVisualizationServer<V, E> vv,
			V startVertex, Point2D startPoint, EdgeType edgeType) {
		this.startVertex = startVertex;
		this.down = startPoint;
		this.edgeType = edgeType;
		this.edgeEffects.startEdgeEffects(vv, startPoint, startPoint);
		if(edgeType == EdgeType.DIRECTED) {
			this.edgeEffects.startArrowEffects(vv, startPoint, startPoint);
		}
		vv.repaint();
	}

	public void midEdgeCreate(BasicVisualizationServer<V, E> vv,
			Point2D midPoint) {
		if(startVertex != null) {
			this.edgeEffects.midEdgeEffects(vv, down, midPoint);
			if(this.edgeType == EdgeType.DIRECTED) {
				this.edgeEffects.midArrowEffects(vv, down, midPoint);
			}
			vv.repaint();
		}
	}

	public void endEdgeCreate(BasicVisualizationServer<V, E> vv, V endVertex) {
		if(startVertex != null && endVertex != null) {
			Graph<V,E> graph = vv.getGraphLayout().getGraph();
			graph.addEdge(edgeFactory.create(), startVertex, endVertex, edgeType);
			vv.repaint();
		}
		startVertex = null;
		edgeType = EdgeType.UNDIRECTED;
		edgeEffects.endEdgeEffects(vv);
		edgeEffects.endArrowEffects(vv);
	}

	public EdgeEffects<V, E> getEdgeEffects() {
		return edgeEffects;
	}

	public void setEdgeEffects(EdgeEffects<V, E> edgeEffects) {
		this.edgeEffects = edgeEffects;
	}

	public EdgeType getEdgeType() {
		return edgeType;
	}

	public void setEdgeType(EdgeType edgeType) {
		this.edgeType = edgeType;
	}

	public Factory<E> getEdgeFactory() {
		return edgeFactory;
	}

	public void setEdgeFactory(Factory<E> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}

}
