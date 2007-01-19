package edu.uci.ics.jung.algorithms.util;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Context;
import edu.uci.ics.graph.util.Pair;


public class SelfLoopEdgePredicate<V,E> implements Predicate<Context<Graph<V,E>,E>> {

    public boolean evaluate(Context<Graph<V,E>,E> context) {
        Pair<V> endpoints = context.graph.getEndpoints(context.element);
        return endpoints.getFirst().equals(endpoints.getSecond());
    }
}
