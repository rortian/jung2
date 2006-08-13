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
 * 
 * Created on Sep 25, 2003
 */
package edu.uci.ics.jung.visualization.decorators;

import edu.uci.ics.graph.Graph;



/**
 * A generalized interface for setting and getting <code>Number</code>s
 * of <code>ArchetypeEdge</code>s.  Using this interface allows 
 * algorithms to work without having to know how edges store this
 * data.
 * 
 * @author Joshua O'Madadhain
 */
public interface NumberDirectionalEdgeValue<V,E>  {
    /**
     * @param e     the edge to examine
     * @return      the Number associated with this edge
     */
    public Number getNumber(Graph<V,E> graph, E e);
    
}
