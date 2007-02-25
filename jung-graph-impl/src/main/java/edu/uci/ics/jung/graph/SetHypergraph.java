/*
 * Created on Feb 4, 2007
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SetHypergraph<V,H> implements Hypergraph<V,H>
{
    protected Map<V, Set<H>> vertices; // Map of vertices to incident hyperedges
    protected Map<H, Set<V>> edges;    // Map of hyperedges to incident vertex sets
 
    public boolean addEdge(H hyperedge, Collection<? extends V> to_attach)
    {
    	if(to_attach.contains(null)) throw new IllegalArgumentException("cannot add an edge with a null enpoint");
        if (edges.containsKey(hyperedge))
            return false;
        edges.put(hyperedge, new HashSet<V>(to_attach));
        for (V v : to_attach)
        {
            // add v if it's not already in the graph
            addVertex(v);
            
            // associate v with hyperedge
            vertices.get(v).add(hyperedge);
        }
        return true;
    }
    
    public Collection<H> getEdges()
    {
        return edges.keySet();
    }
    
    public Collection<V> getVertices()
    {
        return vertices.keySet();
    }

    public int getEdgeCount()
    {
        return edges.size();
    }
    
    public int getVertexCount()
    {
        return vertices.size();
    }
    
    public Collection<V> getNeighbors(V vertex)
    {
        Set<V> neighbors = new HashSet<V>();
        for (H hyperedge : vertices.get(vertex))
        {
            neighbors.addAll(edges.get(hyperedge));
        }
        return neighbors;
    }
    
    public Collection<H> getIncidentEdges(V vertex)
    {
        return vertices.get(vertex);
    }
    
    public Collection<V> getIncidentVertices(H edge)
    {
        return edges.get(edge);
    }
    
    public H findEdge(V v1, V v2)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean addVertex(V vertex)
    {
    	if(vertex == null) throw new IllegalArgumentException("cannot add a null vertex");
        if (vertices.containsKey(vertex))
            return false;
        vertices.put(vertex, new HashSet<H>());
        return true;
    }
    
    public boolean removeVertex(V vertex)
    {
        if (!vertices.containsKey(vertex))
            return false;
        for (H hyperedge : vertices.get(vertex))
        {
            edges.get(hyperedge).remove(vertex);
        }
        vertices.remove(vertex);
        return true;
    }
    
    public boolean removeEdge(H hyperedge)
    {
        if (!edges.containsKey(hyperedge))
            return false;
        for (V vertex : edges.get(hyperedge))
        {
            vertices.get(vertex).remove(hyperedge);
        }
        edges.remove(hyperedge);
        return true;
    }
    
    public boolean areNeighbors(V v1, V v2)
    {
        if (vertices.get(v2).isEmpty())
            return false;
        for (H hyperedge : vertices.get(v1))
        {
            if (edges.get(hyperedge).contains(v2))
                return true;
        }
        return false;
    }
    
    public boolean areIncident(V vertex, H edge)
    {
        return vertices.get(vertex).contains(edge);
    }
    
    public int degree(V vertex)
    {
        return vertices.get(vertex).size();
    }
    
    public int getNeighborCount(V vertex)
    {
        return getNeighbors(vertex).size();
    }
    
    public int getIncidentCount(H edge)
    {
        return edges.get(edge).size();
    }

}
