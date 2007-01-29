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

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;
import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.jung.algorithms.importance.MarkovCentrality;
import edu.uci.ics.jung.algorithms.util.NumericalPrecision;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Scott White
 */
public class TestMarkovCentrality extends TestCase {

    public static Test suite() {
        return new TestSuite(TestMarkovCentrality.class);
    }

    protected void setUp() {

    }

    public void testSimple() {

        DirectedGraph<Number,Number> graph = new DirectedSparseGraph<Number,Number>();
        for(int i=0; i<4; i++) {
        	graph.addVertex(i);
        }
        int j=0;
        graph.addEdge(j++, 0, 1);
        graph.addEdge(j++, 1, 2);
        graph.addEdge(j++, 2, 3);
        graph.addEdge(j++, 3, 0);
        graph.addEdge(j++, 2, 1);

        Set<Number> priors = new HashSet<Number>();
        priors.add(2);

        MarkovCentrality<Number,Number> ranker = new MarkovCentrality<Number,Number>(graph,priors,null);
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.setMaximumIterations(500);

        ranker.evaluate();
        ranker.printRankings(true, true);

        Assert.assertTrue(NumericalPrecision.equal(ranker.getVertexRankScore(0),0.1764,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getVertexRankScore(1),0.3529,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getVertexRankScore(2),0.2352,.001));
        Assert.assertTrue(NumericalPrecision.equal(ranker.getVertexRankScore(3),0.2352,.001));

    }
}
