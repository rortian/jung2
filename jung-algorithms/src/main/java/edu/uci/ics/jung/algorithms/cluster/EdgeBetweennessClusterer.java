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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * An algorithm for computing clusters (community structure) in graphs based on edge betweenness.
 * [Note: The betweenness of an edge measure the extent to which that edge lies along shortest paths
 * between all pairs of nodes.]
 * Edges which are least central to communities are progressively removed until the communities
 * have been adequately seperated.
 *
 * This algorithm works by iteratively following the 2 step process:
 * <ul>
 * <li> Compute edge betweenness for all edges in current graph
 * <li> Remove edge with highest betweenness
 * <p>
 * Running time is: O(kmn) where k is the number of edges to remove, m is the total number of edges, and
 * n is the total number of vertices. For very sparse graphs the running time is closer to O(kn^2) and for
 * graphs with strong community structure, the complexity is even lower.
 * <p>
 * This algorithm is a slight modification of the algorithm discussed below in that the number of edges
 * to be removed is parameterized.
 * @author Scott White
 * @author Tom Nelson (converted to jung2)
 * @see "Community structure in social and biological networks by Michelle Girvan and Mark Newman"
 */
public class EdgeBetweennessClusterer<V,E> implements Transformer<Graph<V,E>,Set<Set<V>>> {
    private int mNumEdgesToRemove;
    private List<E> mEdgesRemoved;

   /**
    * Constructs a new clusterer for the specified graph.
    * @param numEdgesToRemove the number of edges to be progressively removed from the graph
    */
    public EdgeBetweennessClusterer(int numEdgesToRemove) {
        mNumEdgesToRemove = numEdgesToRemove;
       mEdgesRemoved = new ArrayList<E>();
    }

    /**
    * Finds the set of clusters which have the strongest "community structure".
    * The more edges removed the smaller and more cohesive the clusters.
    * @param g the graph
    */
    public Set<Set<V>> transform(Graph<V,E> graph) {
                
        if (mNumEdgesToRemove < 0 || mNumEdgesToRemove > graph.getEdgeCount()) {
            throw new IllegalArgumentException("Invalid number of edges passed in.");
        }
        
        // save the removed edges and endpoints here, as we would
        // otherwise be unable to get the endpoints from a removed
        // edge...
        Map<E,Pair<V>> removedEdges = new HashMap<E,Pair<V>>();
        mEdgesRemoved.clear();

        for (int k=0;k<mNumEdgesToRemove;k++) {
            BetweennessCentrality<V,E> bc = new BetweennessCentrality<V,E>(graph,false);
            bc.setRemoveRankScoresOnFinalize(true);
            bc.evaluate();
            Ranking<E> highestBetweenness = (Ranking<E>)bc.getRankings().get(0);
            E removedEdge = highestBetweenness.getRanked();
            Pair<V> removedEdgeEndpoints = graph.getEndpoints(removedEdge);
            removedEdges.put(removedEdge, removedEdgeEndpoints);
            mEdgesRemoved.add(highestBetweenness.getRanked());
            graph.removeEdge(highestBetweenness.getRanked());
        }

        WeakComponentVertexClusterer wcSearch = new WeakComponentVertexClusterer();
        Set<Set<V>> clusterSet = wcSearch.transform(graph);

        for (E edge : mEdgesRemoved ) {
        	Pair<V> endpoints = removedEdges.get(edge);
            graph.addEdge(edge,endpoints.getFirst(), endpoints.getSecond());
        }
        return clusterSet;
    }

    /**
     * Retrieves the list of all edges that were removed (assuming extract(...) was previously called. The edges returned
     * are stored in order in which they were removed
     * @return the edges in the original graph
     */
    public List<E> getEdgesRemoved() {
        return mEdgesRemoved;
    }
}
