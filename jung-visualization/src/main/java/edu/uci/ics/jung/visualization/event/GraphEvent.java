package edu.uci.ics.jung.visualization.event;

import edu.uci.ics.jung.graph.Graph;
/**
 * 
 * @author tom nelson
 *
 * @param <V>
 * @param <E>
 */
public abstract class GraphEvent<V,E> {
	
	protected Graph<V,E> source;
	protected Type type;

	public GraphEvent(Graph<V, E> source, Type type) {
		this.source = source;
		this.type = type;
	}
	
	public static enum Type {
		VERTEX_ADDED,
		VERTEX_REMOVED,
		EDGE_ADDED,
		EDGE_REMOVED
	}
	
	public static class Vertex<V,E> extends GraphEvent<V,E> {
		protected V vertex;
		public Vertex(Graph<V,E> source, Type type, V vertex) {
			super(source,type);
			this.vertex = vertex;
		}
		public V getVertex() {
			return vertex;
		}
		public String toString() {
			return "GraphEvent type:"+type+" for "+vertex;
		}
		
	}
	public static class Edge<V,E> extends GraphEvent<V,E> {
		protected E edge;
		public Edge(Graph<V,E> source, Type type, E edge) {
			super(source,type);
			this.edge = edge;
		}
		public E getEdge() {
			return edge;
		}
		public String toString() {
			return "GraphEvent type:"+type+" for "+edge;
		}
		
	}
}
