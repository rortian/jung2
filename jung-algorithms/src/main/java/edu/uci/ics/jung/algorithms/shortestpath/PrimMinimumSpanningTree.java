package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.Tree;
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
	protected Tree<V,E> tree;
	protected Map<E,Double> weights;
	protected Set<E> unfinishedEdges = new HashSet<E>();
	
	public PrimMinimumSpanningTree(Graph<V, E> graph, Tree<V,E> tree, 
			V root, Map<E, Double> weights) {
		
		assert tree.getVertexCount() == 0 :
			"Supplied Tree must be empty";
		this.graph = graph;
		this.tree = tree;
		this.weights = weights;
		this.unfinishedEdges.addAll(graph.getEdges());
		this.tree.addVertex(root);
		updateTree(tree.getVertices());
	}
	
	public Tree<V,E> getTree() {
		return tree;
	}
	
	protected void updateTree(Collection<V> tv) {
		double minCost = Double.MAX_VALUE;
		E nextEdge = null;
		V nextVertex = null;
		V currentVertex = null;
		for(E e : unfinishedEdges) {
			
			if(tree.getEdges().contains(e)) continue;
			// find the lowest cost edge, get its opposite endpoint,
			// and then update tree from its Successors
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
		}
		
		if(nextVertex != null && nextEdge != null) {
			unfinishedEdges.remove(nextEdge);
			tree.addEdge(nextEdge, currentVertex, nextVertex);
			updateTree(tree.getVertices());
		}
	}
}
