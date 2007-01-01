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
package edu.uci.ics.jung.algorithms.layout;

/**
 * 
 * @author danyelf
 */
/*
 * This source is under the same license with JUNG.
 * http://jung.sourceforge.net/license.txt for a description.
 */

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.graph.Graph;



/**
 * Positions vertices equally spaced on a regular circle.
 * Does not respect filter calls.
 *
 * @author Masanori Harada
 */
public class CircleLayout<V, E> extends AbstractLayout<V,E> {

	private double radius;
	
	Map<V, CircleVertexData> circleVertexDataMap =
			LazyMap.decorate(new HashMap<V,CircleVertexData>(), 
			new Factory<CircleVertexData>() {
				public CircleVertexData create() {
					return new CircleVertexData();
				}});	

	public CircleLayout(Graph<V,E> g) {
		super(g);
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
	
	public void reset() {
		initialize();
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		Graph<V,E> graph = getGraph();
		Dimension d = getSize();
		if(graph != null && d != null) {
			V[] vertices =
				(V[])graph.getVertices().toArray();
			orderVertices(vertices);

			double height = d.getHeight();
			double width = d.getWidth();

			if (radius <= 0) {
				radius = 0.45 * (height < width ? height : width);
			}

			for (int i = 0; i < vertices.length; i++) {
				Point2D coord = transform(vertices[i]);

				double angle = (2 * Math.PI * i) / vertices.length;

				coord.setLocation(Math.cos(angle) * radius + width / 2,
						Math.sin(angle) * radius + height / 2);

				CircleVertexData data = getCircleData(vertices[i]);
				data.setAngle(angle);
			}
		}
	}

	public CircleVertexData getCircleData(V v) {
		return circleVertexDataMap.get(v);
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
