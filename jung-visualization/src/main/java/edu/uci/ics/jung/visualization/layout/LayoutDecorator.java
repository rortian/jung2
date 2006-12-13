/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */

package edu.uci.ics.jung.visualization.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;

import sun.security.provider.certpath.Vertex;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

/**
 * a pure decorator for the Layout interface. Intended to be overridden
 * to provide specific behavior decoration
 * @see PersistentLayoutImpl
 * @author Tom Nelson 
 *
 *
 */
public abstract class LayoutDecorator<V, E> implements Layout<V, E>, ChangeEventSupport, IterativeContext {
    
    protected Layout<V, E> delegate;
    
    protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);

    public LayoutDecorator(Layout<V, E> delegate) {
        this.delegate = delegate;
    }

    /**
     * getter for the delegate
     * @return the delegate
     */
    public Layout getDelegate() {
        return delegate;
    }

    /**
     * setter for the delegate
     * @param delegate the new delegate
     */
    public void setDelegate(Layout<V,E> delegate) {
        this.delegate = delegate;
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#step()
     */
    public void step() {
    	if(delegate instanceof IterativeContext) {
    		((IterativeContext)delegate).step();
    	}
    }

    /**
	 * 
	 * @see edu.uci.ics.jung.visualization.layout.Layout#initialize()
	 */
	public void initialize() {
		delegate.initialize();
	}

	/**
	 * @param initializer
	 * @see edu.uci.ics.jung.visualization.layout.Layout#setInitializer(org.apache.commons.collections15.Transformer)
	 */
	public void setInitializer(Transformer<V, Point2D> initializer) {
		delegate.setInitializer(initializer);
	}

	/**
	 * @param v
	 * @param location
	 * @see edu.uci.ics.jung.visualization.layout.Layout#setLocation(java.lang.Object, java.awt.geom.Point2D)
	 */
	public void setLocation(V v, Point2D location) {
		delegate.setLocation(v, location);
	}

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getCurrentSize()
     */
    public Dimension getSize() {
        return delegate.getSize();
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getGraph()
     */
    public Graph<V, E> getGraph() {
        return delegate.getGraph();
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getLocation(edu.uci.ics.jung.graph.ArchetypeVertex)
     */
    public Point2D transform(V v) {
        return delegate.transform(v);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#done()
     */
    public boolean done() {
    	if(delegate instanceof IterativeContext) {
    		return ((IterativeContext)delegate).done();
    	}
    	return true;
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#lockVertex(edu.uci.ics.jung.graph.Vertex)
     */
    public void lock(V v, boolean state) {
        delegate.lock(v, state);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#isLocked(Vertex)
     */
    public boolean isLocked(V v) {
        return delegate.isLocked(v);
    }
    
    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#resize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
        delegate.setSize(d);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#reset()
     */
    public void reset() {
    	delegate.reset();
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }
    
    public void setGraph(Graph<V, E> graph) {
        delegate.setGraph(graph);
    }
}
