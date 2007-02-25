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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.HITS;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;


/**
 * @author Scott White
 * @author Tom Nelson - adapted to jung2
 */
public class TestHITS extends TestCase {

	DirectedGraph<Number,Number> graph;
	
    public static Test suite() {
        return new TestSuite(TestHITS.class);
    }

    protected void setUp() {
        graph = new DirectedSparseGraph<Number,Number>();
        for(int i=0; i<5; i++) {
        	graph.addVertex(i);
        }

        int j=0;
        graph.addEdge(j++, 0, 1);
        graph.addEdge(j++, 1, 2);
        graph.addEdge(j++, 2, 3);
        graph.addEdge(j++, 3, 0);
        graph.addEdge(j++, 2, 1);
    }

    public void testRankerAuthorities() {

        HITS<Number,Number> ranker = new HITS<Number,Number>(graph);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.setMaximumIterations(500);
        ranker.evaluate();

        Assert.assertEquals(ranker.getVertexRankScore(0), 0, .0001);
        Assert.assertEquals(ranker.getVertexRankScore(1), 0.618, .001);
        Assert.assertEquals(ranker.getVertexRankScore(2), 0.0, .0001);
        Assert.assertEquals(ranker.getVertexRankScore(3), 0.3819, .001);

    }

    public void testRankerHubs() {

        HITS<Number,Number> ranker = new HITS<Number,Number>(graph,false);
        ranker.setMaximumIterations(500);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();

        Assert.assertEquals(ranker.getVertexRankScore(0), 0.38196, .001);
        Assert.assertEquals(ranker.getVertexRankScore(1), 0.0, .0001);
        Assert.assertEquals(ranker.getVertexRankScore(2), 0.618, .0001);
        Assert.assertEquals(ranker.getVertexRankScore(3), 0.0, .0001);
    }
}
