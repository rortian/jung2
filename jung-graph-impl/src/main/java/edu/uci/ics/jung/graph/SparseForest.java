package edu.uci.ics.jung.graph;

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
public class SparseForest<V,E> implements Forest<V,E> {
	
	protected DirectedGraph<V,E> delegate;

	public SparseForest() {
		this(new DirectedSparseGraph<V,E>());
	}
	public SparseForest(DirectedGraph<V,E> delegate) {
		this.delegate = delegate;
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
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#areIncident(java.lang.Object, java.lang.Object)
	 */
	public boolean areIncident(V vertex, E edge) {
		return delegate.areIncident(vertex, edge);
	}


	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#areNeighbors(java.lang.Object, java.lang.Object)
	 */
	public boolean areNeighbors(V v1, V v2) {
		return delegate.areNeighbors(v1, v2);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#degree(java.lang.Object)
	 */
	public int degree(V vertex) {
		return delegate.degree(vertex);
	}
	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
	 */
	public E findEdge(V v1, V v2) {
		return delegate.findEdge(v1, v2);
	}
	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
	 */
	public V getDest(E directed_edge) {
		return delegate.getDest(directed_edge);
	}
	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
	 */
	public int getEdgeCount() {
		return delegate.getEdgeCount();
	}
	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
	 */
	public Collection<E> getEdges() {
		return delegate.getEdges();
	}
	/**
	 * @param edgeType
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getEdges(edu.uci.ics.jung.graph.util.EdgeType)
	 */
	public Collection<E> getEdges(EdgeType edgeType) {
		return delegate.getEdges(edgeType);
	}
	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getEdgeType(java.lang.Object)
	 */
	public EdgeType getEdgeType(E edge) {
		return delegate.getEdgeType(edge);
	}
	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
	 */
	public Pair<V> getEndpoints(E edge) {
		return delegate.getEndpoints(edge);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
	 */
	public Collection<E> getIncidentEdges(V vertex) {
		return delegate.getIncidentEdges(vertex);
	}
	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentVertices(java.lang.Object)
	 */
	public Collection<V> getIncidentVertices(E edge) {
		return delegate.getIncidentVertices(edge);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
	 */
	public Collection<E> getInEdges(V vertex) {
		return delegate.getInEdges(vertex);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighborCount(java.lang.Object)
	 */
	public int getNeighborCount(V vertex) {
		return delegate.getNeighborCount(vertex);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
	 */
	public Collection<V> getNeighbors(V vertex) {
		return delegate.getNeighbors(vertex);
	}
	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
	 */
	public V getOpposite(V vertex, E edge) {
		return delegate.getOpposite(vertex, edge);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
	 */
	public Collection<E> getOutEdges(V vertex) {
		return delegate.getOutEdges(vertex);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getPredecessorCount(java.lang.Object)
	 */
	public int getPredecessorCount(V vertex) {
		return delegate.getPredecessorCount(vertex);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
	 */
	public Collection<V> getPredecessors(V vertex) {
		return delegate.getPredecessors(vertex);
	}
	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
	 */
	public V getSource(E directed_edge) {
		return delegate.getSource(directed_edge);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getSuccessorCount(java.lang.Object)
	 */
	public int getSuccessorCount(V vertex) {
		return delegate.getSuccessorCount(vertex);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
	 */
	public Collection<V> getSuccessors(V vertex) {
		return delegate.getSuccessors(vertex);
	}
	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
	 */
	public int getVertexCount() {
		return delegate.getVertexCount();
	}
	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
	 */
	public Collection<V> getVertices() {
		return delegate.getVertices();
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#inDegree(java.lang.Object)
	 */
	public int inDegree(V vertex) {
		return delegate.inDegree(vertex);
	}
	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object, java.lang.Object)
	 */
	public boolean isDest(V vertex, E edge) {
		return delegate.isDest(vertex, edge);
	}
	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
	 */
	public boolean isPredecessor(V v1, V v2) {
		return delegate.isPredecessor(v1, v2);
	}
	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object, java.lang.Object)
	 */
	public boolean isSource(V vertex, E edge) {
		return delegate.isSource(vertex, edge);
	}
	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
	 */
	public boolean isSuccessor(V v1, V v2) {
		return delegate.isSuccessor(v1, v2);
	}
	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#outDegree(java.lang.Object)
	 */
	public int outDegree(V vertex) {
		return delegate.outDegree(vertex);
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
	
	public String toString() {
		
		return delegate.toString();
	}

}
