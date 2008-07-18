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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.util.NumericalPrecision;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * This algorithm measures the importance of a node in terms of the fraction of time spent at that node relative to
 * all other nodes. This fraction is measured by first transforming the graph into a first-order Markov chain
 * where the transition probability of going from node u to node v is equal to (1-alpha)*[1/outdegree(u)] + alpha*(1/|V|)
 * where |V| is the # of vertices in the graph and alpha is a parameter typically set to be between 0.1 and 0.2 (according
 * to the authors). If u has no out-edges in the original graph then 0 is used instead of 1/outdegree(v). Once the markov
 * chain is created, the stationary probability of being at each node (state) is computed using an iterative update
 * method that is guaranteed to converge if the markov chain is ergodic.
 * <p>
 * A simple example of usage is:
 * <pre>
 * PageRank ranker = new PageRank(someGraph,0.15);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * <p>
 * Running time: O(|E|*I) where |E| is the number of edges and I is the number of iterations until convergence
 *
 * @author Scott White
 * @see "The Anatomy of a Large-Scale Hypertextual Web Search Engine by L. Page and S. Brin, 1999"
 */
public class PageRank<V,E> extends RelativeAuthorityRanker<V,E> {
    public final static String KEY = "jung.algorithms.importance.PageRank.RankScore";
    private double mAlpha;
    private final HashMap<V,Number> mPreviousRankingsMap = new HashMap<V,Number>();
    private final Set<V> mUnreachableVertices = new HashSet<V>();
    private final Set<V> mReachableVertices = new HashSet<V>();
    private final Set<V> mLeafNodes = new HashSet<V>();

    /**
     * Basic constructor which initializes the algorithm
     * @param graph the graph whose nodes are to be ranked
     * @param bias the value (between 0 and 1) that indicates how much to dampen the underlying markov chain
     * with underlying uniform transitions over all nodes. Generally, values between 0.0-0.3 are used.
     */
    public PageRank(DirectedGraph<V,E> graph, double bias) {
        initialize(graph, bias, null);
        initializeRankings(graph.getVertices(), new HashSet<V>());
    }

    /**
     * Specialized constructor that allows the user to specify an edge key if edges already have user-defined
     * weights assigned to them.
     * @param graph the graph whose nodes are to be ranked
     * @param bias the value (between 0 and 1) that indicates how much to dampen the underlying markov chain
     * with underlying uniform transitions over all nodes. Generally, values between 0.0-0.3 are used.
     * @param edgeWeights if non-null, uses the user-defined weights to compute the transition probabilities;
     * if null then default transition probabilities (1/outdegree(u)) are used
     */
    public PageRank(DirectedGraph<V,E> graph, double bias, Map<E,Number> edgeWeights) {
        initialize(graph, bias, edgeWeights);
        initializeRankings(graph.getVertices(), new HashSet<V>());
    }

    protected PageRank(DirectedGraph<V,E> graph, double bias, Map<E,Number> edgeWeights, Pair<Set<V>> reachables) {
        initialize(graph, bias, edgeWeights);
        initializeRankings(reachables.getFirst(), reachables.getSecond());
    }

    protected void initialize(DirectedGraph<V,E> graph, double bias, Map<E,Number> edgeWeights) {
        super.initialize(graph, true, false);
        if ((bias < 0) || (bias > 1.0)) {
            throw new IllegalArgumentException("Bias " + bias + " must be between 0 and 1.");
        }
        mAlpha = bias;
        if (edgeWeights == null) {
            assignDefaultEdgeTransitionWeights();
        } else {
            setEdgeWeights(edgeWeights);
            normalizeEdgeTransitionWeights();
        }

    }

    protected void initializeRankings(Collection<V> reachableVertices, Collection<V> unreachableVertices) {

    	mReachableVertices.clear();
        mReachableVertices.addAll(reachableVertices);
        double numVertices = reachableVertices.size();
        mPreviousRankingsMap.clear();
        mLeafNodes.clear();
        for (V currentVertex : mReachableVertices) {
            setVertexRankScore(currentVertex, 1.0 / numVertices);
            setPriorRankScore(currentVertex, 1.0 / numVertices);
            mPreviousRankingsMap.put(currentVertex, new Double(1.0 / numVertices));
            if (getGraph().outDegree(currentVertex) == 0) {
                mLeafNodes.add(currentVertex);
            }
        }

        mUnreachableVertices.clear();
        mUnreachableVertices.addAll(unreachableVertices);
        for (V currentVertex : mUnreachableVertices) {
            setVertexRankScore(currentVertex, 0);
            setPriorRankScore(currentVertex, 0);
            mPreviousRankingsMap.put(currentVertex, new Double(0));
        }
    }

    @Override
    public void reset() {
        initializeRankings(mReachableVertices, mUnreachableVertices);
    }

    protected void updateRankings() {
        double totalSum = 0;

        for (V currentVertex : mReachableVertices) {

            Collection<E> incomingEdges = getGraph().getInEdges(currentVertex);
            double currentPageRankSum = 0;
            for (E incomingEdge : incomingEdges) {
                if (mUnreachableVertices.contains(getGraph().getOpposite(currentVertex, incomingEdge))) {
                    continue;
                }

                double currentWeight = getEdgeWeight(incomingEdge);
                currentPageRankSum += 
                	mPreviousRankingsMap.get(getGraph().getOpposite(currentVertex, incomingEdge)).doubleValue() * currentWeight;
            }

            if (getPriorRankScore(currentVertex) > 0) {
                for (V leafNode : mLeafNodes) {
                    double currentWeight = getPriorRankScore(currentVertex);
                    currentPageRankSum += (mPreviousRankingsMap.get(leafNode)).doubleValue() * currentWeight;
                }
            }

            //totalSum += currentPageRankSum;
            totalSum += currentPageRankSum * (1.0 - mAlpha) + mAlpha * getPriorRankScore(currentVertex);
            setVertexRankScore(currentVertex, currentPageRankSum * (1.0 - mAlpha) + mAlpha * getPriorRankScore(currentVertex));
        }
        
        if (!NumericalPrecision.equal(totalSum, 1, .05)) {
            System.err.println("Page rank scores can not be generated because the specified graph is not connected.");
            System.out.println(totalSum);
        }
    }

    @Override
    public void step() {
        updateRankings();

        double rankingMSE = 0;

        //Normalize rankings and test for convergence
        for (V currentVertex : mReachableVertices) {
            double previousRankScore = mPreviousRankingsMap.get(currentVertex).doubleValue();
            rankingMSE += Math.pow(getVertexRankScore(currentVertex) - previousRankScore, 2);
            mPreviousRankingsMap.put(currentVertex, getVertexRankScore(currentVertex));
        }

        rankingMSE = Math.pow(rankingMSE / getVertexCount(), 0.5);
        setPrecision(rankingMSE);
//        return rankingMSE;
    }

    /**
     * The user datum key used to store the rank scores.
     * @return the key
     */
    @Override
    public String getRankScoreKey() {
        return KEY;
    }

}
