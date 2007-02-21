/*
 * Created on Apr 2, 2006
 *
 * Copyright (c) 2006, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;


public abstract class AbstractSparseGraph<V, E> implements Graph<V,E> {
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		return addEdge(edge, 
				vertices instanceof Pair ? (Pair<V>)vertices : new Pair<V>(vertices)
				);
	}

	public boolean addEdge(E edge, Collection<? extends V> vertices, EdgeType edgeType) {
		return addEdge(edge, 
				vertices instanceof Pair ? (Pair<V>)vertices : new Pair<V>(vertices),
				edgeType);
	}
	
	public abstract boolean addEdge(E edge, Pair<? extends V> endpoints);
	
	public abstract boolean addEdge(E edge, Pair<? extends V> endpoints, EdgeType edgeType);
	
    public int inDegree(V vertex)
    {
        return this.getInEdges(vertex).size();
    }

    public int outDegree(V vertex)
    {
        return this.getOutEdges(vertex).size();
    }

    public boolean isPredecessor(V v1, V v2)
    {
        return this.getPredecessors(v1).contains(v2);
    }

    public boolean isSuccessor(V v1, V v2)
    {
        return this.getSuccessors(v1).contains(v2);
    }

    public int getPredecessorCount(V vertex)
    {
        return this.getPredecessors(vertex).size();
    }

    public int getSuccessorCount(V vertex)
    {
        return this.getSuccessors(vertex).size();
    }

    public boolean areNeighbors(V v1, V v2)
    {
        return this.getNeighbors(v1).contains(v2);
    }

    public boolean areIncident(V vertex, E edge)
    {
        return this.getIncidentEdges(vertex).contains(edge);
    }

    public int getNeighborCount(V vertex)
    {
        return this.getNeighbors(vertex).size();
    }

    public int degree(V vertex)
    {
        return this.getIncidentEdges(vertex).size();
    }

    public int getIncidentCount(E edge)
    {
        Pair<V> incident = this.getEndpoints(edge);
        if (incident.getFirst() == incident.getSecond())
            return 1;
        else
            return 2;
    }
    
    public V getOpposite(V vertex, E edge)
    {
        Pair<V> incident = this.getEndpoints(edge); 
        V first = incident.getFirst();
        V second = incident.getSecond();
        if (vertex.equals(first))
            return second;
        else if (vertex.equals(second))
            return first;
        else 
            throw new IllegalArgumentException(vertex + " is not incident to " + edge);
    }

    public Collection<V> getIncidentVertices(E edge)
    {
        Pair<V> endpoints = this.getEndpoints(edge);
        Collection<V> incident = new ArrayList<V>();
        incident.add(endpoints.getFirst());
        incident.add(endpoints.getSecond());
        
        return Collections.unmodifiableCollection(incident);
    }
    
    public String toString() {
    	StringBuffer sb = new StringBuffer("Vertices:");
    	for(V v : getVertices()) {
    		sb.append(v+",");
    	}
    	sb.setLength(sb.length()-1);
    	sb.append("\nEdges:");
    	for(E e : getEdges()) {
    		Pair<V> ep = getEndpoints(e);
    		sb.append(e+"["+ep.getFirst()+","+ep.getSecond()+"]");
    	}
        return sb.toString();
    }

}
