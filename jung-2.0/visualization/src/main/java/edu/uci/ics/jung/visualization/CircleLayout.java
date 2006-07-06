/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Dec 4, 2003
 */
package edu.uci.ics.jung.visualization.contrib;

/**
 * 
 * @author danyelf
 */
/*
 * This source is under the same license with JUNG.
 * http://jung.sourceforge.net/license.txt for a description.
 */
//package edu.uci.ics.jung.visualization;
//package org.ingrid.nexas.graph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.layout.AbstractLayout;

/**
 * Positions vertices equally spaced on a regular circle.
 * Does not respect filter calls.
 *
 * @author Masanori Harada
 */
public class CircleLayout<V, E extends Edge<V>> extends AbstractLayout<V,E> {
//	private static final Object CIRCLE_KEY =
//	"jung.Circle_Visualization_Key";
//	private Pair key;
	private double radius;
    private Map<V, CircleVertexData> circleVertexDataMap = new HashMap<V,CircleVertexData>();

	public CircleLayout(Graph<V,E> g) {
		super(g);
//		key = new Pair(this, CIRCLE_KEY);
	}

	public String getStatus() {
		return "CircleLayout";
	}
	
	/**
	 * This one is not incremental.
	 */
	public boolean isIncremental() {
		return false;
	}

	/**
	 * Returns true;
	 */
	public boolean incrementsAreDone() {
		return true;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Specifies the order of vertices.  The first element of the
	 * specified array will be positioned with angle 0 (on the X
	 * axis), and the second one will be positioned with angle 1/n,
	 * and the third one will be positioned with angle 2/n, and so on.
	 * <p>
	 * The default implemention shuffles elements randomly.
	 */
	public void orderVertices(V[] vertices) {
		List<V> list = Arrays.asList(vertices);
		Collections.shuffle(list);
	}

	/**
	 * Returns a visualization-specific key (that is, specific both
	 * to this instance and <tt>AbstractLayout</tt>) that can be used
	 * to access UserData related to the <tt>AbstractLayout</tt>.
	 */
//	public Object getKey() {
//		if (key == null)
//			key = new Pair(this, CIRCLE_KEY);
//		return key;
//	}

	protected void initialize_local_vertex(V v) {
        if(circleVertexDataMap.get(v) == null) {
            circleVertexDataMap.put(v, new CircleVertexData());
        }
//		if (v.getUserDatum(getKey()) == null) {
//			v.addUserDatum(getKey(), new CircleVertexData(), UserData.REMOVE);
//		}
	}

	protected void initialize_local() {}

	protected void initializeLocations() {
		super.initializeLocations();

		V[] vertices = (V[])getVisibleVertices().toArray();
		orderVertices(vertices);

		Dimension d = getCurrentSize();
		double height = d.getHeight();
		double width = d.getWidth();

		if (radius <= 0) {
			radius = 0.45 * (height < width ? height : width);
		}

		for (int i = 0; i < vertices.length; i++) {
			Point2D coord = getCoordinates(vertices[i]);

			double angle = (2 * Math.PI * i) / vertices.length;
            coord.setLocation(Math.cos(angle) * radius + width / 2,
                    Math.sin(angle) * radius + height / 2);
//			coord.setX(Math.cos(angle) * radius + width / 2);
//			coord.setY(Math.sin(angle) * radius + height / 2);

			CircleVertexData data = getCircleData(vertices[i]);
			data.setAngle(angle);
		}
	}

	public CircleVertexData getCircleData(V v) {
		return circleVertexDataMap.get(v);
        //(CircleVertexData) (v.getUserDatum(getKey()));
	}

	/**
	 * Do nothing.
	 */
	public void advancePositions() {
	}

	public static class CircleVertexData {
		private double angle;

		public double getAngle() {
			return angle;
		}

		public void setAngle(double angle) {
			this.angle = angle;
		}

		public String toString() {
			return "CircleVertexData: angle=" + angle;
		}
	}
}
