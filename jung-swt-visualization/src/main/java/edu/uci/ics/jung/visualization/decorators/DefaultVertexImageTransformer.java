/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 1, 2005
 */

package edu.uci.ics.jung.visualization.decorators;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.graphics.Image;

/**
 * A simple, stateful VertexIconFunction.
 * Stores icons in a Map keyed on the Vertex
 * 
 * @author Tom Nelson 
 *
 *
 */
public class DefaultVertexImageTransformer<V> implements Transformer<V,Image> {
     
    /**
     * icon storage
     */
     protected Map<V,Image> imageMap = new HashMap<V,Image>();

     /**
      * Returns the icon storage as a <code>Map</code>.
      */
    public Map<V,Image> getImageMap() {
		return imageMap;
	}

    /**
     * Sets the icon storage to the specified <code>Map</code>.
     */
	public void setImageMap(Map<V,Image> imageMap) {
		this.imageMap = imageMap;
	}

    /**
     * Returns the <code>Icon</code> associated with <code>v</code>.
     */
	public Image transform(V v) {
		return imageMap.get(v);
	}
}
