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
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.util.UniformDegreeWeight;
import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class VoltageScorer<V, E> extends AbstractIterativeScorer<V, E, Double, Number>
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
        this.edge_weights = new UniformDegreeWeight<V,E>(g);
        initialize();
    }
    
    public VoltageScorer(Graph<V,E> g, Transformer<E, ? extends Number> edge_weights, V source, V sink)
    {
        this(g, edge_weights, Collections.singletonMap(source, 1.0), Collections.singletonList(sink));
        initialize();
    }

    public VoltageScorer(Graph<V,E> g, V source, V sink)
    {
        this(g, Collections.singletonMap(source, 1.0), Collections.singletonList(sink));
        initialize();
    }

    
    
    @Override
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
        }
        
        // set up initial voltages
        for (V v : graph.getVertices())
        {
            if (source_voltages.containsKey(v))
                setOutputValue(v, source_voltages.get(v).doubleValue());
            else
                setOutputValue(v, 0.0);
        }
    }
    
    /**
     * @see edu.uci.ics.jung.algorithms.scoring.AbstractIterativeScorer#update(Object)
     */
    @Override
    public double update(V v)
    {
        // if it's a voltage source or sink, we're done
        Number source_volts = source_voltages.get(v);
        if (source_volts != null) 
        {
            setOutputValue(v, source_volts.doubleValue());
            return 0.0;
        }
        if (sinks.contains(v))
        {
            setOutputValue(v, 0.0);
            return 0.0;
        }
        
        Collection<E> edges = graph.getInEdges(v);
        double voltage_sum = 0;
        double weight_sum = 0;
        for (E e: edges)
        {
            V w = graph.getOpposite(v, e);
            double weight = getEdgeWeight(w,e).doubleValue();
            voltage_sum += getCurrentValue(w).doubleValue() * weight;
            weight_sum += weight;
        }

        // if either is 0, new value is 0
        if (voltage_sum == 0 || weight_sum == 0)
        {
            setOutputValue(v, 0.0);
            return getCurrentValue(v).doubleValue();
        }
        
        setOutputValue(v, voltage_sum / weight_sum);
        return Math.abs(getCurrentValue(v).doubleValue() - voltage_sum / weight_sum);
    }

}

