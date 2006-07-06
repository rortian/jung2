/*
 * Created on Jul 16, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.decorators;

import edu.uci.ics.graph.Edge;

/**
 * Returns the specified label for all edges.  Useful for
 * specifying "no label".
 * 
 * @author Joshua O'Madadhain
 */
public class ConstantEdgeStringer<E extends Edge> implements EdgeStringer<E>
{
    protected String label;
    
    public ConstantEdgeStringer(String label) 
    {
        this.label = label;
    }
    
    public String getLabel(E e)
    {
        return null;
    }
}