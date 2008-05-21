package edu.uci.ics.jung.graph.util;

public class Context<G,E> {
	
	@SuppressWarnings("unchecked")
	private static Context instance = new Context();
	public G graph;
	public E element;
	
	@SuppressWarnings("unchecked")
	public static <G,E> Context<G,E> getInstance(G graph, E element) {
		instance.graph = graph;
		instance.element = element;
		return instance;
	}
	
	@Override
	public int hashCode() {
		return graph.hashCode() ^ element.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof Context))
            return false;
        Context context = (Context)o;
        return context.graph.equals(graph) && context.element.equals(element);
    }
}

