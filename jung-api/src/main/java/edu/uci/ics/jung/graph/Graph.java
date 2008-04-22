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
package edu.uci.ics.jung.graph;

import java.util.Collection;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * A graph consisting of a set of vertices of type <code>V</code>
 * set and a set of edges of type <code>E</code>.  Edges of this
 * graph type have exactly two endpoints; whether these endpoints 
 * must be distinct depends on the implementation.
 * <P>
 * This interface permits, but does not enforce, any of the following 
 * common variations of graphs:
 * <ul>
 * <li> directed and undirected edges
 * <li> vertices and edges with attributes (for example, weighted edges)
 * <li> vertices and edges of different types (for example, bipartite 
 *      or multimodal graphs)
 * <li> parallel edges (multiple edges which connect a single set of vertices)
 * <li> representations as matrices or as adjacency lists or adjacency maps
 * </ul> 
 * Extensions or implementations of this interface 
 * may enforce or disallow any or all of these variations.
 * 
 * <p>Definitions (with respect to a given vertex <code>v</code>:
 * <ul>
 * <li/><b>incoming edge</b> of <code>v</code>: 
 * <li/><b>outgoing edge</b> of <code>v</code>:
 * <li/><b>predecessor</b> of <code>v</code>: 
 * <li/><b>successor</b> of <code>v</code>:
 * <li/>
 * </ul> 
 * 
 * @author Joshua O'Madadhain
 */
public interface Graph<V,E> extends Hypergraph<V,E>
{
    /**
     * Returns a <code>Collection</code> view of the incoming edges incident to <code>vertex</code>
     * in this graph.
     * @param vertex    the vertex whose incoming edges are to be returned
     * @return  a <code>Collection</code> view of the incoming edges incident 
     * to <code>vertex</code> in this graph
     */
    Collection<E> getInEdges(V vertex);
    
    /**
     * Returns a <code>Collection</code> view of the outgoing edges incident to <code>vertex</code>
     * in this graph.
     * @param vertex    the vertex whose outgoing edges are to be returned
     * @return  a <code>Collection</code> view of the outgoing edges incident 
     * to <code>vertex</code> in this graph
     */
    Collection<E> getOutEdges(V vertex);

    /**
     * Returns a <code>Collection</code> view of the predecessors of <code>vertex</code> 
     * in this graph.  A predecessor of <code>vertex</code> is defined as a vertex <code>v</code> 
     * which is connected to 
     * <code>vertex</code> by an edge <code>e</code>, where <code>e</code> is an outgoing edge of 
     * <code>v</code> and an incoming edge of <code>vertex</code>.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a <code>Collection</code> view of the predecessors of 
     * <code>vertex</code> in this graph
     */
    Collection<V> getPredecessors(V vertex);
    
    /**
     * Returns a <code>Collection</code> view of the successors of <code>vertex</code> 
     * in this graph.  A successor of <code>vertex</code> is defined as a vertex <code>v</code> 
     * which is connected to 
     * <code>vertex</code> by an edge <code>e</code>, where <code>e</code> is an incoming edge of 
     * <code>v</code> and an outgoing edge of <code>vertex</code>.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a <code>Collection</code> view of the successors of 
     * <code>vertex</code> in this graph
     */
    Collection<V> getSuccessors(V vertex);
    
    /**
     * Returns all edges in this graph of the specified <code>EdgeType</code>.
     * Current <code>EdgeType</code>s include <code>DIRECTED</code> and <code>UNDIRECTED</code>.
     * @param edgeType the type of the edge to return
     * @return  all edges in this graph of type <code>EdgeType</code>
     */
    Collection<E> getEdges(EdgeType edgeType);

    /**
     * Returns the edge type of <code>Edge</code>.
     * @param edge
     * @return
     */
    EdgeType getEdgeType(E edge); // whether edge is directed or not
    
    
    /**
     * Returns the number of incoming edges incident to <code>vertex</code>.
     * Equivalent to <code>getInEdges(vertex).size()</code>.
     * @param vertex    the vertex whose indegree is to be calculated
     * @return  the number of incoming edges incident to <code>vertex</code>
     */
    int inDegree(V vertex);
    
    /**
     * Returns the number of outgoing edges incident to <code>vertex</code>.
     * Equivalent to <code>getOutEdges(vertex).size()</code>.
     * @param vertex    the vertex whose outdegree is to be calculated
     * @return  the number of outgoing edges incident to <code>vertex</code>
     */
    int outDegree(V vertex);
    
    /**
     * Returns <code>true</code> if <code>v1</code> is a predecessor of <code>v2</code> in this graph.
     * Equivalent to <code>v1.getPredecessors().contains(v2)</code>.
     * @param v1    
     * @param v2
     * @return <code>true</code> if <code>v1</code> is a predecessor of <code>v2</code>, and false otherwise.
     */
    boolean isPredecessor(V v1, V v2);
    
    /**
     * Returns <code>true</code> if <code>v1</code> is a successor of <code>v2</code> in this graph.
     * Equivalent to <code>v1.getSuccessors().contains(v2)</code>.
     * @param v1    
     * @param v2
     * @return <code>true</code> if <code>v1</code> is a successor of <code>v2</code>, and false otherwise.
     */
    boolean isSuccessor(V v1, V v2);

    /**
     * Returns the number of predecessors that <code>vertex</code> has in this graph.
     * Equivalent to <code>vertex.getPredecessors().size()</code>.
     * @param vertex the vertex whose predecessor count is to be returned
     * @return  the number of predecessors that <code>vertex</code> has in this graph
     */
    int getPredecessorCount(V vertex);
    
    /**
     * Returns the number of successors that <code>vertex</code> has in this graph.
     * Equivalent to <code>vertex.getSuccessors().size()</code>.
     * @param vertex the vertex whose successor count is to be returned
     * @return  the number of successors that <code>vertex</code> has in this graph
     */
    int getSuccessorCount(V vertex);
    
    /**
     * If <code>directed_edge</code> is a directed edge in this graph, returns the source; 
     * otherwise returns <code>null</code>. 
     * The source of a directed edge <code>d</code> is defined to be the vertex for which  
     * <code>d</code> is an outgoing edge.
     * <code>directed_edge</code> is guaranteed to be a directed edge if 
     * its <code>EdgeType</code> is <code>DIRECTED</code>. 
     * @param directed_edge
     * @return  the source of <code>directed_edge</code> if it is a directed edge in this graph, or <code>null</code> otherwise
     */
    V getSource(E directed_edge);

    /**
     * If <code>directed_edge</code> is a directed edge in this graph, returns the destination; 
     * otherwise returns <code>null</code>. 
     * The destination of a directed edge <code>d</code> is defined to be the vertex 
     * incident to <code>d</code> for which  
     * <code>d</code> is an incoming edge.
     * <code>directed_edge</code> is guaranteed to be a directed edge if 
     * its <code>EdgeType</code> is <code>DIRECTED</code>. 
     * @param directed_edge
     * @return  the destination of <code>directed_edge</code> if it is a directed edge in this graph, or <code>null</code> otherwise
     */
    V getDest(E directed_edge);
    
    /**
     * Returns <code>true</code> if <code>vertex</code> is the source of <code>edge</code>.
     * Equivalent to <code>getSource(edge).equals(vertex)</code>.
     * @param vertex
     * @param edge
     * @return
     */
    boolean isSource(V vertex, E edge); // get{Source, Dest}(e) == v
    
    /**
     * Returns <code>true</code> if <code>vertex</code> is the destination of <code>edge</code>.
     * Equivalent to <code>getDest(edge).equals(vertex)</code>.
     * @param vertex
     * @param edge
     * @return
     */
    boolean isDest(V vertex, E edge); // get{Source, Dest}(e) == v

    
    boolean addEdge(E e, V v1, V v2);
    
    boolean addEdge(E e, V v1, V v2, EdgeType edgeType);

    Pair<V> getEndpoints(E edge); // build Pair from getIncidentVertices()
    
    V getOpposite(V vertex, E edge); // get edge's incident vertices, find the Vertex that's not the one input

}
