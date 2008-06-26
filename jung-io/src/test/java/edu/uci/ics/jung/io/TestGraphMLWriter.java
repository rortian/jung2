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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.util.SettableTransformer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.TestGraphs;

public class TestGraphMLWriter extends TestCase
{
    public void testBasicWrite() throws IOException, ParserConfigurationException, SAXException
    {
        Graph<String, Number> g = TestGraphs.createTestGraph(true);
        GraphMLWriter<String, Number> gmlw = new GraphMLWriter<String, Number>();
        Transformer<Number, String> edge_weight = new Transformer<Number, String>() 
		{ 
			public String transform(Number n) 
			{ 
				return String.valueOf(n.intValue()); 
			} 
		};

        gmlw.addEdgeData("weight", "integer value for the edge", -1, edge_weight);
        gmlw.setEdgeIDs(edge_weight);
        gmlw.save(g, new FileWriter("src/test/resources/testbasicwrite.graphml"));
        
        // TODO: now read it back in and compare the graph connectivity 
        // and other metadata with what's in TestGraphs.pairs[], etc.
        Factory<String> vertex_factory = null;
        Factory<Object> edge_factory = FactoryUtils.instantiateFactory(Object.class);
        GraphMLReader<Graph<String, Object>, String, Object> gmlr = 
        	new GraphMLReader<Graph<String, Object>, String, Object>(
        			vertex_factory, edge_factory);
        Graph<String, Object> g2 = new DirectedSparseGraph<String, Object>();
        gmlr.load("src/test/resources/testbasicwrite.graphml", g2);
        Map<String, SettableTransformer<Object, String>> edge_data = 
        	gmlr.getEdgeData();
        Transformer<Object, String> edge_weight2 = edge_data.get("weight");
        Assert.assertEquals(g2.getEdgeCount(), g.getEdgeCount());
        List<String> g_vertices = new ArrayList<String>(g.getVertices());
        List<String> g2_vertices = new ArrayList<String>(g2.getVertices());
        Collections.sort(g_vertices); 
		Collections.sort(g2_vertices);
        Assert.assertEquals(g_vertices, g2_vertices);
        for (String v : g2.getVertices())
        {
        	for (String w : g2.getVertices())
        	{
        		Assert.assertEquals(g.areNeighbors(v, w), 
        				g2.areNeighbors(v, w));
        		Number n = g.findEdge(v, w);
        		Object o = g2.findEdge(v, w);
        		if (n != null)
        			Assert.assertEquals(edge_weight2.transform(o),
        					edge_weight.transform(n));
        	}
        }
        
        // TODO: delete graph file when done
    }
    
    public void testMixedGraph() throws IOException
    {
        Graph<Integer, Number> g = TestGraphs.getSmallGraph();
        GraphMLWriter<Integer, Number> gmlw = new GraphMLWriter<Integer, Number>();
        gmlw.addEdgeData("weight", null, null, 
        		new Transformer<Number, String>() 
        		{ 
        			public String transform(Number n) 
        			{ 
        				return String.valueOf(n.intValue()); 
        			} 
        		});
        gmlw.save(g, new FileWriter("src/test/resources/testmixedgraph.graphml"));
        
        // TODO: now read it back in and compare the graph connectivity 
        // and other metadata with what's in TestGraphs, etc.
        
        // TODO: delete graph file when done
    }

}
