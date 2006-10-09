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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.graph.DirectedGraph;



/**
 * This algorithm measures the importance of nodes based upon both the number and length of disjoint paths that lead
 * to a given node from each of the nodes in the root set. Specifically the formula for measuring the importance of a
 * node is given by: I(t|R) = sum_i=1_|P(r,t)|_{alpha^|p_i|} where alpha is the path decay coefficient, p_i is path i
 * and P(r,t) is a set of maximum-sized node-disjoint paths from r to t.
 * <p>
 * This algorithm uses heuristic breadth-first search to try and find the maximum-sized set of node-disjoint paths
 * between two nodes. As such, it is not guaranteed to give exact answers.
 * <p>
 * A simple example of usage is:
 * <pre>
 * WeightedNIPaths ranker = new WeightedNIPaths(someGraph,2.0,6,rootSet);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * 
 * @author Scott White
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class WeightedNIPaths<V,E> extends AbstractRanker<V,E> {
//    public final static String WEIGHTED_NIPATHS_KEY = "jung.algorithms.importance.WEIGHTED_NIPATHS_KEY";
    private double mAlpha;
    private int mMaxDepth;
    private Set<V> mPriors;
    private Map<V,Number> weightedNIPaths = new HashMap<V,Number>();
    private Map<E,Integer> pathIndices = new HashMap<E,Integer>();
    private Map<Object,V> roots = new HashMap<Object,V>();
    private Map<V,Set<Integer>> pathsSeen = new HashMap<V,Set<Integer>>();

    /**
     * Constructs and initializes the algorithm.
     * @param graph the graph whose nodes are being measured for their importance
     * @param alpha the path decay coefficient (>= 1); 2 is recommended
     * @param maxDepth the maximal depth to search out from the root set
     * @param priors the root set (starting vertices)
     */
    public WeightedNIPaths(DirectedGraph<V,E> graph, double alpha, int maxDepth, Set<V> priors) {
        super.initialize(graph, true,false);
        mAlpha = alpha;
        mMaxDepth = maxDepth;
        mPriors = priors;
        for (V v : graph.getVertices()) {
//            Vertex currentVertex = (Vertex) vIt.next();
        	this.weightedNIPaths.put(v, 0.0);
//            currentVertex.setUserDatum(WEIGHTED_NIPATHS_KEY, new MutableDouble(0), UserData.SHARED);
        }
    }

    /**
     * Given a node, returns the corresponding rank score. This implementation of <code>getRankScore</code> assumes
     * the decoration representing the rank score is of type <code>MutableDouble</code>.
     * @return  the rank score for this node
     */
