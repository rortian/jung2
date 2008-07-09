/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Aug 11, 2004
 *
 */
package edu.uci.ics.jung.algorithms.importance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.util.ConstantMap;
import edu.uci.ics.jung.graph.Graph;


/**
 * Ranks vertices in a graph according to their 'voltage' in an approximate 
 * solution to the Kirchoff equations.  This is accomplished by tying "source"
 * vertices to specified positive voltages, "sink" vertices to 0 V, and 
 * iteratively updating the voltage of each other vertex to the (weighted) 
 * average of the voltages of its neighbors.
 * 
 * <p>The resultant voltages will all be in the range <code>[0, max]</code>
 * where <code>max</code> is the largest voltage of any source vertex (in the
 * absence of negative source voltages; see below).
 * 
 * <p>A few notes about this algorithm's interpretation of the graph data: 
 * <ul>
 * <li/>Higher edge weights are interpreted as indicative of greater 
 * influence/effect than lower edge weights.  
 * <li/>Negative edge weights (and negative "source" voltages) invalidate
 * the interpretation of the resultant values as voltages.  However, this 
 * algorithm will not reject graphs with negative edge weights or source voltages.
 * <li/>Parallel edges are equivalent to a single edge whose weight is the 
 * sum of the weights on the parallel edges.
 * <li/>Current flows along undirected edges in both directions, 
 * but only flows along directed edges in the direction of the edge.
 * </ul>
 * </p> 
 * 
 * @author Joshua O'Madadhain, Tom Nelson
 */
public class VoltageRanker<V, E>
{
    protected Map<E,Number> edge_weights;
    protected Map<V,Number> voltages;
    protected int max_iterations;
    protected double convergence_threshold;
    
    /**
     * Creates an instance of <code>VoltageRanker</code> which uses the 
     * edge weights specified by <code>edge_weights</code>, and which stores
     * the voltages (ranks) as specified by <code>voltages</code>.
     */
    public VoltageRanker(Map<E,Number> edge_weights, Map<V,Number> voltages,
        int num_iterations, double convergence_threshold) {
    	
        assert num_iterations > 0 : "num_iterations must be > 0";
        assert convergence_threshold >= 0 : "convergence_threshold must be >= 0";

        this.edge_weights = edge_weights;
        this.voltages = voltages;
        this.max_iterations = num_iterations;
        this.convergence_threshold = convergence_threshold;
    }

    /**
     * Creates an instance of <code>VoltageRanker</code> which treats the
     * edges as though they were unweighted, and which stores
     * the voltages (ranks) as specified by <code>voltages</code>.
     */
    public VoltageRanker(Map<V,Number> voltages, int num_iterations, 
        double threshold) {
        this(new ConstantMap<E, Number>(new Integer(1)), voltages, num_iterations, threshold);
    }
    
    /**
     * Calculates the voltages for <code>g</code> based on assigning each of the 
     * vertices in <code>source</code> a voltage of 1 V.
     * @param sources   vertices tied to 1 V
     * @param sinks     vertices tied to 0 V
     * @see #calculateVoltages(Graph, Map, Set)
     */
    public void calculateVoltages(Graph<V,E> g, Set<V> sources, Set<V> sinks) {
        if(sources == null || sources.isEmpty() || 
            sinks == null || sinks.isEmpty())
            throw new IllegalArgumentException("at least one source and one " +
                    "sink must exist");

        if (sources.size() + sinks.size() > g.getVertexCount())
            throw new IllegalArgumentException("either sources and sinks overlap " + 
                "or sources and sinks contain vertices not in g");
        
        Map<V,Number> unit_sources = new HashMap<V,Number>();
        for(V v : sources) {
        	unit_sources.put(v, new Double(1.0));
        }
        
        calculateVoltages(g, unit_sources, sinks);
    }
    
    /**
     * Calculates the voltages for <code>g</code> based on the specified source
     * and sink vertex sets.
     * 
     * @param g                 the graph for which voltages will be calculated
     * @param source_voltages   a map from vertices to source voltage values
     * @param sinks             a set of vertices to tie to 0 V
     */
    public void calculateVoltages(Graph<V,E> g, Map<V,Number> source_voltages, Set<V> sinks) {
        if (source_voltages == null || source_voltages.isEmpty() || 
            sinks == null || sinks.isEmpty())
            throw new IllegalArgumentException("at least one source and one " +
                "sink must exist");
        
        if (source_voltages.size() + sinks.size() > g.getVertexCount())
            throw new IllegalArgumentException("either sources and sinks overlap " + 
                "or sources and sinks contain vertices not in g");

        Set<V> sources = source_voltages.keySet();

        Collection<V> vertices = g.getVertices();
        List<V> list = new ArrayList<V>(vertices);
        double[] volt_array = new double[vertices.size()];
        for (int i = 0; i < volt_array.length; i++) {
            V v = list.get(i);
            if (sources.contains(v)) {
                Number voltage = source_voltages.get(v);
                volt_array[i] = voltage.doubleValue();
                voltages.put(v, voltage);
            } else {
                volt_array[i] = 0;
                voltages.put(v, new Double(0));
            }
        }
        
        // update voltages of each vertex to the (weighted) average of its 
        // neighbors, until either (a) the number of iterations exceeds the
        // maximum number of iterations specified, or (b) the largest change of
        // any voltage is no greater than the specified convergence threshold. 
        int iteration = 0;
        double max_change = Double.POSITIVE_INFINITY;
        while (iteration++ < max_iterations && max_change > convergence_threshold) {
            max_change = 0;
            for(V v : vertices) {
                if (sources.contains(v) || sinks.contains(v))
                    continue;
                Collection<E> edges = g.getInEdges(v);
                double voltage_sum = 0;
                double weight_sum = 0;
                for(E e : edges) {
                	V w = g.getOpposite(v, e);
                    double weight = edge_weights.get(e).doubleValue();
                    voltage_sum += volt_array[list.indexOf(w)] * weight;
                    weight_sum += weight;
                }
                
                double new_voltage;
                if (voltage_sum == 0 && weight_sum == 0) {
                    new_voltage = 0;
                } else {
                    new_voltage = voltage_sum / weight_sum;
                }
                max_change = Math.max(max_change, 
                    Math.abs(voltages.get(v).doubleValue() - new_voltage));
                voltages.put(v, new Double(new_voltage));
            }
            
            // set up volt_array for next iteration
            for (int j = 0; j < volt_array.length; j++) {
                volt_array[j] = voltages.get(list.get(j)).doubleValue();
            }
        }
    }
        
    
    /**
     * Calculates an approximation of the solution of the Kirchhoff equations
     * for voltage, given that <code>source</code> supplies 1 V and <code>target</code>
     * is tied to ground (O V).  Each other vertex will be assigned a voltage (rank) 
     * in the range [0,1].
     * 
     * @param source            the vertex whose voltage is tied to 1 V
     * @param target            the vertex whose voltage is tied to 0 V
     */
    public void calculateVoltages(Graph<V,E> g, V source, V target) {
        Set<V> sources = Collections.singleton(source);
        Set<V> sinks = Collections.singleton(target);
        calculateVoltages(g, sources, sinks);
    }
}
