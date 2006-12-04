/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */

package edu.uci.ics.jung.visualization.subLayout;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedHashSet;

import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.layout.LayoutDecorator;

/**
 * Extends the base decorator class and overrides methods to 
 * cause the location methods to check with the sublayouts
 * for location information
 * 
 * @author Tom Nelson - RABA Technologies
 *
 *
 */
public class SubLayoutDecorator<V, E> extends LayoutDecorator<V,E> {

    final protected Collection<SubLayout<V>> subLayouts = 
        new LinkedHashSet<SubLayout<V>>();
    
    public SubLayoutDecorator(Layout<V,E> delegate) {
        super(delegate);
    }
    
    public void addSubLayout(SubLayout<V> subLayout) {
        subLayouts.add(subLayout);
        fireStateChanged();
    }
    
    public boolean removeSubLayout(SubLayout<V> subLayout) {
        boolean wasThere = subLayouts.remove(subLayout);
        fireStateChanged();
        return wasThere;
    }
    
    public void removeAllSubLayouts() {
        subLayouts.clear();
        fireStateChanged();
    }
    
    protected Point2D getLocationInSubLayout(V v) {
        Point2D location = null;
        for(SubLayout<V> subLayout : subLayouts) {
//        for(Iterator iterator=subLayouts.iterator(); iterator.hasNext(); ) {
//            SubLayout subLayout = (SubLayout)iterator.next();
            location = subLayout.getLocation(v);
            if(location != null) {
                break;
            }
        }
        return location;
    }
    
    public Point2D getLocation(V v) {
        Point2D p = getLocationInSubLayout(v);
        if(p != null) {
            return p;
        } else {
            return super.transform(v);
        }
    }
    
    public void forceMove(V picked, double x, double y) {
        Point2D p = getLocationInSubLayout(picked);
        if(p != null) {
            p.setLocation(x, y);
        } else {
            super.setLocation(picked, p);
        }
        fireStateChanged();
    }

//    public void applyFilter(Graph<V, E> subgraph) {
//        // TODO Auto-generated method stub
//        
//    }

    public Collection<V> getVertices() {
        // TODO Auto-generated method stub
        return null;
    }

}
