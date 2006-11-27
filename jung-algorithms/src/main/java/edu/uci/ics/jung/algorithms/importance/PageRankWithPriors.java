/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.importance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;

/**
 * Algorithm that extends the PageRank algorithm by incorporating root nodes (priors). Whereas in PageRank
 * the importance of a node is implicitly computed relative to all nodes in the graph now importance
 * is computed relative to the specified root nodes.
 * <p>
 * Note: This algorithm uses the same key as PageRank for storing rank sccores
 * <p>
 * A simple example of usage is:
 * <pre>
 * PageRankWithPriors ranker = new PageRankWithPriors(someGraph,0.3,1,rootSet,null);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * <p>
 * Running time: O(|E|*I) where |E| is the number of edges and I is the number of iterations until convergence
 *
 * @author Scott White
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class PageRankWithPriors<V,E> extends PageRank<V,E> {

    /**
     * Constructs an instance of the ranker.
     * @param graph the graph whose nodes are being ranked
     * @param beta the prior weight to put on the root nodes
     * @param priors the set of root nodes
     * @param edgeWeightKeyName the user datum key associated with any user-defined weights. If there are none,
     * null should be passed in.
     */
    public PageRankWithPriors(DirectedGraph<V,E> graph, double beta, Set<V> priors, String edgeWeightKeyName) {
        super(graph, beta, edgeWeightKeyName,computeReachableVertices(graph,priors));
        setPriors(priors);
        initializePriorWeights();
    }

    protected void initializePriorWeights() {
        Collection<V> allVertices = getVertices();

        Set<V> priors = getPriors();
        double numPriors = priors.size();

        Set<V> nonPriors = new HashSet<V>();
        nonPriors.addAll(allVertices);
        nonPriors.removeAll(priors);

        for (V currentVertex : nonPriors) {
            setPriorRankScore(currentVertex, 0.0);
        }

        for (V currentVertex : getPriors()) {
            setPriorRankScore(currentVertex, 1.0 / numPriors);
        }
    }

    private static <V,E> Pair<Set<V>> computeReachableVertices(Graph<V,E> g, Set<V> priors) {

        BFSDistanceLabeler<V,E> labeler = new BFSDistanceLabeler<V,E>();
        labeler.labelDistances(g, priors);
        labeler.removeDecorations(g);
        Pair<Set<V>> p = new Pair<Set<V>>(new HashSet<V>(labeler.getVerticesInOrderVisited()),
                          new HashSet<V>(labeler.getUnivistedVertices()));

        return p;
    }

    protected void reinitialize() {
        super.reinitialize();
        initializePriorWeights();
    }
}
