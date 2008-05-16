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
 * Created on Feb 3, 2004
 */
package edu.uci.ics.jung.algorithms.blockmodel;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;


/**
 * An EquivalenceRelation holds a number of Equivalent vertices from the
 * same graph.
 * 
 * created Feb 3, 2004
 * @author danyelf
 * @author Tom Nelson - converted to jung2
 */
public class EquivalenceRelation<V,E> {

	private Set<Set<V>> equivalenceSets;
	private Graph<V,E> graph;

	/**
	 * Input is the basic data structure underneath: a Set of Sets.
	 * The sets must be mutually exclusive, non-empty,
	 * and contain only vertices from the graph. A reference to the
	 * underlying sets is maintained; be careful not to accidently
	 * modify them after the ER is created.
	 */
	public EquivalenceRelation(Set<Set<V>> rv, Graph<V,E> g) {
		this.equivalenceSets = Collections.unmodifiableSet( rv );
		this.graph = g;
	}

	/**
	 * Returns the common graph to which all the vertices belong
	 */
	public Graph<V,E> getGraph() {
		return graph;
	}

	/**
	 * Returns the set of vertices whose equivalence class consists of a single vertex.
	 * Takes O(n) time by walking through the whole graph and checking all vertices.
	 */
	public Set<V> getSingletonVertices() {
		Set<V> singletons = new HashSet<V>();
		for(Set<V> s : equivalenceSets) {
		    if (s.size() == 1)
		        singletons.add(s.iterator().next());
		}
		return singletons;
	}

	/**
	 * Iterates through all the equivalence sets. Does not return any singletons.
	 * @return an Iterator of Sets of vertices. 
	 */
	public Set<Set<V>> getAllEquivalences() {
		return equivalenceSets;
	}

	/**
	 * Returns the part of the relation that contains this vertex: it is, of course, a Set 
	 * If the vertex does not belong to any relation, null is returned.
	 */
	public Set<V> getEquivalenceRelationContaining(V v) {
		for (Set<V> s : equivalenceSets) {
			if (s.contains(v))
				return s;
		}
		return null;
	}

	/**
	 * Returns the number of relations defined.
	 */
	public int numRelations() {
		return equivalenceSets.size();
	}
	
	public String toString() {
		return "Equivalence: " + equivalenceSets;
	}

}
