/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Apr 13, 2004
 */
package edu.uci.ics.jung.visualization.decorators;

import java.util.Set;

/**
 * Labels vertices by their toString. This class functions as a drop-in
 * replacement for the default StringLabeller method. This class does not
 * guarantee unique labels; or even consistent ones; as a result,
 * getVertexByLabel will always return NULL.
 * 
 * @author danyelf
 */
public class ToStringLabeller<V> extends StringLabeller<V> {

	/**
	 * This method is not meaningful; it throws an IllegalArgumentException
	 */
	public void assignDefaultLabels(Set vertices, int offset)
    {
		throw new IllegalArgumentException();
	}
	/**
	 * This method is not meaningful; it throws an IllegalArgumentException
	 */
	public V removeLabel(String string) {
		throw new IllegalArgumentException();
	}

    /**
     * Retunrs v.toString()
     */
    public String getLabel(V v) {
        return v.toString();
    }

    /**
     * Always returns null: this impl doesn't keep a table, and so can't
     * meaningfully address this.
     */
    public V getVertex(String label) {
        return null;
    }

    /**
     * This method always throws an IllegalArgument exception: you cannot
     * externally set the setstring method.
     */
    public void setLabel(V v, String l) throws UniqueLabelException {
        throw new IllegalArgumentException(
                "Can't manually set labels on a ToString labeller");
    }
}