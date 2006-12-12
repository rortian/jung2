package edu.uci.ics.jung.algorithms;

import java.util.Collection;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

public class Indexer {
	
	public static <T> BidiMap<T,Integer> create(Collection<T> collection) {
		BidiMap<T,Integer> map = new DualHashBidiMap<T,Integer>();
		int i=0;
		for(T t : collection) {
			map.put(t,i++);
		}
		return map;
	}
}
