/*
 * Created on Jul 18, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.decorators;

import javax.swing.Icon;

/**
 * Returns the specified label for all vertices.  Useful for
 * specifying "no label".
 * 
 * @author Tom Nelson - RABA Technologies
 */
public class ConstantVertexIconFunction<V> implements VertexIconFunction<V> {
//    protected Image image;
    protected Icon icon;
    
    public ConstantVertexIconFunction() {
    }

//    public ConstantVertexIconFunction(Image image) {
//        this.image = image;
//    }

    public ConstantVertexIconFunction(Icon icon) {
        this.icon = icon;
    }

    /**
     * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(ArchetypeVertex)
     */
//    public Image getImage(ArchetypeVertex v) {
//        return image;
//    }

	public Icon getIcon(V v) {
		return icon;
	}
}
