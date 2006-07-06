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

import java.text.NumberFormat;

import edu.uci.ics.graph.Edge;

/**
 *
 * An EdgeStringer provides a string Label for any edge: the
 * String is the Weight produced by the EdgeWeightLabeller that
 * it takes as input.
 *
 * @author danyelf
 *
 */
public class EdgeWeightLabellerStringer<E extends Edge> implements EdgeStringer<E> {

    protected EdgeWeightLabeller<E> ewl;
    protected NumberFormat numberFormat;

    public EdgeWeightLabellerStringer( EdgeWeightLabeller<E> ewl ) {
        this.ewl = ewl;
        if (numberFormat == null ) {
            numberFormat = prepareNumberFormat();
        }
    }
    
    protected NumberFormat prepareNumberFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        return nf;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.EdgeStringer#getLabel(ArchetypeEdge)
     */
    public String getLabel(E e) {
        return numberFormat.format(ewl.getWeight(e));
    }
   
}