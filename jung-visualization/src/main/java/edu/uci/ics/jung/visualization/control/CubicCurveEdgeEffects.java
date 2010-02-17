/**
 * 
 */
package edu.uci.ics.jung.visualization.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.util.ArrowFactory;

public class CubicCurveEdgeEffects<V, E> implements EdgeEffects<V, E> {

	protected CubicCurve2D rawEdgeShape = new CubicCurve2D.Float();
	protected Shape edgeShape;
	protected Shape rawArrowShape;
	protected Shape arrowShape;
	protected VisualizationServer.Paintable edgePaintable;
	protected VisualizationServer.Paintable arrowPaintable;

	public CubicCurveEdgeEffects() {
		rawEdgeShape.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50, 1.0f, 0.0f);
		rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
		edgePaintable = new EdgePaintable();
		arrowPaintable = new ArrowPaintable();

	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.EdgeEffects#startEdgeEffects(java.awt.geom.Point2D, java.awt.geom.Point2D, edu.uci.ics.jung.visualization.BasicVisualizationServer)
	 */
	public void startEdgeEffects(Point2D down, Point2D out,
			BasicVisualizationServer<V, E> vv) {
		transformEdgeShape(down, out);
		vv.addPostRenderPaintable(edgePaintable);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.EdgeEffects#endEdgeEffects(edu.uci.ics.jung.visualization.BasicVisualizationServer)
	 */
	public void endEdgeEffects(BasicVisualizationServer<V, E> vv) {
		vv.removePostRenderPaintable(edgePaintable);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.EdgeEffects#startArrowEffects(java.awt.geom.Point2D, java.awt.geom.Point2D, edu.uci.ics.jung.visualization.BasicVisualizationServer)
	 */
	public void startArrowEffects(Point2D down, Point2D out,
			BasicVisualizationServer<V, E> vv) {
		transformArrowShape(down, out);
		vv.addPostRenderPaintable(arrowPaintable);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.EdgeEffects#endArrowEffects(edu.uci.ics.jung.visualization.BasicVisualizationServer)
	 */
	public void endArrowEffects(BasicVisualizationServer<V, E> vv) {
		vv.removePostRenderPaintable(arrowPaintable);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.EdgeEffects#transformEdgeShape(java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public void transformEdgeShape(Point2D down, Point2D out) {
		float x1 = (float) down.getX();
		float y1 = (float) down.getY();
		float x2 = (float) out.getX();
		float y2 = (float) out.getY();

		AffineTransform xform = AffineTransform
				.getTranslateInstance(x1, y1);

		float dx = x2 - x1;
		float dy = y2 - y1;
		float thetaRadians = (float) Math.atan2(dy, dx);
		xform.rotate(thetaRadians);
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		xform.scale(dist / rawEdgeShape.getBounds().getWidth(), 1.0);
		edgeShape = xform.createTransformedShape(rawEdgeShape);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.EdgeEffects#transformArrowShape(java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public void transformArrowShape(Point2D down, Point2D out) {
		float x1 = (float) down.getX();
		float y1 = (float) down.getY();
		float x2 = (float) out.getX();
		float y2 = (float) out.getY();

		AffineTransform xform = AffineTransform
				.getTranslateInstance(x2, y2);

		float dx = x2 - x1;
		float dy = y2 - y1;
		float thetaRadians = (float) Math.atan2(dy, dx);
		xform.rotate(thetaRadians);
		arrowShape = xform.createTransformedShape(rawArrowShape);
	}

	/**
	 * Used for the edge creation visual effect during mouse drag
	 */
	class EdgePaintable implements VisualizationServer.Paintable {

		public void paint(Graphics g) {
			if (edgeShape != null) {
				Color oldColor = g.getColor();
				g.setColor(Color.black);
				((Graphics2D) g).draw(edgeShape);
				g.setColor(oldColor);
			}
		}

		public boolean useTransform() {
			return false;
		}
	}

	/**
	 * Used for the directed edge creation visual effect during mouse drag
	 */
	class ArrowPaintable implements VisualizationServer.Paintable {

		public void paint(Graphics g) {
			if (arrowShape != null) {
				Color oldColor = g.getColor();
				g.setColor(Color.black);
				((Graphics2D) g).fill(arrowShape);
				g.setColor(oldColor);
			}
		}

		public boolean useTransform() {
			return false;
		}
	}
}