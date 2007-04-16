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
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.ComparableComparator;
import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings("serial")
public class SortedSparseMultigraph<V,E> 
    extends OrderedSparseMultigraph<V,E>
    implements Graph<V,E>, Serializable {
	
	public static <V,E> Factory<Graph<V,E>> getFactory() { 
		return new Factory<Graph<V,E>> () {
			public Graph<V,E> create() {
				return new SortedSparseMultigraph<V,E>();
			}
		};
	}
    
    /**
     * <code>Comparator</code> used in ordering vertices.  Defaults to <code>util.ComparableComparator</code>
     * if no comparators are specified in the constructor.
     */
    protected Comparator<V> vertex_comparator;

    /**
     * <code>Comparator</code> used in ordering edges.  Defaults to <code>util.ComparableComparator</code>
     * if no comparators are specified in the constructor.
     */
    protected Comparator<E> edge_comparator;
    
    public SortedSparseMultigraph(Comparator<V> vertex_comparator, Comparator<E> edge_comparator)
    {
        this.vertex_comparator = vertex_comparator;
        this.edge_comparator = edge_comparator;
        vertices = new TreeMap<V, Pair<Set<E>>>(vertex_comparator);
        edges = new TreeMap<E, Pair<V>>(edge_comparator);
        directedEdges = new TreeSet<E>(edge_comparator);
    }
    
    public SortedSparseMultigraph()
    {
        this(new ComparableComparator<V>(), new ComparableComparator<E>());
    }

    public boolean addVertex(V vertex) {
        if (!vertices.containsKey(vertex)) {
            vertices.put(vertex, new Pair<Set<E>>(new TreeSet<E>(edge_comparator), new TreeSet<E>(edge_comparator)));
            return true;
        } else {
        	return false;
        }
    }
}