//    public String getRankScoreKey() {
//        return WEIGHTED_NIPATHS_KEY;
//    }

    protected void incrementRankScore(V v, double rankValue) {
        setRankScore(v, getRankScore(v) + rankValue);
    }

    protected void computeWeightedPathsFromSource(V root, int depth) {

        int pathIdx = 1;
        for (E e : getGraph().getOutEdges(root)) {//Iterator rootEdgeIt = root.getOutEdges().iterator(); rootEdgeIt.hasNext();) {
//            DirectedEdge currentEdge = (DirectedEdge) rootEdgeIt.next();
            Integer pathIdxValue = new Integer(pathIdx);
            this.pathIndices.put(e, pathIdxValue);
//            currentEdge.setUserDatum(PATH_INDEX_KEY, pathIdxValue, UserData.REMOVE);
            this.roots.put(e, root);
//            currentEdge.setUserDatum(ROOT_KEY, root, UserData.REMOVE);
            newVertexEncountered(pathIdxValue, getGraph().getEndpoints(e).getSecond(), root);
            pathIdx++;
        }

        List<E> edges = new ArrayList<E>();

        V virtualNode = null;
		try {
			virtualNode = (V)getGraph().getVertices().iterator().next().getClass().newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		//addVertex(new SparseVertex());
        getGraph().addVertex(virtualNode);
        E virtualSinkEdge = null;
		try {
			virtualSinkEdge = (E)getGraph().getEdges().iterator().next().getClass().newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        getGraph().addEdge(virtualSinkEdge, virtualNode, root);
//        E virtualSinkEdge = getGraph().addEdge(arg0, arg1, arg2)
        	//GraphUtils.addEdge(getGraph(), virtualNode, root);
        edges.add(virtualSinkEdge);

        int currentDepth = 0;
        while (currentDepth <= depth) {

            double currentWeight = Math.pow(mAlpha, -1.0 * currentDepth);

            for (E currentEdge : edges) { //Iterator it = edges.iterator(); it.hasNext();) {
//                DirectedEdge currentEdge = (DirectedEdge) it.next();
                incrementRankScore(getGraph().getEndpoints(currentEdge).getSecond(),//
//                		currentEdge.getDest(), 
                		currentWeight);
            }

            if ((currentDepth == depth) || (edges.size() == 0)) break;

            List<E> newEdges = new ArrayList<E>();

            for (E currentSourceEdge : edges) { //Iterator sourceEdgeIt = edges.iterator(); sourceEdgeIt.hasNext();) {
//                DirectedEdge currentSourceEdge = (DirectedEdge) sourceEdgeIt.next();
                Integer sourcePathIndex = this.pathIndices.get(currentSourceEdge);
                	//(Integer) currentSourceEdge.getUserDatum(PATH_INDEX_KEY);

                // from the currentSourceEdge, get its opposite end
                // then iterate over the out edges of that opposite end
                V newDestVertex = getGraph().getEndpoints(currentSourceEdge).getSecond();
                for (E currentDestEdge : getGraph().getOutEdges(newDestVertex)) {
                		//Iterator edgeIt = currentSourceEdge.getDest().getOutEdges().iterator(); edgeIt.hasNext();) {
//                    DirectedEdge currentDestEdge = (DirectedEdge) edgeIt.next();
                	V destEdgeRoot = this.roots.get(currentDestEdge);
//                    Vertex destEdgeRoot = (Vertex) currentDestEdge.getUserDatum(ROOT_KEY);
                	V destEdgeDest = getGraph().getEndpoints(currentDestEdge).getSecond();
//                    Vertex destEdgeDest = currentDestEdge.getDest();

                    if (currentSourceEdge == virtualSinkEdge) {
                        newEdges.add(currentDestEdge);
                        continue;
                    }
                    if (destEdgeRoot == root) {
                        continue;
                    }
                    if (destEdgeDest == getGraph().getEndpoints(currentSourceEdge).getFirst()) {//currentSourceEdge.getSource()) {
                        continue;
                    }
                    Set<Integer> pathsSeen = this.pathsSeen.get(destEdgeDest);
//                    Set pathsSeen = (Set) destEdgeDest.getUserDatum(PATHS_SEEN_KEY);

                    /*
                    Set pathsSeen = new HashSet();
        pathsSeen.add(sourcePathIndex);
        dest.setUserDatum(PATHS_SEEN_KEY, pathsSeen, UserData.REMOVE);
        dest.setUserDatum(ROOT_KEY, root, UserData.REMOVE);
        */

                    if (pathsSeen == null) {
                        newVertexEncountered(sourcePathIndex, destEdgeDest, root);
                    } else if (roots.get(destEdgeDest) != root) {
                    		//destEdgeDest.getUserDatum(ROOT_KEY) != root) {
//                        destEdgeDest.setUserDatum(ROOT_KEY, root, UserData.REMOVE);
                        roots.put(destEdgeDest,root);
                        pathsSeen.clear();
                        pathsSeen.add(sourcePathIndex);
                    } else if (!pathsSeen.contains(sourcePathIndex)) {
                        pathsSeen.add(sourcePathIndex);
                    } else {
                        continue;
                    }

//                    currentDestEdge.setUserDatum(PATH_INDEX_KEY, sourcePathIndex, UserData.REMOVE);
                    this.pathIndices.put(currentDestEdge, sourcePathIndex);
//                    currentDestEdge.setUserDatum(ROOT_KEY, root, UserData.REMOVE);
                    this.roots.put(currentDestEdge, root);
                    newEdges.add(currentDestEdge);
                }
            }

            edges = newEdges;
            currentDepth++;
        }

        getGraph().removeVertex(virtualNode);
    }

    private void newVertexEncountered(Integer sourcePathIndex, V dest, V root) {
        Set<Integer> pathsSeen = new HashSet<Integer>();
        pathsSeen.add(sourcePathIndex);
//        dest.setUserDatum(PATHS_SEEN_KEY, pathsSeen, UserData.REMOVE);
        this.pathsSeen.put(dest, pathsSeen);
//        dest.setUserDatum(ROOT_KEY, root, UserData.REMOVE);
        roots.put(dest, root);
    }

    protected double evaluateIteration() {
        for (V v : mPriors) {
        		//Iterator it = mPriors.iterator(); it.hasNext();) {
            computeWeightedPathsFromSource(v, mMaxDepth);
        }

        normalizeRankings();
        return 0;
    }

//    protected void onFinalize(Element udc) {
//        udc.removeUserDatum(PATH_INDEX_KEY);
//        udc.removeUserDatum(ROOT_KEY);
//        udc.removeUserDatum(PATHS_SEEN_KEY);
//    }

//    private static final String PATH_INDEX_KEY = "WeightedNIPathsII.PathIndexKey";
//    private static final String ROOT_KEY = "WeightedNIPathsII.RootKey";
//    private static final String PATHS_SEEN_KEY = "WeightedNIPathsII.PathsSeenKey";
}
