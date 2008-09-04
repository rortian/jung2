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

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;


/**
 * Algorithm that extends the HITS algorithm by incorporating root nodes (priors). Whereas in HITS
 * the importance of a node is implicitly computed relative to all nodes in the graph, now importance
 * is computed relative to the specified root nodes.
 * <p>
 * A simple example of usage is:
 * <pre>
 * HITSWithPriors ranker = new HITSWithPriors(someGraph,0.3,rootSet);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * <p>
 * Running time: O(|V|*I) where |V| is the number of vertices and I is the number of iterations until convergence
 *
 * @deprecated As of JUNG 2.0 beta, replaced with {@link edu.uci.ics.jung.algorithms.scoring.HITSWithPriors}.
 *
 * @author Scott White
 * @author Tom Nelson - adapted to jung2
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class HITSWithPriors<V,E> extends RelativeAuthorityRanker<V,E> {
    protected static final String AUTHORITY_KEY = "jung.algorithms.importance.AUTHORITY";
    protected static final String HUB_KEY = "jung.algorithms.importance.HUB";
//    private static final String IN_EDGE_WEIGHT = "IN_EDGE_WEIGHT";
    private String mKeyToUseForRanking;
    private Map<V,Number> mPreviousAuthorityScores;
    private Map<V,Number> mPreviousHubScores;
    private double mBeta;
    Set<V> mReachableVertices;
    private Set<V> mLeafNodes;
    private Map<E,Number> inEdgeWeights = new HashMap<E,Number>();

    /**
     * Constructs an instance of the ranker where the type of importance that is associated with the
     * rank score is the node's importance as an authority.
     * @param graph the graph whose nodes are to be ranked
     * @param bias the weight that should be placed on the root nodes (between 0 and 1)
     * @param priors the set of root nodes
     */
    public HITSWithPriors(Graph<V,E> graph, double bias, Set<V> priors) {
        mKeyToUseForRanking = AUTHORITY_KEY;
        mBeta = bias;
        setPriors(priors);
        initialize(graph, null);
    }

    /**
     * More specialized constructor where the type of importance can be specified.
     * @param graph the graph whose nodes are to be ranked
     * @param useAuthorityForRanking
     * @param bias the weight that should be placed on the root nodes (between 0 and 1)
     * @param priors the set of root nodes
     */
    public HITSWithPriors(Graph<V,E> graph, boolean useAuthorityForRanking, double bias, 
    		Set<V> priors, String edgeWeightKey) {
        setUseAuthorityForRanking(useAuthorityForRanking);
        mBeta = bias;
        setPriors(priors);
        initialize(graph, edgeWeightKey);
    }

    protected void initialize(Graph<V,E> g, String edgeWeightKeyName) {

        super.initialize(g, true, false);

        mPreviousAuthorityScores = new HashMap<V,Number>();
        mPreviousHubScores = new HashMap<V,Number>();

        for (V v : g.getVertices()) {

            mPreviousAuthorityScores.put(v, 0);
            mPreviousHubScores.put(v, 0);

            setVertexRankScore(v, 0, AUTHORITY_KEY);
            setVertexRankScore(v, 0, HUB_KEY);

            setPriorRankScore(v, 0);
        }

        WeakComponentClusterer<V,E> wcExtractor = 
        	new WeakComponentClusterer<V,E>();
        Set<Set<V>> clusters = wcExtractor.transform(g);
        mReachableVertices = new HashSet<V>();

        double numPriors = getPriors().size();
        for (V v : getPriors()) {
            setPriorRankScore(v, 1.0 / numPriors);
            for (Set<V> members : clusters) {

                if (members.contains(v)) {
                    mReachableVertices.addAll(members);
                }
            }
        }

        mLeafNodes = new HashSet<V>();
        int numReachableVertices = mReachableVertices.size();
        for (V v : mReachableVertices) {
            setVertexRankScore(v, 1.0 / numReachableVertices, AUTHORITY_KEY);
            setVertexRankScore(v, 1.0 / numReachableVertices, HUB_KEY);
            if (getGraph().outDegree(v) == 0) {
                mLeafNodes.add(v);
            }
        }

        if (edgeWeightKeyName == null) {
            assignDefaultEdgeTransitionWeights();
        } else {
            setEdgeWeights(inEdgeWeights);
            normalizeEdgeTransitionWeights();
        }
        assignInlinkEdgeTransitionWeights();

    }

    private void normalizeRankings(double normConstant, Object key) {
        for (V v : getVertices()) {
            double rankScore = getVertexRankScore(v,key);
            setVertexRankScore(v,rankScore/normConstant,key);
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

    protected double getInEdgeWeight(E e) {
    	return inEdgeWeights.get(e).doubleValue();
    }

    protected void setInEdgeWeight(E e, double weight) {
    	inEdgeWeights.put(e, weight);
    }

    private void assignInlinkEdgeTransitionWeights() {

        for (V v : getVertices()) {

            Collection<E> incomingEdges = getGraph().getInEdges(v);

            double total = 0;
            for (E e : incomingEdges) {
                total += getEdgeWeight(e);
            }

            for (E e : incomingEdges) {
                double weight = getEdgeWeight(e);
                setInEdgeWeight(e, weight / total);
            }
        }
    }

    /**
     * the user datum key used to store the rank scores
     * @return the key
     */
    @Override
    public String getRankScoreKey() {
        return mKeyToUseForRanking;
    }

    protected double getPreviousAuthorityScore(V v) {
        return this.mPreviousAuthorityScores.get(v).doubleValue();
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

        //Test for convergence
        int numVertices = mReachableVertices.size();
        for (V v : mReachableVertices) {

            double currentAuthorityScore = getVertexRankScore(v, AUTHORITY_KEY);
            double currentHubScore = getVertexRankScore(v, HUB_KEY);

            double previousAuthorityScore = getPreviousAuthorityScore(v);
            double previousHubScore = getPreviousHubScore(v);

            hubMSE += Math.pow(currentHubScore - previousHubScore, 2);
            authorityMSE += Math.pow(currentAuthorityScore - previousAuthorityScore, 2);
        }

        hubMSE = Math.pow(hubMSE / numVertices, 0.5);
        authorityMSE = Math.pow(authorityMSE / numVertices, 0.5);

        setPrecision(hubMSE + authorityMSE);
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

    private double computeSum(V v, String key) {

        Collection<E> edges = null;
        String oppositeKey = null;
        if (key.equals(HUB_KEY)) {
            edges = getGraph().getOutEdges(v);
            oppositeKey = AUTHORITY_KEY;
        } else {
            edges = getGraph().getInEdges(v);
            oppositeKey = HUB_KEY;
        }

        double sum = 0;
        for (E e : edges) {

//            double currentWeight = 0;
//            if (key.equals(AUTHORITY_KEY)) {
//                currentWeight = getEdgeWeight(e);
//            } else {
//                currentWeight = getInEdgeWeight(e);
//            }
            double currentWeight = 1.0;
            sum += getVertexRankScore(getGraph().getOpposite(v,e), oppositeKey) * currentWeight;
        }

        if (getPriorRankScore(v) > 0) {
            if (key.equals(AUTHORITY_KEY)) {
                for (V leafNode : mLeafNodes) {
                    double currentWeight = getPriorRankScore(v);
                    sum += getVertexRankScore(leafNode, oppositeKey) * currentWeight;
                }
            }
        }

        return sum;
    }

    protected void updateAuthorityRankings() {
        double authoritySum = 0;
        
        double total = 0;
        //compute authority scores
        for (V v : mReachableVertices) {
            double newAuthorityScore = computeSum(v, AUTHORITY_KEY) * (1.0 - mBeta) + mBeta * getPriorRankScore(v);
            authoritySum += newAuthorityScore;
            total += newAuthorityScore * newAuthorityScore;
            setVertexRankScore(v, newAuthorityScore, AUTHORITY_KEY);
        }
        
        normalizeRankings(Math.sqrt(total), AUTHORITY_KEY);

//        if (!NumericalPrecision.equal(authoritySum, 1.0, .1)) {
//            System.err.println("HITS With Priors scores can not be generrated because the specified graph is not connected.");
//            System.err.println("Authority Sum: " + authoritySum);
//        }

    }

    protected void updateHubRankings() {
        double hubSum = 0;
        
        double total = 0;
        //compute hub scores
        for (V v : mReachableVertices) {
            double newHubScore = computeSum(v, HUB_KEY) * (1.0 - mBeta) + mBeta * getPriorRankScore(v);
            hubSum += newHubScore;
            total += newHubScore * newHubScore;
            setVertexRankScore(v, newHubScore, HUB_KEY);
        }

        normalizeRankings(Math.sqrt(total), HUB_KEY);

//        if (!NumericalPrecision.equal(hubSum, 1.0, .1)) {
//            System.err.println("HITS With Priors scores can not be generrated because the specified graph is not connected.");
//            System.err.println("Hub Sum: " + hubSum);
//        }
    }


    protected void updatePreviousScores() {
        for (V v : getVertices()) {

        	mPreviousAuthorityScores.put(v, getVertexRankScore(v, AUTHORITY_KEY));

        	mPreviousHubScores.put(v, getVertexRankScore(v, HUB_KEY));
        }
    }
}
