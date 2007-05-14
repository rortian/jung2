/*
* Created on Mar 10, 2005
*
* Copyright (c) 2005, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.visualization.decorators;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.picking.PickedInfo;

/**
 * Supplies an Icon for each vertex according to the <code>Icon</code>
 * parameters given in the constructor, so that picked and
 * non-picked vertices can be made to look different.
 */
public class PickableVertexIconTransformer<V> implements Transformer<V,Image> {

    protected Image icon;
    protected Image picked_icon;
    protected PickedInfo<V> pi;
    
    /**
     * 
     * @param pi            specifies which vertices report as "picked"
     * @param icon    <code>Icon</code> used to represent vertices
     * @param picked_icon  <code>Icon</code> used to represent picked vertices
     */
    public PickableVertexIconTransformer(PickedInfo<V> pi, Image icon, Image picked_icon)
    {
        if (pi == null)
            throw new IllegalArgumentException("PickedInfo instance must be non-null");
        this.pi = pi;
        this.icon = icon;
        this.picked_icon = picked_icon;
    }

    /**
     * Returns the appropriate <code>Icon</code>, depending on picked state.
     */
	public Image transform(V v) {
        if (pi.isPicked(v))
            return picked_icon;
        else
            return icon;
	}
}
