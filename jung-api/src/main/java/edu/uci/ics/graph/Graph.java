/*
 * Created on Oct 17, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.graph;

import java.util.Collection;

import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;



public interface Graph<V,E> extends HyperGraph<V,E>
{
    Collection<E> getInEdges(V vertex);
    
    Collection<E> getOutEdges(V vertex);

    Collection<V> getPredecessors(V vertex);
    
    Collection<V> getSuccessors(V vertex);
    
    boolean addEdge(E e, V v1, V v2);
    
    boolean addEdge(E e, V v1, V v2, EdgeType edgeType);
    
    Collection<E> getEdges(EdgeType edgeType);
    // convenience methods
    
    Pair<V> getEndpoints(E edge); // build Pair from getIncidentVertices()
    EdgeType getEdgeType(E edge); // whether edge is directed or not
    V getOpposite(V vertex, E edge); // get edge's incident vertices, find the Vertex that's not the one input
    int inDegree(V vertex); // calculate from get{In,Out}Edges
    int outDegree(V vertex); // calculate from get{In,Out}Edges
    boolean isPredecessor(V v1, V v2); // v1.get{Prede,Suc}cessors.contains(v2)
    boolean isSuccessor(V v1, V v2); // v1.get{Prede,Suc}cessors.contains(v2)
    int numPredecessors(V vertex); // get{Prede,Suc}cessors().size()
    int numSuccessors(V vertex); // getSuccessors().size()
    
    // methods from DirectedGraph
    V getSource(E directed_edge);

    V getDest(E directed_edge);
    
    boolean isSource(V vertex, E edge); // get{Source, Dest}(e) == v
    
    boolean isDest(V vertex, E edge); // get{Source, Dest}(e) == v


}
