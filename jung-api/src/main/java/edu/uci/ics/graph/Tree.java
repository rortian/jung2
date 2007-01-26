package edu.uci.ics.graph;

import java.util.Collection;
import java.util.List;

/**
 * An interface for tree data structures
 * @author Tom Nelson
 *
 * @param <V>
 * @param <E>
 */
public interface Tree<V,E> extends Graph<V,E> {
	
	/**
	 * getter for root property
	 * @return
	 */
	V getRoot();
	
	Collection<V> getRoots();
	
	/**
	 * setter for root property
	 * @param root
	 */
	void setRoot(V root);
	
	/**
	 * get the immediate children of the passed parent
	 * @param parent
	 * @return
	 */
	Collection<V> getChildren(V parent);
	
	/**
	 * get the unique parent of the passed child
	 * return null if there is no unique parent
	 * @param child
	 * @return
	 */
	V getParent(V child);
	
	/**
	 * add the node child, as the child of parent
	 * @param parent
	 * @param child
	 * @return
	 */
	boolean addChild(V parent, V child);
	
	/**
	 * get the number of immediate children of the passed
	 * parent node
	 * @param parent
	 * @return
	 */
	int getChildCount(V parent);
	
	/**
	 * return an ordered list of nodes from and including root,
	 * to and including child
	 * @param child
	 * @return
	 */
	List<V> getPath(V child);
	
	/**
	 * true if v has no outgoing edges
	 * @param v
	 * @return
	 */
	boolean isLeaf(V v);
	
	/**
	 * true if v has no parent
	 * @param v
	 * @return
	 */
	boolean isRoot(V v);
	
	/**
	 * true if v has both incoming and outgoing edges
	 * @param v
	 * @return
	 */
	boolean isInternal(V v);
	
	/**
	 * returns the depth of V in the tree
	 * @param v
	 * @return
	 */
	int getDepth(V v);
	
	/**
	 * returns the height of the tree
	 * @return
	 */
	int getHeight();
	
}
