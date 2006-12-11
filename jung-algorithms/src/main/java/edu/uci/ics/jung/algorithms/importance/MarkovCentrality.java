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
import java.util.List;
import java.util.Map;
import java.util.Set;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.jung.algorithms.GraphMatrixOperations;

/**
 * @author Scott White and Joshua O'Madadhain
 * @author Tom Nelson - adapted to jung2
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class MarkovCentrality<V,E> extends RelativeAuthorityRanker<V,E> {
    public final static String MEAN_FIRST_PASSAGE_TIME = "jung.algorithms.importance.mean_first_passage_time";
    private DoubleMatrix1D mRankings;
    private List<V> mIndexer;

    public MarkovCentrality(DirectedGraph<V,E> graph, Set<V> rootNodes) {
        this(graph,rootNodes,null);
    }

    public MarkovCentrality(DirectedGraph<V,E> graph, Set<V> rootNodes, Map<E,Number> edgeWeightKey) {
        super.initialize(graph, true, false);
        setPriors(rootNodes);
        if (edgeWeightKey == null)
            assignDefaultEdgeTransitionWeights();
        else
            setEdgeWeights(edgeWeightKey);
        normalizeEdgeTransitionWeights();

        mIndexer = new ArrayList<V>(graph.getVertices());
        mRankings = new SparseDoubleMatrix1D(graph.getVertices().size());
    }

    /**
     * @see edu.uci.ics.jung.algorithms.importance.AbstractRanker#getRankScoreKey()
     */
    public String getRankScoreKey() {
        return MEAN_FIRST_PASSAGE_TIME;
    }

    /**
     * @see edu.uci.ics.jung.algorithms.importance.AbstractRanker#getRankScore(edu.uci.ics.jung.graph.Element)
     */
    public double getVertexRankScore(V vert) {
        return mRankings.get(mIndexer.indexOf(vert));
    }

    /**
     * @see edu.uci.ics.jung.algorithms.IterativeProcess#evaluateIteration()
     */
    public void step() {
        DoubleMatrix2D mFPTMatrix = GraphMatrixOperations.computeMeanFirstPassageMatrix(getGraph(), getEdgeWeights(), getStationaryDistribution());

        mRankings.assign(0);

        for (V p : getPriors()) {
            int p_id = mIndexer.indexOf(p);
            for (V v : getVertices()) {
                int v_id = mIndexer.indexOf(v);
                mRankings.set(v_id, mRankings.get(v_id) + mFPTMatrix.get(p_id, v_id));
            }
        }

        for (V v : getVertices()) {
            int v_id = mIndexer.indexOf(v);
            mRankings.set(v_id, 1 / (mRankings.get(v_id) / getPriors().size()));
        }

        double total = mRankings.zSum();

        for (V v : getVertices()) {
            int v_id = mIndexer.indexOf(v);
            mRankings.set(v_id, mRankings.get(v_id) / total);
        }
    }


    /**
     * Loads the stationary distribution into a vector if it was passed in,
     * or calculates it if not.
     *
     * @return DoubleMatrix1D
     */
    private DoubleMatrix1D getStationaryDistribution() {
        DoubleMatrix1D piVector = new DenseDoubleMatrix1D(getVertices().size());
        PageRank<V,E> pageRank = new PageRank<V,E>((DirectedGraph<V,E>)getGraph(), 0, getEdgeWeights());
        pageRank.evaluate();
        List<Ranking<?>> rankings = pageRank.getRankings();

        for (Ranking<?> rank : rankings) {
            piVector.set(mIndexer.indexOf(rank.getRanked()), rank.rankScore);
        }
        return piVector;
    }

}
