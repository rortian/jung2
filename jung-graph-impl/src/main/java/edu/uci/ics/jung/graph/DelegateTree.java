package edu.uci.ics.jung.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An implementation of the Tree<V,E> interface that delegates to
 * any implementation of DirectedGraph<V,E>
 * @author Tom Nelson
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 */
public class DelegateTree<V,E> extends GraphDecorator<V,E> implements Tree<V,E>, Serializable {
	
	public static final <V,E> Factory<Tree<V,E>> getFactory() {
		return new Factory<Tree<V,E>> () {
			public Tree<V,E> create() {
				return new DelegateTree<V,E>(new DirectedSparseMultigraph<V,E>());
			}
		};
	}

	protected V root;
    protected Map<V, Integer> vertex_depths;
    
    public DelegateTree() {
    	this(DirectedSparseMultigraph.<V,E>getFactory());
    }

	/**
	 * create an instance with passed values.
	 * @param graphFactory must create a DirectedGraph to use as a delegate
	 * @param edgeFactory must create unique edges to connect tree nodes
	 */
	public DelegateTree(Factory<DirectedGraph<V,E>> graphFactory) {
		super(graphFactory.create());
        this.vertex_depths = new HashMap<V, Integer>();
	}
	
	/**
	 * Creates a new <code>DelegateTree</code> which delegates to <code>graph</code>.
	 * Assumes that <code>graph</code> is already a tree; if it's not, future behavior
	 * of this instance is undefined.
	 */
	public DelegateTree(DirectedGraph<V,E> graph) {
		super(graph);
//		if(graph.getVertexCount() != 0) throw new IllegalArgumentException(
//			"Passed DirectedGraph must be empty");
        this.vertex_depths = new HashMap<V, Integer>();
	}
	
	/**
	 * Add an edge to the tree, connecting v1, the parent and v2, the child.
	 * v1 must already exist in the tree, and v2 must not already exist
	 * the passed edge must be unique in the tree. Passing an edgeType
	 * other than EdgeType.DIRECTED may cause an illegal argument exception 
	 * in the delegate graph.
	 * 
	 * @param e a unique edge to add
	 * @param v1 the parent node
	 * @param v2 the child node
	 * @param edgeType should be EdgeType.DIRECTED
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		return addChild(e, v1, v2, edgeType);
	}

	/**
	 * Add an edge to the tree, connecting v1, the parent and v2, the child.
	 * v1 must already exist in the tree, and v2 must not already exist
	 * the passed edge must be unique in the tree. 
	 * 
	 * @param e a unique edge to add
	 * @param v1 the parent node
	 * @param v2 the child node
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(E e, V v1, V v2) {
		return addChild(e, v1, v2);
	}

	/**
	 * Will set the root of the Tree, only if the Tree is empty and the
	 * root is currently unset.
	 * 
	 * @param vertex the tree root to set
	 * @return true if this call mutates the underlying graph
	 * @see edu.uci.ics.jung.graph.Graph#addVertex(java.lang.Object)
	 * @throws an UnsupportedOperationException if the root was previously set
	 */
	public boolean addVertex(V vertex) {
		if(root == null) {
			this.root = vertex;
            vertex_depths.put(vertex, 0);
			return delegate.addVertex(vertex);
		} else {
			throw new UnsupportedOperationException("Unless you are setting the root, use addChild()");
		}
	}

	/**
	 * remove the passed node, and all nodes that are descendants of the
	 * passed node.
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(V vertex) {
	    if (!delegate.containsVertex(vertex))
	        return false;
		for(V v : getChildren(vertex)) {
			removeVertex(v);
            vertex_depths.remove(v);
		}
        
        // recalculate height
		return delegate.removeVertex(vertex);
	}
	
	/**
	 * add the passed child node as a child of parent.
	 * parent must exist in the tree, and child must not already exist.
	 * the connecting edge will be dynamically created by the 
	 * edgeFactory member
	 * @param parent the existing parent to attach the child to
	 * @param child the new child to add to the tree as a child of parent
	 * @return whether this call mutates the underlying graph
	 */
//	public boolean addChild(V parent, V child) {
//		return addChild(edgeFactory.create(), parent, child);
//	}

	/**
	 * add the passed child node as a child of parent.
	 * parent must exist in the tree, and child must not already exist.
	 * 
	 * @param edge the unique edge to connect the parent and child nodes
	 * @param parent the existing parent to attach the child to
	 * @param child the new child to add to the tree as a child of parent
	 * @param edgeType must be EdgeType.DIRECTED or the underlying graph may throw an exception
	 * @return whether this call mutates the underlying graph
	 */
	public boolean addChild(E edge, V parent, V child, EdgeType edgeType) {
		Collection<V> vertices = delegate.getVertices();
		if(vertices.contains(parent) == false) {
			throw new IllegalArgumentException("Tree must already contain parent "+parent);
		}
		if(vertices.contains(child)) {
			throw new IllegalArgumentException("Tree must not already contain child "+child);
		}
        vertex_depths.put(child, vertex_depths.get(parent) + 1);
		return delegate.addEdge(edge, parent, child, edgeType);
	}

