/*
 * Created on Jul 15, 2007
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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class VoltageScorer<V, E> extends AbstractIterativeScorer<V, E, Double>
        implements VertexScorer<V, Double>
{
    protected Map<V, ? extends Number> source_voltages;
    
    protected Collection<V> sinks;
    
    /**
     * @param g
     * @param edge_weights
     */
    public VoltageScorer(Graph<V, E> g, Transformer<E, ? extends Number> edge_weights, 
            Map<V, ? extends Number> source_voltages, Collection<V> sinks)
    {
        super(g, edge_weights);
        this.source_voltages = source_voltages;
        this.sinks = sinks;
        initialize();
    }

    /**
     * @param g
     */
    public VoltageScorer(Graph<V, E> g, Map<V, ? extends Number> source_voltages, Collection<V> sinks)
    {
        super(g);
        this.source_voltages = source_voltages;
        this.sinks = sinks;
        initialize();
    }

    public void initialize()
    {
        super.initialize();
        
        // sanity check
        if (source_voltages.isEmpty() || sinks.isEmpty())
            throw new IllegalArgumentException("Both sources and sinks (grounds) must be defined");
        
        if (source_voltages.size() + sinks.size() > graph.getVertexCount())
            throw new IllegalArgumentException("Source/sink sets overlap, or contain vertices not in graph");
        
        for (Map.Entry<V, ? extends Number> entry : source_voltages.entrySet())
        {
            V v = entry.getKey();
            if (sinks.contains(v))
                throw new IllegalArgumentException("Vertex " + v + " is incorrectly specified as both source and sink");
            double value = entry.getValue().doubleValue();
            if (value <= 0)
                throw new IllegalArgumentException("Source vertex " + v + " has negative voltage");
//            setVertexScore(v, value);
        }
        
        
    }
    
    /**
     * @see edu.uci.ics.jung.algorithms.scoring.AbstractIterativeScorer#step()
     */
    public void step()
    {
    }

}
