/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Apr 2, 2005
 */
package edu.uci.ics.jung.visualization.picking;

import edu.uci.ics.jung.visualization.GraphElementAccessor;





/**
 * Interface to adapt coordinate-based selection of graph components
 * to their display component
 * @author Tom Nelson
 */
public interface PickSupport<V, E> extends GraphElementAccessor<V, E> {
//    void setHasGraphLayout(HasGraphLayout<V, E> vv);
}