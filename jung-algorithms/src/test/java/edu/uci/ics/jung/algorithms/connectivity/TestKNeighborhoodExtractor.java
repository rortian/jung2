/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.connectivity;

import java.util.HashSet;
import java.util.Set;

import junit.framework.*;
import edu.uci.ics.jung.algorithms.connectivity.KNeighborhoodExtractor;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author Scott White
 */
public class TestKNeighborhoodExtractor extends TestCase {
    public static Test suite() {
        return new TestSuite(TestKNeighborhoodExtractor.class);
    }

    protected void setUp() {

    }

    public void testExtract() {
        DirectedGraph<Integer,Integer> graph = 
        	new DirectedSparseMultigraph<Integer,Integer>();
        for(int i=0; i<5; i++) {
        	graph.addVertex(i);
        }
        int j=0;
        graph.addEdge(j++, 0, 1);
        graph.addEdge(j++, 1, 2);
        graph.addEdge(j++, 2, 3);
		graph.addEdge(j++, 3, 4);

        Set<Integer> rootNodes = new HashSet<Integer>();
        rootNodes.add(0);

        Graph extractedGraph = KNeighborhoodExtractor.extractNeighborhood(graph,rootNodes,2);

        Assert.assertEquals(extractedGraph.getVertexCount(),3);

    }
}
