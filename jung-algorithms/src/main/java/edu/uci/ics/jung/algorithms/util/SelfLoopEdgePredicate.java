package edu.uci.ics.jung.algorithms.util;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.graph.util.EdgeContext;
import edu.uci.ics.graph.util.Pair;


public class SelfLoopEdgePredicate<V,E> implements Predicate<EdgeContext<V,E>> {

    public boolean evaluate(EdgeContext<V,E> context) {
        Pair<V> endpoints = context.graph.getEndpoints(context.edge);
        return endpoints.getFirst().equals(endpoints.getSecond());
    }
}
