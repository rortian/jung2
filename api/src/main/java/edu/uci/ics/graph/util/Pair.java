/*
 * Created on Apr 2, 2006
 *
 * Copyright (c) 2006, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.graph.util;


/**
* Stores a pair of values together. Access either one by directly
* getting the fields. Pairs are not mutable, respect <tt>equals</tt>
* and may be used as indices.<p>
* Note that they do not protect from malevolent behavior: if one or another
* object in the tuple is mutable, then that can be changed with the usual bad
* effects.
* 
* @author scott white and Danyel Fisher
*/

public final class Pair<T>
{
    private final T value1;
    private final T value2;

    public Pair(T value1, T value2) 
    {
        if ( value1 == null  || value2 == null)
            throw new IllegalArgumentException("Both inputs must be non-null");
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * Returns the first constructor argument.
     */
    public T getFirst() 
    {
        return value1;
    }
    
    /**
     * Returns the second constructor argument.
     */
    public T getSecond() 
    {
        return value2;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o ) {
        if (o instanceof Pair) 
        {
            Pair tt = (Pair) o;
            Object first = tt.getFirst();
            Object second = tt.getSecond();
            return ((first == value1 || first.equals(value1)) &&
                    (second == value2 || second.equals(value2)));
        }
        return false;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() 
    {
        return value1.hashCode() + value2.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "<" + value1.toString() + ", " + value2.toString() + ">";
    }
}


