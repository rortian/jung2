package edu.uci.ics.jung.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TreeUtils;

/**
 * An implementation of the Forest<V,E> interface that aggregates 
 * a collection of Tree<V,E>
 * @author Tom Nelson
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
public class SparseForest<V,E> extends GraphDecorator<V,E> implements Forest<V,E>, Serializable {
	
//	protected DirectedGraph<V,E> delegate;

	public SparseForest() {
		this(new DirectedSparseMultigraph<V,E>());
	}
	public SparseForest(DirectedGraph<V,E> delegate) {
		super(delegate);
	}
	
	/**
	 * Add an edge to the tree, connecting v1, the parent and v2, the child.
	 * v1 must already exist in the tree, and v2 must not already exist
	 * the passed edge must be unique in the tree. Passing an edgeType
	 * other than EdgeType.DIRECTED may cause an illegal argument exception 
	 * in the delegate graph.
	 * 
	 * @param e a unique edge to add
	 * @param v1 the parent node
	 * @param v2 the child node
	 * @param edgeType should be EdgeType.DIRECTED
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		if(delegate.getVertices().contains(v1) == false) {
			throw new IllegalArgumentException("Tree must already contain "+v1);
		}
		if(delegate.getVertices().contains(v2)) {
			throw new IllegalArgumentException("Tree must not already contain "+v2);
		}
		return delegate.addEdge(e, v1, v2, edgeType);
	}

	/**
	 * Add an edge to the tree, connecting v1, the parent and v2, the child.
	 * v1 must already exist in the tree, and v2 must not already exist
	 * the passed edge must be unique in the tree. 
	 * 
	 * @param e a unique edge to add
	 * @param v1 the parent node
	 * @param v2 the child node
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(E e, V v1, V v2) {
		if(delegate.getVertices().contains(v1) == false) {
			throw new IllegalArgumentException("Tree must already contain "+v1);
		}
		if(delegate.getVertices().contains(v2)) {
			throw new IllegalArgumentException("Tree must not already contain "+v2);
		}
		return delegate.addEdge(e, v1, v2);
	}

	/**
	 * Add vertex as a root of the tree
	 * 
	 * @param vertex the tree root to add
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addVertex(java.lang.Object)
	 */
	public boolean addVertex(V vertex) {
		setRoot(vertex);
		return true;
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(E edge) {
		Pair<V> endpoints = delegate.getEndpoints(edge);
		return removeVertex(endpoints.getSecond());
//		return delegate.removeEdge(edge);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(V vertex) {
		for(V v : delegate.getSuccessors(vertex)) {
			removeVertex(v);
		}
		return delegate.removeVertex(vertex);
	}
	/**
	 * returns an ordered list of the nodes beginning at the root
	 * and ending at the passed child node, including all intermediate
	 * nodes.
	 * @param child the last node in the path from the root
	 * @return an ordered list of the nodes from root to child
	 */
	public List<V> getPath(V child) {
		List<V> list = new ArrayList<V>();
		list.add(child);
		V parent = getParent(child);
		while(parent != null) {
			list.add(list.size(), parent);
			parent = getParent(parent);
		}
		return list;
	}
	
	public V getParent(V child) {
		Collection<V> parents = delegate.getPredecessors(child);
		if(parents.size() > 0) {
			return parents.iterator().next();
		}
		return null;
	}

	/**
	 * getter for the root of the tree
	 * returns null, as this tree has >1 roots
	 * @return the root
	 */
	public V getRoot() {
		return null;
	}
	
	/**
	 * adds root as a root of the tree
	 * @param root the initial tree root
	 */
	public void setRoot(V root) {
		delegate.addVertex(root);
	}

	/**
	 * removes a node from the tree, causing all descendants of
	 * the removed node also to be removed
	 * @param orphan the node to remove
	 * @return whether this call mutates the underlying graph
	 */
	public boolean removeChild(V orphan) {
		return removeVertex(orphan);
	}

	/**
	 * computes and returns the depth of the tree from the
	 * root to the passed vertex
	 * 
	 * @param v the node who's depth is computed
	 * @return the depth to the passed node.
	 */
	public int getDepth(V v) {
		return getPath(v).size();
	}

	/**
	 * computes and returns the height of the tree
	 * 
	 * @return the height
	 */
	public int getHeight() {
		int height = 0;
		for(V v : getVertices()) {
			height = Math.max(height, getDepth(v));
		}
		return height;
	}

	/**
	 * computes and returns whether the passed node is
	 * neither the root, nor a leaf node.
	 * @return 
	 */
	public boolean isInternal(V v) {
		return isLeaf(v) == false && isRoot(v) == false;
	}

	/**
	 * computes and returns whether the passed node is
	 * a leaf (has no child nodes)
	 */
	public boolean isLeaf(V v) {
		return getChildren(v).size() == 0;
	}

	public Collection<V> getChildren(V v) {
		return delegate.getSuccessors(v);
	}
	/**
	 * computes whether the passed node is a root node
	 * (has no children)
	 */
	public boolean isRoot(V v) {
		return getParent(v) == null;
	}

    public int getIncidentCount(E edge)
    {
        return 2;
    }
    
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		Pair<V> pair = null;
		if(vertices instanceof Pair) {
			pair = (Pair<V>)vertices;
		} else {
			pair = new Pair<V>(vertices);
		}
		return addEdge(edge, pair.getFirst(), pair.getSecond());
	}
	
	public Collection<V> getRoots() {
		Collection<V> roots = new HashSet<V>();
		for(V v : delegate.getVertices()) {
			if(delegate.getPredecessorCount(v) == 0) {
				roots.add(v);
			}
		}
		return roots;
	}

	public Collection<Tree<V, E>> getTrees() {
		Collection<Tree<V,E>> trees = new HashSet<Tree<V,E>>();
		for(V v : getRoots()) {
			Tree<V,E> tree = new SparseTree<V,E>();
			tree.addVertex(v);
			TreeUtils.growSubTree(this, tree, v);
			trees.add(tree);
		}
		return trees;
	}
	
	public void addTree(Tree<V,E> tree) {
		TreeUtils.addSubTree(this, tree, null, null);
	}

}
