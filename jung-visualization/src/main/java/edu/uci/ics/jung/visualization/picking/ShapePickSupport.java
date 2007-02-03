/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Mar 11, 2005
 *
 */
package edu.uci.ics.jung.visualization.picking;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.functors.TruePredicate;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationServer;

/**
 * ShapePickSupport provides access to Vertices and EdgeType based on
 * their actual shapes. 
 * 
 * @author Tom Nelson
 *
 */
public class ShapePickSupport<V, E> implements GraphElementAccessor<V,E> {

	public static enum Style { LOWEST, CENTERED, HIGHEST };
    protected float pickSize;
    protected VisualizationServer<V,E> vv;
    protected Style style = Style.CENTERED;
    
    /**
     * Create an instance.
     * The HasGraphLayout is used as the source of the current
     * Graph Layout. The HasShapes
     * is used to access the VertexShapes and the EdgeShapes
     * @param hasGraphLayout source of the current layout.
     * @param hasShapeFunctions source of Vertex and Edge shapes.
     * @param pickSize how large to make the pick footprint for line edges
     */
    public ShapePickSupport(VisualizationServer<V,E> vv, float pickSize) {
    	this.vv = vv;
        this.pickSize = pickSize;
    }
    
    /**
     * Create an instance.
     * The pickSize footprint defaults to 2.
     */
    public ShapePickSupport(VisualizationServer<V,E> vv) {
        this.vv = vv;
        this.pickSize = 2;
    }
    
    /**
	 * @return the style
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(Style style) {
		this.style = style;
	}

	/** 
     * Iterates over Vertices, checking to see if x,y is contained in the
     * Vertex's Shape. If (x,y) is contained in more than one vertex, use
     * the vertex whose center is closest to the pick point.
     * @see edu.uci.ics.jung.visualization.picking.PickSupport#getVertex(double, double)
     */
    public V getVertex(Layout<V, E> layout, double x, double y) {

        V closest = null;
        double minDistance = Double.MAX_VALUE;
        Point2D ip = vv.getRenderContext().getBasicTransformer().inverseViewTransform(new Point2D.Double(x,y));
        x = ip.getX();
        y = ip.getY();

        while(true) {
            try {
                for(V v : getFilteredVertices(layout)) {
                	
                    Shape shape = vv.getRenderContext().getVertexShapeTransformer().transform(v);
                    // get the vertex location
                    Point2D p = layout.transform(v);
                    if(p == null) continue;
                    // transform the vertex location to screen coords
                    p = vv.getRenderContext().getBasicTransformer().layoutTransform(p);
                    
                    double ox = x - p.getX();
                    double oy = y - p.getY();

                    if(shape.contains(ox, oy)) {
                    	
                    	if(style == Style.LOWEST) {
                    		// return the first match
                    		return v;
                    	} else if(style == Style.HIGHEST) {
                    		// will return the last match
                    		closest = v;
                    	} else {
                    		
                    		// return the vertex closest to the
                    		// center of a vertex shape
	                        Rectangle2D bounds = shape.getBounds2D();
	                        double dx = bounds.getCenterX() - ox;
	                        double dy = bounds.getCenterY() - oy;
	                        double dist = dx * dx + dy * dy;
	                        if (dist < minDistance) {
	                        	minDistance = dist;
	                        	closest = v;
	                        }
                    	}
                    }
                }
                break;
            } catch(ConcurrentModificationException cme) {}
        }
        return closest;
    }

    /**
     * returns the vertices that are contained in the passed shape.
     * The shape is in screen coordinates, and the graph vertices
     * are transformed to screen coordinates before they are tested
     * for inclusion
     */
    public Collection<V> getVertices(Layout<V, E> layout, Shape rectangle) {
    	Set<V> pickedVertices = new HashSet<V>();
    	
    	// remove the view transform from the rectangle
    	rectangle = vv.getRenderContext().getBasicTransformer().inverseViewTransform(rectangle);

        while(true) {
            try {
                for(V v : getFilteredVertices(layout)) {
                    Point2D p = layout.transform(v);
                    if(p == null) continue;

                    p = vv.getRenderContext().getBasicTransformer().layoutTransform(p);
                    if(rectangle.contains(p)) {
                    	pickedVertices.add(v);
                    }
                }
                break;
            } catch(ConcurrentModificationException cme) {}
        }
        return pickedVertices;
    }
    
