package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using a variation of Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class PrimMinimumSpanningTree<V,E> {
	
	protected Graph<V,E> graph;
	protected Tree<V,E> tree;
	protected Map<E,Double> weights = LazyMap.decorate(new HashMap<E,Double>(),
			new ConstantTransformer(1));
	
	/**

	 * @param graph
	 * @param factory
	 * @param root
	 * @param weights
	 */
	public PrimMinimumSpanningTree(Graph<V, E> graph, Factory<Tree<V,E>> factory, 
			V root, Map<E, Double> weights) {
		this(graph, factory.create(), root, weights);
	}
	
	/**
	 * @param graph the Graph to find MST in
	 * @param forest the Forest to populate. Must be empty
	 * @param root first Tree root, may be null
	 * @param weights edge weights, may be null
	 */
	public PrimMinimumSpanningTree(Graph<V, E> graph, Tree<V,E> tree, 
			V root, Map<E, Double> weights) {
		
		if(tree.getVertexCount() != 0) {
			throw new IllegalArgumentException("Supplied Tree must be empty");
		}
		this.graph = graph;
		this.tree = tree;
		if(weights != null) {
			this.weights = weights;
		}
		Set<E> unfinishedEdges = new HashSet<E>(graph.getEdges());
		if(graph.getVertices().contains(root)) {
			this.tree.addVertex(root);
		} else if(graph.getVertexCount() > 0) {
			// pick an arbitrary vertex to make root
			this.tree.addVertex(graph.getVertices().iterator().next());
		}
		updateTree(tree.getVertices(), unfinishedEdges);
	}
	
	public Tree<V,E> getTree() {
		return tree;
	}
	
	protected void updateTree(Collection<V> tv, Collection<E> unfinishedEdges) {
		double minCost = Double.MAX_VALUE;
		E nextEdge = null;
		V nextVertex = null;
		V currentVertex = null;
		for(E e : unfinishedEdges) {
			
			if(tree.getEdges().contains(e)) continue;
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
			tree.addEdge(nextEdge, currentVertex, nextVertex);
			updateTree(tree.getVertices(), unfinishedEdges);
		}
	}
}
