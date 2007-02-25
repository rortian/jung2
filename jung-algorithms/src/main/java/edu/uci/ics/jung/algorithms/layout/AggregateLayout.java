/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * 
 * 
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * Allows multiple layouts to be combined and manipulated
 * as one layout. The relaxer thread will step each layout
 * in sequence.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class AggregateLayout<V, E> implements Layout<V,E>, IterativeContext {

	protected Layout<V,E> delegate;
	protected Map<Layout<V,E>,Point2D> layouts = new HashMap<Layout<V,E>,Point2D>();

	public AggregateLayout(Layout<V, E> delegate) {
		this.delegate = delegate;
	}

	/**
	 * @return the delegate
	 */
	public Layout<V, E> getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(Layout<V, E> delegate) {
		this.delegate = delegate;
	}

	/**
	 * adds the passed layout as a sublayout, also specifying
	 * the center of where this sublayout should appear
	 * @param layout
	 * @param center
	 */
	public void put(Layout<V,E> layout, Point2D center) {
		layouts.put(layout,center);
	}
	
	/**
	 * returns the center of the passed layout
	 * @param layout
	 * @return
	 */
	public Point2D get(Layout<V,E> layout) {
		return layouts.get(layout);
	}
	
	public void remove(Layout<V,E> layout) {
		layouts.remove(layout);
	}
	
	public void removeAll() {
		layouts.clear();
	}
	/**
	 * @return
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#getGraph()
	 */
	public Graph<V, E> getGraph() {
		return delegate.getGraph();
	}

	/**
	 * @return
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#getSize()
	 */
	public Dimension getSize() {
		return delegate.getSize();
	}

	/**
	 * 
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#initialize()
	 */
	public void initialize() {
		delegate.initialize();
		for(Layout<V,E> layout : layouts.keySet()) {
			layout.initialize();
		}
	}

	/**
	 * Override to test if the passed vertex is locked in
	 * any of the layouts.
	 * @param v
	 * @return
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#isLocked(java.lang.Object)
	 */
	public boolean isLocked(V v) {
		boolean locked = false;
		for(Layout<V,E> layout : layouts.keySet()) {
			locked |= layout.isLocked(v);
		}
		locked |= delegate.isLocked(v);
		return locked;
	}

	/**
	 * override to lock or unlock this vertex in any layout with
	 * a subgraph containing it
	 * @param v
	 * @param state
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#lock(java.lang.Object, boolean)
	 */
	public void lock(V v, boolean state) {
		for(Layout<V,E> layout : layouts.keySet()) {
			if(layout.getGraph().getVertices().contains(v)) {
				layout.lock(v, state);
			}
		}
		delegate.lock(v, state);
	}

	/**
	 * 
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#reset()
	 */
	public void reset() {
		for(Layout<V,E> layout : layouts.keySet()) {
			layout.reset();
		}
		delegate.reset();
	}

	/**
	 * @param graph
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#setGraph(edu.uci.ics.jung.graph.Graph)
	 */
	public void setGraph(Graph<V, E> graph) {
		delegate.setGraph(graph);
	}

	/**
	 * @param initializer
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#setInitializer(org.apache.commons.collections15.Transformer)
	 */
	public void setInitializer(Transformer<V, Point2D> initializer) {
		delegate.setInitializer(initializer);
	}

	/**
	 * @param v
	 * @param location
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#setLocation(java.lang.Object, java.awt.geom.Point2D)
	 */
	public void setLocation(V v, Point2D location) {
		boolean wasInSublayout = false;
		for(Layout<V,E> layout : layouts.keySet()) {
			if(layout.getGraph().getVertices().contains(v)) {
				Point2D center = layouts.get(layout);
				// transform by the layout itself, but offset to the
				// center of the sublayout
				Dimension d = layout.getSize();

				AffineTransform at = 
					AffineTransform.getTranslateInstance(-center.getX()+d.width/2,-center.getY()+d.height/2);
				Point2D localLocation = at.transform(location, null);
				layout.setLocation(v, localLocation);
				wasInSublayout = true;
			}
		}
		if(wasInSublayout == false && getGraph().getVertices().contains(v)) {
			delegate.setLocation(v, location);
		}
	}

	/**
	 * @param d
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#setSize(java.awt.Dimension)
	 */
	public void setSize(Dimension d) {
		delegate.setSize(d);
	}
	
	public Map<Layout<V,E>,Point2D> getLayouts() {
		return layouts;
	}

	/**
	 * allow any sublayout first opportunity to specify the location
	 * of a vertex, then default to the delegate graph.
	 * @param input
	 * @return
	 * @see org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	public Point2D transform(V v) {
		boolean wasInSublayout = false;
		for(Layout<V,E> layout : layouts.keySet()) {
			if(layout.getGraph().getVertices().contains(v)) {
				wasInSublayout = true;
				Point2D center = layouts.get(layout);
				// transform by the layout itself, but offset to the
				// center of the sublayout
				Dimension d = layout.getSize();
				AffineTransform at = 
					AffineTransform.getTranslateInstance(center.getX()-d.width/2,
							center.getY()-d.height/2);
				return at.transform(layout.transform(v),null);
			}
		}
		if(wasInSublayout == false) {
			return delegate.transform(v);
		}
		return null;
	
	}

	/**
	 * Check all sublayouts.keySet() and the delegate layout, returning
	 * done == true iff all are done.
	 */
	public boolean done() {
		boolean done = true;
		for(Layout<V,E> layout : layouts.keySet()) {
			if(layout instanceof IterativeContext) {
				done &= ((IterativeContext)layout).done();
			}
		}
		if(delegate instanceof IterativeContext) {
			done &= ((IterativeContext)delegate).done();
		}
		return done;
	}

	/**
	 * call step on any sublayout that is also an IterativeContext
	 * and is not done
	 */
	public void step() {
		for(Layout<V,E> layout : layouts.keySet()) {
			if(layout instanceof IterativeContext) {
				IterativeContext context = (IterativeContext)layout;
				if(context.done() == false) {
					context.step();
				}
			}
		}
		if(delegate instanceof IterativeContext) {
			IterativeContext context = (IterativeContext)delegate;
			if(context.done() == false) {
				context.step();
			}
		}
	}
	
}
