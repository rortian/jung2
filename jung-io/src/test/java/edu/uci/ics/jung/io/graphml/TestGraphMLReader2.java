/*
 * Copyright (c) 2008, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */

package edu.uci.ics.jung.io.graphml;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

//import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.junit.Assert;

import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
//import edu.uci.ics.jung.io.GraphMLReader;
import edu.uci.ics.jung.io.GraphIOException;

import org.junit.After;
import org.junit.Test;

public class TestGraphMLReader2 {
    static final String graphMLDocStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">";

    private GraphMLReader2<Hypergraph<MyNode, MyEdge>, MyNode, MyEdge> reader;

    @After
    public void tearDown() throws Exception {
        if (reader != null) {
            reader.close();
        }
        reader = null;
    }

    class MyGraphObject {

        public int myValue;

        public MyGraphObject() {
        }

        public MyGraphObject(int v) {
            myValue = v;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + myValue;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MyGraphObject other = (MyGraphObject) obj;
            return getOuterType().equals(other.getOuterType()) && myValue == other.myValue;
        }

        private TestGraphMLReader2 getOuterType() {
            return TestGraphMLReader2.this;
        }
    }

    class MyNode extends MyGraphObject {
        public MyNode() {
        }

        public MyNode(int v) {
            super(v);
        }
    }

    class MyEdge extends MyGraphObject {
        public MyEdge() {
        }

        public MyEdge(int v) {
            super(v);
        }
    }

    class GraphFactory implements
            Transformer<GraphMetadata, Hypergraph<MyNode, MyEdge>> {

        public Hypergraph<MyNode, MyEdge> transform(GraphMetadata arg0) {
            return new UndirectedSparseGraph<MyNode, MyEdge>();
        }
    }

    class HyperGraphFactory extends GraphFactory {

        public Hypergraph<MyNode, MyEdge> transform(GraphMetadata arg0) {
            return new SetHypergraph<MyNode, MyEdge>();
        }
    }

    class NodeFactory implements Transformer<NodeMetadata, MyNode> {
        int n = 0;

        public MyNode transform(NodeMetadata md) {
            return new MyNode(n++);
        }
    }

    class EdgeFactory implements Transformer<EdgeMetadata, MyEdge> {
        int n = 100;

        public MyEdge transform(EdgeMetadata md) {
            return new MyEdge(n++);
        }
    }

    class HyperEdgeFactory implements Transformer<HyperEdgeMetadata, MyEdge> {
        int n = 0;

        public MyEdge transform(HyperEdgeMetadata md) {
            return new MyEdge(n++);
        }
    }

    @Test(expected = GraphIOException.class)
    public void testEmptyFile() throws Exception {

        String xml = "";
        readGraph(xml, new GraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());
    }

