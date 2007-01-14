/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.flows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;

/**
 * @author Scott White, Joshua O'Madadhain, Tom Nelson
 */
public class TestEdmondsKarpMaxFlow extends TestCase {

	public static Test suite() {
		return new TestSuite(TestEdmondsKarpMaxFlow.class);
	}

	protected void setUp() {

	}

    public void testSanityChecks() 
    {
        DirectedGraph<Number,Number> g = new SimpleDirectedSparseGraph<Number,Number>();
        Number source = new Integer(1);
        Number sink = new Integer(2);
        g.addVertex(source);
        g.addVertex(sink);
        
        Number v = new Integer(3);
        
        DirectedGraph<Number,Number> h = new SimpleDirectedSparseGraph<Number,Number>();
        Number w = new Integer(4);
        g.addVertex(w);
        
        EdmondsKarpMaxFlow ek = new EdmondsKarpMaxFlow(g, source, sink, null, null, null);
        
        try
        {
            ek = new EdmondsKarpMaxFlow(g, source, source, null, null, null);
            fail("source and sink vertices not distinct");
        }
        catch (IllegalArgumentException iae) {}

        try
        {
            ek = new EdmondsKarpMaxFlow(h, source, w, null, null, null);
            fail("source and sink vertices not both part of specified graph");
        }
        catch (IllegalArgumentException iae) {}

        try
        {
            ek = new EdmondsKarpMaxFlow(g, source, v, null, null, null);
            fail("source and sink vertices not both part of specified graph");
        }
        catch (IllegalArgumentException iae) {}
    }
    
	public void testSimpleFlow() {
		DirectedGraph<Number,Number> graph = new SimpleDirectedSparseGraph<Number,Number>();
		Transformer<DirectedGraph<Number,Number>,Number> edgeFactory = new Transformer<DirectedGraph<Number,Number>,Number>() {
			public Number transform(DirectedGraph<Number,Number> graph) {
				return graph.getEdges().size();
			}
		};

		Map<Number,Number> edgeCapacityMap = new HashMap<Number,Number>();
		for(int i=0; i<6; i++) {
			graph.addVertex(i);
		}
		
		Map<Number,Number> edgeFlowMap = new HashMap<Number,Number>();

		graph.addEdge(0,0,1,EdgeType.DIRECTED);
		edgeCapacityMap.put(0, 16);

		graph.addEdge(1,0,2,EdgeType.DIRECTED);
		edgeCapacityMap.put(1,13);

		graph.addEdge(2,1,2,EdgeType.DIRECTED);
		edgeCapacityMap.put(2, 6);

		graph.addEdge(3,1,3,EdgeType.DIRECTED);
		edgeCapacityMap.put(3, 12);

		graph.addEdge(4,2,4,EdgeType.DIRECTED);
		edgeCapacityMap.put(4, 14);

		graph.addEdge(5,3,2,EdgeType.DIRECTED);
		edgeCapacityMap.put(5, 9);

		graph.addEdge(6,3,5,EdgeType.DIRECTED);
		edgeCapacityMap.put(6, 20);

		graph.addEdge(7,4,3,EdgeType.DIRECTED);
		edgeCapacityMap.put(7, 7);

		graph.addEdge(8,4,5,EdgeType.DIRECTED);
		edgeCapacityMap.put(8, 4);

		EdmondsKarpMaxFlow<Number,Number> ek =
			new EdmondsKarpMaxFlow<Number,Number>(
				graph,
				0,
				5,
				edgeCapacityMap,
				edgeFlowMap,
				edgeFactory);
		ek.evaluate();

		assertTrue(ek.getMaxFlow() == 23);
        Set<Number> nodesInS = ek.getNodesInSourcePartition();
        assertEquals(4,nodesInS.size());

        for (Number v : nodesInS) {
            Assert.assertTrue(v.intValue() != 3 && v.intValue() != 5);
        }

        Set<Number> nodesInT = ek.getNodesInSinkPartition();
        assertEquals(2,nodesInT.size());

        for (Number v : nodesInT) {
            Assert.assertTrue(v.intValue() == 3 || v.intValue() == 5);
        }

        Set<Number> minCutEdges = ek.getMinCutEdges();
        int maxFlow = 0;
        for (Number e : minCutEdges) {
            Number flow = edgeFlowMap.get(e);
            maxFlow += flow.intValue();
        }
        Assert.assertEquals(23,maxFlow);
        Assert.assertEquals(3,minCutEdges.size());
	}

	public void testAnotherSimpleFlow() {
		DirectedGraph<Number,Number> graph = new SimpleDirectedSparseGraph<Number,Number>();
		Transformer<DirectedGraph<Number,Number>,Number> edgeFactory = new Transformer<DirectedGraph<Number,Number>,Number>() {
			public Number transform(DirectedGraph<Number,Number> graph) {
				return graph.getEdges().size();
			}
		};

		Map<Number,Number> edgeCapacityMap = new HashMap<Number,Number>();
		for(int i=0; i<6; i++) {
			graph.addVertex(i);
		}
		
		Map<Number,Number> edgeFlowMap = new HashMap<Number,Number>();

		graph.addEdge(0,0,1,EdgeType.DIRECTED);
		edgeCapacityMap.put(0,5);
		
		graph.addEdge(1,0,2,EdgeType.DIRECTED);
		edgeCapacityMap.put(1,3);
		
		graph.addEdge(2,1,5,EdgeType.DIRECTED);
		edgeCapacityMap.put(2,2);
		
		graph.addEdge(3,1,2,EdgeType.DIRECTED);
		edgeCapacityMap.put(3,8);
		
		graph.addEdge(4,2,3,EdgeType.DIRECTED);
		edgeCapacityMap.put(4,4);
		
		graph.addEdge(5,2,4,EdgeType.DIRECTED);
		edgeCapacityMap.put(5,2);
		
		graph.addEdge(6,3,4,EdgeType.DIRECTED);
		edgeCapacityMap.put(6,3);
		
		graph.addEdge(7,3,5,EdgeType.DIRECTED);
		edgeCapacityMap.put(7,6);
		
		graph.addEdge(8,4,5,EdgeType.DIRECTED);
		edgeCapacityMap.put(8,1);

		EdmondsKarpMaxFlow ek =
			new EdmondsKarpMaxFlow(
				graph,
				0,
				5,
				edgeCapacityMap,
				edgeFlowMap,
				edgeFactory);
		ek.evaluate();

		assertTrue(ek.getMaxFlow() == 7);
	}
}
