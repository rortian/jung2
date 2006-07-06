/*
 * Created on Jul 21, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.layout;

import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;




public class StaticLayout<V, E extends Edge<V>> extends AbstractLayout<V,E>
{
    public StaticLayout(Graph<V,E> g)
    {
        super(g);
    }
    
    protected void initialize_local() {}

    protected void initialize_local_vertex(V v) {}

    public void advancePositions() {}

    public boolean isIncremental()
    {
        return false;
    }

    public boolean incrementsAreDone()
    {
        return true;
    }

}
