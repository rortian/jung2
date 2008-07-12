/*
 * Created on Jul 11, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * 
 * 
 * @author Joshua O'Madadhain
 */
public class UniformInOut<V,E> implements Transformer<E, Pair<Number>>
{
    protected Graph<V,E> graph;
    
    public UniformInOut(Graph<V,E> graph)
    {
        this.graph = graph;
    }
    
    public Pair<Number> transform(E edge)
    {
        return new Pair<Number>(1.0 / graph.outDegree(graph.getSource(edge)),
                                1.0 / graph.inDegree(graph.getDest(edge)));
    }

}
