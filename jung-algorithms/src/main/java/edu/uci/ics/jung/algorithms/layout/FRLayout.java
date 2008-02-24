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
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.algorithms.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Implements the Fruchterman-Reingold algorithm for node layout.
 * 
 * @author Scott White, Yan-Biao Boey, Danyel Fisher
 */
public class FRLayout<V, E> extends AbstractLayout<V, E> implements IterativeContext {

    private double forceConstant;

    private double temperature;

    private int currentIteration;

    private int mMaxIterations = 700;
    
    private Map<V, FRVertexData> frVertexData = 
    	LazyMap.decorate(new HashMap<V,FRVertexData>(), new Factory<FRVertexData>() {
    		public FRVertexData create() {
    			return new FRVertexData();
    		}});

    private double attraction_multiplier = 0.75;
    
    private double attraction_constant;
    
    private double repulsion_multiplier = 0.75;
    
    private double repulsion_constant;
    
    private double max_dimension;
    
    public FRLayout(Graph<V, E> g) {
        super(g);
    }
    
    public FRLayout(Graph<V, E> g, Dimension d) {
        super(g, new RandomLocationTransformer<V>(d), d);
        initialize();
        max_dimension = Math.max(d.height, d.width);
    }
    
    /* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.layout.AbstractLayout#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(Dimension size) {
		if(initialized == false) {
			setInitializer(new RandomLocationTransformer<V>(size));
		}
		super.setSize(size);
        max_dimension = Math.max(size.height, size.width);
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
//                    if (isLocked(v1)) continue;
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
//                    fireStateChanged();
                }
                break;
            } catch(ConcurrentModificationException cme) {}
        }
        cool();
//        fireStateChanged();
    }

    public synchronized void calcPositions(V v) {
        FRVertexData fvd = getFRData(v);
        if(fvd == null) return;
        Point2D xyd = transform(v);
        double deltaLength = Math.max(EPSILON, Math.sqrt(fvd.disp
                .zDotProduct(fvd.disp)));

        double newXDisp = fvd.getXDisp() / deltaLength
                * Math.min(deltaLength, temperature);

        if (Double.isNaN(newXDisp)) { 
        	throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [xdisp]"); }

        double newYDisp = fvd.getYDisp() / deltaLength
                * Math.min(deltaLength, temperature);
        xyd.setLocation(xyd.getX()+newXDisp, xyd.getY()+newYDisp);

        double borderWidth = getSize().getWidth() / 50.0;
        double newXPos = xyd.getX();
        if (newXPos < borderWidth) {
            newXPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newXPos > (getSize().getWidth() - borderWidth)) {
            newXPos = getSize().getWidth() - borderWidth - Math.random()
                    * borderWidth * 2.0;
        }

        double newYPos = xyd.getY();
        if (newYPos < borderWidth) {
            newYPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newYPos > (getSize().getHeight() - borderWidth)) {
            newYPos = getSize().getHeight() - borderWidth
                    - Math.random() * borderWidth * 2.0;
        }

        xyd.setLocation(newXPos, newYPos);
    }

    public void calcAttraction(E e) {
    	Pair<V> endpoints = getGraph().getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        boolean v1_locked = isLocked(v1);
        boolean v2_locked = isLocked(v2);
        
        if(v1_locked && v2_locked) {
        	// both locked, do nothing
        	return;
        }
        Point2D p1 = transform(v1);
        Point2D p2 = transform(v2);
        if(p1 == null || p2 == null) return;
        double xDelta = p1.getX() - p2.getX();
        double yDelta = p1.getY() - p2.getY();

        double deltaLength = Math.max(EPSILON, Math.sqrt((xDelta * xDelta)
                + (yDelta * yDelta)));

        double force = (deltaLength * deltaLength) / attraction_constant;

        if (Double.isNaN(force)) { throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [force]"); }

        double dx = (xDelta / deltaLength) * force;
        double dy = (yDelta / deltaLength) * force;
        if(v1_locked == false) {
        	FRVertexData fvd1 = getFRData(v1);
        	fvd1.decrementDisp(dx, dy);
        }
        if(v2_locked == false) {
        	FRVertexData fvd2 = getFRData(v2);
        	fvd2.incrementDisp(dx, dy);
        }
    }

    public void calcRepulsion(V v1) {
        FRVertexData fvd1 = getFRData(v1);
        if(fvd1 == null) return;
        fvd1.setDisp(0, 0);

        try {
            for(V v2 : getGraph().getVertices()) {

//                if (isLocked(v2)) continue;
                if (v1 != v2) {
                    Point2D p1 = transform(v1);
                    Point2D p2 = transform(v2);
                    if(p1 == null || p2 == null) continue;
                    double xDelta = p1.getX() - p2.getX();
                    double yDelta = p1.getY() - p2.getY();
                    
                    double deltaLength = Math.max(EPSILON, Math
                            .sqrt((xDelta * xDelta) + (yDelta * yDelta)));
                    
                    double force = (repulsion_constant * repulsion_constant) / deltaLength;
                    
                    if (Double.isNaN(force)) { throw new RuntimeException(
                    "Unexpected mathematical result in FRLayout:calcPositions [repulsion]"); }
                    
                    fvd1.incrementDisp((xDelta / deltaLength) * force,
                            (yDelta / deltaLength) * force);
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

    public FRVertexData getFRData(V v) {
        return frVertexData.get(v);
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
        if (currentIteration > mMaxIterations || temperature < 1.0/max_dimension) 
        { 
            return true; 
        } 
        return false;
    }

    public static class FRVertexData {

        private DoubleMatrix1D disp;

        public FRVertexData() {
            initialize();
        }

        public void initialize() {
            disp = new DenseDoubleMatrix1D(2);
        }

        public double getXDisp() {
            return disp.get(0);
        }

        public double getYDisp() {
            return disp.get(1);
        }

        public void setDisp(double x, double y) {
            disp.set(0, x);
            disp.set(1, y);
        }

        public void incrementDisp(double x, double y) {
            disp.set(0, disp.get(0) + x);
            disp.set(1, disp.get(1) + y);
        }

        public void decrementDisp(double x, double y) {
            disp.set(0, disp.get(0) - x);
            disp.set(1, disp.get(1) - y);
        }
     }
}