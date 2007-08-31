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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings("serial")
public class OrderedSparseMultigraph<V,E> 
    extends SparseMultigraph<V,E>
    implements MultiGraph<V,E>, Serializable {
	
	public static <V,E> Factory<Graph<V,E>> getFactory() { 
		return new Factory<Graph<V,E>> () {
			public Graph<V,E> create() {
				return new OrderedSparseMultigraph<V,E>();
			}
		};
	}

    public OrderedSparseMultigraph()
    {
        vertices = new LinkedHashMap<V, Pair<Set<E>>>();
        edges = new LinkedHashMap<E, Pair<V>>();
        directedEdges = new LinkedHashSet<E>();
    }

    public boolean addVertex(V vertex) {
        if(vertex == null) {
            throw new IllegalArgumentException("vertex may not be null");
        }
        if (!vertices.containsKey(vertex)) {
            vertices.put(vertex, new Pair<Set<E>>(new LinkedHashSet<E>(), new LinkedHashSet<E>()));
            return true;
        } else {
        	return false;
        }
    }


    public Collection<V> getPredecessors(V vertex)
    {
        Set<V> preds = new LinkedHashSet<V>();
        for (E edge : getIncoming_internal(vertex)) {
        	if(getEdgeType(edge) == EdgeType.DIRECTED) {
        		preds.add(this.getSource(edge));
        	} else {
        		preds.add(getOpposite(vertex, edge));
        	}
        }
        return Collections.unmodifiableCollection(preds);
    }

    public Collection<V> getSuccessors(V vertex)
    {
        Set<V> succs = new LinkedHashSet<V>();
        for (E edge : getOutgoing_internal(vertex)) {
        	if(getEdgeType(edge) == EdgeType.DIRECTED) {
        		succs.add(this.getDest(edge));
        	} else {
        		succs.add(getOpposite(vertex, edge));
        	}
        }
        return Collections.unmodifiableCollection(succs);
    }

    public Collection<V> getNeighbors(V vertex)
    {
        Collection<V> out = new LinkedHashSet<V>();
        out.addAll(this.getPredecessors(vertex));
        out.addAll(this.getSuccessors(vertex));
        return out;
    }

    public Collection<E> getIncidentEdges(V vertex)
    {
        Collection<E> out = new LinkedHashSet<E>();
        out.addAll(this.getInEdges(vertex));
        out.addAll(this.getOutEdges(vertex));
        return out;
    }
}
