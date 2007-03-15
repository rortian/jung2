package edu.uci.ics.jung.graph;

import java.util.Collection;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphDecorator<V,E> implements Graph<V,E> {
	
	protected Graph<V,E> delegate;

	public GraphDecorator(Graph<V, E> delegate) {
		this.delegate = delegate;
	}

	/**
	 * @param edge
	 * @param vertices
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
	 */
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		return delegate.addEdge(edge, vertices);
	}

	/**
	 * @param e
	 * @param v1
	 * @param v2
	 * @param edgeType
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		return delegate.addEdge(e, v1, v2, edgeType);
	}

	/**
	 * @param e
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(E e, V v1, V v2) {
		return delegate.addEdge(e, v1, v2);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	public boolean addVertex(V vertex) {
		return delegate.addVertex(vertex);
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#areIncident(java.lang.Object, java.lang.Object)
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
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#findEdgeSet(java.lang.Object, java.lang.Object)
	 */
	public Collection<E> findEdgeSet(V v1, V v2) {
		return delegate.findEdgeSet(v1, v2);
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
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentCount(java.lang.Object)
	 */
	public int getIncidentCount(E edge) {
		return delegate.getIncidentCount(edge);
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
		return delegate.removeEdge(edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(V vertex) {
		return delegate.removeVertex(vertex);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
	 */
	public boolean containsEdge(E edge) {
		return delegate.containsEdge(edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
	 */
	public boolean containsVertex(V vertex) {
		return delegate.containsVertex(vertex);
	}
	
	

}
