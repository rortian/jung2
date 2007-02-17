package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.Forest;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class PrimMinimumSpanningTree<V,E> {
	
	protected Graph<V,E> graph;
	protected Forest<V,E> forest;
	protected Map<E,Double> weights;
	
	public PrimMinimumSpanningTree(Graph<V, E> graph, Forest<V,E> forest, 
			V root, Map<E, Double> weights) {
		
		assert forest.getVertexCount() == 0 :
			"Supplied Forest must be empty";
		this.graph = graph;
		this.forest = forest;
		this.weights = weights;
		Set<E> unfinishedEdges = new HashSet<E>(graph.getEdges());
//		this.unfinishedEdges.addAll(graph.getEdges());
		this.forest.addVertex(root);
		updateForest(forest.getVertices(), unfinishedEdges);
	}
	
	public Forest<V,E> getForest() {
		return forest;
	}
	
	protected void updateForest(Collection<V> tv, Collection<E> unfinishedEdges) {
		double minCost = Double.MAX_VALUE;
		E nextEdge = null;
		V nextVertex = null;
		V currentVertex = null;
		for(E e : unfinishedEdges) {
			
			if(forest.getEdges().contains(e)) continue;
			// find the lowest cost edge, get its opposite endpoint,
			// and then update forest from its Successors
			Pair<V> endpoints = graph.getEndpoints(e);
			V first = endpoints.getFirst();
			V second = endpoints.getSecond();
			if(tv.contains(first) == true && tv.contains(second) == false) {
				if(weights.get(e) < minCost) {
					minCost = weights.get(e);
					nextEdge = e;
					currentVertex = first;
					nextVertex = second;
				}
			}
			if(graph.getEdgeType(e) == EdgeType.UNDIRECTED &&
					tv.contains(second) == true && tv.contains(first) == false) {
				if(weights.get(e) < minCost) {
					minCost = weights.get(e);
					nextEdge = e;
					currentVertex = second;
					nextVertex = first;
				}
			}
		}
		
		if(nextVertex != null && nextEdge != null) {
			unfinishedEdges.remove(nextEdge);
			forest.addEdge(nextEdge, currentVertex, nextVertex);
			updateForest(forest.getVertices(), unfinishedEdges);
		}
		Collection<V> leftovers = new HashSet<V>(graph.getVertices());
		leftovers.removeAll(forest.getVertices());
		if(leftovers.size() > 0) {
			V anotherRoot = leftovers.iterator().next();
			forest.addVertex(anotherRoot);
			updateForest(forest.getVertices(), unfinishedEdges);
		}
	}
}
