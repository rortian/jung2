/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.algorithms.util.RandomLocationTransformer;

/**
 * Implements the Fruchterman-Reingold algorithm for node layout.
 * 
 * @author Scott White, Yan-Biao Boey, Danyel Fisher
 */
public class FRLayout2<V, E> extends AbstractLayout<V, E> implements IterativeContext {

    private double forceConstant;

    private double temperature;

    private int currentIteration;

    private int mMaxIterations = 700;
    
    private Map<V, Point2D> frVertexData = 
    	LazyMap.decorate(new HashMap<V,Point2D>(), new Factory<Point2D>() {
    		public Point2D create() {
    			return new Point2D.Double();
    		}});

    private double attraction_multiplier = 0.75;
    
    private double attraction_constant;
    
    private double repulsion_multiplier = 0.75;
    
    private double repulsion_constant;
    
    private Rectangle2D innerBounds = new Rectangle2D.Double();
    
    public FRLayout2(Graph<V, E> g) {
        super(g);
    }
    
    public FRLayout2(Graph<V, E> g, Dimension d) {
        super(g, new RandomLocationTransformer<V>(d), d);
        initialize();
    }
    
    /* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.layout.AbstractLayout#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(Dimension size) {
		setInitializer(new RandomLocationTransformer<V>(size));
		super.setSize(size);
		double t = size.width/50.0;
		innerBounds.setFrameFromDiagonal(t,t,size.width-t,size.height-t);
	}

	public void setAttractionMultiplier(double attraction) {
        this.attraction_multiplier = attraction;
    }
    
    public void setRepulsionMultiplier(double repulsion) {
        this.repulsion_multiplier = repulsion;
    }
    
	public void reset() {
		doInit();
	}
    
    public void initialize() {
    	doInit();
    }

    private void doInit() {
    	Graph<V,E> graph = getGraph();
    	Dimension d = getSize();
    	if(graph != null && d != null) {
    		currentIteration = 0;
    		temperature = d.getWidth() / 10;

    		forceConstant = 
    			Math
    			.sqrt(d.getHeight()
    					* d.getWidth()
    					/ graph.getVertexCount());

    		attraction_constant = attraction_multiplier * forceConstant;
    		repulsion_constant = repulsion_multiplier * forceConstant;
    	}
    }

    private double EPSILON = 0.000001D;

    /**
     * Moves the iteration forward one notch, calculation attraction and
     * repulsion between vertices and edges and cooling the temperature.
     */
    public synchronized void step() {
        currentIteration++;

        /**
         * Calculate repulsion
         */
        while(true) {
            
            try {
                for(V v1 : getGraph().getVertices()) {
                    calcRepulsion(v1);
                }
                break;
            } catch(ConcurrentModificationException cme) {}
        }

        /**
         * Calculate attraction
         */
        while(true) {
            try {
                for(E e : getGraph().getEdges()) {
                    calcAttraction(e);
                }
                break;
            } catch(ConcurrentModificationException cme) {}
        }


        while(true) {
            try {    
                for(V v : getGraph().getVertices()) {
                    if (isLocked(v)) continue;
                    calcPositions(v);
                }
                break;
            } catch(ConcurrentModificationException cme) {}
        }
        cool();
    }

    public synchronized void calcPositions(V v) {
        Point2D fvd = this.frVertexData.get(v);
        if(fvd == null) return;
        Point2D xyd = transform(v);
        double deltaLength = Math.max(EPSILON, 
        		Math.sqrt(fvd.getX()*fvd.getX()+fvd.getY()*fvd.getY()));

        double newXDisp = fvd.getX() / deltaLength
                * Math.min(deltaLength, temperature);

        assert Double.isNaN(newXDisp) == false : "Unexpected mathematical result in FRLayout:calcPositions [xdisp]";

        double newYDisp = fvd.getY() / deltaLength
                * Math.min(deltaLength, temperature);
        double newX = xyd.getX()+Math.max(-5, Math.min(5,newXDisp));
        double newY = xyd.getY()+Math.max(-5, Math.min(5,newYDisp));
        
        newX = Math.max(innerBounds.getMinX(), Math.min(newX, innerBounds.getMaxX()));
        newY = Math.max(innerBounds.getMinY(), Math.min(newY, innerBounds.getMaxY()));
        
        xyd.setLocation(newX, newY);

    }

    public void calcAttraction(E e) {
    	Pair<V> endpoints = getGraph().getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        Point2D p1 = getLocation(v1);
        Point2D p2 = getLocation(v2);
        if(p1 == null || p2 == null) return;
        double xDelta = p1.getX() - p2.getX();
        double yDelta = p1.getY() - p2.getY();

        double deltaLength = Math.max(EPSILON, p1.distance(p2));

        double force = deltaLength  / attraction_constant;

        assert Double.isNaN(force) == false : "Unexpected mathematical result in FRLayout:calcPositions [force]";

        double dx = xDelta * force;
        double dy = yDelta * force;
        Point2D fvd1 = frVertexData.get(v1);
        fvd1.setLocation(fvd1.getX()-dx, fvd1.getY()-dy);
        Point2D fvd2 = frVertexData.get(v2);
        fvd2.setLocation(fvd2.getX()+dx, fvd2.getY()+dy);
    }

    public void calcRepulsion(V v1) {
        Point2D fvd1 = frVertexData.get(v1);
        if(fvd1 == null) return;
        fvd1.setLocation(0, 0);

        try {
            for(V v2 : getGraph().getVertices()) {

                if (v1 != v2) {
                    Point2D p1 = getLocation(v1);
                    Point2D p2 = getLocation(v2);
                    if(p1 == null || p2 == null) continue;
                    double xDelta = p1.getX() - p2.getX();
                    double yDelta = p1.getY() - p2.getY();
                    
                    double deltaLength = Math.max(EPSILON, p1.distanceSq(p2));
                    
                    double force = (repulsion_constant * repulsion_constant);// / deltaLength;
                    
                    assert Double.isNaN(force) == false : "Unexpected mathematical result in FRLayout:calcPositions [repulsion]";
                    
                    fvd1.setLocation(fvd1.getX()+(xDelta / deltaLength) * force,
                    		fvd1.getY()+(yDelta / deltaLength) * force);
                }
            }
        } catch(ConcurrentModificationException cme) {
            calcRepulsion(v1);
        }
    }

    private void cool() {
        temperature *= (1.0 - currentIteration / (double) mMaxIterations);
    }

    public void setMaxIterations(int maxIterations) {
        mMaxIterations = maxIterations;
    }

    /**
     * This one is an incremental visualization.
     */
    public boolean isIncremental() {
        return true;
    }

    /**
     * Returns true once the current iteration has passed the maximum count,
     * <tt>MAX_ITERATIONS</tt>.
     */
    public boolean done() {
        if (currentIteration > mMaxIterations) { 
            return true; 
        } 
        return false;
    }
}