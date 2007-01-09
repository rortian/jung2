/*
 * Created on Oct 17, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.graph;

public interface DirectedGraph<V,E> extends Graph<V,E>
{
    V getSource(E directed_edge);

    V getDest(E directed_edge);
    
    boolean isSource(V vertex, E edge); // get{Source, Dest}(e) == v
    
    boolean isDest(V vertex, E edge); // get{Source, Dest}(e) == v

}
