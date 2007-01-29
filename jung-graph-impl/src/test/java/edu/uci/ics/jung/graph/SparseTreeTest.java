package edu.uci.ics.jung.graph;

import junit.framework.TestCase;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.DirectedGraph;

public class SparseTreeTest extends TestCase {

	SparseTree<String,Integer> tree;
	Factory<DirectedGraph<String,Integer>> graphFactory;
	Factory<Integer> edgeFactory;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		graphFactory = new Factory<DirectedGraph<String,Integer>>() {

			public DirectedGraph<String, Integer> create() {
				return new DirectedSparseGraph<String,Integer>();
			}};
		edgeFactory = new Factory<Integer>() {
			int i=0;
			public Integer create() {
				return i++;
			}};
		tree = new SparseTree<String,Integer>(graphFactory, edgeFactory);
	}
	
	public void testSimpleTree() {
		tree.addVertex("A");
		tree.addEdge(edgeFactory.create(), "A", "B");
		tree.addEdge(edgeFactory.create(), "A", "C");
	}
	
	public void testCreateLoop() {
		try {
			tree.addVertex("A");
			tree.addEdge(edgeFactory.create(), "A", "A");
			fail("should not be able to addChild(v,v)");
		} catch(IllegalArgumentException e) {
			// all is well
		}
		try {
			tree.addEdge(edgeFactory.create(), "A", "B");
			tree.addEdge(edgeFactory.create(), "B", "A");
			fail("should not allow loop");
		} catch(IllegalArgumentException e) {
			// all is well
		}
	}
	
	public void testHeight() {
		tree.addVertex("V0");
    	tree.addEdge(edgeFactory.create(), "V0", "V1");
    	tree.addEdge(edgeFactory.create(), "V0", "V2");
    	tree.addEdge(edgeFactory.create(), "V1", "V4");
    	tree.addEdge(edgeFactory.create(), "V2", "V3");
    	tree.addEdge(edgeFactory.create(), "V2", "V5");
    	tree.addEdge(edgeFactory.create(), "V4", "V6");
    	tree.addEdge(edgeFactory.create(), "V4", "V7");
    	tree.addEdge(edgeFactory.create(), "V3", "V8");
    	tree.addEdge(edgeFactory.create(), "V6", "V9");
    	tree.addEdge(edgeFactory.create(), "V4", "V10");
       	tree.addEdge(edgeFactory.create(), "V4", "V11");
       	tree.addEdge(edgeFactory.create(), "V4", "V12");
       	tree.addEdge(edgeFactory.create(), "V6", "V13");
       	tree.addEdge(edgeFactory.create(), "V10", "V14");
       	tree.addEdge(edgeFactory.create(), "V13", "V15");
       	tree.addEdge(edgeFactory.create(), "V13", "V16");
       	assertEquals(tree.getHeight(), 6);

	}
	
	
}
