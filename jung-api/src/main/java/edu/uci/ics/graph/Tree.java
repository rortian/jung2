package edu.uci.ics.graph;

import java.util.Collection;
import java.util.List;

public interface Tree<V> extends Graph<V,Integer> {
	
	V getRoot();
	
	Collection<V> getChildren(V parent);
	
	V getParent(V child);
	
	boolean addChild(V parent, V child);
	
	boolean removeChild(V orphan);
	
	int getChildCount(V parent);
	
	List<V> getPath(V child);
	
	
}
