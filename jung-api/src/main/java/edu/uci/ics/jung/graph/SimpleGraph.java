package edu.uci.ics.jung.graph;

/**
 * SimpleGraph is a marker interface intended to mean that
 * the implementing Graph shall not accept parallel edges, nor
 * loop edges.
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public interface SimpleGraph<V,E> extends Graph<V,E> {

}
