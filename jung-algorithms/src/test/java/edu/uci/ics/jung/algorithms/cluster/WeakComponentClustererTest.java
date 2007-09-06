package edu.uci.ics.jung.algorithms.cluster;

import java.util.Collection;

import junit.framework.TestCase;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.TestGraphs;

public class WeakComponentClustererTest extends TestCase {
	
	Graph<String,Number> graph =  TestGraphs.getDemoGraph();
	
	public void testWeakComponent() {
		WeakComponentGraphClusterer<String,Number> clusterer = 
			new WeakComponentGraphClusterer<String,Number>();
		Collection<Graph<String,Number>> clusterSet = clusterer.transform(graph);
//		System.err.println("set is "+clusterSet);
	}

}
