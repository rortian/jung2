/*
 * Created on Jul 10, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring;

import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to vertices based on their distances to each other vertex in the graph.
 * 
 * NOTE: This class optionally normalizes its results depending on the value of its
 * 'averaging' constructor parameter.  If it is <code>true</code>, then the value returned for vertex v is the
 * _average_ distance from v to all other vertices; this is usually called <i>closeness centrality</i>.
 * If it is <code>false</code>, then the value returned is the _total_ distance from
 * v to all other vertices; this is sometimes referred to as <i>barycenter centrality</i>.
 * 
 * 
 * @author Joshua O'Madadhain
 */
public class DistanceCentralityScorer<V,E> implements VertexScorer<V, Double>
{
    /**
     * The graph on which the vertex scores are to be calculated.
     */
    protected Hypergraph<V, E> graph;
    
    /**
     * The metric to use for specifying the distance between pairs of vertices.
     */
    protected Distance<V> distance;
    
    /**
     * The storage for the output results.
     */
    protected Map<V, Double> output;
    
    /**
     * Specifies whether the values returned are the sum of the v-distances or the mean v-distance.
     */
    protected boolean averaging;
    
    /**
     * Specifies whether the values returned should ignore missing distance values, or throw an exception.
     * (Throws an exception by default.)
     */
    protected boolean ignore_nulls;
    
    /**
     * Creates an instance with the specified graph, distance metric, and averaging behavior.
     * 
     * @param graph     The graph on which the vertex scores are to be calculated.
     * @param distance  The metric to use for specifying the distance between pairs of vertices.
     * @param averaging Specifies whether the values returned is the sum of all v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, Distance<V> distance, boolean averaging)
    {
        this.graph = graph;
        this.distance = distance;
        this.averaging = averaging;
    }
    
    /**
     * Creates an instance with the specified graph and averaging behavior.  Internally creates a <code>Distance</code>
     * instance based on the specified edge weights.
     * 
     * @param graph         The graph on which the vertex scores are to be calculated.
     * @param edge_weights  The edge weights to use for specifying the distance between pairs of vertices.
     * @param averaging     Specifies whether the values returned is the sum of all v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, 
            Transformer<E, ? extends Number> edge_weights, boolean averaging)
    {
        this(graph, new DijkstraDistance<V,E>(graph, edge_weights), averaging);
        setIgnoreMissingDistances(true);
    }
    
    public DistanceCentralityScorer(Graph<V,E> graph, boolean averaging)
    {
        this(graph, new UnweightedShortestPath<V,E>(graph), averaging);
        setIgnoreMissingDistances(true);
    }

    public void evaluate()
    {
        for (V v : graph.getVertices())
        {
            double sum = 0;
            for (Map.Entry<V,Number> entry : distance.getDistanceMap(v).entrySet())
            {
                double distance = entry.getValue().doubleValue();
                sum += distance;
            }
            if (averaging)
                output.put(v, sum / graph.getVertexCount());
            else
                output.put(v, sum);
        }
        setIgnoreMissingDistances(true);
    }
    
    public void setIgnoreMissingDistances(boolean ignore)
    {
        this.ignore_nulls = ignore;
    }
    
    public Transformer<V, Double> getVertexScores()
    {
        return MapTransformer.getInstance(output);
    }
}
