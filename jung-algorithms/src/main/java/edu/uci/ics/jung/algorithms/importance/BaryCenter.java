/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.importance;

import java.util.Iterator;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;


/**
 * A simple node importance ranker based on the total shortest path of the 
 * node. More central nodes in a connected component will have smaller
 * overall shortest paths, and 'peripheral' nodes on the network will have
 * larger overall shortest paths. Runing this ranker on a graph with more
 * than  one connected component will arbitarily mix nodes from both
 * components. For this reason you should probably run this ranker on one
 * component only (but that goes for all rankers).
 * 
 * <p>
 * A simple example of usage is:
 * <pre>
 * BaryCenter ranker = new BaryCenter(someGraph);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 * 
 * @author Dan Bolser, Scott White
 */
public class BaryCenter<V,E> extends AbstractRanker<V,E> {

    public final static String KEY =
        "edu.uci.ics.jung.algorithms.importance.BaryCenter.RankScore";

    /**
     * Constructor which initializes the algorithm
     * @param g the graph whose nodes are to be analyzed
     */
    public BaryCenter(Graph<V,E> g)
    {
        initialize(g, true, false);
    }

    public void step()
    {
        // Use this class to compute shortest path lengths.
        DijkstraDistance<V,E> p = new DijkstraDistance<V,E>(getGraph());


        for(V u : getVertices()) {        

            double baryCenter = 0;

            Iterator<Number> j = p.getDistanceMap(u).values().iterator();

            while (j.hasNext())
            {
                baryCenter += ((Number) j.next()).doubleValue();
            }
            setVertexRankScore(u, baryCenter);
        }
//        return 0;
    }

    public String getRankScoreKey()
    {
        return KEY;
    }
}
