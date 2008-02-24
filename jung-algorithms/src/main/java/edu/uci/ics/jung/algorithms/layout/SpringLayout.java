/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.algorithms.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.Graph;

/**
 * The SpringLayout package represents a visualization of a set of nodes. The
 * SpringLayout, which is initialized with a Graph, assigns X/Y locations to
 * each node. When called <code>relax()</code>, the SpringLayout moves the
 * visualization forward one step.
 * 
 * @author Danyel Fisher
 * @author Joshua O'Madadhain
 */
public class SpringLayout<V, E> extends AbstractLayout<V,E> implements IterativeContext {

    protected double stretch = 0.70;
    protected LengthFunction<E> lengthFunction;
    protected int repulsion_range = 100;
    protected double force_multiplier = 1.0 / 3.0;
    
    Map<E, SpringEdgeData<E>> springEdgeData = 
    	LazyMap.decorate(new HashMap<E, SpringEdgeData<E>>(),
    			new Transformer<E,SpringEdgeData<E>>() {
					public SpringEdgeData<E> transform(E e) {
						return new SpringEdgeData<E>(e);
					}});
    Map<V, SpringVertexData> springVertexData = 
    	LazyMap.decorate(new HashMap<V, SpringVertexData>(),
    			new Factory<SpringVertexData>() {
					public SpringVertexData create() {
						return new SpringVertexData();
					}});

    /**
     * Constructor for a SpringLayout for a raw graph with associated
     * dimension--the input knows how big the graph is. Defaults to the unit
     * length function.
     */
    public SpringLayout(Graph<V,E> g) {
        this(g, UNITLENGTHFUNCTION);
    }

    /**
     * Constructor for a SpringLayout for a raw graph with associated component.
     * 
     * @param g
     *            the input Graph
     * @param f
     *            the length function
     */
    public SpringLayout(Graph<V,E> g, LengthFunction<E> f) {
        super(g);
        this.lengthFunction = f;
    }

    /**
     * @return the current value for the stretch parameter
     * @see #setStretch(double)
     */
    public double getStretch() {
        return stretch;
    }
    
    /* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.layout.AbstractLayout#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(Dimension size) {
		if(initialized == false)
			setInitializer(new RandomLocationTransformer<V>(size));
		super.setSize(size);
	}
    
    /**
     * <p>Sets the stretch parameter for this instance.  This value 
     * specifies how much the degrees of an edge's incident vertices
     * should influence how easily the endpoints of that edge
     * can move (that is, that edge's tendency to change its length).</p>
     * 
     * <p>The default value is 0.70.  Positive values less than 1 cause
     * high-degree vertices to move less than low-degree vertices, and 
     * values > 1 cause high-degree vertices to move more than 
     * low-degree vertices.  Negative values will have unpredictable
     * and inconsistent results.</p>
     * @param stretch
     */
    public void setStretch(double stretch) {
        this.stretch = stretch;
    }
    
    /**
     * @return the current value for the node repulsion range
     * @see #setRepulsionRange(int)
     */
    public int getRepulsionRange() {
        return repulsion_range;
    }

    /**
     * Sets the node repulsion range (in drawing area units) for this instance.  
     * Outside this range, nodes do not repel each other.  The default value 
     * is 100.  Negative values are treated as their positive equivalents.
     * @param range
     */
    public void setRepulsionRange(int range) {
        this.repulsion_range = range;
    }
    
    /**
     * @return the current value for the edge length force multiplier
     * @see #setForceMultiplier(double)
     */
    public double getForceMultiplier() {
        return force_multiplier;
    }
    
    /**
     * Sets the force multiplier for this instance.  This value is used to 
     * specify how strongly an edge "wants" to be its default length
     * (higher values indicate a greater attraction for the default length),
     * which affects how much its endpoints move at each timestep.
     * The default value is 1/3.  A value of 0 turns off any attempt by the
     * layout to cause edges to conform to the default length.  Negative
     * values cause long edges to get longer and short edges to get shorter; use
     * at your own risk.
     */
    public void setForceMultiplier(double force) {
        this.force_multiplier = force;
    }
    
    public void initialize() {
    	Graph<V,E> graph = getGraph();
    	Dimension d = getSize();
    	if(graph != null && d != null) {

    		try {
    			for(E e : graph.getEdges()) {
    				SpringEdgeData<E> sed = getSpringData(e);
    				calcEdgeLength(sed, lengthFunction);
    			}
    		} catch(ConcurrentModificationException cme) {
    			initialize();
    		}
    	}
    }

    /* ------------------------- */

    protected void calcEdgeLength(SpringEdgeData<E> sed, LengthFunction<E> f) {
        sed.length = f.getLength(sed.e);
    }

    /* ------------------------- */


    /**
     * Relaxation step. Moves all nodes a smidge.
     */
    public void step() {
    	try {
    		for(V v : getGraph().getVertices()) {
    			SpringVertexData svd = getSpringData(v);
    			if (svd == null) {
    				continue;
    			}
    			svd.dx /= 4;
    			svd.dy /= 4;
    			svd.edgedx = svd.edgedy = 0;
    			svd.repulsiondx = svd.repulsiondy = 0;
    		}
    	} catch(ConcurrentModificationException cme) {
    		step();
    	}

    	relaxEdges();
    	calculateRepulsion();
    	moveNodes();
//    	this.fireStateChanged();
    }

    protected V getAVertex(E e) {
    	
        return getGraph().getIncidentVertices(e).iterator().next();
    }

