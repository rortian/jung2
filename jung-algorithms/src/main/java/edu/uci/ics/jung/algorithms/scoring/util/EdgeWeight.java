/*
 * Created on Jul 12, 2007
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

import org.apache.commons.collections15.Transformer;

/**
 * An interface for classes that assign numeric values to edges.
 *
 * @param <E> the edge type
 * @param <W> the weight type
 */
public interface EdgeWeight<E, W extends Number> extends Transformer<E, W>
{

}
