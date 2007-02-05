package edu.uci.ics.graph;

import java.io.Serializable;
import java.util.Collection;

import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;
/**
 * This class consists exclusively of static methods that operate on or return
 * graphs.  It contains "wrappers", which return a new graph backed by a
 * specified graph, and a few other odds and ends.
 *
 * <p>The methods of this class all throw a <tt>NullPointerException</tt>
 * if the graphs or class objects provided to them are null.
 *
 * @author Tom Nelson
 */

public class Graphs {
	
	/**
	 * return a synchronized graph backed by the passed argument graph
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> Graph<V,E> synchronizedGraph(Graph<V,E> graph) {
		return new SynchronizedGraph<V,E>(graph);
	}
	
	/**
	 * return a synchronized DirectedGraph backed by the passed DirectedGraph
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> DirectedGraph<V,E> synchronizedDirectedGraph(DirectedGraph<V,E> graph) {
		return new SynchronizedDirectedGraph<V,E>(graph);
	}
	
	/**
	 * return a synchronized UndirectedGraph backed by the passed UndirectedGraph
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> UndirectedGraph<V,E> synchronizedUndirectedGraph(UndirectedGraph<V,E> graph) {
		return new SynchronizedUndirectedGraph<V,E>(graph);
	}
	
	/**
	 * return an unmodifiable Graph backed by the passed Graph
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> Graph<V,E> unmodifiableGraph(Graph<V,E> graph) {
		return new UnmodifiableGraph<V,E>(graph);
	}
	
	/**
	 * return an unmodifiable Graph backed by the passed Graph
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> DirectedGraph<V,E> unmodifiableDirectedGraph(DirectedGraph<V,E> graph) {
		return new UnmodifiableDirectedGraph<V,E>(graph);
	}
	
	/**
	 * return an unmodifiable Graph backed by the passed Graph
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V,E> UndirectedGraph<V,E> unmodifiableUndirectedGraph(UndirectedGraph<V,E> graph) {
		return new UnmodifiableUndirectedGraph<V,E>(graph);
	}
	
	
	static abstract class SynchronizedAbstractGraph<V,E> implements Graph<V,E>, Serializable {
		protected Graph<V,E> delegate;

		private SynchronizedAbstractGraph(Graph<V, E> delegate) {
			if(delegate == null) {
				throw new NullPointerException();
			}
			this.delegate = delegate;
		}

		/**
		 * @param e
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addDirectedEdge(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
			return delegate.addEdge(e, v1, v2, edgeType);
		}

		/**
		 * @param e
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean addEdge(E e, V v1, V v2) {
			return delegate.addEdge(e, v1, v2);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addVertex(java.lang.Object)
		 */
		public synchronized boolean addVertex(V vertex) {
			return delegate.addVertex(vertex);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#areIncident(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean areIncident(V vertex, E edge) {
			return delegate.areIncident(vertex, edge);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#areNeighbors(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean areNeighbors(V v1, V v2) {
			return delegate.areNeighbors(v1, v2);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#degree(java.lang.Object)
		 */
		public synchronized int degree(V vertex) {
			return delegate.degree(vertex);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
		 */
		public synchronized E findEdge(V v1, V v2) {
			return delegate.findEdge(v1, v2);
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getEdges()
		 */
		public synchronized Collection<E> getEdges() {
			return delegate.getEdges();
		}

		/**
		 * @param directedness
		 * @return
		 * @see edu.uci.ics.graph.Graph#getEdges(edu.uci.ics.graph.Edges)
		 */
		public Collection<E> getEdges(EdgeType edgeType) {
			return delegate.getEdges(edgeType);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getEndpoints(java.lang.Object)
		 */
		public synchronized Pair<V> getEndpoints(E edge) {
			return delegate.getEndpoints(edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getIncidentEdges(java.lang.Object)
		 */
		public synchronized Collection<E> getIncidentEdges(V vertex) {
			return delegate.getIncidentEdges(vertex);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getIncidentVertices(java.lang.Object)
		 */
		public synchronized Collection<V> getIncidentVertices(E edge) {
			return delegate.getIncidentVertices(edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getInEdges(java.lang.Object)
		 */
		public synchronized Collection<E> getInEdges(V vertex) {
			return delegate.getInEdges(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getNeighbors(java.lang.Object)
		 */
		public synchronized Collection<V> getNeighbors(V vertex) {
			return delegate.getNeighbors(vertex);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
		 */
		public synchronized V getOpposite(V vertex, E edge) {
			return delegate.getOpposite(vertex, edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getOutEdges(java.lang.Object)
		 */
		public synchronized Collection<E> getOutEdges(V vertex) {
			return delegate.getOutEdges(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getPredecessors(java.lang.Object)
		 */
		public synchronized Collection<V> getPredecessors(V vertex) {
			return delegate.getPredecessors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getSuccessors(java.lang.Object)
		 */
		public synchronized Collection<V> getSuccessors(V vertex) {
			return delegate.getSuccessors(vertex);
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getVertices()
		 */
		public synchronized Collection<V> getVertices() {
			return delegate.getVertices();
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getEdgeCount()
		 */
		public int getEdgeCount() {
			return delegate.getEdgeCount();
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getVertexCount()
		 */
		public int getVertexCount() {
			return delegate.getVertexCount();
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#inDegree(java.lang.Object)
		 */
		public synchronized int inDegree(V vertex) {
			return delegate.inDegree(vertex);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getEdgeType(java.lang.Object)
		 */
		public synchronized EdgeType getEdgeType(E edge) {
			return delegate.getEdgeType(edge);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isPredecessor(V v1, V v2) {
			return delegate.isPredecessor(v1, v2);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
		 */
		public synchronized boolean isSuccessor(V v1, V v2) {
			return delegate.isSuccessor(v1, v2);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#numNeighbors(java.lang.Object)
		 */
		public synchronized int numNeighbors(V vertex) {
			return delegate.numNeighbors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#numPredecessors(java.lang.Object)
		 */
		public synchronized int numPredecessors(V vertex) {
			return delegate.numPredecessors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#numSuccessors(java.lang.Object)
		 */
		public synchronized int numSuccessors(V vertex) {
			return delegate.numSuccessors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#outDegree(java.lang.Object)
		 */
		public synchronized int outDegree(V vertex) {
			return delegate.outDegree(vertex);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#removeEdge(java.lang.Object)
		 */
		public synchronized boolean removeEdge(E edge) {
			return delegate.removeEdge(edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#removeVertex(java.lang.Object)
		 */
		public synchronized boolean removeVertex(V vertex) {
			return delegate.removeVertex(vertex);
		}

		/**
		 * @param directed_edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getDest(java.lang.Object)
		 */
		public V getDest(E directed_edge) {
			return delegate.getDest(directed_edge);
		}

		/**
		 * @param directed_edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getSource(java.lang.Object)
		 */
		public V getSource(E directed_edge) {
			return delegate.getSource(directed_edge);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#isDest(java.lang.Object, java.lang.Object)
		 */
		public boolean isDest(V vertex, E edge) {
			return delegate.isDest(vertex, edge);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#isSource(java.lang.Object, java.lang.Object)
		 */
		public boolean isSource(V vertex, E edge) {
			return delegate.isSource(vertex, edge);
		}
		
        public int numIncident(E edge)
        {
            return delegate.numIncident(edge);
        }

		/**
		 * @param hyperedge
		 * @param vertices
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
		 */
		public boolean addEdge(E hyperedge, Collection<V> vertices) {
			return delegate.addEdge(hyperedge, vertices);
		}
        
	}
	
	static class SynchronizedGraph<V,E> extends SynchronizedAbstractGraph<V,E> implements Serializable {
		
		private SynchronizedGraph(Graph<V,E> delegate) {
			super(delegate);
		}
	}
	
	static class SynchronizedUndirectedGraph<V,E> extends SynchronizedAbstractGraph<V,E> 
		implements UndirectedGraph<V,E>, Serializable {
		private SynchronizedUndirectedGraph(UndirectedGraph<V,E> delegate) {
			super(delegate);
		}
//	    public boolean addEdge(E e, V v1, V v2, boolean directed) {
//	        throw new UnsupportedOperationException("Cannot add a directed edge to an undirected graph");
//	    }

	}
	
	static class SynchronizedDirectedGraph<V,E> extends SynchronizedAbstractGraph<V,E> 
		implements DirectedGraph<V,E>, Serializable {
		
		private SynchronizedDirectedGraph(DirectedGraph<V,E> delegate) {
			super(delegate);
		}

		public V getDest(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getDest(directed_edge);
		}

		public V getSource(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getSource(directed_edge);
		}

		public boolean isDest(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isDest(vertex, edge);
		}

		public boolean isSource(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isSource(vertex, edge);
		}
	}
	
	static abstract class UnmodifiableAbstractGraph<V,E> implements Graph<V,E>, Serializable {
		protected Graph<V,E> delegate;


		private UnmodifiableAbstractGraph(Graph<V, E> delegate) {
			if(delegate == null) {
				throw new NullPointerException();
			}
			this.delegate = delegate;
		}

		/**
		 * @param e
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addDirectedEdge(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @param e
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		public boolean addEdge(E e, V v1, V v2) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addVertex(java.lang.Object)
		 */
		public boolean addVertex(V vertex) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#areIncident(java.lang.Object, java.lang.Object)
		 */
		public boolean areIncident(V vertex, E edge) {
			return delegate.areIncident(vertex, edge);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#areNeighbors(java.lang.Object, java.lang.Object)
		 */
		public boolean areNeighbors(V v1, V v2) {
			return delegate.areNeighbors(v1, v2);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#degree(java.lang.Object)
		 */
		public int degree(V vertex) {
			return delegate.degree(vertex);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
		 */
		public E findEdge(V v1, V v2) {
			return delegate.findEdge(v1, v2);
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getEdges()
		 */
		public Collection<E> getEdges() {
			return delegate.getEdges();
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getEdgeCount()
		 */
		public int getEdgeCount() {
			return delegate.getEdgeCount();
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getVertexCount()
		 */
		public int getVertexCount() {
			return delegate.getVertexCount();
		}

		/**
		 * @param directedness
		 * @return
		 * @see edu.uci.ics.graph.Graph#getEdges(edu.uci.ics.graph.util.EdgeType)
		 */
		public Collection<E> getEdges(EdgeType edgeType) {
			return delegate.getEdges(edgeType);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getEndpoints(java.lang.Object)
		 */
		public Pair<V> getEndpoints(E edge) {
			return delegate.getEndpoints(edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getIncidentEdges(java.lang.Object)
		 */
		public Collection<E> getIncidentEdges(V vertex) {
			return delegate.getIncidentEdges(vertex);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getIncidentVertices(java.lang.Object)
		 */
		public Collection<V> getIncidentVertices(E edge) {
			return delegate.getIncidentVertices(edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getInEdges(java.lang.Object)
		 */
		public Collection<E> getInEdges(V vertex) {
			return delegate.getInEdges(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getNeighbors(java.lang.Object)
		 */
		public Collection<V> getNeighbors(V vertex) {
			return delegate.getNeighbors(vertex);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
		 */
		public V getOpposite(V vertex, E edge) {
			return delegate.getOpposite(vertex, edge);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getOutEdges(java.lang.Object)
		 */
		public Collection<E> getOutEdges(V vertex) {
			return delegate.getOutEdges(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getPredecessors(java.lang.Object)
		 */
		public Collection<V> getPredecessors(V vertex) {
			return delegate.getPredecessors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#getSuccessors(java.lang.Object)
		 */
		public Collection<V> getSuccessors(V vertex) {
			return delegate.getSuccessors(vertex);
		}

		/**
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#getVertices()
		 */
		public Collection<V> getVertices() {
			return delegate.getVertices();
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#inDegree(java.lang.Object)
		 */
		public int inDegree(V vertex) {
			return delegate.inDegree(vertex);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getEdgeType(java.lang.Object)
		 */
		public EdgeType getEdgeType(E edge) {
			return delegate.getEdgeType(edge);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
		 */
		public boolean isPredecessor(V v1, V v2) {
			return delegate.isPredecessor(v1, v2);
		}

		/**
		 * @param v1
		 * @param v2
		 * @return
		 * @see edu.uci.ics.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
		 */
		public boolean isSuccessor(V v1, V v2) {
			return delegate.isSuccessor(v1, v2);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#numNeighbors(java.lang.Object)
		 */
		public int numNeighbors(V vertex) {
			return delegate.numNeighbors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#numPredecessors(java.lang.Object)
		 */
		public int numPredecessors(V vertex) {
			return delegate.numPredecessors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#numSuccessors(java.lang.Object)
		 */
		public int numSuccessors(V vertex) {
			return delegate.numSuccessors(vertex);
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Graph#outDegree(java.lang.Object)
		 */
		public int outDegree(V vertex) {
			return delegate.outDegree(vertex);
		}

		/**
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#removeEdge(java.lang.Object)
		 */
		public boolean removeEdge(E edge) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @param vertex
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#removeVertex(java.lang.Object)
		 */
		public boolean removeVertex(V vertex) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * @param directed_edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getDest(java.lang.Object)
		 */
		public V getDest(E directed_edge) {
			return delegate.getDest(directed_edge);
		}

		/**
		 * @param directed_edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#getSource(java.lang.Object)
		 */
		public V getSource(E directed_edge) {
			return delegate.getSource(directed_edge);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#isDest(java.lang.Object, java.lang.Object)
		 */
		public boolean isDest(V vertex, E edge) {
			return delegate.isDest(vertex, edge);
		}

		/**
		 * @param vertex
		 * @param edge
		 * @return
		 * @see edu.uci.ics.graph.Graph#isSource(java.lang.Object, java.lang.Object)
		 */
		public boolean isSource(V vertex, E edge) {
			return delegate.isSource(vertex, edge);
		}

        public int numIncident(E edge)
        {
            return delegate.numIncident(edge);
        }

		/**
		 * @param hyperedge
		 * @param vertices
		 * @return
		 * @see edu.uci.ics.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
		 */
		public boolean addEdge(E hyperedge, Collection<V> vertices) {
			return delegate.addEdge(hyperedge, vertices);
		}
	}
	
	static class UnmodifiableGraph<V,E> extends UnmodifiableAbstractGraph<V,E> implements Serializable {
		private UnmodifiableGraph(Graph<V,E> delegate) {
			super(delegate);
		}
	}
	
	static class UnmodifiableDirectedGraph<V,E> extends UnmodifiableAbstractGraph<V,E> 
		implements DirectedGraph<V,E>, Serializable {
		private UnmodifiableDirectedGraph(DirectedGraph<V,E> delegate) {
			super(delegate);
		}

		public V getDest(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getDest(directed_edge);
		}

		public V getSource(E directed_edge) {
			return ((DirectedGraph<V,E>)delegate).getSource(directed_edge);
		}

		public boolean isDest(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isDest(vertex, edge);
		}

		public boolean isSource(V vertex, E edge) {
			return ((DirectedGraph<V,E>)delegate).isSource(vertex, edge);
		}
	}
	
	static class UnmodifiableUndirectedGraph<V,E> extends UnmodifiableAbstractGraph<V,E> 
		implements UndirectedGraph<V,E>, Serializable {
		private UnmodifiableUndirectedGraph(UndirectedGraph<V,E> delegate) {
			super(delegate);
		}
	}

}
