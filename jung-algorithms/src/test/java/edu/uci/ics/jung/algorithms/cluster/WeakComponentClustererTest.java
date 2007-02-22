package edu.uci.ics.jung.algorithms.cluster;

import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.util.TestGraphs;

public class WeakComponentClustererTest extends TestCase {
	
	Graph<String,Number> graph =  TestGraphs.getDemoGraph();
	
	public void testWeakComponent() {
		WeakComponentGraphClusterer<String,Number> clusterer = 
			new WeakComponentGraphClusterer<String,Number>();
		Set<Graph<String,Number>> clusterSet = clusterer.transform(graph);
		System.err.println("set is "+clusterSet);
	}

}
