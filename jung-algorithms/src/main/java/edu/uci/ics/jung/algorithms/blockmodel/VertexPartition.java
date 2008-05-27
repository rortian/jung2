/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Feb 3, 2004
 */
package edu.uci.ics.jung.algorithms.blockmodel;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;


/**
 * Maintains information about a vertex partition of a graph.
 */
public class VertexPartition<V,E> {

	private Map<V,Set<V>> vertex_partitions;
	private Set<Set<V>> partitions;
	private Graph<V,E> graph;

	
	/**
	 * Creates an instance based on the specified graph and mapping from vertices
	 * to vertex sets, and generates a set of partitions based on this mapping.
	 * @param g the graph over which the vertex partition is defined
	 * @param rv the mapping from vertices to vertex sets (partitions)
	 */
	public VertexPartition(Graph<V,E> g, Map<V, Set<V>> rv) 
	{
		this.vertex_partitions = Collections.unmodifiableMap( rv );
		this.partitions = new HashSet<Set<V>>();
		this.partitions.addAll(vertex_partitions.values());
		this.graph = g;
	}

	/**
     * Creates an instance based on the specified graph, vertex-set mapping, and vertex partitions.
     * The vertex-set mapping and vertex partitions must be consistent, i.e., the mapping must
     * reflect the division of vertices into partitions, and each vertex must appear in exactly
     * one partition.
     * @param g the graph over which the vertex partition is defined
     * @param rv the mapping from vertices to vertex sets (partitions)
	 * @param vertex_partitions 
	 */
    public VertexPartition(Graph<V,E> g, Map<V, Set<V>> rv, Set<Set<V>> vertex_partitions) {
        this.vertex_partitions = Collections.unmodifiableMap( rv );
        this.partitions = vertex_partitions;
        this.graph = g;
    }

    public VertexPartition(Graph<V,E> g, Set<Set<V>> equivalence_sets)
    {
        this.partitions = equivalence_sets;
        this.graph = g;
        this.vertex_partitions = new HashMap<V, Set<V>>();
        for (Set<V> set : equivalence_sets)
            for (V v : set)
                vertex_partitions.put(v, set);
    }
	
    /**
     * Returns the graph on which the partition is defined.
     * @return the graph on which the partition is defined
     */
	public Graph<V,E> getGraph() 
	{
		return graph;
	}

	/**
	 * Returns a map from each vertex in the input graph to its partition.
	 * @return
	 */
	public Map<V,Set<V>> getVertexToPartitionMap() 
	{
		return vertex_partitions;
	}
	
	public Set<Set<V>> getVertexPartitions() 
	{
	    return partitions;
	}

	/**
	 * Returns the number of partitions.
	 */
	public int numPartitions() 
	{
		return partitions.size();
	}
	
	@Override
  	public String toString() 
	{
		return "Partitions: " + vertex_partitions;
	}

}
