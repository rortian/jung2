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
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;

import edu.uci.ics.graph.Graph;



/**
 * Finds all weak components in a graph where a weak component is defined as
 * a maximal subgraph in which all pairs of vertices in the subgraph are reachable from one
 * another in the underlying undirected subgraph.
 * <p>
 * Running time: O(|V| + |E|) where |V| is the number of vertices and |E| is the number of edges.
 * @author Scott White
 */
public class WeakComponentClusterer<V,E> implements GraphClusterer<V,E,V> {

    /**
     * Extracts the weak components from a graph.
     * @param aGraph the graph whose weak components are to be extracted
     * @return the list of weak components
     */
    public ClusterSet extract(Graph<V,E> aGraph) {

        ClusterSet<V,E,V> clusterSet = new VertexClusterSet<V,E>(aGraph);

        HashSet<V> unvisitedVertices = new HashSet<V>();
        for (Iterator<V> vIt=aGraph.getVertices().iterator(); vIt.hasNext();) {
            unvisitedVertices.add(vIt.next());
        }

        while (!unvisitedVertices.isEmpty()) {
            Set<V> weakComponentSet = new HashSet<V>();
            V root = unvisitedVertices.iterator().next();
            unvisitedVertices.remove(root);
            weakComponentSet.add(root);

            Buffer<V> queue = new UnboundedFifoBuffer<V>();
            queue.add(root);

            while (!queue.isEmpty()) {
                V currentVertex = queue.remove();
                Collection<V> neighbors = aGraph.getNeighbors(currentVertex);

                for (Iterator<V> nIt = neighbors.iterator(); nIt.hasNext();) {
                    V neighbor = nIt.next();
                    if (unvisitedVertices.contains(neighbor)) {
                        queue.add(neighbor);
                        unvisitedVertices.remove(neighbor);
                        weakComponentSet.add(neighbor);
                    }
                }
            }
            clusterSet.addCluster(weakComponentSet);
        }

        return clusterSet;
    }

}
