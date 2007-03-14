package edu.uci.ics.jung.graph;

import edu.uci.ics.jung.graph.util.Pair;

public class Memory {
	
	public static void main(String[] args) {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long usedMemory = total-free;
		System.err.println("total="+total+", free="+free+", used="+usedMemory);
		Graph<Number,Number> graph = new UndirectedSparseMultigraph<Number,Number>();
		for(int i=0; i<100; i++) {
			graph.addVertex(i);
		}
		for(int i=0; i<200; i++) {
			graph.addEdge(i, Math.random()*100, Math.random()*100);
		}
		total = Runtime.getRuntime().totalMemory();
		free = Runtime.getRuntime().freeMemory();
		usedMemory = total-free;
		System.err.println("total="+total+", free="+free+", used="+usedMemory);
		
		Graph<Number,Number> copyOne = new UndirectedSparseMultigraph<Number,Number>();
		for(Number v : graph.getVertices()) {
			copyOne.addVertex(v);
		}
		for(Number e : graph.getEdges()) {
			Pair<Number> ep = graph.getEndpoints(e);
			copyOne.addEdge(e, ep.getFirst(), ep.getSecond());
		}
		total = Runtime.getRuntime().totalMemory();
		free = Runtime.getRuntime().freeMemory();
		usedMemory = total-free;
		System.err.println("total="+total+", free="+free+", used="+usedMemory);
		
		Graph<Number,Number> copyTwo = new UndirectedSparseMultigraph<Number,Number>();
		for(Number v : graph.getVertices()) {
			copyTwo.addVertex(v);
		}
		for(Number e : graph.getEdges()) {
			Pair<Number> ep = graph.getEndpoints(e);
			copyTwo.addEdge(e, ep.getFirst(), ep.getSecond());
		}
		total = Runtime.getRuntime().totalMemory();
		free = Runtime.getRuntime().freeMemory();
		usedMemory = total-free;
		System.err.println("total="+total+", free="+free+", used="+usedMemory);

		Graph<Number,Number> copyThree = new UndirectedSparseMultigraph<Number,Number>();
		for(Number v : graph.getVertices()) {
			copyThree.addVertex(v);
		}
		for(Number e : graph.getEdges()) {
			Pair<Number> ep = graph.getEndpoints(e);
			copyThree.addEdge(e, ep.getFirst(), ep.getSecond());
		}
		total = Runtime.getRuntime().totalMemory();
		free = Runtime.getRuntime().freeMemory();
		usedMemory = total-free;
		System.err.println("total="+total+", free="+free+", used="+usedMemory);
//		for(Number v : graph.getVertices()) 
	}

}
