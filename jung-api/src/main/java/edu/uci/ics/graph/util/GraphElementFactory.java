package edu.uci.ics.graph.util;

import edu.uci.ics.graph.Graph;

public interface GraphElementFactory<V,E> {
	
	V generateVertex(Graph<V,E> graph);
	
	E generateEdge(Graph<V,E> graph);

}
