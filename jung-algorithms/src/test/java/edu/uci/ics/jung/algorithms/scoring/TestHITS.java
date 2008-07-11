/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.scoring;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;


/**
 * @author Scott White
 * @author Tom Nelson - adapted to jung2
 */
public class TestHITS extends TestCase {

	DirectedGraph<Number,Number> graph;
	
    public static Test suite() {
        return new TestSuite(TestHITS.class);
    }

    @Override
    protected void setUp() {
        graph = new DirectedSparseMultigraph<Number,Number>();
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

    public void testRanker() {

//        HITS<Number,Number> ranker = new HITS<Number,Number>(graph);
//        for (int i = 0; i < 10; i++)
//        {
//            ranker.step();
//            double auth_sum = 0;
//            double hub_sum = 0;
//            for (int j = 0; j < 5; j++)
//            {
//                auth_sum += ranker.getAuthScore(j);
//                hub_sum += ranker.getHubScore(j);
//            }
//            Assert.assertEquals(auth_sum, 1.0, .0001);
//            Assert.assertEquals(hub_sum, 1.0, 0.0001);
//        }
//        
//        ranker.evaluate();
//
//        Assert.assertEquals(ranker.getAuthScore(0), 0, .0001);  
//        Assert.assertEquals(ranker.getAuthScore(1), 0.618, .001);
//        Assert.assertEquals(ranker.getAuthScore(2), 0.0, .0001);
//        Assert.assertEquals(ranker.getAuthScore(3), 0.3819, .001);
//
//        Assert.assertEquals(ranker.getHubScore(0), 0.38196, .001);
//        Assert.assertEquals(ranker.getHubScore(1), 0.0, .0001);
//        Assert.assertEquals(ranker.getHubScore(2), 0.618, .0001);
//        Assert.assertEquals(ranker.getHubScore(3), 0.0, .0001);
    }

}
