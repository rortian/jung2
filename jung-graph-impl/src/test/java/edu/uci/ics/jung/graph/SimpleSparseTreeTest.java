package edu.uci.ics.jung.graph;

import edu.uci.ics.graph.Tree;
import junit.framework.TestCase;

public class SimpleSparseTreeTest extends TestCase {

	Tree<String> tree;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tree = new SimpleSparseTree<String>("A");
	}
	
	public void testSimpleTree() {
		tree.addChild("A", "B");
		tree.addChild("A", "C");
	}
	
	public void testCreateLoop() {
		try {
			tree.addChild("A", "A");
			fail("should not be able to addChild(A,A)");
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
	
	
}
