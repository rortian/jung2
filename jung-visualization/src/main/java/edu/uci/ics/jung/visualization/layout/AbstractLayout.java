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
package edu.uci.ics.jung.visualization.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.RandomVertexLocationDecorator;
import edu.uci.ics.jung.visualization.VertexLocationFunction;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

/**
 * Implements some of the dirty work of writing a layout algorithm, allowing
 * the user to express their major intent more simply. When writing a <tt>Layout</tt>,
 * there are many shared tasks: handling tracking locked nodes, applying
 * filters, and tracing nearby vertices. This package automates all of those.
 * 
 * @author Danyel Fisher, Scott White
 * @param <V>
 */
abstract public class AbstractLayout<V, E> implements Layout<V,E>, ChangeEventSupport {

    protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);
    
    

    /**
     * a set of vertices that should not move in relation to the
     * other vertices
     */
	private Set<V> dontmove;

	private Dimension currentSize;
	private Graph<V, E> graph;
    protected VertexLocationFunction<V> vertexLocationFunction;
    
    protected Map<V, Point2D> locations = new HashMap<V, Point2D>();


	/**
	 * Constructor. Initializes the current size to be 100x100, both the graph
	 * and the showing graph to the argument, and creates the <tt>dontmove</tt>
	 * set.
	 * 
	 * @param g
	 */
	public AbstractLayout(Graph<V, E> g) {
		this.graph = g;
//		this.visibleEdges = g.getEdges();
//		this.visibleVertices = g.getVertices();
		this.dontmove = new HashSet<V>();
	}
    
    public void setGraph(Graph<V,E> graph) {
        this.graph = graph;
        initialize(getCurrentSize(), this);
    }
    
    /**
     * The set of vertices that have been locked. When running layout, it is
     * important to check
     * 
     * <pre>
     *  if (dontmove( v )) { ... }
     * </pre>
     * 
     * @return whether this vertex may be legally moved or not
     * @deprecated As of version 1.7.5, superseded by <code>Layout.isLocked(Vertex)</code>.
     */
    public boolean dontMove(V v) {
        return isLocked(v);
    }
    
    public boolean isLocked(V v)
    {
        return dontmove.contains(v);
    }

    public Iterator<V> getVertexIterator()
    {
        return getVisibleVertices().iterator();
    }
    
    public Collection<V> getVertices() {
    	return getGraph().getVertices();
    }
    
	/**
	 * Initializer, calls <tt>intialize_local</tt> and <tt>initializeLocations</tt>
	 * to start construction process.
	 */
	public void initialize(Dimension size) 
    {
        initialize(size, new RandomVertexLocationDecorator<V>(size));
	}

    public void initialize(Dimension size, VertexLocationFunction<V> v_locations)
    {
        this.currentSize = size;
        this.vertexLocationFunction = v_locations;
        initialize_local();
        initializeLocations();
    }
    
	/**
	 * Initializes all local information, and is called immediately within the
	 * <tt>initialize()</tt> process. The user is responsible for overriding
	 * this method to do any construction that may be necessary: for example,
	 * to initialize local per-edge or graph-wide data.
     * 
	 */
	protected void initialize_local() {}
    
    /**
     * may be overridden to do something after initializeLocations call
     *
     */
    protected void postInitialize() {}

	/**
	 * Initializes the local information on a single vertex. The user is
	 * responsible for overriding this method to do any vertex-level
	 * construction that may be necessary: for example, to attach vertex-level
	 * information to each vertex.
	 */
	protected abstract void initialize_local_vertex(V v);

//	private Object key;


