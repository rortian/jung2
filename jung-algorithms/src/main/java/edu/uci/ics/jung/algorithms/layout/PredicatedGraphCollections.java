/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Apr 12, 2005
 */
package edu.uci.ics.jung.algorithms.layout;

import java.util.Collection;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.graph.util.EdgeContext;
import edu.uci.ics.graph.util.VertexContext;



/**
 * Interface for coordinate-based selection of filtered graph components.
 * @author Tom Nelson
 */
public interface PredicatedGraphCollections<V, E>  {

    Collection<V> getFilteredVertices(Layout<V,E> layout);
    
    Collection<E> getFilteredEdges(Layout<V,E> layout);
    
    void setVertexIncludePredicate(Predicate<VertexContext<V,E>> vertexPredicate);
    
    void setEdgeIncludePredicate(Predicate<EdgeContext<V,E>> context);

}