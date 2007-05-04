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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections15.BidiMap;

import edu.uci.ics.jung.algorithms.Indexer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * @author Scott White
 */
public class TestBetweennessCentrality extends TestCase {
    public static Test suite() {
        return new TestSuite(TestBetweennessCentrality.class);
    }

    protected void setUp() {}

    private static <V,E> E getEdge(Graph<V,E> g, int v1Index, int v2Index, BidiMap<V,Integer> id) {
        V v1 = id.getKey(v1Index);
        V v2 = id.getKey(v2Index);
        return g.findEdge(v1, v2);
    }

    public void testRanker() {
        UndirectedGraph<Integer,Integer> graph = 
        	new UndirectedSparseGraph<Integer,Integer>();
        for(int i=0; i<9; i++) {
        	graph.addVertex(i);
        }

		BidiMap<Integer,Integer> id = Indexer.create(graph.getVertices());
		int edge = 0;
        graph.addEdge(edge++, id.get(0),id.get(1));
        graph.addEdge(edge++, id.get(0),id.get(6));
        graph.addEdge(edge++, id.get(1),id.get(2));
        graph.addEdge(edge++, id.get(1),id.get(3));
        graph.addEdge(edge++, id.get(2),id.get(4));
        graph.addEdge(edge++, id.get(3),id.get(4));
        graph.addEdge(edge++, id.get(4),id.get(5));
        graph.addEdge(edge++, id.get(5),id.get(8));
        graph.addEdge(edge++, id.get(7),id.get(8));
        graph.addEdge(edge++, id.get(6),id.get(7));

//        BetweennessCentrality<Integer,Integer> bc = 
//        	new BetweennessCentrality<Integer,Integer>(graph);
//        bc.setRemoveRankScoresOnFinalize(false);
//        bc.evaluate();

//        Assert.assertEquals(bc.getVertexRankScore(id.get(0))/28.0,0.2142,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(1))/28.0,0.2797,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(2))/28.0,0.0892,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(3))/28.0,0.0892,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(4))/28.0,0.2797,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(5))/28.0,0.2142,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(6))/28.0,0.1666,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(7))/28.0,0.1428,.001);
//        Assert.assertEquals(bc.getVertexRankScore(id.get(8))/28.0,0.1666,.001);
//
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,0,1,id)),10.66666,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,0,6,id)),9.33333,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,1,2,id)),6.5,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,1,3,id)),6.5,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,2,4,id)),6.5,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,3,4,id)),6.5,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,4,5,id)),10.66666,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,5,8,id)),9.33333,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,6,7,id)),8.0,.001);
//        Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,7,8,id)),8.0,.001);
    }
    
    public void testRankerDirected() {
    	DirectedGraph<Integer,Integer> graph = new DirectedSparseGraph<Integer,Integer>();
    	for(int i=0; i<5; i++) {
    		graph.addVertex(i);
    	}
    	BidiMap<Integer,Integer> id = Indexer.create(graph.getVertices());
    	int edge=0;
    	graph.addEdge(edge++, id.get(0),id.get(1));
    	graph.addEdge(edge++, id.get(1),id.get(2));
    	graph.addEdge(edge++, id.get(3),id.get(1));
    	graph.addEdge(edge++, id.get(4),id.get(2));

//    	BetweennessCentrality<Integer,Integer> bc = 
//    		new BetweennessCentrality<Integer,Integer>(graph);
//    	bc.setRemoveRankScoresOnFinalize(false);
//    	bc.evaluate();

//    	Assert.assertEquals(bc.getVertexRankScore(id.get(0)),0,.001);
//    	Assert.assertEquals(bc.getVertexRankScore(id.get(1)),2,.001);
//    	Assert.assertEquals(bc.getVertexRankScore(id.get(2)),0,.001);
//    	Assert.assertEquals(bc.getVertexRankScore(id.get(3)),0,.001);
//    	Assert.assertEquals(bc.getVertexRankScore(id.get(4)),0,.001);
//
//    	Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,0,1,id)),2,.001);
//    	Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,1,2,id)),3,.001);
//    	Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,3,1,id)),2,.001);
//    	Assert.assertEquals(bc.getEdgeRankScore(TestBetweennessCentrality.<Integer,Integer>getEdge(graph,4,2,id)),1,.001);
    }
}
