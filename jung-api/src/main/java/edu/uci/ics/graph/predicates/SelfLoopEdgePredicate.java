package edu.uci.ics.graph.predicates;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Pair;


public class SelfLoopEdgePredicate<V,E> extends AbstractGraphPredicate<V,E> {

    public boolean evaluateEdge(Graph<V,E> graph, E edge) {
        Pair<V> endpoints = graph.getEndpoints(edge);
        return endpoints.getFirst().equals(endpoints.getSecond());
    }
}
