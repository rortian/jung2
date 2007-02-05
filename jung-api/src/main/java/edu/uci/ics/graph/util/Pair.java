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

import java.util.Collection;
import java.util.Iterator;


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
//    private List<T> list = new ArrayList<T>(2);
//    private final List<T> delegate;
    private T first;
    private T second;

    public Pair(T value1, T value2) 
    {
        assert value1 != null && value2 != null : "Both inputs must be non-null";
        first = value1;
        second = value2;
//        list.add(value1);
//        list.add(value2);
//        delegate = Collections.unmodifiableList(list);
    }
    
    /**
     * create a Pair from the passed Collection.
     * The size of the Collection must be 1 or 2.
     * @param values
     */
    public Pair(Collection<T> values) 
    {
        if (values.size() == 2)
        {
            Iterator<T> iter = values.iterator();
            first = iter.next();
            second = iter.next();
        }
        else
            throw new IllegalArgumentException("Pair may only be created from a Collection of 2 elements");
        
//    	if(values.size() == 1 || values.size() == 2) {
//    		list.addAll(values);
//    		delegate = Collections.unmodifiableList(list);
//    	} 
//        else {
//    		throw new IllegalArgumentException("Pair may only be created from a Collection of 1 or 2 elements");
//    	}
    }

    /**
     * Returns the first constructor argument.
     */
    public T getFirst() 
    {
//        return delegate.get(0);
        return first;
    }
    
    /**
     * Returns the second constructor argument.
     */
    public T getSecond() 
    {
//        return delegate.get(1);
        return second;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o ) {
        if (o == this)
            return true;

        if (o instanceof Pair) {
            Pair tt = (Pair) o;
            Object first = tt.getFirst();
            Object second = tt.getSecond();
            return ((first == this.first || first.equals(this.first)) &&
                    (second == this.second || second.equals(this.second)));
        } else {
            return false;
        }

        
        // FIXME: should we allow other types of Collections?
//        if (!(o instanceof Pair))
//            return false;

        
//        Iterator<T> e1 = delegate.iterator();
//        Iterator e2 = ((Pair) o).iterator();
//        while(e1.hasNext() && e2.hasNext()) {
//            T o1 = e1.next();
//            Object o2 = e2.next();
//            if (!(o1==null ? o2==null : o1.equals(o2)))
//                return false;
//        }
//        return !(e1.hasNext() || e2.hasNext());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() 
    {
        // FIXME: do we need something more robust here?
        return first.hashCode() + second.hashCode();
//        return delegate.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "<" + first.toString() + ", " + second.toString() + ">";
//        return delegate.toString();
    }

    public boolean add(T o) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
//        return delegate.add(o);
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
//        return delegate.addAll(c);
    }

    public void clear() {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
//        delegate.clear();
    }

    public boolean contains(Object o) {
        return (first == o || first.equals(o) || second == o || second.equals(o));
//        return delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        if (c.size() > 2)
            return false;
        Iterator iter = c.iterator();
        Object c_first = iter.next();
        Object c_second = iter.next();
        return this.contains(c_first) && this.contains(c_second);
//        return delegate.containsAll(c);
    }

    public boolean isEmpty() {
        return false;
//        return delegate.isEmpty();
    }

    public Iterator<T> iterator() {
        return new PairIterator();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
//        return delegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
//        return delegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
//        return delegate.retainAll(c);
    }

    public int size() {
        return 2;
//        return delegate.size();
    }

    public Object[] toArray() {
        Object[] to_return = new Object[2];
        to_return[0] = first;
        to_return[1] = second;
        return to_return;
        
//        return delegate.toArray();
    }

    public <S> S[] toArray(S[] a) {
        S[] to_return = a;
        Class<?> type = a.getClass().getComponentType();
//        if (!(T instanceof S))
//            throw new ArrayStoreException("input type is not a supertype of this Pair's type");
        if (a.length < 2)
            to_return = (S[])java.lang.reflect.Array.newInstance(type, 2);
        to_return[0] = (S)first;
        to_return[1] = (S)second;
        
        if (to_return.length > 2)
            to_return[2] = null;
        return to_return;
//        return delegate.<S>toArray(a);
    }
    
    private class PairIterator implements Iterator
    {
        int position;
        
        private PairIterator()
        {
            position = 0;
        }

        public boolean hasNext()
        {
            return position < 2;
        }

        public Object next()
        {
            position++;
            if (position == 1)
                return first;
            else if (position == 2)
                return second;
            else
                return null;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Pairs cannot be mutated");
        }
    }
}


