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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cern.colt.list.DoubleArrayList;
import corejava.Format;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.IterativeProcess;

/**
 * Abstract class for algorithms that rank nodes or edges by some "importance" metric. Provides a common set of
 * services such as:
 * <ul>
 *  <li> storing rank scores</li>
 *  <li> getters and setters for rank scores</li>
 *  <li> computing default edge weights</li>
 *  <li> normalizing default or user-provided edge transition weights </li>
 *  <li> normalizing rank scores</li>
 *  <li> automatic cleanup of decorations</li>
 *  <li> creation of Ranking list</li>
 * <li>print rankings in sorted order by rank</li>
 * </ul>
 * <p>
 * By default, all rank scores are removed from the vertices (or edges) being ranked.
 * @author Scott White
 */
public abstract class AbstractRanker<V,E> extends IterativeProcess {
    private Graph<V,E> mGraph;
    private List<Ranking> mRankings;
    public static final String DEFAULT_EDGE_WEIGHT_KEY = "jung.algorithms.importance.AbstractRanker.EdgeWeight";
//    private String mUserDefinedEdgeWeightKey;
    private boolean mRemoveRankScoresOnFinalize;
    private boolean mRankNodes;
    private boolean mRankEdges;
    private boolean mNormalizeRankings;
    private Map<Object, Number> scoreMap = new HashMap<Object, Number>();
    private Map<E,Number> edgeWeights = new HashMap<E,Number>();

    protected void initialize(Graph<V,E> graph, boolean isNodeRanker, 
        boolean isEdgeRanker)
    {
        if (!isNodeRanker && !isEdgeRanker)
            throw new IllegalArgumentException("Must rank edges, vertices, or both");
        mGraph = graph;
        mRemoveRankScoresOnFinalize = true;
        mNormalizeRankings = true;
//        mUserDefinedEdgeWeightKey = null;
        mRankNodes = isNodeRanker;
        mRankEdges = isEdgeRanker;
    }
    
    /**
	 * @return the scoreMap
	 */
	public Map<Object, Number> getScoreMap() {
		return scoreMap;
	}

	protected Collection<V> getVertices() {
        return mGraph.getVertices();
    }

    protected Graph<V,E> getGraph() {
        return mGraph;
    }

    protected void reinitialize() {
    }

    /**
     * Returns <code>true</code> if this ranker ranks nodes, and 
     * <code>false</code> otherwise.
     */
    public boolean isRankingNodes() {
        return mRankNodes;
    }

    /**
     * Returns <code>true</code> if this ranker ranks edges, and 
     * <code>false</code> otherwise.
     */
    public boolean isRankingEdges()
    {
        return mRankEdges;
    }
    
    /**
     * Instructs the ranker whether or not it should remove the rank scores from the nodes (or edges) once the ranks
     * have been computed.
     * @param removeRankScoresOnFinalize <code>true</code> if the rank scores are to be removed, <code>false</code> otherwise
     */
    public void setRemoveRankScoresOnFinalize(boolean removeRankScoresOnFinalize) {
        this.mRemoveRankScoresOnFinalize = removeRankScoresOnFinalize;
    }

    protected void onFinalize(Object e) {}

    protected void finalizeIterations() {
        ArrayList<Ranking> sortedRankings = new ArrayList<Ranking>();

        int id = 1;
        if (mRankNodes) 
        {
            for (V currentVertex : getVertices()) {
                NodeRanking<V> ranking = new NodeRanking<V>(id,getRankScore(currentVertex),currentVertex);
                sortedRankings.add(ranking);
                if (mRemoveRankScoresOnFinalize) {
                	this.scoreMap.remove(currentVertex);
                }
                id++;
                onFinalize(currentVertex);
            }
        }
        if (mRankEdges) 
        {
            for (E currentEdge : mGraph.getEdges()) {

                EdgeRanking<E> ranking = new EdgeRanking<E>(id,getRankScore(currentEdge),currentEdge);
                sortedRankings.add(ranking);
                if (mRemoveRankScoresOnFinalize) {
                	this.scoreMap.remove(currentEdge);
                }
                id++;
                onFinalize(currentEdge);
            }
        }

        mRankings = sortedRankings;
        Collections.<Ranking>sort(mRankings);
    }

    /**
     * Retrieves the list of ranking instances in descending sorted order by rank score
     * If the algorithm is ranking edges, the instances will be of type <code>EdgeRanking</code>, otherwise
     * if the algorithm is ranking nodes the instances will be of type <code>NodeRanking</code>
     * @return  the list of rankings
     */
    public List<Ranking> getRankings() {
        return mRankings;
    }

    /**
     * Return a list of the top k rank scores.
     * @param topKRankings the value of k to use
     * @return list of rank scores
     */
    public DoubleArrayList getRankScores(int topKRankings) {
        DoubleArrayList scores = new DoubleArrayList();
        int count=1;
        for (Iterator rIt=getRankings().iterator(); rIt.hasNext();) {
            if (count > topKRankings) {
                return scores;
            }
            NodeRanking currentRanking = (NodeRanking) rIt.next();
            scores.add(currentRanking.rankScore);
            count++;
        }

        return scores;
    }

