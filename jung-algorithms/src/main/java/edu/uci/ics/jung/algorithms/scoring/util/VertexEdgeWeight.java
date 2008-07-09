/*
 * Created on Jul 8, 2007
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

/**
 * Convenience interface for edge weight functions that
 * assign edge weights with respect to a specified 'source' vertex.
 * Useful (for example) in the context of random-walk-based scoring 
 * algorithms on graphs with undirected edges, in which
 * an edge's weight may depend on the direction in which it is being
 * traversed.
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <W> the weight type
 */
public interface VertexEdgeWeight<V,E,W extends Number> 
	extends EdgeWeight<VEPair<V,E>,W> {}
