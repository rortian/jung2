/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.cluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.graph.Graph;


/**
 * A ClusterSet where each cluster is a set of vertices
 * @author Scott White
 */
public class VertexClusterSet<V,E> extends ClusterSet<V,E> {

    /**
     * Constructs and initializes the set
     * @param underlyingGraph
     */
    public VertexClusterSet(Graph<V,E> underlyingGraph) {
        super(underlyingGraph);
    }

    /**
     * Constructs a new graph from the given cluster
     * @param index the position index of the cluster in the collection
     * @return a new graph representing the cluster
     */
    public Graph<V,E> getClusterAsNewSubGraph(int index) {
    	Graph<V, E> graph = null;
		try {
			graph = getUnderlyingGraph().getClass().newInstance();
	    	for(V v : getCluster(index)) {
	    		graph.addVertex(v);
	    	}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return graph;
//        return GraphUtils.vertexSetToGraph(getCluster(index));
    }

    /**
     * Creates a new cluster set where each vertex and cluster in the new cluster set correspond 1-to-1 with
     * those in the original graph
     * @param anotherGraph a new graph whose vertices are equivalent to those in the original graph
     * @return a new cluster set for the specified graph
     */
    public ClusterSet<V,E> createEquivalentClusterSet(Graph<V,E> anotherGraph) {
        ClusterSet<V,E> newClusterSet = new VertexClusterSet<V,E>(anotherGraph);
        for (Iterator<Set<V>> cIt=iterator();cIt.hasNext();) {
            Set<V> cluster = cIt.next();
            Set<V> newCluster = new HashSet<V>();
            for (Iterator<V> vIt=cluster.iterator();vIt.hasNext();) {
                V vertex = vIt.next();
//                V equivalentVertex = vertex.getEqualVertex(anotherGraph);
//                if (equivalentVertex == null) {
//                    throw new IllegalArgumentException("Can not create equivalent cluster set because equivalent vertices could not be found in the other graph.");
//                }
                newCluster.add(vertex);
            }
            newClusterSet.addCluster(newCluster);
        }
        return newClusterSet;

    }
}
