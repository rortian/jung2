package edu.uci.ics.jung.visualization.control;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public interface EdgeEffects<V, E> {

	void startEdgeEffects(Point2D down, Point2D out,
			BasicVisualizationServer<V, E> vv);

	void endEdgeEffects(BasicVisualizationServer<V, E> vv);

	void startArrowEffects(Point2D down, Point2D out,
			BasicVisualizationServer<V, E> vv);

	void endArrowEffects(BasicVisualizationServer<V, E> vv);

	/**
	 * code lifted from PluggableRenderer to move an edge shape into an
	 * arbitrary position
	 */
	void transformEdgeShape(Point2D down, Point2D out);

	void transformArrowShape(Point2D down, Point2D out);

}