	/**
	 * add the passed child node as a child of parent.
	 * parent must exist in the tree, and child must not already exist
	 * @param edge the unique edge to connect the parent and child nodes
	 * @param parent the existing parent to attach the child to
	 * @param child the new child to add to the tree as a child of parent
	 * @return whether this call mutates the underlying graph
	 */
	public boolean addChild(E edge, V parent, V child) {
		Collection<V> vertices = delegate.getVertices();
		if(vertices.contains(parent) == false) {
			throw new IllegalArgumentException("Tree must already contain parent "+parent);
		}
		if(vertices.contains(child)) {
			throw new IllegalArgumentException("Tree must not already contain child "+child);
		}
        vertex_depths.put(child, vertex_depths.get(parent) + 1);
		return delegate.addEdge(edge, parent, child);
	}
	
	/**
	 * get the number of children of the passed parent node
	 */
	public int getChildCount(V parent) {
	    if (!delegate.containsVertex(parent))
	        return 0;
		return getChildren(parent).size();
	}

	/**
	 * get the immediate children nodes of the passed parent
	 */
	public Collection<V> getChildren(V parent) {
        if (!delegate.containsVertex(parent))
            return null;
		return delegate.getSuccessors(parent);
	}

	/**
	 * get the single parent node of the passed child
	 */
	public V getParent(V child) {
        if (!delegate.containsVertex(child))
            return null;
		Collection<V> predecessors = delegate.getPredecessors(child);
		if(predecessors.size() == 0) {
			return null;
		}
		return predecessors.iterator().next();
	}

	/**
	 * returns an ordered list of the nodes beginning at the root
	 * and ending at the passed child node, including all intermediate
	 * nodes.
	 * @param vertex the last node in the path from the root
	 * @return an ordered list of the nodes from root to child
	 */
	public List<V> getPath(V vertex) {
        if (!delegate.containsVertex(vertex))
            return null;
		List<V> list = new ArrayList<V>();
		list.add(vertex);
		V parent = getParent(vertex);
		while(parent != null) {
			list.add(list.size(), parent);
			parent = getParent(parent);
		}
		return list;
	}

	/**
	 * getter for the root of the tree
	 * @return the root
	 */
	public V getRoot() {
		return root;
	}
	
	/**
	 * sets the root to the passed value, only if the root is
	 * previously unset
	 * @param root the initial tree root
	 */
	public void setRoot(V root) {
		addVertex(root);
	}

	/**
	 * removes a node from the tree, causing all descendants of
	 * the removed node also to be removed
	 * @param orphan the node to remove
	 * @return whether this call mutates the underlying graph
	 */
	public boolean removeChild(V orphan) {
		return removeVertex(orphan);
	}

	/**
	 * computes and returns the depth of the tree from the
	 * root to the passed vertex
	 * 
	 * @param v the node who's depth is computed
	 * @return the depth to the passed node.
	 */
	public int getDepth(V v) {
        return this.vertex_depths.get(v);
//        return getPath(v).size() - 1;
        // the below definition was incorrect: it should have been getPath(v).size() - 1
//		return getPath(v).size();
	}

	/**
	 * computes and returns the height of the tree
	 * 
	 * @return the height
	 */
	public int getHeight() {
		int height = 0;
		for(V v : getVertices()) {
			height = Math.max(height, getDepth(v));
		}
		return height;
	}

	/**
	 * computes and returns whether the passed node is
	 * neither the root, nor a leaf node.
	 * @return 
	 */
	public boolean isInternal(V v) {
	    if (!delegate.containsVertex(v))
	        return false;
		return isLeaf(v) == false && isRoot(v) == false;
	}

	/**
	 * computes and returns whether the passed node is
	 * a leaf (has no child nodes)
	 */
	public boolean isLeaf(V v) {
        if (!delegate.containsVertex(v))
            return false;
		return getChildren(v).size() == 0;
	}

	/**
	 * computes whether the passed node is a root node
	 * (has no children)
	 */
	public boolean isRoot(V v) {
        if (!delegate.containsVertex(v))
            return false;
		return getParent(v) == null;
	}

	public Collection<V> getRoots() {
		return Collections.singleton(getRoot());
	}

    public int getIncidentCount(E edge)
    {
        if (!delegate.containsEdge(edge))
            return 0;
        // all edges in a tree connect exactly 2 vertices
        return 2;
    }
    
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		Pair<V> pair = null;
		if(vertices instanceof Pair) {
			pair = (Pair<V>)vertices;
		} else {
			pair = new Pair<V>(vertices);
		}
		return addEdge(edge, pair.getFirst(), pair.getSecond());
	}
	
	public String toString() {
		return "Tree of "+delegate.toString();
	}

	public Collection<Tree<V, E>> getTrees() {
		return Collections.<Tree<V,E>>singleton(this);
	}

//	public void addTree(Tree<V, E> tree) {
//		// TODO Auto-generated method stub
//		
//	}
}
