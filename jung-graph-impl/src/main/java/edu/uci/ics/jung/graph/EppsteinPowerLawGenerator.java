/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.uci.ics.graph.Graph;


/**
 * Graph generator that generates undirected sparse graphs with power-law distributions.
 * @author Scott White
 * @see "A Steady State Model for Graph Power Law by David Eppstein and Joseph Wang"
 */
public class EppsteinPowerLawGenerator implements GraphGenerator {
    private int mNumVertices;
    private int mNumEdges;
    private int mNumIterations;
    private double mMaxDegree;
    private Random mRandom;

    /**
     * Constructor which specifies the parameters of the generator
     * @param numVertices the number of vertices for the generated graph
     * @param numEdges the number of edges the generated graph will have, should be Theta(numVertices)
     * @param r the model parameter. The larger the value for this parameter the better the graph's degree
     * distribution will approximate a power-law.
     */
    public EppsteinPowerLawGenerator(int numVertices, int numEdges,int r) {
        mNumVertices = numVertices;
        mNumEdges = numEdges;
        mNumIterations = r;
        mRandom = new Random();
    }

    protected Graph<Number,Number> initializeGraph() {
        Graph<Number,Number> graph = null;
        graph = new SimpleUndirectedSparseGraph<Number,Number>();
        for(int i=0; i<mNumVertices; i++) {
        	graph.addVertex(i);
        }
//        GraphUtils.addVertices(graph,mNumVertices);

//        Indexer id = Indexer.getIndexer(graph);

        while (graph.getEdges().size() < mNumEdges) {
            Number u = (int) (mRandom.nextDouble() * mNumVertices);
            Number v = (int) (mRandom.nextDouble() * mNumVertices);
            if (!graph.isSuccessor(v,u)) {
            	graph.addEdge(graph.getEdges().size(), u, v);
//                GraphUtils.addEdge(graph,u,v);
            }
        }

        double maxDegree = 0;
        for (Number v : graph.getVertices()) {
//            Vertex v = (Vertex) vIt.next();
            maxDegree = Math.max(graph.degree(v),maxDegree);
        }
        mMaxDegree = maxDegree; //(maxDegree+1)*(maxDegree)/2;

        return graph;
    }

    /**
     * Generates a graph whose degree distribution approximates a power-law.
     * @return the generated graph
     */
    public Graph<Number,Number> generateGraph() {
        Graph<Number,Number> graph = initializeGraph();

//        Indexer id = Indexer.getIndexer(graph);
        for (int rIdx = 0; rIdx < mNumIterations; rIdx++) {

            Number v = null;
            int degree = 0;
            do {
                v = (int) (mRandom.nextDouble() * mNumVertices);
                degree = graph.degree(v);

            } while (degree == 0);

            List<Number> edges = new ArrayList<Number>(graph.getIncidentEdges(v));
            Number randomExistingEdge = edges.get((int) (mRandom.nextDouble()*degree));

            Number x = (int) (mRandom.nextDouble() * mNumVertices);
            Number y = null;
            do {
                y = (int) (mRandom.nextDouble() * mNumVertices);

            } while (mRandom.nextDouble() > ((double) (graph.degree(y)+1)/mMaxDegree));

            if (!graph.isSuccessor(y,x) && x != y) {
                graph.removeEdge(randomExistingEdge);
                graph.addEdge(graph.getEdges().size(), x, y);
//                GraphUtils.addEdge(graph,x,y);
            }
        }

        return graph;
    }

    public void setSeed(long seed) {
        mRandom.setSeed(seed);
    }
}
