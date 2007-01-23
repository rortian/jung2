package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.Tree;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;

public class SimpleSparseTree<V> implements Tree<V>, DirectedGraph<V,Integer>, Graph<V,Integer> {
	
	protected DirectedGraph<V,Integer> delegate;
	protected V root;
	protected Factory<Integer> edgeFactory = new Factory<Integer>() {
		int i=0;
		public Integer create() {
			return i++;
		}};

	public SimpleSparseTree(V root) {
		delegate = new SimpleDirectedSparseGraph<V,Integer>();
		this.root = root;
		delegate.addVertex(root);
	}
	/**
	 * @param e
	 * @param v1
	 * @param v2
	 * @param edgeType
	 * @return
	 * @see edu.uci.ics.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.graph.util.EdgeType)
	 */
	public boolean addEdge(Integer e, V v1, V v2, EdgeType edgeType) {
		throw new UnsupportedOperationException("Instead, use addChild(V parent, V child)");
	}

	/**
	 * @param e
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(Integer e, V v1, V v2) {
		throw new UnsupportedOperationException("Instead, use addChild(V parent, V child)");
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#addVertex(java.lang.Object)
	 */
	public boolean addVertex(V vertex) {
		throw new UnsupportedOperationException("Instead, use addChild(V parent, V child)");
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#areIncident(java.lang.Object, java.lang.Object)
	 */
	public boolean areIncident(V vertex, Integer edge) {
		return delegate.areIncident(vertex, edge);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#areNeighbors(java.lang.Object, java.lang.Object)
	 */
	public boolean areNeighbors(V v1, V v2) {
		return delegate.areNeighbors(v1, v2);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#degree(java.lang.Object)
	 */
	public int degree(V vertex) {
		return delegate.degree(vertex);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#findEdge(java.lang.Object, java.lang.Object)
	 */
	public Integer findEdge(V v1, V v2) {
		return delegate.findEdge(v1, v2);
	}

	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getDest(java.lang.Object)
	 */
	public V getDest(Integer directed_edge) {
		return delegate.getDest(directed_edge);
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#getEdgeCount()
	 */
	public int getEdgeCount() {
		return delegate.getEdgeCount();
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#getEdges()
	 */
	public Collection<Integer> getEdges() {
		return delegate.getEdges();
	}

	/**
	 * @param edgeType
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEdges(edu.uci.ics.graph.util.EdgeType)
	 */
	public Collection<Integer> getEdges(EdgeType edgeType) {
		return delegate.getEdges(edgeType);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEdgeType(java.lang.Object)
	 */
	public EdgeType getEdgeType(Integer edge) {
		return delegate.getEdgeType(edge);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.Graph#getEndpoints(java.lang.Object)
	 */
	public Pair<V> getEndpoints(Integer edge) {
		return delegate.getEndpoints(edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#getIncidentEdges(java.lang.Object)
	 */
	public Collection<Integer> getIncidentEdges(V vertex) {
		return delegate.getIncidentEdges(vertex);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#getIncidentVertices(java.lang.Object)
	 */
	public Collection<V> getIncidentVertices(Integer edge) {
		return delegate.getIncidentVertices(edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getInEdges(java.lang.Object)
	 */
	public Collection<Integer> getInEdges(V vertex) {
		return delegate.getInEdges(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#getNeighbors(java.lang.Object)
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
	public V getOpposite(V vertex, Integer edge) {
		return delegate.getOpposite(vertex, edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#getOutEdges(java.lang.Object)
	 */
	public Collection<Integer> getOutEdges(V vertex) {
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
	public V getSource(Integer directed_edge) {
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
	 * @see edu.uci.ics.graph.ArchetypeGraph#getVertexCount()
	 */
	public int getVertexCount() {
		return delegate.getVertexCount();
	}

	/**
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#getVertices()
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
	public boolean isDest(V vertex, Integer edge) {
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
	public boolean isSource(V vertex, Integer edge) {
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
	 * @see edu.uci.ics.graph.ArchetypeGraph#numNeighbors(java.lang.Object)
	 */
	public int numNeighbors(V vertex) {
		return delegate.numNeighbors(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#numPredecessors(java.lang.Object)
	 */
	public int numPredecessors(V vertex) {
		return delegate.numPredecessors(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.Graph#numSuccessors(java.lang.Object)
	 */
	public int numSuccessors(V vertex) {
		return delegate.numSuccessors(vertex);
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
	 * @see edu.uci.ics.graph.ArchetypeGraph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(Integer edge) {
		throw new UnsupportedOperationException("Instead, use removeChild(V orphan)");

	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.graph.ArchetypeGraph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(V vertex) {
		return delegate.removeVertex(vertex);
	}

	public boolean addChild(V parent, V child) {
		return delegate.addEdge(edgeFactory.create(), parent, child);
	}

	public int getChildCount(V parent) {
		return delegate.getSuccessors(parent).size();
	}

	public Collection<V> getChildren(V parent) {
		return delegate.getSuccessors(parent);
	}

	public V getParent(V child) {
		Collection<V> predecessors = delegate.getPredecessors(child);
		if(predecessors.size() == 0) {
			return null;
		}
		return predecessors.iterator().next();
	}

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

	public V getRoot() {
		return root;
	}

	public boolean removeChild(V orphan) {
		return delegate.removeVertex(orphan);
	}
	

}
