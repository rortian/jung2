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
 * Created on Jun 13, 2003
 *
 */
package edu.uci.ics.jung.visualization.decorators;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.graph.Edge;

/**
 * 
 * A EdgeWeightLabeller applies a label to the edges of a Graph. 
 * All edge weights are integers; weights need not be unique.
 * (The cost of not being unique is that there's no way to
 * look up edges by weight.)
 * 
 * Note that this stores information with the graph, and
 * as such is not flexible to addition and substraction of
 * nodes.
 * 
 * @author danyelf
 * 
 * TODO : Should store weight in a decorator, per-Edge instead of 
 * per-Graph.
 *
 */
public class EdgeWeightLabeller<E extends Edge> implements NumberEdgeValue<E> {

	/**
	 * Gets the weight of a particualr edge. Throws an exception if
	 * the edge is not weighted, or if the edge is not a part of
	 * the graph. 
	 * @param e	an edge that has been weighted.
	 */
	public int getWeight(E e ) {	
		if (! edgeToWeight.containsKey( e )) {
			throw new IllegalArgumentException("This edge has no assigned weight");
		}
		return ((Number) edgeToWeight.get( e )).intValue();
	}

	/**
	 * Sets an edge to this weight.
	 * @param e	the edge
	 * @param i the weight
	 * @throws if the edge is not part of the graph 
	 */
	public void setWeight(E e, int i) {
			edgeToWeight.put( e, new Integer( i ));
	}
	
    /**
     * Removes the weight stored by this decorator for the indicated edge <code>e</code>,
     * and returns the value of this weight (or <code>null</code> if there was no
     * such weight for this edge).
     */
    public Number removeWeight(E e)
    {
        return edgeToWeight.remove(e);
    }

    /**
     * Clears all weights stored by this decorator.
     */
    public void clear()
    {
        edgeToWeight.clear();
    }
    
	private Map<E, Number> edgeToWeight = new HashMap<E, Number>();


    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#getNumber(edu.uci.ics.jung.graph.ArchetypeEdge)
     */
    public Number getNumber(E e)
    {
        return edgeToWeight.get(e);
    }

    /**
     * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#setNumber(edu.uci.ics.jung.graph.ArchetypeEdge, java.lang.Number)
     */
    public void setNumber(E e, Number n)
    {
        edgeToWeight.put( e, n);
    }
}