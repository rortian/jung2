/*
 * Copyright (c) 2008, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */

package edu.uci.ics.jung.io.graphml.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.GraphMetadata.EdgeDefault;

public class TestGraphElementParser extends AbstractParserTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test(expected= GraphIOException.class)
    public void testNoEdgeDefault() throws Exception {
        
        String xml = 
            "<graph/>";
        
        readObject(xml);
    }

    @Test
    public void testEdgeDefaultDirected() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"directed\"/>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.DIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(0, g.getNodes().size());
        Assert.assertEquals(0, g.getEdges().size());
        Assert.assertEquals(0, g.getHyperEdges().size());
    }

    @Test
    public void testEdgeDefaultUndirected() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\"/>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(0, g.getNodes().size());
        Assert.assertEquals(0, g.getEdges().size());
        Assert.assertEquals(0, g.getHyperEdges().size());
    }

    @Test
    public void testDesc() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\">" +
                "<desc>hello world</desc>" +
            "</graph>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals("hello world", g.getDescription());
        Assert.assertEquals(0, g.getNodes().size());
        Assert.assertEquals(0, g.getEdges().size());
        Assert.assertEquals(0, g.getHyperEdges().size());
    }

    @Test
    public void testNodes() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\">" +
                "<node id=\"1\"/>" +
                "<node id=\"2\"/>" +
                "<node id=\"3\"/>" +
            "</graph>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(3, g.getNodes().size());
        Assert.assertEquals("1", g.getNodes().get(0).getId());
        Assert.assertEquals("2", g.getNodes().get(1).getId());
        Assert.assertEquals("3", g.getNodes().get(2).getId());
    }

    @Test
    public void testEdges() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\">" +
                "<edge source=\"1\" target=\"2\"/>" +
                "<edge source=\"2\" target=\"3\"/>" +
            "</graph>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(2, g.getEdges().size());
        Assert.assertEquals("1", g.getEdges().get(0).getSource());
        Assert.assertEquals("2", g.getEdges().get(1).getSource());
    }

    @Test
    public void testHyperEdges() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\">" +
                "<hyperedge/>" +
                "<hyperedge/>" +
                "<hyperedge/>" +
            "</graph>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(3, g.getHyperEdges().size());
    }

    @Test
    public void testUserAttributes() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\" bob=\"abc123\">" +
            "</graph>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(1, g.getProperties().size());
        Assert.assertEquals("abc123", g.getProperty("bob"));
    }

    @Test
    public void testData() throws Exception {
        
        String xml = 
            "<graph edgedefault=\"undirected\">" +
                "<data key=\"d1\">value1</data>" +
                "<data key=\"d2\">value2</data>" +
            "</graph>";
        
        GraphMetadata g = (GraphMetadata) readObject(xml);
        Assert.assertNotNull(g);
        Assert.assertEquals(EdgeDefault.UNDIRECTED, g.getEdgeDefault());
        Assert.assertEquals(null, g.getId());
        Assert.assertEquals(null, g.getDescription());
        Assert.assertEquals(2, g.getProperties().size());
        Assert.assertEquals("value1", g.getProperty("d1"));
        Assert.assertEquals("value2", g.getProperty("d2"));
    }
}
