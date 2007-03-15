package edu.uci.ics.jung.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class ObservableGraph<V,E> extends GraphDecorator<V,E> {

	List<GraphEventListener> listenerList = 
		Collections.synchronizedList(new LinkedList<GraphEventListener>());

	public ObservableGraph(Graph<V, E> delegate) {
		super(delegate);
	}
	public void addGraphEventListener(GraphEventListener<V,E> l) {
		listenerList.add(l);
	}

	public void removeFooListener(GraphEventListener<V,E> l) {
		listenerList.remove(l);
	}

	protected void fireGraphEvent(GraphEvent<V,E> evt) {
		for(GraphEventListener<V,E> listener : listenerList) {
			listener.handleGraphEvent(evt);
		 }
	 }

	/**
	 * @param edge
	 * @param vertices
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
	 */
	public boolean addEdge(E edge, Collection<? extends V> vertices) {
		boolean state = super.addEdge(edge, vertices);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_ADDED, edge);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @param e
	 * @param v1
	 * @param v2
	 * @param edgeType
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
	 */
	public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
		boolean state = super.addEdge(e, v1, v2, edgeType);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_ADDED, e);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @param e
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(E e, V v1, V v2) {
		boolean state = super.addEdge(e, v1, v2);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_ADDED, e);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	public boolean addVertex(V vertex) {
		boolean state = super.addVertex(vertex);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Vertex<V,E>(delegate, GraphEvent.Type.VERTEX_ADDED, vertex);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(E edge) {
		boolean state = delegate.removeEdge(edge);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Edge<V,E>(delegate, GraphEvent.Type.EDGE_REMOVED, edge);
			fireGraphEvent(evt);
		}
		return state;
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(V vertex) {
		boolean state = delegate.removeVertex(vertex);
		if(state) {
			GraphEvent<V,E> evt = new GraphEvent.Vertex<V,E>(delegate, GraphEvent.Type.VERTEX_REMOVED, vertex);
			fireGraphEvent(evt);
		}
		return state;
	}

}
