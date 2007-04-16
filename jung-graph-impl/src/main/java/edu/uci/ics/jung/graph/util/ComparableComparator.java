/*
 * Created on Apr 15, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph.util;

import java.util.Comparator;

public class ComparableComparator<T> implements Comparator<T>
{
    public int compare(T o1, T o2)
    {
        return ((Comparable)o1).compareTo((Comparable)o2);
    }
}
