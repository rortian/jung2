/*
 * Created on Feb 3, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.graph;

/**
 * Defines the operations available to a tree.
 * What we refer to as a "tree" here is actually (in the terminology of graph theory) a
 * rooted tree.  (That is, there is a designated single vertex--the <i>root</i>--from which we measure
 * an unweighted distance for each vertex which we call its <i>depth</i>; the maximum over all such 
 * depths is the tree's <i>height</i>.)
 * 
 * @author Joshua O'Madadhain
 */
public interface Tree<V,E> extends Graph<V,E>
{
    public int getDepth(V vertex);
    
    public int getHeight();
    
    public V getRoot();
}
