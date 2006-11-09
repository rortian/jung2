/*
 * Created on Oct 18, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.graph.UndirectedGraph;
import edu.uci.ics.graph.util.Pair;

@SuppressWarnings("serial")
public class SimpleUndirectedSparseGraph<V,E> 
    extends SimpleAbstractSparseGraph<V,E>
    implements UndirectedGraph<V,E>, Serializable
{
    protected Map<V, Set<E>> vertices; // Map of vertices to adjacency sets
    protected Map<E, Pair<V>> edges;    // Map of edges to incident vertex sets

    public SimpleUndirectedSparseGraph()
    {
        vertices = new HashMap<V, Set<E>>();
        edges = new HashMap<E, Pair<V>>();
    }

    public Collection<E> getEdges()
    {
        return Collections.unmodifiableCollection(edges.keySet());
    }

    public Collection<V> getVertices()
    {
        return Collections.unmodifiableCollection(vertices.keySet());
    }

    public void addVertex(V vertex)
    {
        if (!vertices.containsKey(vertex))
        {
            vertices.put(vertex, new HashSet<E>());
//            return true;
        }
//        else
//            return false;
    }

    public boolean removeVertex(V vertex) {
        
        // copy to avoid concurrent modification in removeEdge
        Set<E> adj_set = new HashSet<E>(vertices.get(vertex));
        if (adj_set == null)
            return false;
        
        for (E edge : adj_set)
            removeEdge(edge);
        
        vertices.remove(vertex);
        return true;
    }
    
    public void addEdge(E edge, V v1, V v2)
    {
        if (edges.containsKey(edge))
            return;
        
        if (!vertices.containsKey(v1))
            this.addVertex(v1);
        
        if (!vertices.containsKey(v2))
            this.addVertex(v2);

        Pair<V> endpoints = new Pair<V>(v1, v2);
        edges.put(edge, endpoints);
        vertices.get(v1).add(edge);
        vertices.get(v2).add(edge);        
//        return true;
    }

    public boolean removeEdge(E edge)
    {
        if (!edges.containsKey(edge))
            return false;
        
        Pair<V> endpoints = getEndpoints(edge);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        
        // remove edge from incident vertices' adjacency sets
        vertices.get(v1).remove(edge);
        vertices.get(v2).remove(edge);

        edges.remove(edge);
        return true;
    }
    
    public Collection<E> getInEdges(V vertex)
    {
        return this.getIncidentEdges(vertex);
    }

    public Collection<E> getOutEdges(V vertex)
    {
        return this.getIncidentEdges(vertex);
    }

    public Collection<V> getPredecessors(V vertex)
    {
        return this.getNeighbors(vertex);
    }

    public Collection<V> getSuccessors(V vertex)
    {
        return this.getNeighbors(vertex);
    }

    public Collection<V> getNeighbors(V vertex)
    {
        Set<E> incident_edges = vertices.get(vertex);        
        Set<V> neighbors = new HashSet<V>();
        for (E edge : incident_edges)
        {
            Pair<V> endpoints = this.getEndpoints(edge);
            V e_a = endpoints.getFirst();
            V e_b = endpoints.getSecond();
            if (vertex.equals(e_a))
                neighbors.add(e_b);
            else
                neighbors.add(e_a);
        }
        
        return Collections.unmodifiableCollection(neighbors);
    }

    public Collection<E> getIncidentEdges(V vertex)
    {
        return Collections.unmodifiableCollection(vertices.get(vertex));
    }

    public E findEdge(V v1, V v2)
    {
        Set<E> incident_edges = vertices.get(v1);
        for (E edge : incident_edges)
        {
            Pair<V> endpoints = this.getEndpoints(edge);
            V e_a = endpoints.getFirst();
            V e_b = endpoints.getSecond();
            if ((v1.equals(e_a) && v2.equals(e_b)) || (v1.equals(e_b) && v2.equals(e_a)))
                return edge;
        }
        
        return null;
    }

    public Pair<V> getEndpoints(E edge)
    {
        return edges.get(edge);
    }

    public boolean isDirected(E edge) {
        return false;
    }

    public void addDirectedEdge(E e, V v1, V v2) {
        throw new UnsupportedOperationException("Cannot add a directed edge to an undirected graph");
    }
}
