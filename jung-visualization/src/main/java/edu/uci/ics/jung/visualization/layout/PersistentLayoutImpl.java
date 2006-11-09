/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Oct 8, 2004
 *
 */
package edu.uci.ics.jung.visualization.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.visualization.RadiusGraphElementAccessor;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;


/**
 * Implementation of PersistentLayout.
 * Defers to another layout until 'restore' is called,
 * then it uses the saved vertex locations
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 *  
 */
public class PersistentLayoutImpl<V, E> extends LayoutDecorator<V,E>
    implements PersistentLayout<V,E> {

    protected DefaultChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);

    /**
     * a container for Vertices
     */
    protected Map<V,Point> map;
    
    /**
     * a collection of Vertices that should not move
     */
    protected Set<V> dontmove;

    /**
     * whether the graph is locked (stops the VisualizationViewer rendering thread)
     */
    protected boolean locked;

    protected RadiusGraphElementAccessor elementAccessor;

    /**
     * create an instance with a passed layout
     * create containers for graph components
     * @param layout 
     */
    public PersistentLayoutImpl(Layout<V,E> layout) {
        super(layout);
        this.map = new HashMap<V,Point>();
        this.dontmove = new HashSet<V>();
        this.elementAccessor = new RadiusGraphElementAccessor<V,E>();
        if(layout instanceof ChangeEventSupport) {
            ((ChangeEventSupport)layout).addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    fireStateChanged();
                }
            });
        }
    }

    /**
     * This method calls <tt>initialize_local_vertex</tt> for each vertex, and
     * also adds initial coordinate information for each vertex. (The vertex's
     * initial location is set by calling <tt>initializeLocation</tt>.
     */
    protected void initializeLocations() {
        for(V v : getGraph().getVertices()) {
            Point2D coord = delegate.getLocation(v);
            if (!dontmove.contains(v))
                initializeLocation(v, coord, getCurrentSize());
        }
    }


    /**
     * Sets persisted location for a vertex within the dimensions of the space.
     * If the vertex has not been persisted, sets a random location. If you want
     * to initialize in some different way, override this method.
     * 
     * @param v
     * @param coord
     * @param d
     */
    protected void initializeLocation(V v, Point2D coord, Dimension d) {
        double x;
        double y;
        Point point = map.get(v);
        if (point == null) {
            x = Math.random() * d.getWidth();
            y = Math.random() * d.getHeight();
        } else {
            x = point.x;
            y = point.y;
        }
        coord.setLocation(x, y);
    }

    /**
     * save the Vertex locations to a file
     * @param fileName the file to save to	
     * @throws an IOException if the file cannot be used
     */
    public void persist(String fileName) throws IOException {

        for(V v : getGraph().getVertices()) {

            Point p = new Point(getX(v), getY(v));
            map.put(v, p);
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                fileName));
        oos.writeObject(map);
        oos.close();
    }

    /**
     * Restore the graph Vertex locations from a file
     * @param fileName the file to use
     * @throws IOException for file problems
     * @throws ClassNotFoundException for classpath problems
     */
    @SuppressWarnings("unchecked")
	public void restore(String fileName) throws IOException,
            ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                fileName));
        map = (Map) ois.readObject();
        ois.close();
        initializeLocations();
        locked = true;
        fireStateChanged();
    }

    public void lock(boolean locked) {
        this.locked = locked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.uci.ics.jung.visualization.Layout#incrementsAreDone()
     */
    public boolean incrementsAreDone() {
        return locked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.uci.ics.jung.visualization.Layout#lockVertex(edu.uci.ics.jung.graph.Vertex)
     */
    public void lockVertex(V v) {
        dontmove.add(v);
        delegate.lockVertex(v);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.uci.ics.jung.visualization.Layout#unlockVertex(edu.uci.ics.jung.graph.Vertex)
     */
    public void unlockVertex(V v) {
        dontmove.remove(v);
        delegate.unlockVertex(v);
    }

    public void update() {
        if(delegate instanceof LayoutMutable) {
            ((LayoutMutable<V,E>)delegate).update();
        }
    }

    public void addChangeListener(ChangeListener l) {
        if(delegate instanceof ChangeEventSupport) {
            ((ChangeEventSupport)delegate).addChangeListener(l);
        }
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        if(delegate instanceof ChangeEventSupport) {
            ((ChangeEventSupport)delegate).removeChangeListener(l);
        }
        changeSupport.removeChangeListener(l);
    }

    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }

    public Collection<V> getVertices() {
        return getGraph().getVertices();
        
    }

}