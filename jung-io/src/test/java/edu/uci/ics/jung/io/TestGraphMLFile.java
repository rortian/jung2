/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections15.Factory;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLFile;

/**
 * @author Scott White
 * @author Tom Nelson - converted to jung2
 */
public class TestGraphMLFile extends TestCase {
	
	Factory<Graph<Number,Number>> graphFactory;
	Factory<Number> vertexFactory;
	Factory<Number> edgeFactory;
    GraphMLFile<Number,Number> graphmlFile;

    public static Test suite() {
        return new TestSuite(TestGraphMLFile.class);
    }

    protected void setUp() {
    	graphFactory = new Factory<Graph<Number,Number>>() {
    		public Graph<Number,Number> create() {
    			return new SimpleDirectedSparseGraph<Number,Number>();
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
    	graphmlFile = new GraphMLFile<Number,Number>(graphFactory,vertexFactory,edgeFactory);
    }

    public void testLoad() {
        String testFilename = "toy_graph.ml";

        Graph<Number,Number> graph = loadGraph(testFilename);

        Assert.assertEquals(graph.getVertexCount(),3);
        Assert.assertEquals(graph.getEdgeCount(),3);

        GraphMLFileHandler<Number,Number> handler = graphmlFile.getMFileHandler();
        Map<String,Number> labeller = handler.getLabeller();
        	//StringLabeller.getLabeller(graph);

        Number joe = labeller.get("1");
        Number bob = labeller.get("2");
        Number sue = labeller.get("3");

        Assert.assertEquals(handler.getVertexAttributes().get(joe).get("name"),"Joe");
        Assert.assertEquals(handler.getVertexAttributes().get(bob).get("name"),"Bob");
        Assert.assertEquals(handler.getVertexAttributes().get(sue).get("name"),"Sue");

        Assert.assertTrue(graph.isPredecessor(joe, bob));
        Assert.assertTrue(graph.isPredecessor(bob, joe));
        Assert.assertTrue(graph.isPredecessor(sue, joe));
        Assert.assertFalse(graph.isPredecessor(joe, sue));
        Assert.assertFalse(graph.isPredecessor(sue, bob));
        Assert.assertFalse(graph.isPredecessor(bob, sue));


        File testFile = new File(testFilename);
        testFile.delete();
    }

    private Graph<Number,Number> loadGraph(String testFilename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFilename));
            writer.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
             writer.write("<?meta name=\"GENERATOR\" content=\"XML::Smart 1.3.1\" ?>\n");
            writer.write("<graph edgedefault=\"directed\">\n");
            writer.write("<node id=\"1\" name=\"Joe\"/>\n");
            writer.write("<node id=\"2\" name=\"Bob\"/>\n");
            writer.write("<node id=\"3\" name=\"Sue\"/>\n");
            writer.write("<edge source=\"1\" target=\"2\"/>\n");
            writer.write("<edge source=\"2\" target=\"1\"/>\n");
            writer.write("<edge source=\"1\" target=\"3\"/>\n");
             writer.write("</graph>\n");
            writer.close();

        } catch (IOException ioe) {

        }

//        GraphMLFile graphmlFile = new GraphMLFile<Numb>();
        Graph<Number,Number> graph = graphmlFile.load(testFilename);
        return graph;
    }

    public void testSave() {
        String testFilename = "toy_graph.ml";
        Graph<Number,Number> oldGraph = loadGraph(testFilename);
//        GraphMLFile<Number,Number> graphmlFile = new GraphMLFile();
        String newFilename = testFilename + "_save";
        graphmlFile.save(oldGraph,newFilename);
		Graph<Number,Number> newGraph = graphmlFile.load(newFilename);
        Assert.assertEquals(oldGraph.getVertexCount(),newGraph.getVertexCount());
        Assert.assertEquals(oldGraph.getEdgeCount(),newGraph.getEdgeCount());
        File testFile = new File(testFilename);
        testFile.delete();
        File newFile = new File(newFilename);
        newFile.delete();


    }
}
