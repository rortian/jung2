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

import sun.security.provider.certpath.Vertex;
import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

/**
 * a pure decorator for the Layout interface. Intended to be overridden
 * to provide specific behavior decoration
 * @see PersistentLayoutImpl
 * @author Tom Nelson - RABA Technologies
 *
 *
 */
public abstract class LayoutDecorator<V, E extends Edge<V>> implements Layout<V, E>, ChangeEventSupport {
    
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
    public void setDelegate(Layout delegate) {
        this.delegate = delegate;
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#advancePositions()
     */
    public void advancePositions() {
        delegate.advancePositions();
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#applyFilter(edu.uci.ics.jung.graph.Graph)
     */
    public void applyFilter(Graph subgraph) {
        delegate.applyFilter(subgraph);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#forceMove(edu.uci.ics.jung.graph.Vertex, double, double)
     */
    public void forceMove(V picked, double x, double y) {
        delegate.forceMove(picked, x, y);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getCurrentSize()
     */
    public Dimension getCurrentSize() {
        return delegate.getCurrentSize();
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
    public Point2D getLocation(V v) {
        return delegate.getLocation(v);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getStatus()
     */
    public String getStatus() {
        return delegate.getStatus();
    }

    /**
     * @see edu.uci.ics.jung.visualization.Layout#getVertex(double, double, double)
     */
//    public V getVertex(double x, double y, double maxDistance) {
//        return delegate.getVertex(x, y, maxDistance);
//    }

    /**
     * @see edu.uci.ics.jung.visualization.Layout#getVertex(double, double)
     */
//    public V getVertex(double x, double y) {
//        return delegate.getVertex(x, y);
//    }

    /**
     * @see edu.uci.ics.jung.visualization.VertexLocationFunction#getVertexIterator()
     */
//    public Iterator getVertexIterator() {
//        return delegate.getVertexIterator();
//    }

    /**
     * @see edu.uci.ics.jung.visualization.Layout#getVisibleEdges()
     */
//    public Set<E> getVisibleEdges() {
//        return delegate.getEdges();
//    }

    /**
     * @see edu.uci.ics.jung.visualization.Layout#getVisibleVertices()
     */
//    public Set<V> getVisibleVertices() {
//        return delegate.getVisibleVertices();
//    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getX(edu.uci.ics.jung.graph.Vertex)
     */
    public double getX(V v) {
        return delegate.getX(v);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#getY(edu.uci.ics.jung.graph.Vertex)
     */
    public double getY(V v) {
        return delegate.getY(v);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#incrementsAreDone()
     */
    public boolean incrementsAreDone() {
        return delegate.incrementsAreDone();
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#initialize(java.awt.Dimension)
     */
    public void initialize(Dimension currentSize) {
        delegate.initialize(currentSize);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#isIncremental()
     */
    public boolean isIncremental() {
        return delegate.isIncremental();
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#lockVertex(edu.uci.ics.jung.graph.Vertex)
     */
    public void lockVertex(V v) {
        delegate.lockVertex(v);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#isLocked(Vertex)
     */
    public boolean isLocked(V v)
    {
        return delegate.isLocked(v);
    }
    
    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#resize(java.awt.Dimension)
     */
    public void resize(Dimension d) {
        delegate.resize(d);
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#restart()
     */
    public void restart() {
        delegate.restart();
    }

    /**
     * @see edu.uci.ics.jung.visualization.layout.Layout#unlockVertex(edu.uci.ics.jung.graph.Vertex)
     */
    public void unlockVertex(V v) {
        delegate.unlockVertex(v);
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
}