    @Test
    public void testBasics() throws Exception {

        String xml = graphMLDocStart
                + "<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">"
                + "<default>yellow</default>"
                + "</key>"
                + "<key id=\"d1\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>"
                + "<graph id=\"G\" edgedefault=\"undirected\">"
                + "<node id=\"n0\">" + "<data key=\"d0\">green</data>"
                + "</node>" + "<node id=\"n1\"/>" + "<node id=\"n2\">"
                + "<data key=\"d0\">blue</data>" + "</node>"
                + "<edge id=\"e0\" source=\"n0\" target=\"n2\">"
                + "<data key=\"d1\">1.0</data>" + "</edge>" + "</graph>" + "</graphml>";

        // Read the graph object.
        Hypergraph<MyNode, MyEdge> graph = readGraph(xml, new GraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());

        // Check out the graph.
        Assert.assertNotNull(graph);
        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(1, graph.getEdgeCount());
        Assert.assertEquals(0, graph.getEdgeCount(EdgeType.DIRECTED));
        Assert.assertEquals(1, graph.getEdgeCount(EdgeType.UNDIRECTED));

        // Check out metadata.
        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().size());
        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().size());
        Assert.assertEquals("n0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(0).getSource());
        Assert.assertEquals("n2", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(0).getTarget());
    }

    @Test
    public void testData() throws Exception {

        String xml =
                graphMLDocStart +
                        "<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">" +
                        "<default>yellow</default>" +
                        "</key>" +
                        "<key id=\"d1\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>" +
                        "<graph id=\"G\" edgedefault=\"undirected\">" +
                        "<node id=\"n0\">" +
                        "<data key=\"d0\">green</data>" +
                        "</node>" +
                        "<node id=\"n1\"/>" +
                        "<node id=\"n2\">" +
                        "<data key=\"d0\">blue</data>" +
                        "</node>" +
                        "<edge id=\"e0\" source=\"n0\" target=\"n2\">" +
                        "<data key=\"d1\">1.0</data>" +
                        "</edge>" +
                        "</graph>" +
                        "</graphml>";

        // Read the graph object.
        readGraph(xml, new GraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());

        // Check out metadata.
        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().size());
        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().size());
        Assert.assertEquals("1.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(0).getProperties().get("d1"));
        Assert.assertEquals(3, reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().size());
        Assert.assertEquals("green", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(0).getProperties().get("d0"));
        Assert.assertEquals("yellow", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(1).getProperties().get("d0"));
        Assert.assertEquals("blue", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(2).getProperties().get("d0"));
    }

    @Test(expected = GraphIOException.class)
    public void testEdgeWithInvalidNode() throws Exception {

        String xml = graphMLDocStart
                + "<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">"
                + "<default>yellow</default>"
                + "</key>"
                + "<key id=\"d1\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>"
                + "<graph id=\"G\" edgedefault=\"undirected\">"
                + "<node id=\"n0\">" + "<data key=\"d0\">green</data>"
                + "</node>" + "<node id=\"n1\"/>" + "<node id=\"n2\">"
                + "<data key=\"d0\">blue</data>" + "</node>"
                + "<edge id=\"e0\" source=\"n0\" target=\"n3\">" + // Invalid
                // node: n3
                "<data key=\"d1\">1.0</data>" + "</edge>" + "</graphml>";

        readGraph(xml, new GraphFactory(), new NodeFactory(),
                new EdgeFactory(), new HyperEdgeFactory());
    }

    @Test
    public void testHypergraph() throws Exception {

        String xml = graphMLDocStart
                + "<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">"
                + "<default>yellow</default>"
                + "</key>"
                + "<key id=\"d1\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>"
                + "<graph id=\"G\" edgedefault=\"undirected\">"
                + "<node id=\"n0\">" + "<data key=\"d0\">green</data>"
                + "</node>" + "<node id=\"n1\"/>" + "<node id=\"n2\">"
                + "<data key=\"d0\">blue</data>" + "</node>"
                + "<hyperedge id=\"e0\">"
                + "<endpoint node=\"n0\"/>" + "<endpoint node=\"n1\"/>"
                + "<endpoint node=\"n2\"/>" + "</hyperedge>" + "</graph>" + "</graphml>";

        // Read the graph object.
        Hypergraph<MyNode, MyEdge> graph = readGraph(xml, new HyperGraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());

        // Check out the graph.
        Assert.assertNotNull(graph);
        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(1, graph.getEdgeCount());
        Assert.assertEquals(0, graph.getEdgeCount(EdgeType.DIRECTED));
        Assert.assertEquals(1, graph.getEdgeCount(EdgeType.UNDIRECTED));

        // Check out metadata.
        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().size());
        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().get(0).getHyperEdges().size());
        Assert.assertEquals(3, reader.getGraphMLDocument().getGraphMetadata().get(0).getHyperEdges().get(0).getEndpoints().size());
        Assert.assertEquals("n0", reader.getGraphMLDocument().getGraphMetadata().get(0).getHyperEdges().get(0).getEndpoints().get(0).getNode());
        Assert.assertEquals("n1", reader.getGraphMLDocument().getGraphMetadata().get(0).getHyperEdges().get(0).getEndpoints().get(1).getNode());
        Assert.assertEquals("n2", reader.getGraphMLDocument().getGraphMetadata().get(0).getHyperEdges().get(0).getEndpoints().get(2).getNode());
    }

    @Test(expected = GraphIOException.class)
    public void testInvalidGraphFactory() throws Exception {

        // Need a hypergraph
        String xml = graphMLDocStart
                + "<key id=\"d0\" for=\"node\" attr.name=\"color\" attr.type=\"string\">"
                + "<default>yellow</default>"
                + "</key>"
                + "<key id=\"d1\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>"
                + "<graph id=\"G\" edgedefault=\"undirected\">"
                + "<node id=\"n0\">" + "<data key=\"d0\">green</data>"
                + "</node>" + "<node id=\"n1\"/>" + "<node id=\"n2\">"
                + "<data key=\"d0\">blue</data>" + "</node>"
                + "<hyperedge id=\"e0\">"
                + "<endpoint node=\"n0\"/>" + "<endpoint node=\"n1\"/>"
                + "<endpoint node=\"n2\"/>" + "</hyperedge>" + "</graphml>";

        readGraph(xml, new GraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());
    }

    @Test
    public void testAttributesFile() throws Exception {

        // Read the graph object.
        Hypergraph<MyNode, MyEdge> graph = readGraphFromFile("attributes.graphml", new GraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());

        Assert.assertEquals(6, graph.getVertexCount());
        Assert.assertEquals(7, graph.getEdgeCount());

        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().size());

        // Test node ids
        int id = 0;
        Assert.assertEquals(6, reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().size());
        for (NodeMetadata md : reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes()) {
            Assert.assertEquals('n', md.getId().charAt(0));
            Assert.assertEquals(id++, Integer.parseInt(md.getId().substring(1)));
        }

        // Test edge ids
        id = 0;
        Assert.assertEquals(7, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().size());
        for (EdgeMetadata md : reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges()) {
            Assert.assertEquals('e', md.getId().charAt(0));
            Assert.assertEquals(id++, Integer.parseInt(md.getId().substring(1)));
        }

        Assert.assertEquals("green", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(0).getProperties().get("d0"));
        Assert.assertEquals("yellow", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(1).getProperties().get("d0"));
        Assert.assertEquals("blue", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(2).getProperties().get("d0"));
        Assert.assertEquals("red", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(3).getProperties().get("d0"));
        Assert.assertEquals("yellow", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(4).getProperties().get("d0"));
        Assert.assertEquals("turquoise", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(5).getProperties().get("d0"));

        Assert.assertEquals("1.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(0).getProperties().get("d1"));
        Assert.assertEquals("1.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(1).getProperties().get("d1"));
        Assert.assertEquals("2.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(2).getProperties().get("d1"));
        Assert.assertEquals(null, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(3).getProperties().get("d1"));
        Assert.assertEquals(null, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(4).getProperties().get("d1"));
        Assert.assertEquals(null, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(5).getProperties().get("d1"));
        Assert.assertEquals("1.1", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(6).getProperties().get("d1"));
    }

    @Test
    public void testHypergraphFile() throws Exception {

        Transformer<GraphMetadata, Hypergraph<Number, Number>> graphFactory = new Transformer<GraphMetadata, Hypergraph<Number, Number>>() {
            public Hypergraph<Number, Number> transform(GraphMetadata md) {
                return new SetHypergraph<Number, Number>();
            }
        };

        Transformer<NodeMetadata, Number> vertexFactory = new Transformer<NodeMetadata, Number>() {
            int n = 0;

            public Number transform(NodeMetadata md) {
                return n++;
            }
        };

        Transformer<EdgeMetadata, Number> edgeFactory = new Transformer<EdgeMetadata, Number>() {
            int n = 100;

            public Number transform(EdgeMetadata md) {
                return n++;
            }
        };

        Transformer<HyperEdgeMetadata, Number> hyperEdgeFactory = new Transformer<HyperEdgeMetadata, Number>() {
            int n = 0;

            public Number transform(HyperEdgeMetadata md) {
                return n++;
            }
        };

        // Read the graph object.        
        Reader fileReader = new InputStreamReader(getClass().getResourceAsStream("hyper.graphml"));
        GraphMLReader2<Hypergraph<Number, Number>, Number, Number> hyperreader =
                new GraphMLReader2<Hypergraph<Number, Number>, Number, Number>(fileReader,
                        graphFactory, vertexFactory, edgeFactory, hyperEdgeFactory);

        // Read the graph.
        Hypergraph<Number, Number> graph = hyperreader.readGraph();

        Assert.assertEquals(graph.getVertexCount(), 7);
        Assert.assertEquals(graph.getEdgeCount(), 4);

        // n0
        Set<Number> incident = new HashSet<Number>();
        incident.add(0);
        incident.add(100);
        Assert.assertEquals(incident, graph.getIncidentEdges(0));

        // n1
        incident.clear();
        incident.add(0);
        incident.add(2);
        Assert.assertEquals(incident, graph.getIncidentEdges(1));

        // n2
        incident.clear();
        incident.add(0);
        Assert.assertEquals(incident, graph.getIncidentEdges(2));

        // n3
        incident.clear();
        incident.add(1);
        incident.add(2);
        Assert.assertEquals(incident, graph.getIncidentEdges(3));

        // n4
        incident.clear();
        incident.add(1);
        incident.add(100);
        Assert.assertEquals(incident, graph.getIncidentEdges(4));

        // n5
        incident.clear();
        incident.add(1);
        Assert.assertEquals(incident, graph.getIncidentEdges(5));

        // n6
        incident.clear();
        incident.add(1);
        Assert.assertEquals(incident, graph.getIncidentEdges(6));
    }

    @Test
    public void testMultigraphFile() throws Exception {

        // This test reads a file that is a merge of the attributes.graphml 
        // and hyper.graphml.  The test will merge the two tests together as well.

        // Read the graph object.
        Hypergraph<MyNode, MyEdge> graph1 = readGraphFromFile("multigraph.graphml", new GraphFactory(),
                new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());

        Assert.assertEquals(6, graph1.getVertexCount());
        Assert.assertEquals(7, graph1.getEdgeCount());

        Assert.assertEquals(1, reader.getGraphMLDocument().getGraphMetadata().size());

        // Test node ids
        int id = 0;
        Assert.assertEquals(6, reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().size());
        for (NodeMetadata md : reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes()) {
            Assert.assertEquals('n', md.getId().charAt(0));
            Assert.assertEquals(id++, Integer.parseInt(md.getId().substring(1)));
        }

        // Test edge ids
        id = 0;
        Assert.assertEquals(7, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().size());
        for (EdgeMetadata md : reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges()) {
            Assert.assertEquals('e', md.getId().charAt(0));
            Assert.assertEquals(id++, Integer.parseInt(md.getId().substring(1)));
        }

        Assert.assertEquals("green", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(0).getProperties().get("d0"));
        Assert.assertEquals("yellow", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(1).getProperties().get("d0"));
        Assert.assertEquals("blue", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(2).getProperties().get("d0"));
        Assert.assertEquals("red", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(3).getProperties().get("d0"));
        Assert.assertEquals("yellow", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(4).getProperties().get("d0"));
        Assert.assertEquals("turquoise", reader.getGraphMLDocument().getGraphMetadata().get(0).getNodes().get(5).getProperties().get("d0"));

        Assert.assertEquals("1.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(0).getProperties().get("d1"));
        Assert.assertEquals("1.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(1).getProperties().get("d1"));
        Assert.assertEquals("2.0", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(2).getProperties().get("d1"));
        Assert.assertEquals(null, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(3).getProperties().get("d1"));
        Assert.assertEquals(null, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(4).getProperties().get("d1"));
        Assert.assertEquals(null, reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(5).getProperties().get("d1"));
        Assert.assertEquals("1.1", reader.getGraphMLDocument().getGraphMetadata().get(0).getEdges().get(6).getProperties().get("d1"));

        // Now set the graph transformer for the hypergraph.
        reader.setGraphTransformer(new Transformer<GraphMetadata, Hypergraph<MyNode, MyEdge>>() {
            public Hypergraph<MyNode, MyEdge> transform(GraphMetadata arg0) {
                return new SetHypergraph<MyNode, MyEdge>();
            }
        });

        // Set new vertex/edge factories to reset the value counters
        reader.setVertexTransformer(new NodeFactory());
        reader.setEdgeTransformer(new EdgeFactory());
        reader.setHyperEdgeTransformer(new HyperEdgeFactory());

        // Read the graph.
        Hypergraph<MyNode, MyEdge> graph2 = reader.readGraph();

        Assert.assertEquals(graph2.getVertexCount(), 7);
        Assert.assertEquals(graph2.getEdgeCount(), 4);

        // n0
        Set<MyEdge> incident = new HashSet<MyEdge>();
        incident.add(new MyEdge(0));
        incident.add(new MyEdge(100));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(0)));

        // n1
        incident.clear();
        incident.add(new MyEdge(0));
        incident.add(new MyEdge(2));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(1)));

        // n2
        incident.clear();
        incident.add(new MyEdge(0));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(2)));

        // n3
        incident.clear();
        incident.add(new MyEdge(1));
        incident.add(new MyEdge(2));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(3)));

        // n4
        incident.clear();
        incident.add(new MyEdge(1));
        incident.add(new MyEdge(100));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(4)));

        // n5
        incident.clear();
        incident.add(new MyEdge(1));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(5)));

        // n6
        incident.clear();
        incident.add(new MyEdge(1));
        Assert.assertEquals(incident, graph2.getIncidentEdges(new MyNode(6)));
    }

    /*@Test
    public void testReader1Perf() throws Exception {
        String fileName = "attributes.graphml";                
        
        long totalTime = 0;
        int numTrials = 1000;

        for( int ix=0; ix<numTrials; ++ix ) {
            Reader fileReader = new InputStreamReader(getClass().getResourceAsStream(fileName));
                        
            GraphMLReader<Hypergraph<MyNode, MyEdge>, MyNode, MyEdge> reader = new GraphMLReader<Hypergraph<MyNode, MyEdge>, MyNode, MyEdge>(new Factory<MyNode>() {

                public MyNode create() {
                    return new MyNode();
                }
                
            }, new Factory<MyEdge>() {
                public MyEdge create() {
                    return new MyEdge();
                }
            });   
            
            Thread.sleep(10);
            
            long start = System.currentTimeMillis();
            Hypergraph<MyNode, MyEdge> graph = new UndirectedSparseGraph<MyNode, MyEdge>();
            reader.load(fileReader, graph);
            long duration = System.currentTimeMillis() - start;
            totalTime += duration;
        }
        
        double avgTime = ((double)totalTime / (double)numTrials) / 1000.0; 
        
        System.out.printf("Reader1: totalTime=%6d, numTrials=%6d, avgTime=%2.6f seconds", totalTime, numTrials, avgTime);
        System.out.println();
    }

    @Test
    public void testReader2Perf() throws Exception {
        String fileName = "attributes.graphml";                
        
        long totalTime = 0;
        int numTrials = 1000;

        // Test reader2
        for( int ix=0; ix<numTrials; ++ix ) {
            Reader fileReader = new InputStreamReader(getClass().getResourceAsStream(fileName));       
            reader = new GraphMLReader2<Hypergraph<MyNode, MyEdge>, MyNode, MyEdge>(
                    fileReader, new GraphFactory(),
                    new NodeFactory(), new EdgeFactory(), new HyperEdgeFactory());
            reader.init();
            
            Thread.sleep(10);
            
            long start = System.currentTimeMillis();
            reader.readGraph();
            long duration = System.currentTimeMillis() - start;
            totalTime += duration;
            
            reader.close();
        }
        
        double avgTime = ((double)totalTime / (double)numTrials) / 1000.0; 
        
        System.out.printf("Reader2: totalTime=%6d, numTrials=%6d, avgTime=%2.6f seconds", totalTime, numTrials, avgTime);
        System.out.println();
    }*/

    private Hypergraph<MyNode, MyEdge> readGraph(String xml, GraphFactory gf,
                                                 NodeFactory nf, EdgeFactory ef, HyperEdgeFactory hef)
            throws GraphIOException {
        Reader fileReader = new StringReader(xml);
        reader = new GraphMLReader2<Hypergraph<MyNode, MyEdge>, MyNode, MyEdge>(
                fileReader, gf, nf, ef, hef);

        return reader.readGraph();
    }

    private Hypergraph<MyNode, MyEdge> readGraphFromFile(String file, GraphFactory gf,
                                                         NodeFactory nf, EdgeFactory ef, HyperEdgeFactory hef)
            throws Exception {
        Reader fileReader = new InputStreamReader(getClass().getResourceAsStream(file));
        reader = new GraphMLReader2<Hypergraph<MyNode, MyEdge>, MyNode, MyEdge>(
                fileReader, gf, nf, ef, hef);

        return reader.readGraph();
    }

}
