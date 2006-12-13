/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms;

import java.util.Collection;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

/**
 * A class providing static methods useful for improving the
 * performance of graph algorithms.
 * 
 * @author Tom Nelson
 *
 */
public class Indexer {
	
	/**
	 * Returns a <code>BidiMap</code> mapping each element of the collection to its
	 * index as encountered while iterating over the collection. The purpose
	 * of the index operation is to supply an O(1) replacement operation for the
	 * O(n) <code>indexOf(element)</code> method of a <code>List</code>
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T> BidiMap<T,Integer> create(Collection<T> collection) {
		BidiMap<T,Integer> map = new DualHashBidiMap<T,Integer>();
		int i=0;
		for(T t : collection) {
			map.put(t,i++);
		}
		return map;
	}
}
