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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedGraph;

/**
 * Computes betweenness centrality for each vertex and edge in the graph. The result is that each vertex
 * and edge has a UserData element of type MutableDouble whose key is 'centrality.BetweennessCentrality'.
 * Note: Many social network researchers like to normalize the betweenness values by dividing the values by
 * (n-1)(n-2)/2. The values given here are unnormalized.<p>
 *
 * A simple example of usage is:
 * <pre>
 * BetweennessCentrality ranker = new BetweennessCentrality(someGraph);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 *
 * Running time is: O(n^2 + nm).
 * @see "Ulrik Brandes: A Faster Algorithm for Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001."
 * @author Scott White
 */

public class BetweennessCentrality<V,E> extends AbstractRanker<V,E> {

    public static final String CENTRALITY = "centrality.BetweennessCentrality";
	protected Map<Object,Number> centralityMap = new HashMap<Object,Number>();

    /**
     * Constructor which initializes the algorithm
     * @param g the graph whose nodes are to be analyzed
     */
    public BetweennessCentrality(Graph<V,E> g) {
        initialize(g, true, true);
    }

    public BetweennessCentrality(Graph<V,E> g, boolean rankNodes) {
        initialize(g, rankNodes, true);
    }

    public BetweennessCentrality(Graph<V,E> g, boolean rankNodes, boolean rankEdges)
    {
        initialize(g, rankNodes, rankEdges);
    }
    
    /**
	 * @return the centralityMap
	 */
	public Map<Object, Number> getCentralityMap() {
		return centralityMap;
	}

	/**
	 * @param centralityMap the centralityMap to set
	 */
	public void setCentralityMap(Map<Object, Number> centralityMap) {
		this.centralityMap = centralityMap;
	}

	protected void computeBetweenness(Graph<V,E> graph) {

    	Map<V,BetweennessData> decorator = new HashMap<V,BetweennessData>();
//        BetweennessDataDecorator decorator = new BetweennessDataDecorator();
    	Map<Object,Number> bcDecorator = new HashMap<Object,Number>();
//        NumericDecorator bcDecorator = new NumericDecorator(CENTRALITY, UserData.SHARED);

        
        Collection<V> vertices = graph.getVertices();
        
        // clean up previous decorations, if any; otherwise the new calculations will 
        // incorporate the old data
//        UserDataUtils.cleanup(vertices, getRankScoreKey());
//        UserDataUtils.cleanup(graph.getEdges(), getRankScoreKey());
        centralityMap.clear();

        for (V s : vertices) {
//            Vertex s = (Vertex) vIt.next();

            initializeData(graph,decorator);

            decorator.get(s).numSPs = 1;
            decorator.get(s).distance = 0;

            Stack<V> stack = new Stack<V>();
            Buffer<V> queue = new UnboundedFifoBuffer<V>();
            queue.add(s);

            while (!queue.isEmpty()) {
                V v = queue.remove();
                stack.push(v);

                for(V w : getGraph().getSuccessors(v)) {
//                for (Iterator nIt = v.getSuccessors().iterator(); nIt.hasNext();) {
//                    Vertex w = (Vertex) nIt.next();

                    if (decorator.get(w).distance < 0) {
                        queue.add(w);
                        decorator.get(w).distance = decorator.get(v).distance + 1;
                    }

                    if (decorator.get(w).distance == decorator.get(v).distance + 1) {
                        decorator.get(w).numSPs += decorator.get(v).numSPs;
                        decorator.get(w).predecessors.add(v);
                    }
                }
            }

            while (!stack.isEmpty()) {
                V w = stack.pop();

                for (V v : decorator.get(w).predecessors) {
//                    Vertex v = (Vertex) v2It.next();
                    double partialDependency = (decorator.get(v).numSPs / decorator.get(w).numSPs);
                    partialDependency *= (1.0 + decorator.get(w).dependency);
                    decorator.get(v).dependency +=  partialDependency;
                    E currentEdge = getGraph().findEdge(v, w);
                    double edgeValue = bcDecorator.get(currentEdge).doubleValue();
                    edgeValue += partialDependency;
                    bcDecorator.put(currentEdge, edgeValue);
                }
                if (w != s) {
                	double bcValue = bcDecorator.get(w).doubleValue();
                	bcValue += decorator.get(w).dependency;
                	bcDecorator.put(w, bcValue);
//                    MutableDouble bcValue = (MutableDouble) bcDecorator.getValue(w);
//                    bcValue.add(decorator.data(w).dependency);
                }
            }
        }

//        if (PredicateUtils.enforcesEdgeConstraint(graph, Graph.UNDIRECTED_EDGE)) {
        if(graph instanceof UndirectedGraph) {
            for (V v : vertices) { //Iterator v3It = vertices.iterator(); v3It.hasNext();) {
            	double bcValue = bcDecorator.get(v).doubleValue();
            	bcValue /= 2.0;
            	bcDecorator.put(v, bcValue);
//                MutableDouble bcValue = (MutableDouble) bcDecorator.getValue((Vertex) v3It.next());
//                bcValue.setDoubleValue(bcValue.doubleValue() / 2.0);
            }
            for (E e : graph.getEdges()) {
            	double bcValue = bcDecorator.get(e).doubleValue();
            	bcValue /= 2.0;
            	bcDecorator.put(e, bcValue);
//                MutableDouble bcValue = (MutableDouble) bcDecorator.getValue((Edge) eIt.next());
//                bcValue.setDoubleValue(bcValue.doubleValue() / 2.0);
            }
        }

        for (V vertex : vertices) {
//            Vertex vertex = (Vertex) vIt.next();
            decorator.remove(vertex);
        }

    }

    private void initializeData(Graph<V,E> g, Map<V,BetweennessData> decorator) {
        for (V vertex : g.getVertices()) {

//            if (vertex.getUserDatum(CENTRALITY) == null) {
//                vertex.addUserDatum(CENTRALITY, new MutableDouble(), UserData.SHARED);
//            }
            if(centralityMap.containsKey(vertex) == false) {
            	centralityMap.put(vertex, 0.0);
            }

            decorator.put(vertex, new BetweennessData());
        }
        for (E e : g.getEdges()) {
//            Edge e = (Edge) eIt.next();

//            if (e.getUserDatum(CENTRALITY) == null) {
//                e.addUserDatum(CENTRALITY, new MutableDouble(), UserData.SHARED);
//            }
            if(centralityMap.containsKey(e) == false) {
            	centralityMap.put(e, 0.0);
            }
        }
    }
    
    /**
     * the user datum key used to store the rank scores
     * @return the key
     */
    public String getRankScoreKey() {
        return CENTRALITY;
    }

    public void step() {
        computeBetweenness(getGraph());
//        return 0;
    }

//    class BetweennessDataDecorator extends Decorator {
//        public BetweennessDataDecorator() {
//            super("centrality.BetwennessData", UserData.REMOVE);
//        }
//
//        public BetweennessData data(Element udc) {
//            return (BetweennessData) udc.getUserDatum(getKey());
//        }
//
//        public void setData(BetweennessData value, Element udc) {
//            udc.setUserDatum(getKey(), value, getCopyAction());
//        }
//
//    }

    class BetweennessData {
        double distance;
        double numSPs;
        List<V> predecessors;
        double dependency;

        BetweennessData() {
            distance = -1;
            numSPs = 0;
            predecessors = new ArrayList<V>();
            dependency = 0;
        }
    }
}
