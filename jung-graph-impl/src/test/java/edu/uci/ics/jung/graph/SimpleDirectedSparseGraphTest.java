package edu.uci.ics.jung.graph;

import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;
import edu.uci.ics.graph.EdgeType;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Pair;

public class SimpleDirectedSparseGraphTest extends TestCase {

    Integer v0 = new Integer(0);
    Integer v1 = new Integer(1);
    Integer v2 = new Integer(2);
    
    Float e01 = new Float(.1f);
    Float e10 = new Float(.2f);
    Float e12 = new Float(.3f);
    Float e21 = new Float(.4f);
    
    Graph<Integer,Number> graph;

    protected void setUp() throws Exception {
        super.setUp();
        graph = new SimpleDirectedSparseGraph<Integer,Number>();
        graph.addEdge(e01, v0, v1);
        graph.addEdge(e10, v1, v0);
        graph.addEdge(e12, v1, v2);
        graph.addEdge(e21, v2, v1);

    }

    public void testGetEdges() {
        assertEquals(graph.getEdges().size(), 4);
    }

    public void testGetVertices() {
        assertEquals(graph.getVertices().size(), 3);
    }

    public void testAddVertex() {
        int count = graph.getVertices().size();
        graph.addVertex(new Integer(3));
        assertEquals(graph.getVertices().size(), count+1);
    }

    public void testRemoveEndVertex() {
        int vertexCount = graph.getVertices().size();
        graph.removeVertex(v0);
        assertEquals(vertexCount-1, graph.getVertices().size());
        assertEquals(2, graph.getEdges().size());
    }

    public void testRemoveMiddleVertex() {
        int vertexCount = graph.getVertices().size();
        graph.removeVertex(v1);
        assertEquals(vertexCount-1, graph.getVertices().size());
        assertEquals(0, graph.getEdges().size());
    }

    public void testAddEdge() {
        int edgeCount = graph.getEdges().size();
        graph.addEdge(new Double(.5), v0, v1);
        assertEquals(graph.getEdges().size(), edgeCount+1);
    }

    public void testRemoveEdge() {
        int edgeCount = graph.getEdges().size();
        graph.removeEdge(e12);
        assertEquals(graph.getEdges().size(), edgeCount-1);
    }

    public void testGetInEdges() {
        assertEquals(graph.getInEdges(v1).size(), 2);
    }

    public void testGetOutEdges() {
        assertEquals(graph.getOutEdges(v1).size(), 2);
    }

    public void testGetPredecessors() {
        assertTrue(graph.getPredecessors(v0).containsAll(Collections.singleton(v1)));
    }

    public void testGetSuccessors() {
        assertTrue(graph.getPredecessors(v1).contains(v0));
        assertTrue(graph.getPredecessors(v1).contains(v2));
    }

    public void testGetNeighbors() {
        Collection neighbors = graph.getNeighbors(v1);
        assertTrue(neighbors.contains(v0));
        assertTrue(neighbors.contains(v2));
    }

    public void testGetIncidentEdges() {
        assertEquals(graph.getIncidentEdges(v0).size(), 2);
    }

    public void testFindEdge() {
        Number edge = graph.findEdge(v1, v2);
        assertTrue(edge == e12 || edge == e21);
    }

    public void testGetEndpoints() {
        Pair<Integer> endpoints = graph.getEndpoints(e01);
        assertTrue((endpoints.getFirst() == v0 && endpoints.getSecond() == v1) ||
                endpoints.getFirst() == v1 && endpoints.getSecond() == v0);
    }

    public void testIsDirected() {
        for(Number edge : graph.getEdges()) {
            assertEquals(graph.getEdgeType(edge), EdgeType.DIRECTED);
        }
    }

    public void testAddDirectedEdge() {
        Float edge = new Float(.9);
        graph.addEdge(edge, v1, v2, EdgeType.DIRECTED);
        assertEquals(graph.getEdgeType(edge), EdgeType.DIRECTED);
    }
    
    public void testAddUndirectedEdge() {
        try {
            graph.addEdge(new Float(.9), v1, v2, EdgeType.UNDIRECTED);
            fail("Cannot add an undirected edge to this graph");
        } catch(IllegalArgumentException uoe) {
            // all is well
        }
    }

}