    /**
     * return an edge whose shape intersects the 'pickArea' footprint of the passed
     * x,y, coordinates.
     */
    public E getEdge(Layout<V, E> layout, double x, double y) {

        Point2D ip = vv.getRenderContext().getBasicTransformer().inverseViewTransform(new Point2D.Double(x,y));
        x = ip.getX();
        y = ip.getY();

        // as a Line has no area, we can't always use edgeshape.contains(point) so we
        // make a small rectangular pickArea around the point and check if the
        // edgeshape.intersects(pickArea)
        Rectangle2D pickArea = 
            new Rectangle2D.Float((float)x-pickSize/2,(float)y-pickSize/2,pickSize,pickSize);
        E closest = null;
        double minDistance = Double.MAX_VALUE;
        while(true) {
            try {
                for(E e : getFilteredEdges(layout)) {

                    Pair<V> pair = layout.getGraph().getEndpoints(e);
                    V v1 = pair.getFirst();
                    V v2 = pair.getSecond();
                    boolean isLoop = v1.equals(v2);
                    Point2D p1 = vv.getRenderContext().getBasicTransformer().layoutTransform(layout.transform(v1));
                    Point2D p2 = vv.getRenderContext().getBasicTransformer().layoutTransform(layout.transform(v2));
                    if(p1 == null || p2 == null) continue;
                    float x1 = (float) p1.getX();
                    float y1 = (float) p1.getY();
                    float x2 = (float) p2.getX();
                    float y2 = (float) p2.getY();

                    // translate the edge to the starting vertex
                    AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

                    Shape edgeShape = 
                    	vv.getRenderContext().getEdgeShapeTransformer().transform(Context.<Graph<V,E>,E>getInstance(vv.getGraphLayout().getGraph(),e));
                    if(isLoop) {
                        // make the loops proportional to the size of the vertex
                        Shape s2 = vv.getRenderContext().getVertexShapeTransformer().transform(v2);
                        Rectangle2D s2Bounds = s2.getBounds2D();
                        xform.scale(s2Bounds.getWidth(),s2Bounds.getHeight());
                        // move the loop so that the nadir is centered in the vertex
                        xform.translate(0, -edgeShape.getBounds2D().getHeight()/2);
                    } else {
                        float dx = x2 - x1;
                        float dy = y2 - y1;
                        // rotate the edge to the angle between the vertices
                        double theta = Math.atan2(dy,dx);
                        xform.rotate(theta);
                        // stretch the edge to span the distance between the vertices
                        float dist = (float) Math.sqrt(dx*dx + dy*dy);
                        xform.scale(dist, 1.0f);
                    }

                    // transform the edge to its location and dimensions
                    edgeShape = xform.createTransformedShape(edgeShape);

                    // because of the transform, the edgeShape is now a GeneralPath
                    // see if this edge is the closest of any that intersect
                    if(edgeShape.intersects(pickArea)) {
                        float cx=0;
                        float cy=0;
                        float[] f = new float[6];
                        PathIterator pi = new GeneralPath(edgeShape).getPathIterator(null);
                        if(pi.isDone()==false) {
                            pi.next();
                            pi.currentSegment(f);
                            cx = f[0];
                            cy = f[1];
                            if(pi.isDone()==false) {
                                pi.currentSegment(f);
                                cx = f[0];
                                cy = f[1];
                            }
                        }
                        float dx = (float) (cx - x);
                        float dy = (float) (cy - y);
                        float dist = dx * dx + dy * dy;
                        if (dist < minDistance) {
                            minDistance = dist;
                            closest = e;
                        }
                    }
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {}
		}
		return closest;
    }

    public Collection<V> getFilteredVertices(Layout<V,E> layout) {
    	if(verticesAreFiltered()) {
    		Collection<V> unfiltered = layout.getGraph().getVertices();
    		Collection<V> filtered = new HashSet<V>();
    		for(V v : unfiltered) {
    			if(isVertexRendered(Context.<Graph<V,E>,V>getInstance(layout.getGraph(),v))) {
    				filtered.add(v);
    			}
    		}
    		return filtered;
    	} else {
    		return layout.getGraph().getVertices();
    	}
    }

    public Collection<E> getFilteredEdges(Layout<V,E> layout) {
    	if(edgesAreFiltered()) {
    		Collection<E> unfiltered = layout.getGraph().getEdges();
    		Collection<E> filtered = new HashSet<E>();
    		for(E e : unfiltered) {
    			if(isEdgeRendered(Context.<Graph<V,E>,E>getInstance(layout.getGraph(),e))) {
    				filtered.add(e);
    			}
    		}
    		return filtered;
    	} else {
    		return layout.getGraph().getEdges();
    	}
    }
    
    /**
     * quick test to allow optimization of getFilteredVertices
     * method
     * @return
     */
    protected boolean verticesAreFiltered() {
		Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate =
			vv.getRenderContext().getVertexIncludePredicate();
		return vertexIncludePredicate != null &&
			vertexIncludePredicate instanceof TruePredicate == false;
    }
    
    /**
     * quick test to allow optimization of getFilteredEdges method
     * @return
     */
    protected boolean edgesAreFiltered() {
		Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate =
			vv.getRenderContext().getEdgeIncludePredicate();
		return edgeIncludePredicate != null &&
			edgeIncludePredicate instanceof TruePredicate == false;
    }
    
	protected boolean isVertexRendered(Context<Graph<V,E>,V> context) {
		Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate =
			vv.getRenderContext().getVertexIncludePredicate();
		return vertexIncludePredicate == null || vertexIncludePredicate.evaluate(context);
	}
	
	protected boolean isEdgeRendered(Context<Graph<V,E>,E> context) {
		Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate =
			vv.getRenderContext().getVertexIncludePredicate();
		Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate =
			vv.getRenderContext().getEdgeIncludePredicate();
		Graph<V,E> g = context.graph;
		E e = context.element;
		boolean edgeTest = edgeIncludePredicate == null || edgeIncludePredicate.evaluate(context);
		Pair<V> endpoints = g.getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		boolean endpointsTest = vertexIncludePredicate == null ||
			(vertexIncludePredicate.evaluate(Context.<Graph<V,E>,V>getInstance(g,v1)) && 
					vertexIncludePredicate.evaluate(Context.<Graph<V,E>,V>getInstance(g,v2)));
		return edgeTest && endpointsTest;
	}

	/**
	 * @return the pickSize
	 */
	public float getPickSize() {
		return pickSize;
	}

	/**
	 * @param pickSize the pickSize to set
	 */
	public void setPickSize(float pickSize) {
		this.pickSize = pickSize;
	}

}
