/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Jul 7, 2003
 * 
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.apache.commons.collections15.functors.CloneTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;

/**
 * Implements some of the dirty work of writing a layout algorithm, allowing
 * the user to express their major intent more simply. When writing a <tt>Layout</tt>,
 * there are many shared tasks: handling tracking locked nodes, applying
 * filters, and tracing nearby vertices. This package automates all of those.
 * 
 * @author Danyel Fisher, Scott White
 * @author Tom Nelson - converted to jung2
 * @param <V>
 */
abstract public class AbstractLayout<V, E> implements Layout<V,E> {

    /**
     * a set of vertices that should not move in relation to the
     * other vertices
     */
	private Set<V> dontmove = new HashSet<V>();

	private Dimension size;
	private Graph<V, E> graph;
	protected boolean initialized;
    
    protected Map<V, Point2D> locations = 
    	LazyMap.decorate(new HashMap<V, Point2D>(),
    			new Transformer<V,Point2D>() {
					public Point2D transform(V arg0) {
						return new Point2D.Double();
					}});


	/**
	 * Constructor. Initializes the current size to be 100x100, both the graph
	 * and the showing graph to the argument, and creates the <tt>dontmove</tt>
	 * set.
	 * 
	 * @param g
	 */
	protected AbstractLayout(Graph<V, E> graph) {
		this.graph = graph;
	}
	
	protected AbstractLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer) {
		this.graph = graph;
		Transformer chain = 
			ChainedTransformer.getInstance(initializer, CloneTransformer.getInstance());
		this.locations = LazyMap.decorate(new HashMap<V,Point2D>(), chain);
		initialized = true;
	}
	
	protected AbstractLayout(Graph<V,E> graph, Dimension size) {
		this.graph = graph;
		this.size = size;
	}
	
	protected AbstractLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer, Dimension size) {
		this.graph = graph;
		Transformer chain = 
			ChainedTransformer.getInstance(initializer, CloneTransformer.getInstance());
		this.locations = LazyMap.decorate(new HashMap<V,Point2D>(), chain);
		this.size = size;
	}
    
    public void setGraph(Graph<V,E> graph) {
        this.graph = graph;
        if(size != null && graph != null) {
        	initialize();
        }
    }
    
	/**
	 * When a visualization is resized, it presumably wants to fix the
	 * locations of the vertices and possibly to reinitialize its data. The
	 * current method calls <tt>initializeLocations</tt> followed by <tt>initialize_local</tt>.
	 * TODO: A better implementation wouldn't destroy the current information,
	 * but would either scale the current visualization, or move the nodes
	 * toward the new center.
	 */
	public void setSize(Dimension size) {
		
		if(size != null && graph != null) {
			
			Dimension oldSize = this.size;
			this.size = size;
			initialize();
			
			if(oldSize != null) {
				adjustLocations(oldSize, size);
			}
		}
	}
	
	private void adjustLocations(Dimension oldSize, Dimension size) {

		int xOffset = (size.width - oldSize.width) / 2;
		int yOffset = (size.height - oldSize.height) / 2;

		// now, move each vertex to be at the new screen center
		while(true) {
		    try {
                for(V v : getGraph().getVertices()) {
		            offsetVertex(v, xOffset, yOffset);
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {
		    }
		}
	}
    
    public boolean isLocked(V v) {
        return dontmove.contains(v);
    }
    
    public Collection<V> getVertices() {
    	return getGraph().getVertices();
    }
    
    public void setInitializer(Transformer<V,Point2D> initializer) {
		Transformer chain = 
			ChainedTransformer.getInstance(initializer, CloneTransformer.getInstance());
    	this.locations = LazyMap.decorate(new HashMap<V,Point2D>(), chain);
    	initialized = true;
    }
    
	/**
	 * Returns the current size of the visualization space, accoring to the
	 * last call to resize().
	 * 
	 * @return the current size of the screen
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * Returns the Coordinates object that stores the vertex' x and y location.
	 * 
	 * @param v
	 *            A Vertex that is a part of the Graph being visualized.
	 * @return A Coordinates object with x and y locations.
	 */
	private Point2D getCoordinates(V v) {
        return locations.get(v);
	}
	
	public Point2D transform(V v) {
		return getCoordinates(v);
	}
	
	/**
	 * Returns the x coordinate of the vertex from the Coordinates object.
	 * in most cases you will be better off calling getLocation(Vertex v);
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#getX(edu.uci.ics.jung.graph.Vertex)
	 */
	public double getX(V v) {
        assert getCoordinates(v) != null : "Cannot getX for an unmapped vertex "+v;
        return getCoordinates(v).getX();
	}

	/**
	 * Returns the y coordinate of the vertex from the Coordinates object.
	 * In most cases you will be better off calling getLocation(Vertex v)
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#getX(edu.uci.ics.jung.graph.Vertex)
	 */
	public double getY(V v) {
        assert getCoordinates(v) != null : "Cannot getY for an unmapped vertex "+v;
        return getCoordinates(v).getY();
	}
	
    /**
     * @param v a Vertex of interest
     * @return the location point of the supplied vertex
     */
//	public Point2D getLocation(V v) {
//	    return getCoordinates(v);
//	}

	/**
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	protected void offsetVertex(V v, double xOffset, double yOffset) {
		Point2D c = getCoordinates(v);
        c.setLocation(c.getX()+xOffset, c.getY()+yOffset);
		setLocation(v, c);
	}

	/**
	 * Accessor for the graph that represets all vertices.
	 * 
	 * @return the graph that contains all vertices.
	 */
	public Graph<V, E> getGraph() {
	    return graph;
	}
	
	/**
	 * Forcibly moves a vertex to the (x,y) location by setting its x and y
	 * locations to the inputted location. Does not add the vertex to the
	 * "dontmove" list, and (in the default implementation) does not make any
	 * adjustments to the rest of the graph.
	 */
	public void setLocation(V picked, double x, double y) {
		Point2D coord = getCoordinates(picked);
		coord.setLocation(x, y);
	}

	public void setLocation(V picked, Point2D p) {
		Point2D coord = getCoordinates(picked);
		coord.setLocation(p);
	}

	/**
	 * Adds the vertex to the DontMove list
	 */
	public void lock(V v, boolean state) {
		if(state == true) dontmove.add(v);
		else dontmove.remove(v);
	}
	
	public void lock(boolean lock) {
		for(V v : graph.getVertices()) {
			lock(v, lock);
		}
	}
}
