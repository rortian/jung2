/*
 * Created on May 3, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.io;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedGraph;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;
import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.graph.SimpleUndirectedSparseGraph;


/**
 * Needed tests:
 * - edgeslist, arcslist
 * - unit test to catch bug in readArcsOrEdges() [was skipping until e_pred, not c_pred]
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson - converted to jung2
 */
public class PajekNetIOTest extends TestCase
{
    protected String[] vertex_labels = {"alpha", "beta", "gamma", "delta", "epsilon"};
    
	Factory<DirectedGraph<Number,Number>> directedGraphFactory;
	Factory<UndirectedGraph<Number,Number>> undirectedGraphFactory;
	Factory<Graph<Number,Number>> graphFactory;
	Factory<Number> vertexFactory;
	Factory<Number> edgeFactory;
	
    protected void setUp() {
    	directedGraphFactory = new Factory<DirectedGraph<Number,Number>>() {
    		public DirectedGraph<Number,Number> create() {
    			return new SimpleDirectedSparseGraph<Number,Number>();
    		}
    	};
    	undirectedGraphFactory = new Factory<UndirectedGraph<Number,Number>>() {
    		public UndirectedGraph<Number,Number> create() {
    			return new SimpleUndirectedSparseGraph<Number,Number>();
    		}
    	};
    	graphFactory = new Factory<Graph<Number,Number>>() {
    		public Graph<Number,Number> create() {
    			return new SimpleSparseGraph<Number,Number>();
    		}
    	};
    	vertexFactory = new Factory<Number>() {
    		int n = 0;
    		public Number create() { return n++; }
    	};
    	edgeFactory = new Factory<Number>() {
    		int n = 0;
    		public Number create() { return n++; }
    	};

    }


    public void testFileNotFound()
    {
        PajekNetReader<Number,Number> pnf = new PajekNetReader<Number,Number>();
        pnf.setGraphFactory(directedGraphFactory);
        try
        {
            pnf.load("/dev/null/foo");
            fail("File load did not fail on nonexistent file");
        }
        catch (FileNotFoundException fnfe)
        {
        }
        catch (IOException ioe)
        {
            fail("unexpected IOException");
        }
    }

    public void testNoLabels() throws IOException
    {
        String test = "*Vertices 3\n1\n2\n3\n*Edges\n1 2\n2 2";
        Reader r = new StringReader(test);
        
        PajekNetReader<Number,Number> pnr = new PajekNetReader<Number,Number>();
        pnr.setGraphFactory(undirectedGraphFactory);
        pnr.setVertexFactory(vertexFactory);
        pnr.setEdgeFactory(edgeFactory);
        Graph g = pnr.load(r);
        assertEquals(g.getVertexCount(), 3);
        assertEquals(g.getEdgeCount(), 2);
    }
    
    public void testDirectedSaveLoadSave() throws IOException
    {
        Graph<Number,Number> graph1 = directedGraphFactory.create();
        for(int i=0; i<5; i++) {
        	graph1.addVertex(i);
        }
//        GraphUtils.addVertices(graph1, 5);
        List<Number> id = new ArrayList<Number>(graph1.getVertices());//Indexer.getIndexer(graph1);
        GreekLabels<Number> gl = new GreekLabels<Number>(id);
        int j=0;
        graph1.addEdge(j++, 0, 1);
        graph1.addEdge(j++, 0, 2);
        graph1.addEdge(j++, 1, 2);
        graph1.addEdge(j++, 1, 3);
        graph1.addEdge(j++, 1, 4);
        graph1.addEdge(j++, 4, 3);
        
        
        System.err.println("graph1 = "+graph1);
        for(Number edge : graph1.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph1.getEdgeType(edge));
        }
        for(Number v : graph1.getVertices()) {
        	System.err.println(v+" outedges are "+graph1.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph1.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph1.getIncidentEdges(v));
        }

        assertEquals(graph1.getEdgeCount(), 6);

        String testFilename = "dtest.net";
        String testFilename2 = testFilename + "2";

        PajekNetWriter<Number,Number> pnw = new PajekNetWriter<Number,Number>();
        PajekNetReader<Number,Number> pnr = new PajekNetReader<Number,Number>();
        pnr.setGraphFactory(directedGraphFactory);
        pnr.setVertexFactory(vertexFactory);
        pnr.setEdgeFactory(edgeFactory);
        pnw.save(graph1, testFilename, gl, null, null);

        Graph<Number,Number> graph2 = pnr.load(testFilename);

