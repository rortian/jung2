package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Collection;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentGraphClusterer;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using a variation of Prim's algorithm.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class MinimumSpanningForest2<V,E> {
	
	protected Graph<V,E> graph;
	protected Forest<V,E> forest;
	protected Transformer<E,Double> weights = 
		(Transformer<E,Double>)new ConstantTransformer<Double>(1.0);
	
	/**
	 * create a Forest from the supplied Graph and supplied Factory, which
	 * is used to create a new, empty Forest. If non-null, the supplied root
	 * will be used as the root of the tree/forest. If the supplied root is
	 * null, or not present in the Graph, then an arbitary Graph vertex
	 * will be selected as the root.
	 * If the Minimum Spanning Tree does not include all vertices of the
	 * Graph, then a leftover vertex is selected as a root, and another
	 * tree is created
	 * @param graph
	 * @param factory
	 * @param root
	 * @param weights
	 */
	public MinimumSpanningForest2(Graph<V, E> graph, 
			Factory<Forest<V,E>> factory, 
			Factory<Tree<V,E>> treeFactory,
			Transformer<E, Double> weights) {
		this(graph, factory.create(), 
				treeFactory, 
				weights);
	}
	
	/**
	 * create a forest from the supplied graph, populating the
	 * supplied Forest, which must be empty. 
	 * If the supplied root is null, or not present in the Graph,
	 * then an arbitary Graph vertex will be selected as the root.
	 * If the Minimum Spanning Tree does not include all vertices of the
	 * Graph, then a leftover vertex is selected as a root, and another
	 * tree is created
	 * @param graph the Graph to find MST in
	 * @param forest the Forest to populate. Must be empty
	 * @param root first Tree root, may be null
	 * @param weights edge weights, may be null
	 */
	public MinimumSpanningForest2(Graph<V, E> graph, 
			Forest<V,E> forest, 
			Factory<Tree<V,E>> treeFactory,
			Transformer<E, Double> weights) {
		
		if(forest.getVertexCount() != 0) {
			throw new IllegalArgumentException("Supplied Forest must be empty");
		}
		this.graph = graph;
		this.forest = forest;
		if(weights != null) {
			this.weights = weights;
		}
		
		WeakComponentGraphClusterer<V,E> wcgc =
			new WeakComponentGraphClusterer<V,E>();
		Collection<Graph<V,E>> components = wcgc.transform(graph);
		
		for(Graph<V,E> component : components) {
			PrimMinimumSpanningTree<V,E> mst = 
				new PrimMinimumSpanningTree<V,E>(treeFactory, weights);
			forest.getTrees().add(mst.transform(component));
		}
	}
	
	public Forest<V,E> getForest() {
		return forest;
	}
}
