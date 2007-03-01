package edu.uci.ics.jung.graph;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.util.TreeUtils;

public class TreeUtilsTest extends TestCase {
	
	Tree<String,Integer> tree;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tree = new SparseTree<String,Integer>();
		tree.addVertex("A");
		tree.addEdge(1, "A", "B0");
		tree.addEdge(2, "A", "B1");
		tree.addEdge(3, "B0", "C0");
		tree.addEdge(4, "C0", "D0");
		tree.addEdge(5, "C0", "D1");
	}
	
	public void testRemove() {
		System.err.println("tree is "+tree);
		try {
			Forest<String,Integer> subTree = TreeUtils.getSubTree(tree, "C0");
			
			tree.removeVertex("C0");
			System.err.println("Tree now "+tree);
			System.err.println("subTree now "+subTree);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testAdd() {
		System.err.println("tree is "+tree);
		try {
			Forest<String,Integer> subTree = TreeUtils.getSubTree(tree, "C0");
			Integer edge = tree.getInEdges("C0").iterator().next();
			String parent = tree.getPredecessors("C0").iterator().next();
			tree.removeVertex("C0");
			System.err.println("tree now "+tree);
			System.err.println("subTree now "+subTree);
			
			TreeUtils.addSubTree(tree, subTree, parent, edge);
			System.err.println("after add, tree "+tree);
			
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
