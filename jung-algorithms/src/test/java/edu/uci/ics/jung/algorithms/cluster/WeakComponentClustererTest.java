package edu.uci.ics.jung.algorithms.cluster;

import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.TestGraphs;
import junit.framework.TestCase;

public class WeakComponentClustererTest extends TestCase {
	
	Graph<String,Number> graph =  TestGraphs.getDemoGraph();
	Factory<Graph<String,Number>> factory = new Factory<Graph<String,Number>>() {

		public Graph<String, Number> create() {
			return new SparseGraph<String,Number>();
		}};
	
	public void testWeakComponent() {
		WeakComponentGraphClusterer<String,Number> clusterer = 
			new WeakComponentGraphClusterer<String,Number>(factory);
		Set<Graph<String,Number>> clusterSet = clusterer.transform(graph);
		System.err.println("set is "+clusterSet);
	}

}
