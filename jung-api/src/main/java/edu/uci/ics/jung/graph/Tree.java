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
package edu.uci.ics.jung.graph;

import java.util.Collection;

/**
 * A subtype of <code>Graph</code> which is a (directed, rooted) tree.
 * What we refer to as a "tree" here is actually (in the terminology of graph theory) a
 * rooted tree.  (That is, there is a designated single vertex--the <i>root</i>--from which we measure
 * the shortest path to each vertex, which we call its <i>depth</i>; the maximum over all such 
 * depths is the tree's <i>height</i>.  Note that for a tree, there is exactly
 * one unique path from the root to any vertex.)
 * 
 * @author Joshua O'Madadhain
 */
public interface Tree<V,E> extends Forest<V,E>
{
    /**
     * Returns the (unweighted) distance of <code>vertex</code> 
     * from the root of this tree.
     * @param vertex    the vertex whose depth is to be returned.
     * @return the length of the shortest unweighted path 
     * from <code>vertex</code> to the root of this tree
     * @see #getHeight()
     */
    public int getDepth(V vertex);
    
    /**
     * Returns the maximum depth in this tree.
     * @return the maximum depth in this tree
     * @see #getDepth(Object)
     */
    public int getHeight();
    
    /**
     * Returns the root of this tree.
     * The root is defined to be the vertex (designated either at the tree's
     * creation time, or as the first vertex to be added) with respect to which 
     * vertex depth is measured.
     * @return the root of this tree
     */
    public V getRoot();
    
    /**
     * Returns the parent of <code>vertex</code> in this tree.
     * (If <code>vertex</code> is the root, returns <code>null</code>.)
     * The parent of a vertex is defined as being its predecessor in the 
     * (unique) shortest path from the root to this vertex.
     * This is a convenience method which is equivalent to 
     * <code>Graph.getPredecessors(vertex).iterator().next()</code>.
     * @return the parent of <code>vertex</code> in this tree
     * @see Graph#getPredecessors(Object)
     * @see #getParentEdge(Object)
     */
    public V getParent(V vertex);
    
    /**
     * Returns the edge connecting <code>vertex</code> to its parent in
     * this tree.
     * (If <code>vertex</code> is the root, returns <code>null</code>.)
     * The parent of a vertex is defined as being its predecessor in the 
     * (unique) shortest path from the root to this vertex.
     * This is a convenience method which is equivalent to 
     * <code>Graph.getInEdges(vertex).iterator().next()</code>,
     * and also to <code>Graph.findEdge(vertex, getParent(vertex))</code>.
     * @return the edge connecting <code>vertex</code> to its parent, or 
     * <code>null</code> if <code>vertex</code> is the root
     * @see Graph#getInEdges(Object)
     * @see #getParent(Object)
     */
    public E getParentEdge(V vertex);
    
    /**
     * Returns the children of <code>vertex</code> in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getSuccessors(vertex)</code>. 
     * @param vertex the vertex whose children are to be returned
     * @return the <code>Collection</code> of children of <code>vertex</code> 
     * in this tree
     * @see Graph#getSuccessors(Object)
     * @see #getChildEdges(Object)
     */
    public Collection<V> getChildren(V vertex);
    
    /**
     * Returns the edges connecting <code>vertex</code> to its children 
     * in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getOutEdges(vertex)</code>. 
     * @param vertex the vertex whose child edges are to be returned
     * @return the <code>Collection</code> of edges connecting 
     * <code>vertex</code> to its children in this tree
     * @see Graph#getOutEdges(Object)
     * @see #getChildren(Object)
     */
    public Collection<E> getChildEdges(V vertex);
    
    /**
     * Returns the number of children that <code>vertex</code> has in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getSuccessorCount(vertex)</code>. 
     * @param vertex the vertex whose child edges are to be returned
     * @return the <code>Collection</code> of edges connecting 
     * <code>vertex</code> to its children in this tree
     * @see #getChildEdges(Object)
     * @see #getChildren(Object)
     * @see Graph#getSuccessorCount(Object)
     */
    public int getChildCount(V vertex);
}
