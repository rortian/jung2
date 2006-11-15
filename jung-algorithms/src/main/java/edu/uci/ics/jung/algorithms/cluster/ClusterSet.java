/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.graph.Graph;


/**
 * A data structure representing the clusters, connected set of vertices (or edges), in a graph. The clusters can be
 * retrieved based upon their position index in the collection. Also, given a vertex (or edge) the corresponding
 * clusters can be retrieved. There is no requirement that the union of the set of vertices (or edges) in each
 * cluster needs to equal the set of all vertices in the graph.
 * @author Scott White
 */
public abstract class ClusterSet<V,E,T> {
    private List<Set<T>> mClusters;
    private Map<T,Set<T>> mUDCToClustersMap;
    private Graph<V,E> mUnderlyingGraph;

    /**
     * Creates a new instance.
     */
    public ClusterSet(Graph<V,E> underlyingGraph) {
        mClusters = new ArrayList<Set<T>>();
        mUDCToClustersMap = new HashMap<T,Set<T>>();
        mUnderlyingGraph = underlyingGraph;
    }

    /**
     * Adds a new cluster to the collection.
     * @param elements the set of vertices (or edges) comprising a component to be added
     */
    public void addCluster(Set<T> elements) {
        if (elements == null || elements.size() == 0) {
            throw new IllegalArgumentException("The set of elements must have at least one element");
        }

        for (Iterator<T> udcIt=elements.iterator();udcIt.hasNext();) {
            T udc = udcIt.next();

            checkLegality(udc);

            Set<T> components = mUDCToClustersMap.get(udc);
            if (components == null) {
                components = new HashSet<T>();
                mUDCToClustersMap.put(udc,components);
            }

            components.addAll(elements);
        }
        mClusters.add(elements);

    }

    protected void checkLegality(Object e) 
    {
        if (getUnderlyingGraph().getVertices().contains(e) == false &&
                getUnderlyingGraph().getEdges().contains(e) == false) {
            throw new IllegalArgumentException("All elements passed in must be from the correct underlying graph.");
        }
    }

    /**
     * Constructs a new graph from the given cluster
     * @param index the position index of the cluster in the collection
     * @return a new graph representing the cluster
     */
    abstract public Graph<V,E> getClusterAsNewSubGraph(int index);

    /**
     * Returns the corresponding cluster set in the other graph. If any of the vertices (or edges) in the specified
     * graph are not equivalent to the corresponding vertices (or edges) in this graph then an IllegalArgumentException
     * is thrown.
     * @param anotherGraph another graph whose corresponding clusters are to be retrieved
     * @return the set of clsuters for the new graph
     */
    abstract public ClusterSet createEquivalentClusterSet(Graph<V,E> anotherGraph);

    /**
     * Given a vertex (or edge), retrieves the clusters which that vertex (or edge) belongs to if any
     * @param element the vertex (or edge) whose cluster is to be retrieved.
     * @return the set of clusters (set of non-overlapping vertices (or edges))
     */
    public Set getClusters(Object element) {
        return (Set) mUDCToClustersMap.get(element);
    }

    /**
     * Given the cluster's position in the list (0-based), retrieve the cluster (set of vertices)
     * @param index the 0-based index of the cluster in the list.
     * @return the set of vertices (or edges) comprising the cluster
     */
    public Set<V> getCluster(int index) {
        return (Set) mClusters.get(index);
    }

    /**
     * Returns an iterator to the component list.
     * @return the iterator to the component list
     */
    public Iterator<Set<T>> iterator() {
        return mClusters.iterator();
    }

    /**
     * the size of the cluster collection.
     * @return the number of clusters in the collection
     */
    public int size() {
        return mClusters.size();
    }

    /**
     * Sorts the clusters by size.
     */
    public void sort() {
        Collections.sort(mClusters, new Comparator() {
            public int compare(Object o1, Object o2) {
                Set cluster1 = (Set) o1;
                Set cluster2 = (Set) o2;
                if (cluster1.size() < cluster2.size()) return 1;
                if (cluster1.size() > cluster2.size()) return -1;
                return 0;
            }
        });

    }

    public Graph<V,E> getUnderlyingGraph() {
        return mUnderlyingGraph;
    }
}
