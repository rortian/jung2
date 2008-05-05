/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.connectivity;

import java.util.Set;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.algorithms.filters.impl.KNeighborhoodFilter;



/**
 * Extracts the subgraph (neighborhood) from a graph whose nodes are no more than distance k away from at
 * least one of the root nodes (starting vertices).
 * @author Scott White and Danyel Fisher
 */
public class KNeighborhoodExtractor {

	/**
	 * Extracts the subgraph comprised of all vertices within distance K (undirected) from any
	 * node in rootNodes.
	 * @param graph the graph whose subgraph is to be extracted
	 * @param rootNodes the set of root nodes (starting vertices) in the graph
	 * @param radiusK the radius of the subgraph to be extracted
	 */
	static public <V,E> Graph<V,E> extractNeighborhood(
		Graph<V,E> graph,
		Set<V> rootNodes,
		int radiusK) {
		return extract(graph, rootNodes, radiusK, KNeighborhoodFilter.EdgeType.IN_OUT);
	}

	/**
	 * Extracts the subgraph comprised of all vertices within distance K (out-directed) from any
	 * node in rootNodes.
	  *@param graph the graph whose subgraph is to be extracted
	 * @param rootNodes the set of root nodes (starting vertices) in the graph
	 * @param radiusK the radius of the subgraph to be extracted
	 */
	static public <V,E> Graph<V,E> extractOutDirectedNeighborhood(
		DirectedGraph<V,E> graph,
		Set<V> rootNodes,
		int radiusK) {
		return extract(graph, rootNodes, radiusK, KNeighborhoodFilter.EdgeType.OUT);
	}

	/**
	 * Extracts the subgraph comprised of all vertices within distance K (in-directed) from any
	 * node in rootNodes.
	  * @param graph the graph whose subgraph is to be extracted
	 * @param rootNodes the set of root nodes (starting vertices) in the graph
	 * @param radiusK the radius of the subgraph to be extracted
	 */
	static public <V,E> Graph<V,E> extractInDirectedNeighborhood(
		DirectedGraph<V,E> graph,
		Set<V> rootNodes,
		int radiusK) {
		return extract(graph, rootNodes, radiusK, KNeighborhoodFilter.EdgeType.IN);
	}

	static private <V,E> Graph<V,E> extract(
		Graph<V,E> graph,
		Set<V> rootNodes,
		int radiusK,
		KNeighborhoodFilter.EdgeType edgeType) {
		Filter<V,E> nf = new KNeighborhoodFilter<V,E>(rootNodes, radiusK, edgeType);
		return nf.filter(graph);
	}

}