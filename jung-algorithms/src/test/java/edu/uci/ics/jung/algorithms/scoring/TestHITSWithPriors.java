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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.algorithms.importance.HITSWithPriors;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;


/**
 * @author Scott White
 * @author Tom Nelson - adapted to jung2
 */
public class TestHITSWithPriors extends TestCase {

	DirectedGraph<Number,Number> graph;
	Set<Number> priors;
	
    public static Test suite() {
        return new TestSuite(TestHITSWithPriors.class);
    }

    @Override
    protected void setUp() {
    	graph = new DirectedSparseMultigraph<Number,Number>();
    	for(int i=0; i<4; i++) {
    		graph.addVertex(i);
    	}
    	int j=0;
    	graph.addEdge(j++, 0, 1);
    	graph.addEdge(j++, 1, 2);
    	graph.addEdge(j++, 2, 3);
    	graph.addEdge(j++, 3, 0);
    	graph.addEdge(j++, 2, 1);

        priors = new HashSet<Number>();
        priors.add(2);
    }

    public void testAuthoritiesRankings() {

//        HITSWithPriors<Number,Number> ranker = new HITSWithPriors<Number,Number>(graph, true, 0.3, priors, null);
//        ranker.setMaximumIterations(500);
//        ranker.setRemoveRankScoresOnFinalize(false);
//        ranker.evaluate();
//        
//        Assert.assertEquals(ranker.getVertexRankScore(0), 0, .0001);
//        Assert.assertEquals(ranker.getVertexRankScore(1), 0.246074, .0001);
//        Assert.assertEquals(ranker.getVertexRankScore(2), 0.588245, .0001);
//        Assert.assertEquals(ranker.getVertexRankScore(3), 0.165690, .0001);
    }

    public void testHubsRankings() {

//        HITSWithPriors<Number,Number> ranker = new HITSWithPriors<Number,Number>(graph, false, 0.3, priors, null);
//        ranker.setMaximumIterations(500);
//        ranker.setRemoveRankScoresOnFinalize(false);
//        ranker.evaluate();
//
//        Assert.assertEquals(ranker.getVertexRankScore(0), 0.114834, .0001);
//        Assert.assertEquals(ranker.getVertexRankScore(1), 0.411764, .0001);
//        Assert.assertEquals(ranker.getVertexRankScore(2), 0.473400, .0001);
//        Assert.assertEquals(ranker.getVertexRankScore(3), 0.0, .0001);
    }
}
