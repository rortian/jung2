/*
 * Created on Feb 4, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.graph;

import java.util.Collection;

/**
 * Defines the operations available to a hypergraph.
 * 
 * @author Joshua O'Madadhain
 */
public interface Hypergraph<V, H> extends ArchetypeGraph<V, H>
{
    public boolean addHyperedge(H hyperedge, Collection<V> vertices);
}
