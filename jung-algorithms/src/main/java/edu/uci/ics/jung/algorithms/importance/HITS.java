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
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;


/**
 * Calculates the "hubs-and-authorities" importance measures for each node in a graph.
 * These measures are defined recursively as follows:
 *
 * <ul>
 * <li>The *hubness* of a node is the degree to which a node links to other important authorities</li>
 * <li>The *authoritativeness* of a node is the degree to which a node is pointed to by important hubs</li>
 * <p>
 * Note: This algorithm uses the same key as HITSWithPriors for storing rank sccores.
 * <p>
 * A simple example of usage is:
 * <pre>
 * HITS ranker = new HITS(someGraph);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * <p>
 * Running time: O(|V|*I) where |V| is the number of vertices and I is the number of iterations until convergence
 *
 * @deprecated As of JUNG 2.0 beta, replaced with {@link edu.uci.ics.jung.algorithms.scoring.HITS}.
 * 
 * @author Scott White
 * @author Tom Nelson - adapted to jung2
 * @see "Authoritative sources in a hyperlinked environment by Jon Kleinberg, 1997"
 */
public class HITS<V,E> extends AbstractRanker<V,E> {
    protected static final String AUTHORITY_KEY = "jung.algorithms.importance.AUTHORITY";
    protected static final String HUB_KEY = "jung.algorithms.importance.HUB";
    private Object mKeyToUseForRanking;
    private Map<V,Number> mPreviousAuthorityScores;
    private Map<V,Number> mPreviousHubScores;

    /**
     * Constructs an instance of the ranker where the type of importance that is associated with the
     * rank score is the node's importance as an authority.
     * @param graph the graph whose nodes are to be ranked
     * @param useAuthorityForRanking
     */
    public HITS(Graph<V,E> graph, boolean useAuthorityForRanking) {
        mKeyToUseForRanking = AUTHORITY_KEY;
        if (!useAuthorityForRanking) {
        	mKeyToUseForRanking = HUB_KEY;
        }
        initialize(graph);
    }

    /**
     * Constructs an instance of the ranker where the type of importance that is associated with the
     * rank score is the node's importance as an authority.
     * @param graph the graph whose nodes are to be ranked
     */
    public HITS(Graph<V,E> graph) {
        mKeyToUseForRanking = AUTHORITY_KEY;
        initialize(graph);
    }

    protected void initialize(Graph<V,E> g) {

        super.initialize(g, true, false);

        mPreviousAuthorityScores = new HashMap<V,Number>();
        mPreviousHubScores = new HashMap<V,Number>();

        for (V currentVertex : g.getVertices()) {
            setVertexRankScore(currentVertex, 1.0, AUTHORITY_KEY);
            setVertexRankScore(currentVertex, 1.0, HUB_KEY);

            mPreviousAuthorityScores.put(currentVertex, 0);
            mPreviousHubScores.put(currentVertex, 0);
        }
    }

    @Override
    protected void finalizeIterations() {
        super.finalizeIterations();
        for (V v : getVertices()) {
            if (mKeyToUseForRanking.equals(AUTHORITY_KEY)) {
            	super.removeVertexRankScore(v, HUB_KEY);
            } else {
            	super.removeVertexRankScore(v, AUTHORITY_KEY);
            }
        }
    }

    /**
     * the user datum key used to store the rank scores
     * @return the key
     */
    @Override
    public Object getRankScoreKey() {
        return mKeyToUseForRanking;
    }

    /**
     * Given a node, returns the corresponding rank score. This implementation of <code>getRankScore</code> assumes
     * the decoration representing the rank score is of type <code>MutableDouble</code>.
     * @return the rank score for this node
     */
    @Override
    public double getVertexRankScore(V v) {
        return getVertexRankScore(v, mKeyToUseForRanking);
    }

    protected double getPreviousAuthorityScore(V v) {
        return mPreviousAuthorityScores.get(v).doubleValue();
    }

    protected double getPreviousHubScore(V v) {
        return mPreviousHubScores.get(v).doubleValue();
    }

    @Override
    public void step() {
        updatePreviousScores();

        //Perform 2 update steps
        updateAuthorityRankings();
        updateHubRankings();

        double hubMSE = 0;
        double authorityMSE = 0;

        //Normalize rankings and test for convergence
        int numVertices = getVertexCount();
        for (V v : getVertices()) {

            double currentAuthorityScore = getVertexRankScore(v, AUTHORITY_KEY);
            double currentHubScore = getVertexRankScore(v, HUB_KEY);

            double previousAuthorityScore = getPreviousAuthorityScore(v);
            double previousHubScore = getPreviousHubScore(v);

            hubMSE += Math.pow(currentHubScore - previousHubScore, 2);
            authorityMSE += Math.pow(currentAuthorityScore - previousAuthorityScore, 2);
        }

        hubMSE = Math.pow(hubMSE / numVertices, 0.5);
        authorityMSE = Math.pow(authorityMSE / numVertices, 0.5);

        setPrecision(hubMSE+authorityMSE);
    }

    /**
     * If <code>evaluate()</code> has not already been called, the user can override the type of importance.
     * (hub or authority) that should be associated with the rank score.
     * @param useAuthorityForRanking if <code>true</code>, authority is used; if <code>false</code>, hub is used
     */
    public void setUseAuthorityForRanking(boolean useAuthorityForRanking) {
        if (useAuthorityForRanking) {
            mKeyToUseForRanking = AUTHORITY_KEY;
        } else {
            mKeyToUseForRanking = HUB_KEY;
        }
    }

    private double computeSum(Collection<V> neighbors, Object key) {
        double sum = 0;
        for (V v : neighbors) {
//            Vertex currentNeighbor = (Vertex) neighborIt.next();
            sum += getVertexRankScore(v, key);
        }
        return sum;
    }

    private void normalizeRankings(double normConstant, Object key) {
        for (V v : getVertices()) {
            double rankScore = getVertexRankScore(v,key);
            setVertexRankScore(v,rankScore/normConstant,key);
        }
    }

    protected void updateAuthorityRankings() {
        double total = 0;
        //compute authority scores
        for (V v : getVertices()) {
            double currentHubSum = computeSum(getGraph().getPredecessors(v), HUB_KEY);
            double newAuthorityScore = currentHubSum;
//            total += newAuthorityScore;
            total += newAuthorityScore * newAuthorityScore;
            setVertexRankScore(v, newAuthorityScore, AUTHORITY_KEY);
        }

        normalizeRankings(Math.sqrt(total), AUTHORITY_KEY);
    }

    protected void updateHubRankings() {
        double total = 0;

        //compute hub scores
        for (V v : getVertices()) {
            double currentAuthoritySum = computeSum(getGraph().getSuccessors(v), AUTHORITY_KEY);
            double newHubScore = currentAuthoritySum;
//            total += newHubScore;
            total += newHubScore * newHubScore;
            setVertexRankScore(v, newHubScore, HUB_KEY);
        }
        normalizeRankings(Math.sqrt(total), HUB_KEY);
    }

    protected void updatePreviousScores() {
        for (V v : getVertices()) {
            double currentAuthorityScore = getVertexRankScore(v, AUTHORITY_KEY);
            mPreviousAuthorityScores.put(v,currentAuthorityScore);

            double currentHubScore = getVertexRankScore(v, HUB_KEY);
            mPreviousHubScores.put(v, currentHubScore);
        }
    }
}
