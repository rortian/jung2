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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;


/**
 * @author Scott White
 */
public class TestBicomponentClusterer extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBicomponentClusterer.class);
	}

	protected void setUp() {

	}

    public void testExtract0() throws Exception
    {
        Graph<String,Number> graph = new UndirectedSparseGraph<String,Number>();
        String[] v = {"0"};
        graph.addVertex(v[0]);
        
        Set[] c = {new HashSet()};
        
        c[0].add(v[0]);
     
        testComponents(graph, v, c);
    }

    public void testExtractEdge() throws Exception
    {
        Graph<String,Number> graph = new UndirectedSparseGraph<String,Number>();
        String[] v = {"0","1"}; 
        graph.addVertex(v[0]);
        graph.addVertex(v[1]);
        graph.addEdge(0, v[0], v[1]);
        
        Set[] c = {new HashSet()};
        
        c[0].add(v[0]);
        c[0].add(v[1]);
     
        testComponents(graph, v, c);
    }
    
    public void testExtractV() throws Exception
    {
        Graph<String,Number> graph = new UndirectedSparseGraph<String,Number>();
        String[] v = new String[3];
        for (int i = 0; i < 3; i++)
        {
            v[i] = ""+i;
            graph.addVertex(v[i]);
        }
        graph.addEdge(0, v[0], v[1]);
        graph.addEdge(1, v[0], v[2]);
        
        Set[] c = {new HashSet(), new HashSet()};
              
        c[0].add(v[0]);
        c[0].add(v[1]);
        
        c[1].add(v[0]);
        c[1].add(v[2]);
           
        testComponents(graph, v, c);
    }
    
    public void createEdges(String[] v, int[][] edge_array, Graph<String,Number> g)
    {
        for (int k = 0; k < edge_array.length; k++)
        {
            int i = edge_array[k][0];
            int j = edge_array[k][1];
            String v1 = getVertex(v, i, g);
            String v2 = getVertex(v, j, g);
            
            g.addEdge(k, v1, v2);
        }
    }
    
    public String getVertex(String[] v_array, int i, Graph<String,Number> g)
    {
        String v = v_array[i];
        if (v == null)
        {
        	v_array[i] = Character.toString((char)('0'+i));
            g.addVertex(v_array[i]);
            v = v_array[i];
        }
        return v;
    }
    
	public void testExtract1() {
        String[] v = new String[6];
        int[][] edges1 = {{0,1}, {0,5}, {0,3}, {0,4}, {1,5}, {3,4}, {2,3}};
        Graph<String,Number> graph = new UndirectedSparseGraph<String,Number>();
        createEdges(v, edges1, graph);
        
//		StringBuffer buffer= new StringBuffer();
//        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
//        buffer.append("<graph edgedefault=\"undirected\">\n");
//        buffer.append("<node id=\"0\"/>\n");
//        buffer.append("<node id=\"1\"/>\n");
//        buffer.append("<node id=\"2\"/>\n");
//        buffer.append("<node id=\"3\"/>\n");
//        buffer.append("<node id=\"4\"/>\n");
//        buffer.append("<node id=\"5\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"1\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"5\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"3\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"4\"/>\n");
//        buffer.append("<edge source=\"1\" target=\"5\"/>\n");
//        buffer.append("<edge source=\"3\" target=\"4\"/>\n");
//        buffer.append("<edge source=\"2\" target=\"3\"/>\n");
//        buffer.append("</graph>\n");
//
//        GraphMLFile graphmlFile = new GraphMLFile();
//        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

        Set[] c = new Set[3];
        for (int i = 0; i < c.length; i++)
            c[i] = new HashSet();
        
        c[0].add(v[0]);
        c[0].add(v[1]);
        c[0].add(v[5]);
        
        c[1].add(v[0]);
        c[1].add(v[3]);
        c[1].add(v[4]);
        
        c[2].add(v[2]);
        c[2].add(v[3]);
        
        testComponents(graph, v, c);
	}
    
    public void testExtract2() {
        String[] v = new String[9];
        int[][] edges1 = {{0,2}, {0,4}, {1,0}, {2,1}, {3,0}, {4,3}, {5,3}, {6,7}, {6,8}, {8,7}};
        Graph<String,Number> graph = new UndirectedSparseGraph<String,Number>();
        createEdges(v, edges1, graph);
        
        
        
//		StringBuffer buffer= new StringBuffer();
//        buffer.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
//        buffer.append("<graph edgedefault=\"undirected\">\n");
//        buffer.append("<node id=\"0\"/>\n");
//        buffer.append("<node id=\"1\"/>\n");
//        buffer.append("<node id=\"2\"/>\n");
//        buffer.append("<node id=\"3\"/>\n");
//        buffer.append("<node id=\"4\"/>\n");
//        buffer.append("<node id=\"5\"/>\n");
//        buffer.append("<node id=\"6\"/>\n");
//        buffer.append("<node id=\"7\"/>\n");
//        buffer.append("<node id=\"8\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"2\"/>\n");
//        buffer.append("<edge source=\"0\" target=\"4\"/>\n");
//        buffer.append("<edge source=\"1\" target=\"0\"/>\n");
//        buffer.append("<edge source=\"2\" target=\"1\"/>\n");
//        buffer.append("<edge source=\"3\" target=\"0\"/>\n");
//        buffer.append("<edge source=\"4\" target=\"3\"/>\n");
//        buffer.append("<edge source=\"5\" target=\"3\"/>\n");
//        buffer.append("<edge source=\"6\" target=\"7\"/>\n");
//        buffer.append("<edge source=\"6\" target=\"8\"/>\n");
//        buffer.append("<edge source=\"8\" target=\"7\"/>\n");
//        buffer.append("</graph>\n");

//        GraphMLFile graphmlFile = new GraphMLFile();
//        Graph graph = graphmlFile.load(new StringReader(buffer.toString()));

//        StringLabeller sl = StringLabeller.getLabeller(graph);
//        Vertex[] v = getVerticesByLabel(graph, sl);
        
        Set[] c = new Set[4];
        for (int i = 0; i < c.length; i++)
            c[i] = new HashSet();
        
        c[0].add(v[0]);
        c[0].add(v[1]);
        c[0].add(v[2]);
        
        c[1].add(v[0]);
        c[1].add(v[3]);
        c[1].add(v[4]);
        
        c[2].add(v[5]);
        c[2].add(v[3]);
        
        c[3].add(v[6]);
        c[3].add(v[7]);
        c[3].add(v[8]);

        testComponents(graph, v, c);
	}

    public void testComponents(Graph<String,Number> graph, String[] vertices, Set[] c)
    {
        BicomponentClusterer<String,Number> finder = new BicomponentClusterer<String,Number>();
        ClusterSet<String,Number,String> bicomponents = finder.extract(graph);
        
        // check number of components
        assertEquals(bicomponents.size(), c.length);

        // diagnostic; should be commented out for typical unit tests
//        for (int i = 0; i < bicomponents.size(); i++)
//        {
//            System.out.print("Component " + i + ": ");
//            Set bicomponent = bicomponents.getCluster(i);
//            for (Iterator iter = bicomponent.iterator(); iter.hasNext(); )
//            {
//                Vertex w = (Vertex)iter.next();
//                System.out.print(sl.getLabel(w) + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
        
        // make sure that each set in c[] is found in bicomponents
        boolean found = false;
        for (int i = 0; i < c.length; i++)
        {
            for (int j = 0; j < bicomponents.size(); j++)
                if (bicomponents.getCluster(j).equals(c[i]))
                {
                    found = true;
                    break;
                }
            assertTrue(found);
        }
        
        // make sure that each vertex is represented in >=1 element of bicomponents 
        for (String v : graph.getVertices())
        {
            assertFalse(bicomponents.getClusters(v).isEmpty());
        }
    }
    
}