        System.err.println("graph2 = "+graph2);
        for(Number edge : graph2.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph2.getEdgeType(edge));
        }
        for(Number v : graph2.getVertices()) {
        	System.err.println(v+" outedges are "+graph2.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph2.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph2.getIncidentEdges(v));
       }

        assertEquals(graph1.getVertexCount(), graph2.getVertexCount());
        assertEquals(graph1.getEdgeCount(), graph2.getEdgeCount());

        pnw.save(graph2, testFilename2, new PajekLabels<Number>(pnr.getVertexLabeller()), null, null);

        compareIndexedGraphs(graph1, graph2);

        Graph<Number,Number> graph3 = pnr.load(testFilename2);

        System.err.println("graph3 = "+graph3);
        for(Number edge : graph3.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph3.getEdgeType(edge));
        }
        for(Number v : graph3.getVertices()) {
        	System.err.println(v+" outedges are "+graph3.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph3.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph3.getIncidentEdges(v));
        }

       compareIndexedGraphs(graph2, graph3);

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
    }

    public void testUndirectedSaveLoadSave() throws IOException
    {
        UndirectedGraph<Number,Number> graph1 = 
        	undirectedGraphFactory.create();
        for(int i=0; i<5; i++) {
        	graph1.addVertex(i);
        }

        List<Number> id = new ArrayList<Number>(graph1.getVertices());
        int j=0;
        GreekLabels<Number> gl = new GreekLabels<Number>(id);
        graph1.addEdge(j++, 0, 1);
        graph1.addEdge(j++, 0, 2);
        graph1.addEdge(j++, 1, 2);
        graph1.addEdge(j++, 1, 3);
        graph1.addEdge(j++, 1, 4);
        graph1.addEdge(j++, 4, 3);

        assertEquals(graph1.getEdgeCount(), 6);

        System.err.println("graph1 = "+graph1);
        for(Number edge : graph1.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph1.getEdgeType(edge));
        }
        for(Number v : graph1.getVertices()) {
        	System.err.println(v+" outedges are "+graph1.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph1.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph1.getIncidentEdges(v));
        }

        String testFilename = "utest.net";
        String testFilename2 = testFilename + "2";

        PajekNetWriter<Number,Number> pnw = new PajekNetWriter<Number,Number>();
        PajekNetReader<Number,Number> pnr = new PajekNetReader<Number,Number>();
        pnr.setGraphFactory(undirectedGraphFactory);
        pnr.setVertexFactory(vertexFactory);
        pnr.setEdgeFactory(edgeFactory);
        pnw.save(graph1, testFilename, gl, null, null);

        Graph<Number,Number> graph2 = pnr.load(testFilename);
        
        
        System.err.println("graph2 = "+graph2);
        for(Number edge : graph2.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph2.getEdgeType(edge));
        }
        for(Number v : graph2.getVertices()) {
        	System.err.println(v+" outedges are "+graph2.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph2.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph2.getIncidentEdges(v));
        }


        assertEquals(graph1.getVertexCount(), graph2.getVertexCount());
        assertEquals(graph1.getEdgeCount(), graph2.getEdgeCount());

        pnw.save(graph2, testFilename2, new PajekLabels<Number>(pnr.getVertexLabeller()), null, null);
        compareIndexedGraphs(graph1, graph2);

        Graph<Number,Number> graph3 = pnr.load(testFilename2);
        System.err.println("graph3 = "+graph3);
        for(Number edge : graph3.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph3.getEdgeType(edge));
        }
        for(Number v : graph3.getVertices()) {
        	System.err.println(v+" outedges are "+graph3.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph3.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph3.getIncidentEdges(v));
        }

        compareIndexedGraphs(graph2, graph3);

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
    }

    public void testMixedSaveLoadSave() throws IOException
    {
        Graph<Number,Number> graph1 = new SimpleSparseGraph<Number,Number>();
        for(int i=0; i<5; i++) {
        	graph1.addVertex(i);
        }
        int j=0;

        List<Number> id = new ArrayList<Number>(graph1.getVertices());//Indexer.getIndexer(graph1);
        GreekLabels<Number> gl = new GreekLabels<Number>(id);
        Number[] edges = { 0,1,2,3,4,5 };

        graph1.addEdge(j++, 0, 1, EdgeType.DIRECTED);
        graph1.addEdge(j++, 0, 2, EdgeType.DIRECTED);
        graph1.addEdge(j++, 1, 2, EdgeType.DIRECTED);
        graph1.addEdge(j++, 1, 3);
        graph1.addEdge(j++, 1, 4);
        graph1.addEdge(j++, 4, 3);

        Map<Number,Number> nr = new HashMap<Number,Number>();
        for (int i = 0; i < edges.length; i++)
        {
            nr.put(edges[i], new Float(Math.random()));
        }
        
        assertEquals(graph1.getEdgeCount(), 6);

        System.err.println(" mixed graph1 = "+graph1);
        for(Number edge : graph1.getEdges()) {
        	System.err.println("edge "+edge+" is directed? "+graph1.getEdgeType(edge));
        }
        for(Number v : graph1.getVertices()) {
        	System.err.println(v+" outedges are "+graph1.getOutEdges(v));
        	System.err.println(v+" inedges are "+graph1.getInEdges(v));
        	System.err.println(v+" incidentedges are "+graph1.getIncidentEdges(v));
        }

        String testFilename = "mtest.net";
        String testFilename2 = testFilename + "2";

        // lay out network
        Dimension d = new Dimension(100, 200);
        Transformer<Number,Point2D> vld = 
        	TransformerUtils.mapTransformer(
        			LazyMap.decorate(new HashMap<Number,Point2D>(),
        					new RandomLocationTransformer<Number>(d)));
        
        PajekNetWriter<Number,Number> pnw = new PajekNetWriter<Number,Number>();
        pnw.save(graph1, testFilename, gl, nr, vld);
        
        PajekNetReader<Number,Number> pnr = new PajekNetReader<Number,Number>(false, true);
        pnr.setGraphFactory(graphFactory);
        pnr.setVertexFactory(vertexFactory);
        pnr.setEdgeFactory(edgeFactory);
        Map<Number,Number> nr2 = new HashMap<Number,Number>();
        Graph<Number,Number> graph2 = pnr.load(testFilename, nr2);
        PajekLabels<Number> pl = new PajekLabels<Number>(pnr.getVertexLabeller());
        List<Number> id2 = new ArrayList<Number>(graph2.getVertices());
        	//Indexer.getIndexer(graph2);
        Transformer<Number,Point2D> vld2 = 
        	TransformerUtils.mapTransformer(pnr.getVertexLocationTransformer());
        
        assertEquals(graph1.getVertexCount(), graph2.getVertexCount());
        assertEquals(graph1.getEdgeCount(), graph2.getEdgeCount());
        
        // test vertex labels and locations
        for (int i = 0; i < graph1.getVertexCount(); i++)
        {
            Number v1 = id.get(i);
            Number v2 = id2.get(i);
            assertEquals(gl.transform(v1), pl.transform(v2));
            assertEquals(vld.transform(v1), vld2.transform(v2));
        }
        
        // test edge weights
        for (Number e2 : graph2.getEdges()) 
        {
            Pair<Number> endpoints = graph2.getEndpoints(e2);
            Number v1_2 = endpoints.getFirst();
            Number v2_2 = endpoints.getSecond();
            Number v1_1 = id.get(id2.indexOf(v1_2));
            Number v2_1 = id.get(id2.indexOf(v2_2));
            Number e1 = graph1.findEdge(v1_1, v2_1);
            assertNotNull(e1);
            assertEquals(nr.get(e1).floatValue(), nr2.get(e2).floatValue(), 0.0001);
        }

        pnw.save(graph2, testFilename2, pl, nr2, vld2);

        compareIndexedGraphs(graph1, graph2);

        pnr.setGetLocations(false);
        Graph<Number,Number> graph3 = pnr.load(testFilename2);

        compareIndexedGraphs(graph2, graph3);

        File file1 = new File(testFilename);
        File file2 = new File(testFilename2);

        Assert.assertTrue(file1.length() == file2.length());
        file1.delete();
        file2.delete();
        
    }
    
    /**
     * Tests to see whether these two graphs are structurally equivalent, based
     * on the connectivity of the vertices with matching indices in each graph.
     * Assumes a 0-based index. 
     * 
     * @param g1
     * @param g2
     * @return
     */
    private void compareIndexedGraphs(Graph<Number,Number> g1, Graph<Number,Number> g2)
    {
        int n1 = g1.getVertexCount();
        int n2 = g2.getVertexCount();

        assertEquals(n1, n2);

        assertEquals(g1.getEdgeCount(), g2.getEdgeCount());

        List<Number> id1 = new ArrayList<Number>(g1.getVertices());
        List<Number> id2 = new ArrayList<Number>(g2.getVertices());

        for (int i = 0; i < n1; i++)
        {
            Number v1 = id1.get(i);
            Number v2 = id2.get(i);
            assertNotNull(v1);
            assertNotNull(v2);
            
            checkSets(g1.getPredecessors(v1), g2.getPredecessors(v2), id1, id2);
            checkSets(g1.getSuccessors(v1), g2.getSuccessors(v2), id1, id2);
        }
    }

    private void checkSets(Collection<Number> s1, Collection<Number> s2, List<Number> id1, List<Number> id2)
    {
        for (Number u : s1)
        {
            int j = id1.indexOf(u);
            assertTrue(s2.contains(id2.get(j)));
        }
    }

    private class GreekLabels<V> implements Transformer<V,String>
    {
        private List<V> id; 
        
        public GreekLabels(List<V> id)
        {
            this.id = id;
        }
        
        /**
         * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(ArchetypeVertex)
         */
        public String transform(V v)
        {
            return vertex_labels[id.indexOf(v)];
        }
        
    }
    
    private class PajekLabels<V> implements Transformer<V,String>
    {
    	Map<V,String> labeller;
    	
        public PajekLabels(Map<V, String> labeller) {
			this.labeller = labeller;
		}

		public String transform(V v)
        {
            return labeller.get(v);
        }
    }
    
//    private class NumberReader implements Map<E,Number>
//    {
//        private Map edge_weights;
//        
//        public NumberReader()
//        {
//            edge_weights = new HashMap();
//        }
//        
//        public Number getNumber(E e)
//        {
//            return (Float)edge_weights.get(e);
//        }
//
//        /* (non-Javadoc)
//         * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue#setNumber(edu.uci.ics.jung.graph.ArchetypeEdge, java.lang.Number)
//         */
//        public void setNumber(ArchetypeEdge e, Number n)
//        {
//            edge_weights.put(e, n);
//        }
//    }
    
}