    /**
     * The user datum key used to store the rank score.
     * @return the key
     */
//    abstract public String getRankScoreKey();

    /**
     * Given an edge or node, returns the corresponding rank score. This is a default
     * implementation of getRankScore which assumes the decorations are of type MutableDouble.
     * This method only returns legal values if <code>setRemoveRankScoresOnFinalize(false)</code> was called
     * prior to <code>evaluate()</code>.
     * @return  the rank score value
     */
    public double getRankScore(Object e) {
        Number rankScore = scoreMap.get(e);
        if (rankScore != null) {
            return rankScore.doubleValue();
        } else {
            throw new RuntimeException("setRemoveRankScoresOnFinalize(false) must be called before evaluate().");
        }

    }

    protected void setRankScore(Object e, double rankValue) {
    	if(scoreMap.containsKey(e) == false) {
    		scoreMap.put(e, rankValue);
        } else {
        	scoreMap.put(e, scoreMap.get(e).doubleValue() + rankValue);
        }

    }

    protected double getEdgeWeight(E e) {
    	return edgeWeights.get(e).doubleValue();
    }

    /**
     * the user datum key used to store the edge weight, if any
     * @return  the key
     */
//    public String getEdgeWeightKeyName() {
//        if (mUserDefinedEdgeWeightKey == null) {
//           return DEFAULT_EDGE_WEIGHT_KEY;
//        } else {
//            return mUserDefinedEdgeWeightKey;
//        }
//    }

    protected void setEdgeWeight(E e, double weight) {
    	edgeWeights.put(e, weight);
    }

    protected void assignDefaultEdgeTransitionWeights() {

        for (V currentVertex : getVertices()) {

            Collection<E> outgoingEdges = mGraph.getOutEdges(currentVertex);

            double numOutEdges = outgoingEdges.size();
            for (E currentEdge : outgoingEdges) {
                setEdgeWeight(currentEdge,1.0/numOutEdges);
            }
        }

    }


    protected void normalizeEdgeTransitionWeights() {

        for (V currentVertex : getVertices()) {

        	Collection<E> outgoingEdges = mGraph.getOutEdges(currentVertex);

            double totalEdgeWeight = 0;
            for (E currentEdge : outgoingEdges) {
                totalEdgeWeight += getEdgeWeight(currentEdge);
            }

            //double numOutEdges = outgoingEdges.size();
            for (E currentEdge : outgoingEdges) {
                setEdgeWeight(currentEdge,getEdgeWeight(currentEdge)/totalEdgeWeight);
            }
        }
    }

    protected void normalizeRankings() {
        if (!mNormalizeRankings) {
            return;
        }
        double totalWeight = 0;

        for (V currentVertex : getVertices()) {
            totalWeight += getRankScore(currentVertex);
        }

        for (V currentVertex : getVertices()) {
            setRankScore(currentVertex,getRankScore(currentVertex)/totalWeight);
        }
    }

    /**
     * Print the rankings to standard out in descending order of rank score
     * @param verbose if <code>true</code>, include information about the actual rank order as well as
     * the original position of the vertex before it was ranked
     * @param printScore if <code>true</code>, include the actual value of the rank score
     */
    public void printRankings(boolean verbose,boolean printScore) {
            double total = 0;
            Format formatter = new Format("%7.6f");
            int rank = 1;
//            boolean hasLabels = StringLabeller.hasStringLabeller(getGraph());
//            StringLabeller labeller = StringLabeller.getLabeller(getGraph());
            for (Iterator it = getRankings().iterator(); it.hasNext();) {
                Ranking currentRanking = (Ranking) it.next();
                double rankScore = currentRanking.rankScore;
                if (verbose) {
                    System.out.print("Rank " + rank + ": ");
                    if (printScore) {
                        System.out.print(formatter.format(rankScore));
                    }
                    System.out.print("\tVertex Id: " + currentRanking.originalPos);
                    if (currentRanking instanceof NodeRanking) {
                        V v = ((NodeRanking<V>) currentRanking).getNode();
                        System.out.print(" (" + v + ")");
                    }
                    System.out.println();
                } else {
                    System.out.print(rank + "\t");
                     if (printScore) {
                        System.out.print(formatter.format(rankScore));
                    }
                    System.out.println("\t" + currentRanking.originalPos);

                }
                total += rankScore;
                rank++;
            }

            if (verbose) {
                System.out.println("Total: " + formatter.format(total));
            }
    }

    /**
     * Allows the user to specify whether or not s/he wants the rankings to be normalized.
     * In some cases, this will have no effect since the algorithm doesn't allow normalization
     * as an option
     * @param normalizeRankings
     */
    public void setNormalizeRankings(boolean normalizeRankings) {
        mNormalizeRankings = normalizeRankings;
    }

    /**
     * Allows the user to provide his own set of data instances as edge weights by giving the ranker
     * the <code>UserDatum</code> key where those instances can be found.
     * @param keyName the name of the <code>UserDatum</code> key where the data instance representing an edge weight
     * can be found
     */
//    public void setUserDefinedEdgeWeightKey(String keyName) {
//        mUserDefinedEdgeWeightKey = keyName;
//    }
}
