package edu.uci.ics.graph.util;


public class Context<G,E> {
	
	private static Context instance = new Context();
	public G graph;
	public E element;
	
	public static <G,E> Context<G,E> getInstance(G graph, E element) {
		instance.graph = graph;
		instance.element = element;
		return instance;
	}
}

