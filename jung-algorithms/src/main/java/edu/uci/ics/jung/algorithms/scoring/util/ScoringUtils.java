/*
 * Created on Jul 12, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Methods for assigning transition probabilities to vertices in the context
 * of random-walk-based scoring algorithms.
 */
public class ScoringUtils
{
    public static <V> Transformer<V, Number> getUniformRootPrior(Collection<V> roots)
    {
        final Collection<V> inner_roots = roots;
        Transformer<V, Number> distribution = new Transformer<V, Number>()
        {
            public Number transform(V input)
            {
                if (inner_roots.contains(input))
                    return new Double(1.0 / inner_roots.size());
                else
                    return 0.0;
            }
        };
        
        return distribution;
    }
    
    public static <V> Transformer<V, Pair<Number>> getTwoValueUniformRootPrior(Collection<V> roots)
    {
        final Collection<V> inner_roots = roots;
        Transformer<V, Pair<Number>> distribution = new Transformer<V, Pair<Number>>()
        {
            public Pair<Number> transform(V input)
            {
                if (inner_roots.contains(input))
                    return new Pair<Number>(1.0 / inner_roots.size(), 1.0 / inner_roots.size());
                else
                    return new Pair<Number>(0.0, 0.0);
            }
        };
        
        return distribution;
    }

}