    protected void relaxEdges() {
    	try {
    		for(E e : getGraph().getEdges()) {

    			V v1 = getAVertex(e);
    			V v2 = getGraph().getOpposite(v1, e);

    			Point2D p1 = transform(v1);
    			Point2D p2 = transform(v2);
    			if(p1 == null || p2 == null) continue;
    			double vx = p1.getX() - p2.getX();
    			double vy = p1.getY() - p2.getY();
    			double len = Math.sqrt(vx * vx + vy * vy);

    			SpringEdgeData<E> sed = getSpringData(e);
    			if (sed == null) {
    				continue;
    			}
    			double desiredLen = sed.length;

    			// round from zero, if needed [zero would be Bad.].
    			len = (len == 0) ? .0001 : len;

    			double f = force_multiplier * (desiredLen - len) / len;

    			f = f * Math.pow(stretch, (getGraph().degree(v1) + getGraph().degree(v2) - 2));

    			// the actual movement distance 'dx' is the force multiplied by the
    			// distance to go.
    			double dx = f * vx;
    			double dy = f * vy;
    			SpringVertexData v1D, v2D;
    			v1D = getSpringData(v1);
    			v2D = getSpringData(v2);

    			sed.f = f;

    			v1D.edgedx += dx;
    			v1D.edgedy += dy;
    			v2D.edgedx += -dx;
    			v2D.edgedy += -dy;
    		}
    	} catch(ConcurrentModificationException cme) {
    		relaxEdges();
    	}
    }

    protected void calculateRepulsion() {
        try {
        for (V v : getGraph().getVertices()) {
            if (isLocked(v)) continue;

            SpringVertexData svd = getSpringData(v);
            if(svd == null) continue;
            double dx = 0, dy = 0;

            for (V v2 : getGraph().getVertices()) {
                if (v == v2) continue;
                Point2D p = transform(v);
                Point2D p2 = transform(v2);
                if(p == null || p2 == null) continue;
                double vx = p.getX() - p2.getX();
                double vy = p.getY() - p2.getY();
                double distance = vx * vx + vy * vy;
                if (distance == 0) {
                    dx += Math.random();
                    dy += Math.random();
                } else if (distance < repulsion_range * repulsion_range) {
                    double factor = 1;
                    dx += factor * vx / Math.pow(distance, 2);
                    dy += factor * vy / Math.pow(distance, 2);
                }
            }
            double dlen = dx * dx + dy * dy;
            if (dlen > 0) {
                dlen = Math.sqrt(dlen) / 2;
                svd.repulsiondx += dx / dlen;
                svd.repulsiondy += dy / dlen;
            }
        }
        } catch(ConcurrentModificationException cme) {
            calculateRepulsion();
        }
    }

    protected void moveNodes() {

        synchronized (getSize()) {
            try {
                for (V v : getGraph().getVertices()) {
                    if (isLocked(v)) continue;
                    SpringVertexData vd = getSpringData(v);
                    if(vd == null) continue;
                    Point2D xyd = transform(v);
                    
                    vd.dx += vd.repulsiondx + vd.edgedx;
                    vd.dy += vd.repulsiondy + vd.edgedy;
                    
                    // keeps nodes from moving any faster than 5 per time unit
                    xyd.setLocation(xyd.getX()+Math.max(-5, Math.min(5, vd.dx)),
                    		xyd.getY()+Math.max(-5, Math.min(5, vd.dy)));
                    
                    Dimension d = getSize();
                    int width = d.width;
                    int height = d.height;
                    
                    if (xyd.getX() < 0) {
                        xyd.setLocation(0, xyd.getY());//                     setX(0);
                    } else if (xyd.getX() > width) {
                        xyd.setLocation(width, xyd.getY());             //setX(width);
                    }
                    if (xyd.getY() < 0) {
                        xyd.setLocation(xyd.getX(),0);//setY(0);
                    } else if (xyd.getY() > height) {
                        xyd.setLocation(xyd.getX(), height);      //setY(height);
                    }
                    
                }
            } catch(ConcurrentModificationException cme) {
                moveNodes();
            }
        }
    }

    public SpringVertexData getSpringData(V v) {
        return springVertexData.get(v);
    }

    public SpringEdgeData<E> getSpringData(E e) {
            return springEdgeData.get(e);
    }

    public double getLength(E e) {
    	return springEdgeData.get(e).length;
    }

    /* ---------------Length Function------------------ */

    /**
     * If the edge is weighted, then override this method to show what the
     * visualized length is.
     * 
     * @author Danyel Fisher
     */
    public static interface LengthFunction<E> {

        public double getLength(E e);
    }

    /**
     * Returns all edges as the same length: the input value
     * @author danyelf
     */
    public static final class UnitLengthFunction<E> implements LengthFunction<E> {

        int length;

        public UnitLengthFunction(int length) {
            this.length = length;
        }

        public double getLength(E e) {
            return length;
        }
    }

    public static final LengthFunction UNITLENGTHFUNCTION = new UnitLengthFunction(
            30);

    /* ---------------User Data------------------ */

    protected static class SpringVertexData {

        public double edgedx;

        public double edgedy;

        public double repulsiondx;

        public double repulsiondy;

        public SpringVertexData() {
        }

        /** movement speed, x */
        public double dx;

        /** movement speed, y */
        public double dy;
    }

    protected static class SpringEdgeData<E> {

        public double f;

        public SpringEdgeData(E e) {
            this.e = e;
        }

        E e;

        double length;
    }

    /* ---------------Resize handler------------------ */

    public class SpringDimensionChecker extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            setSize(e.getComponent().getSize());
        }
    }

    /**
     * This one is an incremental visualization
     */
    public boolean isIncremental() {
        return true;
    }

    /**
     * For now, we pretend it never finishes.
     */
    public boolean done() {
        return false;
    }

	public void reset() {
		// no counter, do nothing.
//		locations.clear();
//		initialize();
	}
}