/*
 * Created on Oct 17, 2005
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

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;



@SuppressWarnings("serial")
public class DirectedSparseGraph<V,E> 
    extends AbstractSparseGraph<V,E>
    implements DirectedGraph<V,E>, Serializable {
    protected Map<V, Pair<Set<E>>> vertices; // Map of vertices to Pair of adjacency sets {incoming, outgoing}
    protected Map<E, Pair<V>> edges;            // Map of edges to incident vertex pairs

    public DirectedSparseGraph() {
        vertices = new HashMap<V, Pair<Set<E>>>();
        edges = new HashMap<E, Pair<V>>();
    }
    
    public Collection<E> getEdges() {
        return Collections.unmodifiableCollection(edges.keySet());
    }

    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(vertices.keySet());
    }

    public boolean addVertex(V vertex) {
    	if(vertex == null) {
    		throw new IllegalArgumentException("vertex may not be null");
    	}
        if (!vertices.containsKey(vertex)) {
            vertices.put(vertex, new Pair<Set<E>>(new HashSet<E>(), new HashSet<E>()));
            return true;
        } else {
            return false;
        }
    }

    public boolean removeVertex(V vertex) {
        // copy to avoid concurrent modification in removeEdge
        Pair<Set<E>> i_adj_set = vertices.get(vertex);
        Pair<Set<E>> adj_set = new Pair<Set<E>>(new HashSet<E>(i_adj_set.getFirst()), 
                new HashSet<E>(i_adj_set.getSecond()));
        

//        Pair<Set<E>> adj_set = vertices.get(vertex);
        if (adj_set == null)
            return false;
        
        for (E edge : adj_set.getFirst())
            removeEdge(edge);
        for (E edge : adj_set.getSecond())
            removeEdge(edge);
        
        vertices.remove(vertex);
        
        return true;
    }
    
    public boolean removeEdge(E edge) {
        if (!edges.containsKey(edge))
            return false;
        
        Pair<V> endpoints = this.getEndpoints(edge);
        V source = endpoints.getFirst();
        V dest = endpoints.getSecond();
        
        // remove edge from incident vertices' adjacency sets
        vertices.get(source).getSecond().remove(edge);
        vertices.get(dest).getFirst().remove(edge);
        
        edges.remove(edge);
        return true;
    }

    
    public Collection<E> getInEdges(V vertex) {
        return Collections.unmodifiableCollection(vertices.get(vertex).getFirst());
    }

    public Collection<E> getOutEdges(V vertex) {
        return Collections.unmodifiableCollection(vertices.get(vertex).getSecond());
    }

    public Collection<V> getPredecessors(V vertex) {
        Set<E> incoming = vertices.get(vertex).getFirst();        
        Set<V> preds = new HashSet<V>();
        for (E edge : incoming)
            preds.add(this.getSource(edge));
        
        return Collections.unmodifiableCollection(preds);
    }

    public Collection<V> getSuccessors(V vertex) {
        Set<E> outgoing = vertices.get(vertex).getSecond();        
        Set<V> succs = new HashSet<V>();
        for (E edge : outgoing)
            succs.add(this.getDest(edge));
        
        return Collections.unmodifiableCollection(succs);
    }

    public Collection<V> getNeighbors(V vertex) {
        Collection<V> out = new HashSet<V>();
        out.addAll(this.getPredecessors(vertex));
        out.addAll(this.getSuccessors(vertex));
        return out;
//        return CollectionUtils.union(this.getPredecessors(vertex), this.getSuccessors(vertex));
    }

    public Collection<E> getIncidentEdges(V vertex) {
        Collection<E> out = new HashSet<E>();
        out.addAll(this.getInEdges(vertex));
        out.addAll(this.getOutEdges(vertex));
        return out;
//        return CollectionUtils.union(this.getInEdges(vertex), this.getOutEdges(vertex));
    }

    public E findEdge(V v1, V v2) {
        Set<E> outgoing = vertices.get(v1).getSecond();
        for (E edge : outgoing)
            if (this.getDest(edge).equals(v2))
                return edge;
        
        return null;
    }
    
    public boolean addEdge(E edge, V source, V dest) {
        return addEdge(edge, source, dest, EdgeType.DIRECTED);
    }

    /**
     * Adds <code>edge</code> to the graph.  Also adds 
     * <code>source</code> and <code>dest</code> to the graph if they
     * are not already present.  Returns <code>false</code> if 
     * the specified edge is 
     */
    public boolean addEdge(E edge, V source, V dest, EdgeType edgeType) {
    	return addEdge(edge, new Pair<V>(source, dest), edgeType);
    }

	public boolean addEdge(E edge, Pair<V> endpoints, EdgeType edgeType) {
    	if(edgeType != EdgeType.DIRECTED) throw new IllegalArgumentException();
    	return addEdge(edge, endpoints);
	}

	public boolean addEdge(E edge, Pair<V> endpoints) {
        edges.put(edge, endpoints);
        V source = endpoints.getFirst();
        V dest = endpoints.getSecond();
        
        if (!vertices.containsKey(source))
            this.addVertex(source);
        
        if (!vertices.containsKey(dest))
            this.addVertex(dest);
        
        vertices.get(source).getSecond().add(edge);        
        vertices.get(dest).getFirst().add(edge);        

        if (edges.containsKey(edge)) {
            Pair<V> existingEndpoints = edges.get(edge);
            Pair<V> new_endpoints = new Pair<V>(source, dest);
            if (!existingEndpoints.equals(new_endpoints)) {
                throw new IllegalArgumentException("EdgeType " + edge + 
                        " exists in this graph with endpoints " + source + ", " + dest);
            } else {
                return false;
            }
        }
        
       
        return true;
	}

    
    public V getSource(E edge) {
        return this.getEndpoints(edge).getFirst();
    }

    public V getDest(E edge) {
        return this.getEndpoints(edge).getSecond();
    }

    public boolean isSource(V vertex, E edge) {
        return vertex.equals(this.getEndpoints(edge).getFirst());
    }

    public boolean isDest(V vertex, E edge) {
        return vertex.equals(this.getEndpoints(edge).getSecond());
    }

    public Pair<V> getEndpoints(E edge) {
        return edges.get(edge);
    }

    public EdgeType getEdgeType(E edge) {
        return EdgeType.DIRECTED;
    }

	public Collection<E> getEdges(EdgeType edgeType) {
		return getEdges();
	}

	public int getEdgeCount() {
		return edges.keySet().size();
	}

	public int getVertexCount() {
		return vertices.keySet().size();
	}

}
