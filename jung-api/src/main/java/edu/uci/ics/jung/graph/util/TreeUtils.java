/*
 * Created on Mar 3, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph.util;

import java.util.Collection;

import edu.uci.ics.jung.graph.Forest;

/**
 * Contains static methods for operating on instances of <code>Tree</code>.
 */
public class TreeUtils 
{
	
    /**
     * Returns the subtree of <code>tree</code> which is rooted at <code>root</code> as a <code>Forest<V,E></code>.
     * The tree returned is an independent entity, although it uses the same vertex and edge objects.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param tree the tree whose subtree is to be extracted
     * @param root the root of the subtree to be extracted
     * @return the subtree of <code>tree</code> which is rooted at <code>root</code>
     * @throws InstantiationException if a new tree of the same type cannot be created
     * @throws IllegalAccessException 
     */
	public static <V,E> Forest<V,E> getSubTree(Forest<V,E> tree, V root) throws InstantiationException, IllegalAccessException 
	{
	    if (!tree.containsVertex(root))
	        throw new IllegalArgumentException("Specified tree does not contain the specified root as a vertex");
		Forest<V,E> subTree = tree.getClass().newInstance();
		subTree.addVertex(root);
		growSubTree(tree, subTree, root);
		
		return subTree;
	}
	
	/**
     * Populates <code>subtree</code> with the subtree of <code>tree</code> 
     * which is rooted at <code>root</code>.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param tree the tree whose subtree is to be extracted
     * @param subtree the tree instance which is to be populated with the subtree of <code>tree</code>
     * @param root the root of the subtree to be extracted
	 */
	public static <V,E> void growSubTree(Forest<V,E> tree, Forest<V,E> subTree, V root) {
		if(tree.getSuccessorCount(root) > 0) {
			Collection<E> edges = tree.getOutEdges(root);
			for(E e : edges) {
				subTree.addEdge(e, tree.getEndpoints(e));
			}
			Collection<V> kids = tree.getSuccessors(root);
			for(V kid : kids) {
				growSubTree(tree, subTree, kid);
			}
		}
	}
	
	/**
	 * Connects <code>subTree</code> to <code>tree</code> by attaching it as a child 
	 * of <code>node</code> with edge <code>connectingEdge</code>.
     * @param <V> the vertex type
     * @param <E> the edge type
     * @param tree the tree to which <code>subTree</code> is to be added
     * @param subtree the tree which is to be grafted on to <code>tree</code>
     * @param node the parent of <code>subTree</code> in its new position in <code>tree</code>
	 * @param connectingEdge the edge used to connect <code>subtree</code>'s root as a child of <code>node</code>
	 */
	public static <V,E> void addSubTree(Forest<V,E> tree, Forest<V,E> subTree, 
			V node, E connectingEdge) {
        if (!tree.containsVertex(node))
            throw new IllegalArgumentException("Specified tree does not contain the specified node as a vertex");
		V root = ((Forest<V,E>)subTree).getTrees().iterator().next().getRoot();
		addFromSubTree(tree, subTree, connectingEdge, node, root);
	}
	
	private static <V,E> void addFromSubTree(Forest<V,E> tree, Forest<V,E> subTree, 
			E edge, V parent, V root) {

		// add edge connecting parent and root to tree
		if(edge != null && parent != null) {
			tree.addEdge(edge, parent, root);
		} else {
			tree.addVertex(root);
		}
		Collection<E> outEdges = subTree.getOutEdges(root);
		for(E e : outEdges) {
			V opposite = subTree.getOpposite(root, e);
			addFromSubTree(tree, subTree, e, root, opposite);
		}
	}
}
