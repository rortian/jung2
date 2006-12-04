/*
 * Created on Jul 19, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import cern.jet.random.engine.DRand;


public class RandomVertexLocationDecorator<V> implements Transformer<V,Point2D> {
    Map<V,Point2D> v_locations;
    
    public RandomVertexLocationDecorator(Dimension d) {
    	this(d, (int)(new Date().getTime()));
    }
    
    public RandomVertexLocationDecorator(final Dimension d, int seed) {
    	final DRand rand = new DRand(seed);
        v_locations = LazyMap.decorate(new HashMap<V,Point2D>(), new Transformer<V,Point2D>() {
			public Point2D transform(V v) {
				return new Point2D.Double(rand.nextDouble() * d.width, rand.nextDouble() * d.height);
			}});
    }
    
    /**
     * Resets all vertex locations returned by <code>getLocation</code>
     * to new (random) locations.
     */
    public void reset() {
        v_locations.clear();
    }
    
    public Point2D transform(V v) {
        return v_locations.get(v);
    }

    public Collection<V> getVertices() {
        return v_locations.keySet();
    }
}
