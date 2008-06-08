/*
 * Created on May 8, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

import org.apache.commons.collections15.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * An implementation of <code>Tree</code> in which each vertex has
 * 0, 1, or 2 children.
 */
public class BinaryTree<V, E> implements Tree<V, E> 
{
    protected Map<V, E> parent_edges;
    // We don't use a Pair<E> for child edges because
    // (a) either or both edges might be null
    // (b) Pairs are immutable and the graph can change
    protected Map<V, E[]> child_edges;
    protected Map<E, Pair<V>> edge_vpairs;
    protected Map<V, Integer> vertex_depths;
    protected int height;
    protected V root;
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getChildCount(java.lang.Object)
     */
    public int getChildCount(V vertex) {
        if (!containsVertex(vertex)) return 0;
        E[] edges = child_edges.get(vertex);
        int count = 0;
        for (int i = 0; i < edges.length; i++)
            count += edges[i] == null ? 0 : 1;
    
        return count;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getChildEdges(java.lang.Object)
     */
    public Collection<E> getChildEdges(V vertex) 
    {
        if (!containsVertex(vertex)) return null;
        E[] edge_array = child_edges.get(vertex);
        Collection<E> edges = new ArrayList<E>(2);
        for (int i = 0; i < edge_array.length; i++) 
            if (edge_array[i] != null) edges.add(edge_array[i]);
        return CollectionUtils.unmodifiableCollection(edges);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getChildren(java.lang.Object)
     */
    public Collection<V> getChildren(V vertex) 
    {
        if (!containsVertex(vertex)) return null;
        E[] edges = child_edges.get(vertex);
        Collection<V> children = new ArrayList<V>(2);
        for (int i = 0; i < edges.length; i++) 
          if (edges[i] != null) children.add(this.getOpposite(vertex, edges[i]));
        return CollectionUtils.unmodifiableCollection(children);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getDepth(java.lang.Object)
     * @return the depth of the vertex in this tree, or -1 if the vertex is
     * not present in this tree
     */
    public int getDepth(V vertex) 
    {
        if (!containsVertex(vertex))
            return -1;
        return vertex_depths.get(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getHeight()
     */
    public int getHeight() 
    {
        return height;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getParent(java.lang.Object)
     */
    public V getParent(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        else if (vertex.equals(root))
            return null;
        return this.getOpposite(vertex, parent_edges.get(vertex));
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getParentEdge(java.lang.Object)
     */
    public E getParentEdge(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        return parent_edges.get(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getRoot()
     */
    public V getRoot() 
    {
        return root;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Forest#getTrees()
     */
    public Collection<Tree<V, E>> getTrees() 
    {
        Collection<Tree<V, E>> forest = new ArrayList<Tree<V, E>>(1);
        forest.add(this);
        return forest;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public boolean addEdge(E e, V v1, V v2) {
      // TODO Auto-generated method stub
      return false;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
     */
    public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
     */
    public V getDest(E directed_edge) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getEdgeType(java.lang.Object)
     */
    public EdgeType getEdgeType(E edge) 
    {
        if (containsEdge(edge))
            return EdgeType.DIRECTED;
        else
            return null;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getEdges(edu.uci.ics.jung.graph.util.EdgeType)
     */
    public Collection<E> getEdges(EdgeType edgeType) 
    {
        return CollectionUtils.unmodifiableCollection(edge_vpairs.keySet());
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
     */
    public Pair<V> getEndpoints(E edge) 
    {
        if (!containsEdge(edge))
            return null;
        return edge_vpairs.get(edge);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
     */
    public Collection<E> getInEdges(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        else if (vertex.equals(root))
            return Collections.emptySet();
        else
        {
            Collection<E> inedge = new ArrayList<E>(1);
            inedge.add(getParentEdge(vertex));
            return inedge;
        }
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
     */
    public V getOpposite(V vertex, E edge) 
    {
        if (!containsVertex(vertex) || !containsEdge(edge))
            return null;
        Pair<V> endpoints = edge_vpairs.get(edge);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        return v1.equals(vertex) ? v2 : v1;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
     */
    public Collection<E> getOutEdges(V vertex) 
    {
        return getChildEdges(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getPredecessorCount(java.lang.Object)
     * @return 0 if <code>vertex</code> is the root, -1 if the vertex is 
     * not an element of this tree, and 1 otherwise
     */
    public int getPredecessorCount(V vertex) 
    {
        if (!containsVertex(vertex))
            return -1;
        return vertex.equals(root) ? 0 : 1;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
     */
    public Collection<V> getPredecessors(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        if (vertex.equals(root))
            return Collections.emptySet();
        return Collections.singleton(getParent(vertex));
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
     */
    public V getSource(E directed_edge) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getSuccessorCount(java.lang.Object)
     */
    public int getSuccessorCount(V vertex) 
    {
        return getChildCount(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
     */
    public Collection<V> getSuccessors(V vertex) 
    {
        return getChildren(vertex);
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#inDegree(java.lang.Object)
     */
    public int inDegree(V vertex) 
    {
        if (!containsVertex(vertex))
            return 0;
        if (vertex.equals(root))
            return 0;
        return 1;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object, java.lang.Object)
     */
    public boolean isDest(V vertex, E edge) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
     */
    public boolean isPredecessor(V v1, V v2) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object, java.lang.Object)
     */
    public boolean isSource(V vertex, E edge) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
     */
    public boolean isSuccessor(V v1, V v2) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Graph#outDegree(java.lang.Object)
     */
    public int outDegree(V vertex) {
      // TODO Auto-generated method stub
      return 0;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
     */
    public boolean addEdge(E edge, Collection<? extends V> vertices) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
     */
    public boolean addVertex(V vertex) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#areIncident(java.lang.Object, java.lang.Object)
     */
    public boolean areIncident(V vertex, E edge) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#areNeighbors(java.lang.Object, java.lang.Object)
     */
    public boolean areNeighbors(V v1, V v2) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
     */
    public boolean containsEdge(E edge) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
     */
    public boolean containsVertex(V vertex) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#degree(java.lang.Object)
     */
    public int degree(V vertex) {
      // TODO Auto-generated method stub
      return 0;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
     */
    public E findEdge(V v1, V v2) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#findEdgeSet(java.lang.Object, java.lang.Object)
     */
    public Collection<E> findEdgeSet(V v1, V v2) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
     */
    public int getEdgeCount() {
      // TODO Auto-generated method stub
      return 0;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
     */
    public Collection<E> getEdges() {
      // TODO Auto-generated method stub
      return null;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentCount(java.lang.Object)
     */
    public int getIncidentCount(E edge) {
      // TODO Auto-generated method stub
      return 0;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
     */
    public Collection<E> getIncidentEdges(V vertex) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentVertices(java.lang.Object)
     */
    public Collection<V> getIncidentVertices(E edge) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getNeighborCount(java.lang.Object)
     */
    public int getNeighborCount(V vertex) {
      return (vertex.equals(root) ? 0 : 1) + this.getChildCount(vertex);
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
     */
    public Collection<V> getNeighbors(V vertex) {
      // TODO Auto-generated method stub
      return null;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
     */
    public int getVertexCount() {
      return parent_edges.size();
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
     */
    public Collection<V> getVertices() {
      return CollectionUtils.unmodifiableCollection(parent_edges.keySet());
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
     */
    public boolean removeEdge(E edge) {
      // TODO Auto-generated method stub
      return false;
    }
  
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
     */
    public boolean removeVertex(V vertex) {
      // TODO Auto-generated method stub
      return false;
    }

	public boolean addEdge(E edge, Collection<? extends V> vertices,
			EdgeType edge_type) {
		// TODO Auto-generated method stub
		return false;
	}

}
