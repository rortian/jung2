package edu.uci.ics.jung.graph;

import junit.framework.TestCase;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Tree;

public class SimpleSparseTreeTest extends TestCase {

	Tree<String,Integer> tree;
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
				return new SimpleDirectedSparseGraph<String,Integer>();
			}};
		edgeFactory = new Factory<Integer>() {
			int i=0;
			public Integer create() {
				return i++;
			}};
		tree = new SimpleSparseTree<String,Integer>(graphFactory, edgeFactory);
	}
	
	public void testSimpleTree() {
		tree.setRoot("A");
		tree.addChild("A", "B");
		tree.addChild("A", "C");
	}
	
	public void testCreateLoop() {
		try {
			tree.setRoot("A");
			tree.addChild("A", "A");
			fail("should not be able to addChild(v,v)");
		} catch(IllegalArgumentException e) {
			// all is well
		}
		try {
			tree.addChild("A", "B");
			tree.addChild("B", "A");
			fail("should not allow loop");
		} catch(IllegalArgumentException e) {
			// all is well
		}
	}
	
	public void testHeight() {
		tree.setRoot("V0");
    	tree.addChild("V0", "V1");
    	tree.addChild("V0", "V2");
    	tree.addChild("V1", "V4");
    	tree.addChild("V2", "V3");
    	tree.addChild("V2", "V5");
    	tree.addChild("V4", "V6");
    	tree.addChild("V4", "V7");
    	tree.addChild("V3", "V8");
    	tree.addChild("V6", "V9");
    	tree.addChild("V4", "V10");
       	tree.addChild("V4", "V11");
       	tree.addChild("V4", "V12");
       	tree.addChild("V6", "V13");
       	tree.addChild("V10", "V14");
       	tree.addChild("V13", "V15");
       	tree.addChild("V13", "V16");
       	assertEquals(tree.getHeight(), 6);

	}
	
	
}
