package edu.uci.ics.jung.algorithms.shortestpath;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public class TestPrimMinimumSpanningTree extends TestCase {
	
	public void testSimpleTree() {
		Tree<String,Integer> tree = new SparseTree<String,Integer>();
		tree.addVertex("A");
		tree.addEdge(0,"A","B0");
		tree.addEdge(1,"A","B1");
		
		System.err.println("tree = "+tree);
		PrimMinimumSpanningTree pmst = 
			new PrimMinimumSpanningTree(SparseTree.<String,Integer>getFactory());
		
		Graph<String,Integer> mst = pmst.transform(tree);
		System.err.println("mst = "+mst);
		
//		assertEquals(tree.getVertices(), mst.getVertices());
//		assertEquals(tree.getEdges(), mst.getEdges());
		
	}
	
	public void testDAG() {
		DirectedGraph<String,Integer> graph = new DirectedSparseMultigraph<String,Integer>();
		graph.addVertex("B0");
		graph.addEdge(0, "A", "B0");
		graph.addEdge(1, "A", "B1");
		System.err.println("graph = "+graph);
		PrimMinimumSpanningTree pmst = 
			new PrimMinimumSpanningTree(SparseTree.<String,Integer>getFactory());
		
		Graph<String,Integer> mst = pmst.transform(graph);
		System.err.println("mst = "+mst);
		
	}

	public void testUAG() {
		UndirectedGraph<String,Integer> graph = new UndirectedSparseMultigraph<String,Integer>();
		graph.addVertex("B0");
		graph.addEdge(0, "A", "B0");
		graph.addEdge(1, "A", "B1");
		System.err.println("graph = "+graph);
		PrimMinimumSpanningTree pmst = 
			new PrimMinimumSpanningTree(SparseTree.<String,Integer>getFactory());
		
		Graph<String,Integer> mst = pmst.transform(graph);
		System.err.println("mst = "+mst);
		
	}

}
