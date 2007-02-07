package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;

/**
 * An implementation of the Tree<V,E> interface that delegates to
 * any implementation of DirectedGraph<V,E>
 * @author Tom Nelson
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
public class SparseForest<V,E> implements DirectedGraph<V,E> {
	
	protected Set<V> roots = new HashSet<V>();
	protected DirectedGraph<V,E> delegate;
	protected Factory<E> edgeFactory;

	/**
	 * create an instance with passed values.
	 * @param graphFactory must create a DirectedGraph to use as a delegate
	 * @param edgeFactory must create unique edges to connect tree nodes
	 */
	public SparseForest(Factory<DirectedGraph<V,E>> graphFactory, 
			Factory<E> edgeFactory) {
		this.delegate = graphFactory.create();
		this.edgeFactory = edgeFactory;
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
	 * @see edu.uci.ics.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.graph.util.EdgeType)
	 */
	public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		return addChild(e, v1, v2, edgeType);
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
	 * @see edu.uci.ics.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(E e, V v1, V v2) {
		return addChild(e, v1, v2);
	}

	/**
	 * Add vertex as a root of the tree
	 * 
	 * @param vertex the tree root to add
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.graph.Graph#addVertex(java.lang.Object)
	 */
	public boolean addVertex(V vertex) {
		setRoot(vertex);
		return true;
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#areIncident(java.lang.Object, java.lang.Object)
	 */
	public boolean areIncident(V vertex, E edge) {
		return delegate.areIncident(vertex, edge);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.Graph#areNeighbors(java.lang.Object, java.lang.Object)
	 */
	public boolean areNeighbors(V v1, V v2) {
		return delegate.areNeighbors(v1, v2);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#degree(java.lang.Object)
	 */
	public int degree(V vertex) {
		return delegate.degree(vertex);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.Graph#findEdge(java.lang.Object, java.lang.Object)
	 */
	public E findEdge(V v1, V v2) {
		return delegate.findEdge(v1, v2);
	}

	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getDest(java.lang.Object)
	 */
	public V getDest(E directed_edge) {
		return delegate.getDest(directed_edge);
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEdgeCount()
	 */
	public int getEdgeCount() {
		return delegate.getEdgeCount();
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEdges()
	 */
	public Collection<E> getEdges() {
		return delegate.getEdges();
	}

	/**
	 * @param edgeType
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEdges(edu.uci.ics.graph.util.EdgeType)
	 */
	public Collection<E> getEdges(EdgeType edgeType) {
		return delegate.getEdges(edgeType);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEdgeType(java.lang.Object)
	 */
	public EdgeType getEdgeType(E edge) {
		return delegate.getEdgeType(edge);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEndpoints(java.lang.Object)
	 */
	public Pair<V> getEndpoints(E edge) {
		return delegate.getEndpoints(edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getIncidentEdges(java.lang.Object)
	 */
	public Collection<E> getIncidentEdges(V vertex) {
		return delegate.getIncidentEdges(vertex);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getIncidentVertices(java.lang.Object)
	 */
	public Collection<V> getIncidentVertices(E edge) {
		return delegate.getIncidentVertices(edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getInEdges(java.lang.Object)
	 */
	public Collection<E> getInEdges(V vertex) {
		return delegate.getInEdges(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getNeighbors(java.lang.Object)
	 */
	public Collection<V> getNeighbors(V vertex) {
		return delegate.getNeighbors(vertex);
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
	 */
	public V getOpposite(V vertex, E edge) {
		return delegate.getOpposite(vertex, edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getOutEdges(java.lang.Object)
	 */
	public Collection<E> getOutEdges(V vertex) {
		return delegate.getOutEdges(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getPredecessors(java.lang.Object)
	 */
	public Collection<V> getPredecessors(V vertex) {
		return delegate.getPredecessors(vertex);
	}

	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getSource(java.lang.Object)
	 */
	public V getSource(E directed_edge) {
		return delegate.getSource(directed_edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getSuccessors(java.lang.Object)
	 */
	public Collection<V> getSuccessors(V vertex) {
		return delegate.getSuccessors(vertex);
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.Graph#getVertexCount()
	 */
	public int getVertexCount() {
		return delegate.getVertexCount();
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.Graph#getVertices()
	 */
	public Collection<V> getVertices() {
		return delegate.getVertices();
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#inDegree(java.lang.Object)
	 */
	public int inDegree(V vertex) {
		return delegate.inDegree(vertex);
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#isDest(java.lang.Object, java.lang.Object)
	 */
	public boolean isDest(V vertex, E edge) {
		return delegate.isDest(vertex, edge);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
	 */
	public boolean isPredecessor(V v1, V v2) {
		return delegate.isPredecessor(v1, v2);
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#isSource(java.lang.Object, java.lang.Object)
	 */
	public boolean isSource(V vertex, E edge) {
		return delegate.isSource(vertex, edge);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
	 */
	public boolean isSuccessor(V v1, V v2) {
		return delegate.isSuccessor(v1, v2);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getNeighborCount(java.lang.Object)
	 */
	public int getNeighborCount(V vertex) {
		return delegate.getNeighborCount(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getPredecessorCount(java.lang.Object)
	 */
	public int getPredecessorCount(V vertex) {
		return delegate.getPredecessorCount(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getSuccessorCount(java.lang.Object)
	 */
	public int getSuccessorCount(V vertex) {
		return delegate.getSuccessorCount(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#outDegree(java.lang.Object)
	 */
	public int outDegree(V vertex) {
		return delegate.outDegree(vertex);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(E edge) {
		throw new UnsupportedOperationException("Instead, use removeChild(V orphan)");

	}

	/**
	 * remove the passed node, and all nodes that are descendants of the
	 * passed node.
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(V vertex) {
		for(V v : getChildren(vertex)) {
			removeVertex(v);
		}
		return delegate.removeVertex(vertex);
	}
	
	/**
	 * add the passed child node as a child of parent.
	 * parent must exist in the tree, and child must not already exist.
	 * the connecting edge will be dynamically created by the 
	 * edgeFactory member
	 * @param parent the existing parent to attach the child to
	 * @param child the new child to add to the tree as a child of parent
	 * @return whether this call mutates the underlying graph
	 */
	public boolean addChild(V parent, V child) {
		return addChild(edgeFactory.create(), parent, child);
	}

	/**
	 * add the passed child node as a child of parent.
	 * parent must exist in the tree, and child must not already exist.
	 * 
	 * @param edge the unique edge to connect the parent and child nodes
	 * @param parent the existing parent to attach the child to
	 * @param child the new child to add to the tree as a child of parent
	 * @param edgeType must be EdgeType.DIRECTED or the underlying graph may throw an exception
	 * @return whether this call mutates the underlying graph
	 */
	public boolean addChild(E edge, V parent, V child, EdgeType edgeType) {
		Collection<V> vertices = delegate.getVertices();
		if(vertices.contains(parent) == false) {
			throw new IllegalArgumentException("Tree must already contain parent "+parent);
		}
		if(vertices.contains(child)) {
			throw new IllegalArgumentException("Tree must not already contain child "+child);
		}
		return delegate.addEdge(edge, parent, child, edgeType);
	}

	/**
	 * add the passed child node as a child of parent.
	 * parent must exist in the tree, and child must not already exist
	 * @param edge the unique edge to connect the parent and child nodes
	 * @param parent the existing parent to attach the child to
	 * @param child the new child to add to the tree as a child of parent
	 * @return whether this call mutates the underlying graph
	 */
	public boolean addChild(E edge, V parent, V child) {
		Collection<V> vertices = delegate.getVertices();
		if(vertices.contains(parent) == false) {
			throw new IllegalArgumentException("Tree must already contain parent "+parent);
		}
		if(vertices.contains(child)) {
			throw new IllegalArgumentException("Tree must not already contain child "+child);
		}
		return delegate.addEdge(edge, parent, child);
	}
	
	/**
	 * get the number of children of the passed parent node
	 */
	public int getChildCount(V parent) {
		return getChildren(parent).size();
	}

	/**
	 * get the immediate children nodes of the passed parent
	 */
	public Collection<V> getChildren(V parent) {
		return delegate.getSuccessors(parent);
	}

	/**
	 * get the single parent node of the passed child
	 */
	public V getParent(V child) {
		Collection<V> predecessors = delegate.getPredecessors(child);
		if(predecessors.size() == 0) {
			return null;
		}
		return predecessors.iterator().next();
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
		roots.add(root);
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

	/**
	 * computes whether the passed node is a root node
	 * (has no children)
	 */
	public boolean isRoot(V v) {
		return getParent(v) == null;
	}

	public Collection<V> getRoots() {
		return roots;
	}

    public int getIncidentCount(E edge)
    {
        // all edges in a forest connect exactly 2 vertices
        return 2;
    }
    
	public boolean addEdge(E edge, Collection<V> vertices) {
		Pair<V> pair = null;
		if(vertices instanceof Pair) {
			pair = (Pair<V>)vertices;
		} else {
			pair = new Pair<V>(vertices);
		}
		return addEdge(edge, pair.getFirst(), pair.getSecond());
	}

}
