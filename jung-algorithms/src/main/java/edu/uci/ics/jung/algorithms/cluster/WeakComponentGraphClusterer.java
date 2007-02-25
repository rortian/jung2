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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;



/**
 * Finds all weak components in a graph where a weak component is defined as
 * a maximal subgraph in which all pairs of vertices in the subgraph are reachable from one
 * another in the underlying undirected subgraph.
 * <p>
 * Running time: O(|V| + |E|) where |V| is the number of vertices and |E| is the number of edges.
 * @author Scott White
 */
public class WeakComponentGraphClusterer<V,E> implements Transformer<Graph<V,E>, Collection<Graph<V,E>>> {

	private Factory<Graph<V,E>> graphFactory;
	
    public WeakComponentGraphClusterer() {
    	this(null);
	}

    public WeakComponentGraphClusterer(Factory<Graph<V, E>> graphFactory) {
		this.graphFactory = graphFactory;
	}

	/**
     * Extracts the weak components from a graph.
     * @param graph the graph whose weak components are to be extracted
     * @return the list of weak components
     */
    public Collection<Graph<V,E>> transform(Graph<V,E> graph) {

        Set<Graph<V,E>> clusterSet = new HashSet<Graph<V,E>>();

        HashSet<V> unvisitedVertices = new HashSet<V>(graph.getVertices());

        while (!unvisitedVertices.isEmpty()) {
        	Graph<V, E> cluster;
        	if(graphFactory != null) {
        		cluster = graphFactory.create();
        	} else {
        		try {
        			cluster = (Graph<V,E>)graph.getClass().newInstance();
        		} catch (Exception e1) {
        			throw new RuntimeException(e1);
        		}
        	}
            V root = unvisitedVertices.iterator().next();
            unvisitedVertices.remove(root);
            cluster.addVertex(root);

            Buffer<V> queue = new UnboundedFifoBuffer<V>();
            queue.add(root);

            while (!queue.isEmpty()) {
                V currentVertex = queue.remove();
                Collection<V> neighbors = graph.getNeighbors(currentVertex);

                for(V neighbor : neighbors) {
                    if (unvisitedVertices.contains(neighbor)) {
                        queue.add(neighbor);
                        unvisitedVertices.remove(neighbor);
                        cluster.addVertex(neighbor);
                        Collection<E> neighborIncidentEdges = graph.getIncidentEdges(neighbor);
                        for(E e : neighborIncidentEdges) {
                        	Pair<V> ep = graph.getEndpoints(e);
                        	if(cluster.getEdges().contains(e) == false && 
                        			cluster.getVertices().containsAll(ep)) {
                        		cluster.addEdge(e, ep.getFirst(), ep.getSecond());
                        	}
                        }
                    }
                }
            }
            clusterSet.add(cluster);
        }
        return clusterSet;
    }

}
