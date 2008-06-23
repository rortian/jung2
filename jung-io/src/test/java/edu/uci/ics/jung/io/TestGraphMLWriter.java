/*
 * Created on Jun 22, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.TestGraphs;

public class TestGraphMLWriter extends TestCase
{
    public void testBasicWrite() throws IOException
    {
        Graph<String, Number> g = TestGraphs.createTestGraph(true);
        GraphMLWriter<String, Number> gmlw = new GraphMLWriter<String, Number>();
        Map<String, Transformer<Number, String>> edge_data = new HashMap<String, Transformer<Number, String>>();
        edge_data.put("value", new Transformer<Number, String>() { public String transform(Number n) { return String.valueOf(n.intValue()); } });
        gmlw.setEdgeData(edge_data);
        gmlw.save(g, new FileWriter("src/test/resources/testbasicwrite.graphml"));
        
        // TODO: now read it back in and compare the graph connectivity and edge weights with what's in TestGraphs.pairs[]
        
        // TODO: delete graph file when done
    }
    
    public void testMixedGraph() throws IOException
    {
        Graph<Integer, Number> g = TestGraphs.getSmallGraph();
        GraphMLWriter<Integer, Number> gmlw = new GraphMLWriter<Integer, Number>();
        Map<String, Transformer<Number, String>> edge_data = new HashMap<String, Transformer<Number, String>>();
        edge_data.put("value", new Transformer<Number, String>() { public String transform(Number n) { return String.valueOf(n.intValue()); } });
        gmlw.setEdgeData(edge_data);
        gmlw.save(g, new FileWriter("src/test/resources/testmixedgraph.graphml"));
        
        // TODO: now read it back in and compare the graph connectivity and edge weights with what's in TestGraphs.pairs[]
        
        // TODO: delete graph file when done
    }

}
