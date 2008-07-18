/*
 * Created on Jul 6, 2007
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

import org.apache.commons.collections15.Transformer;

/**
 * An interface for algorithms that assign scores to edges.
 *
 * @param <E> the edge type
 * @param <S> the score type
 */
public interface EdgeScorer<E, S>
{
    /**
     * Returns a transformer that can retrieve the algorithm's score for each edge.
     * @return a transformer that can retrieve the algorithm's score for each edge
     */
    public Transformer<E,S> getEdgeScores();
    
    /**
     * Invokes the algorithm that assigns scores to edges.
     */
    public void evaluate();
}
