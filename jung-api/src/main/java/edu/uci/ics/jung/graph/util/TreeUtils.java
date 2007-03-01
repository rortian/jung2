package edu.uci.ics.jung.graph.util;

import java.util.Collection;

import edu.uci.ics.jung.graph.Forest;

public class TreeUtils {
	
	public static <V,E> Forest<V,E> getSubTree(Forest<V,E> tree, V root) throws InstantiationException, IllegalAccessException {
		Forest<V,E> subTree = tree.getClass().newInstance();
		subTree.addVertex(root);
		growSubTree(tree, subTree, root);
		
		return subTree;
	}
	
	private static <V,E> void growSubTree(Forest<V,E> tree, Forest<V,E> subTree, V root) {
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
	 * Add the given subTree as a child of the passed node, using
	 * the passed edge to connect
	 * @param <V>
	 * @param <E>
	 * @param tree
	 * @param subTree
	 * @param node
	 * @param connectingEdge
	 */
	public static <V,E> void addSubTree(Forest<V,E> tree, Forest<V,E> subTree, 
			V node, E connectingEdge) {
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
