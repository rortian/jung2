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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


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

public final class Pair<T> implements Collection<T> 
{
    private List<T> list = new ArrayList<T>(2);
    private final List<T> delegate;

    public Pair(T value1, T value2) 
    {
        assert value1 != null && value2 != null : "Both inputs must be non-null";
        list.add(value1);
        list.add(value2);
        delegate = Collections.unmodifiableList(list);
    }

    /**
     * Returns the first constructor argument.
     */
    public T getFirst() 
    {
        return delegate.get(0);
    }
    
    /**
     * Returns the second constructor argument.
     */
    public T getSecond() 
    {
        return delegate.get(1);
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o ) {
        if (o == this)
            return true;
        if (!(o instanceof Pair))
            return false;

        Iterator<T> e1 = delegate.iterator();
        Iterator e2 = ((Pair) o).iterator();
        while(e1.hasNext() && e2.hasNext()) {
            T o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() 
    {
        return delegate.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return delegate.toString();
    }

    public boolean add(T o) {
        return delegate.add(o);
    }

    public boolean addAll(Collection<? extends T> c) {
        return delegate.addAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public int size() {
        return delegate.size();
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <S> S[] toArray(S[] a) {
        return delegate.<S>toArray(a);
    }
}