//	/**
//	 * Returns a visualization-specific key (that is, specific both to this
//	 * instance and <tt>AbstractLayout</tt>) that can be used to access
//	 * UserData related to the <tt>AbstractLayout</tt>.
//	 */
//	public Object getBaseKey() {
//		if (key == null)
//			key = new Pair(this, BASE_KEY);
//		return key;
//	}

	/**
	 * This method calls <tt>initialize_local_vertex</tt> for each vertex,
	 * and also adds initial coordinate information for each vertex. (The
	 * vertex's initial location is set by calling <tt>initializeLocation</tt>.
	 */
	protected void initializeLocations() {
	    try {
            for(V v : graph.getVertices()) {
	            
                Point2D coord = locations.get(v);
	            if (coord == null) {
	                coord = new Point2D.Float();
                    locations.put(v, coord);
	            }
	            if (!dontmove.contains(v))
	                initializeLocation(v, coord, currentSize);
	            initialize_local_vertex(v);
	        }
	    } catch(ConcurrentModificationException cme) {
	        initializeLocations();
	    }
	    
	}

	/* ------------------------- */

	/**
	 * Sets random locations for a vertex within the dimensions of the space.
	 * If you want to initialize in some different way, override this method.
	 * 
	 * @param coord
	 * @param d
	 */
	protected void initializeLocation(
		V v,
		Point2D coord,
		Dimension d) {
	    coord.setLocation(vertexLocationFunction.getLocation(v));
	}

	/**
	 * {@inheritDoc}By default, an <tt>AbstractLayout</tt> returns null for
	 * its status.
	 */
	public String getStatus() {
		return null;
	}

	/**
	 * Implementors must override this method in order to create a Layout. If
	 * the Layout is the sort that only calculates locations once, this method
	 * may be overridden with an empty method.
	 * <p>
	 * Note that "locked" vertices are not to be moved; however, it is the
	 * policy of the visualization to decide how to handle them, and what to do
	 * with the vertices around them. Prototypical code might include a
	 * clipping like
	 * 
	 * <pre>
	 *  for (Iterator i = getVertices().iterator(); i.hasNext() ) { Vertex v = (Vertex) i.next(); if (! dontmove.contains( v ) ) { ... // handle the node } else { // ignore the node } }
	 * </pre>
	 * 
	 * @see Layout#advancePositions()
	 */
	public abstract void advancePositions();

	/**
	 * Accessor for the graph that represets all visible vertices. <b>Warning:
	 * </b> This graph consists of vertices that are equivalent to, but are <b>
	 * not the same as</b> the vertices in <tt>getGraph()</tt>, nor the
	 * vertices in <tt>getAllVertices()</tt>. Rather, it returns the
	 * vertices and edges that were passed in during a call to <tt>applyFilter</tt>.
	 * The call <tt>getVisibleGraph().getVertices()</tt>, is almost
	 * indubitably incorrect.
	 * <p>
	 * 
	 * @return the current visible graph.
	 * @see #getVisibleEdges
	 * @see #getVisibleVertices
	 */
//	protected Graph<V, E> getGraph() {
//		return graph;
//	}

	/**
	 * Returns the current size of the visualization space, accoring to the
	 * last call to resize().
	 * 
	 * @return the current size of the screen
	 */
	public Dimension getCurrentSize() {
		return currentSize;
	}

	/**
	 * Utility method, gets a single vertex from this edge. The utility's
	 * implementation is to get the iterator from the edge's <tt>getIncidentVertices()</tt>
	 * and then return the first element.
	 */
	protected V getAVertex(E edge) {
        return graph.getIncidentVertices(edge).iterator().next();
//		V v = e.getIncidentVertices().iterator().next();
//		return v;
	}

	/**
	 * Returns the Coordinates object that stores the vertex' x and y location.
	 * 
	 * @param v
	 *            A Vertex that is a part of the Graph being visualized.
	 * @return A Coordinates object with x and y locations.
	 */
	public Point2D getCoordinates(V v) {
        return locations.get(v);
//		return (Coordinates) v.getUserDatum(getBaseKey());
	}
	
	/**
	 * Returns the x coordinate of the vertex from the Coordinates object.
	 * in most cases you will be better off calling getLocation(Vertex v);
	 * @see edu.uci.ics.jung.visualization.layout.Layout#getX(edu.uci.ics.jung.graph.Vertex)
	 */
	public double getX(V v) {
        assert getCoordinates(v) != null : "Cannot getX for an unmapped vertex "+v;
        return getCoordinates(v).getX();
//	    Coordinates coords = (Coordinates)v.getUserDatum(getBaseKey());
//	    return coords.getX();
	}

	/**
	 * Returns the y coordinate of the vertex from the Coordinates object.
	 * In most cases you will be better off calling getLocation(Vertex v)
	 * @see edu.uci.ics.jung.visualization.layout.Layout#getX(edu.uci.ics.jung.graph.Vertex)
	 */
	public double getY(V v) {
        assert getCoordinates(v) != null : "Cannot getY for an unmapped vertex "+v;
        return getCoordinates(v).getY();

//	    Coordinates coords = (Coordinates)v.getUserDatum(getBaseKey());
//	    return coords.getY();
	}
	
    /**
     * @param v a Vertex of interest
     * @return the location point of the supplied vertex
     */
	public Point2D getLocation(V v) {
	    return getCoordinates(v);
	}

	/**
	 * When a visualization is resized, it presumably wants to fix the
	 * locations of the vertices and possibly to reinitialize its data. The
	 * current method calls <tt>initializeLocations</tt> followed by <tt>initialize_local</tt>.
	 * TODO: A better implementation wouldn't destroy the current information,
	 * but would either scale the current visualization, or move the nodes
	 * toward the new center.
	 */
	public void resize(Dimension size) {
		// are we initialized yet?

		if (currentSize == null) {
			currentSize = size;
			return;
		}
		
		Dimension oldSize;
		synchronized (currentSize) {
			if (currentSize.equals(size))
				return;
			oldSize = currentSize;
			this.currentSize = size;
		}

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

		// optionally, we may want to restart
	}

	/**
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	protected void offsetVertex(V v, double xOffset, double yOffset) {
		Point2D c = getCoordinates(v);
        c.setLocation(c.getX()+xOffset, c.getY()+yOffset);
		forceMove(v, c.getX(), c.getY());
	}

	/**
     * @see Layout#restart()
	 */
	public void restart() {
        initialize_local();
        initializeLocations();
	}

