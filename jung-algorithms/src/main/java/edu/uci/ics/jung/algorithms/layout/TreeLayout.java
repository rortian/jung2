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
import java.awt.Point;
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

import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;

/**
 * @author Karlheinz Toni
 * @author Tom Nelson - converted to jung2
 *  
 */

public class TreeLayout<V,E> implements Layout<V,E> {

	private Dimension size;
	private Forest<V,E> graph;
	protected Map<V,Integer> basePositions = new HashMap<V,Integer>();
    
    protected Map<V, Point2D> locations = 
    	LazyMap.decorate(new HashMap<V, Point2D>(),
    			new Transformer<V,Point2D>() {
					public Point2D transform(V arg0) {
						return new Point2D.Double();
					}});
    public static int DEFAULT_DISTX = 50;
    public static int DEFAULT_DISTY = 50;
    
    public List<V> getAtomics(V p) {
        List<V> v = new ArrayList<V>();
        getAtomics(p, v);
        return v;
    }

    private void getAtomics(V p, List<V> v) {
        for (V c : graph.getSuccessors(p)) {
            if (graph.getSuccessors(c).size() == 0) {
                v.add(c);
            } else {
                getAtomics(c, v);
            }
        }
    }
    private transient Set<V> allreadyDone = new HashSet<V>();

    private int distX = DEFAULT_DISTX;
    private int distY = DEFAULT_DISTY;
    private transient Point m_currentPoint = new Point();

    private Collection<V> roots;

    public TreeLayout(Forest<V,E> g) {
    	this(g, DEFAULT_DISTX, DEFAULT_DISTY);
    }

    public TreeLayout(Forest<V,E> g, int distx) {
        this(g, distx, DEFAULT_DISTY);
    }

    public TreeLayout(Forest<V,E> g, int distx, int disty) {
    	
    	this.graph = g;
        this.roots = getRoots(g);
        this.distX = distx;
        this.distY = disty;
    }
    
    private Collection<V> getRoots(Forest<V,E> forest) {
    	Set<V> roots = new HashSet<V>();
    	for(Tree<V,E> tree : forest.getTrees()) {
    			roots.add(tree.getRoot());
    	}
    	System.err.println("roots = "+roots);
    	return roots;
    }

    public Dimension getCurrentSize() {
    	return size;
    }

	void buildTree() {
        this.m_currentPoint = new Point(0, 20);
        if (roots.size() > 0 && graph != null) {
       		calculateDimensionX(roots);
       		for(V v : roots) {
        		calculateDimensionX(v);
        		m_currentPoint.x += this.basePositions.get(v)/2 + 50;
        		buildTree(v, this.m_currentPoint.x);
        	}
        }
        int width = 0;
        for(V v : roots) {
        	width += basePositions.get(v);
        }
    }

    void buildTree(V v, int x) {

        if (!allreadyDone.contains(v)) {
            allreadyDone.add(v);

            //go one level further down
            this.m_currentPoint.y += this.distY;
            this.m_currentPoint.x = x;

            this.setCurrentPositionFor(v);

            int sizeXofCurrent = basePositions.get(v);

            int lastX = x - sizeXofCurrent / 2;

            int sizeXofChild;
            int startXofChild;

            for (V element : graph.getSuccessors(v)) {
                sizeXofChild = this.basePositions.get(element);
                startXofChild = lastX + sizeXofChild / 2;
                buildTree(element, startXofChild);
                lastX = lastX + sizeXofChild + distX;
            }
            this.m_currentPoint.y -= this.distY;
        }
    }
    private int calculateDimensionX(V v) {

        int size = 0;
        int childrenNum = graph.getSuccessors(v).size();

        if (childrenNum != 0) {
            for (V element : graph.getSuccessors(v)) {
                size += calculateDimensionX(element) + distX;
            }
        }
        size = Math.max(0, size - distX);
        basePositions.put(v, size);

        return size;
    }

    private int calculateDimensionX(Collection<V> roots) {

    	int size = 0;
    	for(V v : roots) {
    		int childrenNum = graph.getSuccessors(v).size();

    		if (childrenNum != 0) {
    			for (V element : graph.getSuccessors(v)) {
    				size += calculateDimensionX(element) + distX;
    			}
    		}
    		size = Math.max(0, size - distX);
    		basePositions.put(v, size);
    	}

    	return size;
    }
    
    public int getDepth(V v) {
        int depth = 0;
        for (V c : graph.getSuccessors(v)) {

            if (graph.getSuccessors(c).size() == 0) {
                depth = 0;
            } else {
                depth = Math.max(depth, getDepth(c));
            }
        }

        return depth + 1;
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
        buildTree();
    }

    private void setCurrentPositionFor(V vertex) {
    	locations.get(vertex).setLocation(m_currentPoint);
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
		if(graph instanceof Forest) {
			this.graph = (Forest<V,E>)graph;
		} else {
			throw new IllegalArgumentException("graph must be a Forest");
		}
	}

	public void setInitializer(Transformer<V, Point2D> initializer) {
	}
	
	public Point2D getCenter() {
		return new Point2D.Double(size.getWidth()/2,size.getHeight()/2);
	}

	public void setLocation(V v, Point2D location) {
			locations.get(v).setLocation(location);
	}
	
	public Point2D transform(V v) {
			return locations.get(v);
	}
	
	private Point2D getMaxXY() {
		double maxx = 0;
		double maxy = 0;
		for(Point2D p : locations.values()) {
			maxx = Math.max(maxx, p.getX());
			maxy = Math.max(maxy, p.getY());
		}
		return new Point2D.Double(maxx,maxy);
	}
}
