/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.HashMap;
import java.util.Map;

import sun.security.provider.certpath.Vertex;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;

/**
 * Computes the shortest path distances for graphs whose edges are not weighted (using BFS).
 * 
 * @author Scott White
 */
public class UnweightedShortestPath<V, E> 
    implements ShortestPath<V,E>, Distance<V>
{
	private Map<V,Map<V,Number>> mDistanceMap;
	private Map<V,Map<V,E>> mIncomingEdgeMap;
	private Graph<V,E> mGraph;
    private Map<V, Number> distances = new HashMap<V,Number>();

	/**
	 * Constructs and initializes algorithm
	 * @param g the graph
	 */
	public UnweightedShortestPath(Graph<V,E> g)
	{
		mDistanceMap = new HashMap<V,Map<V,Number>>();
		mIncomingEdgeMap = new HashMap<V,Map<V,E>>();
		mGraph = g;
	}

    /**
     * @see edu.uci.ics.jung.algorithms.shortestpath.Distance#getDistance(edu.uci.ics.jung.graph.ArchetypeVertex, edu.uci.ics.jung.graph.ArchetypeVertex)
     */
	public Number getDistance(V source, V target)
	{
		Map<V, Number> sourceSPMap = getDistanceMap(source);
		return sourceSPMap.get(target);
	}

    /**
     * @see edu.uci.ics.jung.algorithms.shortestpath.Distance#getDistanceMap(edu.uci.ics.jung.graph.ArchetypeVertex)
     */
	public Map<V,Number> getDistanceMap(V source)
	{
		Map<V,Number> sourceSPMap = mDistanceMap.get(source);
		if (sourceSPMap == null)
		{
			computeShortestPathsFromSource(source);
			sourceSPMap = mDistanceMap.get(source);
		}
		return sourceSPMap;
	}

	/**
	 * @see edu.uci.ics.jung.algorithms.shortestpath.ShortestPath#getIncomingEdgeMap(edu.uci.ics.jung.graph.Vertex)
	 */
	public Map<V,E> getIncomingEdgeMap(V source)
	{
		Map<V,E> sourceIEMap = mIncomingEdgeMap.get(source);
		if (sourceIEMap == null)
		{
			computeShortestPathsFromSource(source);
			sourceIEMap = mIncomingEdgeMap.get(source);
		}
		return sourceIEMap;
	}

	/**
	* Computes the shortest path distance from the source to target. If the shortest path distance has not already
	* been computed, then all pairs shortest paths will be computed.
	* @param source the source node
	* @param target the target node
	* @return the shortest path value (if the target is unreachable, NPE is thrown)
	* @deprecated use getDistance
	*/
	public int getShortestPath(V source, V target)
	{
		return getDistance(source, target).intValue();
	}

	/**
	 * Computes the shortest path distances from a given node to all other nodes.
	 * @param graph the graph
	 * @param source the source node
	 * @return A <code>Map</code> whose keys are target vertices and whose values are <code>Integers</code> representing the shortest path distance
	 */
	private void computeShortestPathsFromSource(V source)
	{
		BFSDistanceLabeler<V,E> labeler = new BFSDistanceLabeler<V,E>();
		labeler.labelDistances(mGraph, source);
        distances = labeler.getDistanceDecorator();
		Map<V,Number> currentSourceSPMap = new HashMap<V,Number>();
		Map<V,E> currentSourceEdgeMap = new HashMap<V,E>();

        for(V vertex : mGraph.getVertices()) {
            
			Number distanceVal = distances.get(vertex);
            // BFSDistanceLabeler uses -1 to indicate unreachable vertices;
            // don't bother to store unreachable vertices
            if (distanceVal != null && distanceVal.intValue() >= 0) 
            {
                currentSourceSPMap.put(vertex, distanceVal);
                int minDistance = distanceVal.intValue();
                for(E incomingEdge : mGraph.getInEdges(vertex)) {

                    V neighbor = mGraph.getOpposite(vertex, incomingEdge);

                    Number predDistanceVal = distances.get(neighbor);

                    int pred_distance = predDistanceVal.intValue();
                    if (pred_distance < minDistance && pred_distance >= 0)
                    {
                        minDistance = predDistanceVal.intValue();
                        currentSourceEdgeMap.put(vertex, incomingEdge);
                    }
                }
            }
		}
		mDistanceMap.put(source, currentSourceSPMap);
		mIncomingEdgeMap.put(source, currentSourceEdgeMap);
	}
    
    /**
     * Clears all stored distances for this instance.  
     * Should be called whenever the graph is modified (edge weights 
     * changed or edges added/removed).  If the user knows that
     * some currently calculated distances are unaffected by a
     * change, <code>reset(Vertex)</code> may be appropriate instead.
     * 
     * @see #reset(Vertex)
     */
    public void reset()
    {
        mDistanceMap.clear();
        mIncomingEdgeMap.clear();
    }
    
    /**
     * Clears all stored distances for the specified source vertex 
     * <code>source</code>.  Should be called whenever the stored distances
     * from this vertex are invalidated by changes to the graph.
     * 
     * @see #reset()
     */
    public void reset(Vertex v)
    {
        mDistanceMap.remove(v);
        mIncomingEdgeMap.remove(v);
    }
}