//	/**
//	 * Gets the vertex nearest to the location of the (x,y) location selected.
//	 * Calls the longer form of the call.
//	 * @deprecated Use PickSupport instead
//	 */
//	public Vertex getVertex(double x, double y) {
//		return getVertex(x, y, Math.sqrt(Double.MAX_VALUE - 1000));
//	}
//
//	/**
//	 * Gets the vertex nearest to the location of the (x,y) location selected,
//	 * within a distance of <tt>maxDistance</tt>. Iterates through all
//	 * visible vertices and checks their distance from the click. Override this
//	 * method to provde a more efficient implementation.
//	 * @deprecated Use PickSupport instead
//	 */
//	public Vertex getVertex(double x, double y, double maxDistance) {
//	    
//        double minDistance = maxDistance * maxDistance;
//		Vertex closest = null;
//		while(true) {
//		    try {
//		        for (Iterator iter = getVisibleVertices().iterator();
//		        iter.hasNext();
//		        ) {
//		            Vertex v = (Vertex) iter.next();
//		            Point2D p = getLocation(v);
//		            double dx = p.getX() - x;
//		            double dy = p.getY() - y;
//		            double dist = dx * dx + dy * dy;
//		            if (dist < minDistance) {
//		                minDistance = dist;
//		                closest = v;
//		            }
//		        }
//                break;
//		    } catch(ConcurrentModificationException cme) {}
//		}
//		return closest;
//	}
//
//	/**
//	 * Gets the edge nearest to the location of the (x,y) location selected.
//	 * Calls the longer form of the call.
//	 * @deprecated Use PickSupport instead
//	 */
//	public Edge getEdge(double x, double y) {
//		return getEdge(x, y, Math.sqrt(Double.MAX_VALUE - 1000));
//	}
//
//	/**
//	 * Gets the edge nearest to the location of the (x,y) location selected,
//	 * within a distance of <tt>maxDistance</tt>, Iterates through all
//	 * visible edges and checks their distance from the click. Override this
//	 * method to provide a more efficient implementation.
//	 * @deprecated Use PickSupport instead
//	 * @param x
//	 * @param y
//	 * @param maxDistance
//	 * @return Edge closest to the click.
//	 */
//	public Edge getEdge(double x, double y, double maxDistance) {
//	    
//  		double minDistance = maxDistance * maxDistance;
//		Edge closest = null;
//		while(true) {
//		    try {
//		        for (Iterator iter = getVisibleEdges().iterator(); iter.hasNext();) {
//		            Edge e = (Edge) iter.next();
//		            // if anyone uses a hyperedge, this is too complex.
//		            if (e.numVertices() != 2)
//		                continue;
//		            // Could replace all this set stuff with getFrom_internal() etc.
//		            Set vertices = e.getIncidentVertices();
//		            Iterator vertexIterator = vertices.iterator();
//		            Vertex v1 = (Vertex) vertexIterator.next();
//		            Vertex v2 = (Vertex) vertexIterator.next();
//		            // Get coords
//		            Point2D p1 = getLocation(v1);
//		            Point2D p2 = getLocation(v2);
//		            double x1 = p1.getX();
//		            double y1 = p1.getY();
//		            double x2 = p2.getX();
//		            double y2 = p2.getY();
//		            // Calculate location on line closest to (x,y)
//		            // First, check that v1 and v2 are not coincident.
//		            if (x1 == x2 && y1 == y2)
//		                continue;
//		            double b =
//		                ((y - y1) * (y2 - y1) + (x - x1) * (x2 - x1))
//		                / ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
//		            //
//		            double distance2; // square of the distance
//		            if (b <= 0)
//		                distance2 = (x - x1) * (x - x1) + (y - y1) * (y - y1);
//		            else if (b >= 1)
//		                distance2 = (x - x2) * (x - x2) + (y - y2) * (y - y2);
//		            else {
//		                double x3 = x1 + b * (x2 - x1);
//		                double y3 = y1 + b * (y2 - y1);
//		                distance2 = (x - x3) * (x - x3) + (y - y3) * (y - y3);
//		            }
//		            
//		            if (distance2 < minDistance) {
//		                minDistance = distance2;
//		                closest = e;
//		            }
//		        }
//		        break;
//		    } catch(ConcurrentModificationException cme) {}
//		}
//		return closest;
//	}
	
	/**
	 * Accessor for the graph that represets all vertices.
	 * 
	 * @return the graph that contains all vertices.
	 */
	public Graph<V, E> getGraph() {
	    return graph;
	}
	
	/**
	 * Returns the set of edges from the original <tt>getGraph</tt> that are
	 * now visible. These edges are equivalent to the ones passed in from the
	 * <tt>Graph</tt> argument to <tt>applyFilter()</tt>.
	 */
	public Collection<E> getVisibleEdges() {
	    return graph.getEdges();
	}
	
	/**
	 * Returns the set of vertices from the original <tt>getGraph</tt> that
	 * are now visible. These vertices are equivalent to the ones passed in
	 * from the <tt>Graph</tt> argument to <tt>applyFilter()</tt>.
	 */
	public Collection<V> getVisibleVertices() {
	    return graph.getVertices();
	}
	
	/**
	 * Forcibly moves a vertex to the (x,y) location by setting its x and y
	 * locations to the inputted location. Does not add the vertex to the
	 * "dontmove" list, and (in the default implementation) does not make any
	 * adjustments to the rest of the graph.
	 */
	public void forceMove(V picked, double x, double y) {
		Point2D coord = getCoordinates(picked);
		coord.setLocation(x, y);
        fireStateChanged();
	}

	/**
	 * Adds the vertex to the DontMove list
	 */
	public void lockVertex(V v) {
		dontmove.add(v);
	}

	/**
	 * Removes the vertex from the DontMove list
	 */
	public void unlockVertex(V v) {
		dontmove.remove(v);
	}

	/**
	 * Applies the filter to the current graph. The default implementation
	 * merely makes fewer vertices available to the <tt>getVisibleVertices</tt>
	 * and <tt>getVisibleEdges</tt> methods.
	 * 
	 * @see Layout#applyFilter(Graph g)
	 */
	public void applyFilter(Graph<V, E> g) {
		this.graph = g;
//		this.visibleVertices =
//			GraphUtils.getEqualVertices(g.getVertices(), baseGraph);
//		this.visibleEdges =
//			GraphUtils.getEqualEdges(g.getEdges(), baseGraph);
	}
    
    /**
     * Adds a <code>ChangeListener</code>.
     * @param l the listener to be added
     */
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }
    
    /**
     * Removes a ChangeListener.
     * @param l the listener to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    
    /**
     * Returns an array of all the <code>ChangeListener</code>s added
     * with addChangeListener().
     *
     * @return all of the <code>ChangeListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created.
     * The primary listeners will be views that need to be repainted
     * because of changes in this model instance
     * @see EventListenerList
     */
    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }   
}
