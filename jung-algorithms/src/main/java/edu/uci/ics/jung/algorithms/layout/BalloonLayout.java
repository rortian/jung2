/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 9, 2005
 */

package edu.uci.ics.jung.algorithms.layout;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.graph.Forest;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.Tree;

/**
 * implements the balloon layout for the supplied Tree
 * or Forest.
 * 
 * @author Tom Nelson 
 *  
 */
public class BalloonLayout<V,E> implements Layout<V,E> {

	private Dimension size;
	private Graph<V,E> graph;
    
    protected Map<V, Point2D> locations = 
    	LazyMap.decorate(new HashMap<V, Point2D>(),
    			new Transformer<V,Point2D>() {
					public Point2D transform(V arg0) {
						return new Point2D.Double(size.getWidth()/2,size.getHeight()/2);
					}});
    
    protected Map<V,PolarPoint> polarLocations =
    	LazyMap.decorate(new HashMap<V, PolarPoint>(),
    			new Transformer<V,PolarPoint>() {
					public PolarPoint transform(V arg0) {
						return new PolarPoint();
					}});
    
    protected Map<V,Double> radii = new HashMap<V,Double>();
    
    private Collection<V> roots;

    public BalloonLayout(Graph<V,E> g) {
    	
    	assert g instanceof Tree || g instanceof Forest : 
    		"Graph must be a Tree or a Forest";
    	this.graph = g;
        this.roots = getRoots(g);
    }
    
    private Collection<V> getRoots(Graph<V,E> graph) {
    	Set<V> roots = new HashSet<V>();
    	for(V v : graph.getVertices()) {
    		if(graph.getPredecessors(v).size() == 0) {
    			roots.add(v);
    		}
    	}
    	return roots;
    }
    
    protected void setRootPolars(List<V> roots) {
    	if(roots.size() == 0) {
    		// do nothing
    	} else if(roots.size() == 1) {
    		// its a Tree
    		V root = roots.get(0);
    		setRootPolar(root);
    		setPolars(new ArrayList<V>(getChildren(root)),
    				getCenter(), getSize().width/2);
    	} else {
    		// its a Forest
    		setPolars(roots, getCenter(), getSize().width/2);
    	}
    }
    
    protected void setRootPolar(V root) {
    	PolarPoint pp = new PolarPoint(0,0);
    	Point2D p = getCenter();
    	polarLocations.put(root, pp);
    	locations.put(root, p);
    }
    

    protected void setPolars(List<V> kids, Point2D parentLocation, double parentRadius) {

    	int childCount = kids.size();
    	if(childCount == 0) return;
    	// handle the 1-child case with 0 limit on angle.
    	double angle = Math.max(0, Math.PI / 2 * (1 - 2.0/childCount));
    	double childRadius = parentRadius*Math.cos(angle) / (1 + Math.cos(angle));
    	double radius = parentRadius - childRadius;

    	double rand = Math.random();

    	for(int i=0; i< childCount; i++) {
    		V child = kids.get(i);
    		double theta = i* 2*Math.PI/childCount + rand;
    		radii.put(child, childRadius);
    		
    		PolarPoint pp = new PolarPoint(theta, radius);
    		polarLocations.put(child, pp);
    		
    		Point2D p = PolarPoint.polarToCartesian(pp);
//    		Point2D parentLocation = locations.get(parent);
    		p.setLocation(p.getX()+parentLocation.getX(), p.getY()+parentLocation.getY());
    		locations.put(child, p);
    		setPolars(new ArrayList<V>(getChildren(child)), p, childRadius);
    	}
    }

    /**
     * ?
     * 
     * @see edu.uci.ics.jung.visualization.Layout#incrementsAreDone()
     */
    public boolean incrementsAreDone() {
        return true;
    }
    public void setSize(Dimension size) {
    	this.size = size;
    	setRootPolars(new ArrayList<V>(roots));
    }

	public Graph<V,E> getGraph() {
		return graph;
	}

	public Dimension getSize() {
		return size;
	}

	public void initialize() {

	}

	public boolean isLocked(V v) {
		return false;
	}

	public void lock(V v, boolean state) {
	}

	public void reset() {
	}

	public void setGraph(Graph<V,E> graph) {
		this.graph = graph;
	}

	public void setInitializer(Transformer<V, Point2D> initializer) {
	}
	
	public Point2D getCenter() {
		return new Point2D.Double(size.getWidth()/2,size.getHeight()/2);
	}
	
	public Point2D getCenter(V v) {
		// return the cartesian coords of the parent
		V parent = getParent(v);
		if(parent == null) {
			return getCenter();
		}
		return locations.get(parent);
	}

	public void setLocation(V v, Point2D location) {
			Point2D c = getCenter(v);
			Point2D pv = new Point2D.Double(location.getX()-c.getX(),location.getY()-c.getY());
			PolarPoint newLocation = PolarPoint.cartesianToPolar(pv);
			polarLocations.get(v).setLocation(newLocation);
			
			Point2D center = getCenter(v);
			pv.setLocation(pv.getX()+center.getX(), pv.getY()+center.getY());
			locations.put(v, pv);
	}

	public Point2D transform(V v) {
		return locations.get(v);
	}

	private V getParent(V v) {
		return (graph.getPredecessorCount(v) == 1) ?
			graph.getPredecessors(v).iterator().next() : null;
	}
	
	private Collection<V> getChildren(V parent) {
		return graph.getSuccessors(parent);
	}

	/**
	 * @return the radii
	 */
	public Map<V, Double> getRadii() {
		return radii;
	}
}
