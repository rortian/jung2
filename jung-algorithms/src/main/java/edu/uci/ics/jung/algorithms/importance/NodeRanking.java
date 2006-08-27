/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.importance;


/**
 * A data container for a node ranking.
 * 
 * @author Scott White
 */
public class NodeRanking<V> extends Ranking {

    /**
     * Allows the values to be set on construction.
     * @param originalPos The original (0-indexed) position of the instance being ranked
     * @param rankScore The actual rank score (normally between 0 and 1)
     * @param vertex The vertex being ranked
     */
    public NodeRanking(int originalPos, double rankScore, V node) {
        super(originalPos, rankScore);
        this.node = node;
    }

    /**
     * The vertex being ranked
     */
    protected V node;
    
    public V getNode() {
    	return node;
    }
}
