/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Apr 2, 2005
 */
package edu.uci.ics.jung.visualization;

import java.awt.ItemSelectable;
import java.util.Set;

/**
 * An interface for classes that keep track of the "picked" state
 * of edges and vertices.
 * 
 * @author Tom Nelson
 * @author Joshua O'Madadhain
 */
public interface PickedState<T> extends PickedInfo<T>, ItemSelectable
{
    /**
     * Marks <code>v</code> as "picked" if <code>b == true</code>,
     * and unmarks <code>v</code> as picked if <code>b == false</code>.
     * @return the "picked" state of <code>v</code> prior to this call
     */
    boolean pick(T v, boolean b);
    
    /**
     * Marks <code>e</code> as "picked" if <code>b == true</code>,
     * and unmarks <code>e</code> as picked if <code>b == false</code>.
     * @return the "picked" state of <code>e</code> prior to this call
     */
//    boolean pick(E e, boolean b);
    
    /**
     * Clears the "picked" state from all vertices.
     */
    void clear();
    
    /**
     * Returns all "picked" vertices.
     */
    Set<T> getPicked();
    
    /** 
     * Returns <code>true</code> if <code>v</code> is currently "picked".
     */
    boolean isPicked(T v);
    
    /**
     * Clears the "picked" state from all edges.
     */
//    void clearPickedEdges();

    /**
     * Returns all "picked" edges.
     */
//    Set getPickedEdges();
    
    /** 
     * Returns <code>true</code> if <code>e</code> is currently "picked".
     */
//    boolean isPicked(E e);
    
    /**
     * Adds a listener to this instance.
     * @deprecated Use addItemListener
     * @param pel
     */
//    void addListener(PickEventListener pel);
    
    /**
     * Removes a listener from this instance.
     * @deprecated Use removeItemListener
     * @param pel
     */
//    void removeListener(PickEventListener pel);
}