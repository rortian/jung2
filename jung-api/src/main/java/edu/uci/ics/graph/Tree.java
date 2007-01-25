package edu.uci.ics.graph;

import java.util.Collection;
import java.util.List;

public interface Tree<V,E> extends Graph<V,E> {
	
	V getRoot();
	
	void setRoot(V root);
	
	Collection<V> getChildren(V parent);
	
	V getParent(V child);
	
	boolean addChild(V parent, V child);
	
	int getChildCount(V parent);
	
	List<V> getPath(V child);
	
//	boolean removeChild(V orphan);
	
	boolean isLeaf(V v);		// true if it has no outgoing edges
	boolean isRoot(V v);		// true if it has no incoming edges
	boolean isInternal(V v);	// true if it has both incoming and outgoing edges
	int getDepth(V v);		// returns the depth of V in the tree
	int getHeight();		// returns the height of the tree

	
	
}
