/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Jul 2, 2003
 *  
 */
package samples.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.graph.DirectedEdge;
import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedEdge;
import edu.uci.ics.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.BarabasiAlbertGenerator;
import edu.uci.ics.jung.graph.DirectedSparseEdge;
import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;
import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.graph.SimpleUndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseEdge;




/**
 * 
 * Generates a series of potentially useful test graphs.
 * 
 * @author danyelf
 *  
 */
public class TestGraphs {

	/**
	 * A series of pairs that may be useful for generating graphs. The
	 * miniature graph consists of 8 edges, 10 nodes, and is formed of two
	 * connected components, one of 8 nodes, the other of 2.
	 *  
	 */
	public static String[][] pairs = { { "a", "b", "3" }, {
			"a", "c", "4" }, {
			"a", "d", "5" }, {
			"d", "c", "6" }, {
			"d", "e", "7" }, {
			"e", "f", "8" }, {
			"f", "g", "9" }, {
			"h", "i", "1" }
	};

	/**
	 * Creates a small sample graph that can be used for testing purposes. The
	 * graph is as described in the section on {@link #pairs pairs}. If <tt>isDirected</tt>,
	 * the graph is a {@link DirectedSparseGraph DirectedSparseGraph},
	 * otherwise, it is an {@link UndirectedSparseGraph UndirectedSparseGraph}.
	 * 
	 * @param isDirected:
	 *            Is the graph directed?
	 * @return a graph consisting of eight edges and ten nodes.
	 */
	public static Graph<String, ? extends Edge<String>> createTestGraph(boolean isDirected) {

		if (isDirected) {
			SimpleDirectedSparseGraph<String, DirectedEdge<String>> g = 
				new SimpleDirectedSparseGraph<String, DirectedEdge<String>>();
			for (int i = 0; i < pairs.length; i++) {
				String[] pair = pairs[i];
				createDirectedEdge(g, pair[0], pair[1], Integer.parseInt(pair[2]));
			}
			return g;
		} else {
			SimpleUndirectedSparseGraph<String, UndirectedEdge<String>> g = 
				new SimpleUndirectedSparseGraph<String, UndirectedEdge<String>>();
			for (int i = 0; i < pairs.length; i++) {
				String[] pair = pairs[i];
				createUndirectedEdge(g, pair[0], pair[1], Integer.parseInt(pair[2]));
			}
			return g;
		}
//		StringLabeller sl = StringLabeller.getLabeller(g);
//		EdgeWeightLabeller el = EdgeWeightLabeller.getLabeller(g);
//		for (int i = 0; i < pairs.length; i++) {
//			String[] pair = pairs[i];
//			createDirectedEdge(g, pair[0], pair[1], Integer.parseInt(pair[2]));
//		}
//		return g;

	}

    /**
     * Returns a graph consisting of a chain of <code>vertex_count - 1</code> vertices
     * plus one isolated vertex.
     */
    public static Graph<String,UndirectedEdge<String>> createChainPlusIsolates(int chain_length, int isolate_count)
    {
        Graph<String, UndirectedEdge<String>> g = 
            new SimpleUndirectedSparseGraph<String, UndirectedEdge<String>>();
        if (chain_length > 0)
        {
            String[] v = new String[chain_length];
            v[0] = "v"+0;
            g.addVertex(v[0]);
            for (int i = 1; i < chain_length; i++)
            {
                v[i] = "v"+i;
                g.addVertex(v[i]);
                g.addEdge(new UndirectedSparseEdge<String>(v[i], v[i-1]));
            }
        }
        for (int i = 0; i < isolate_count; i++) {
            String v = "v"+(chain_length+i);
            g.addVertex(v);
        }
        return g;
    }
    
	/**
	 * Creates a sample directed acyclic graph by generating several "layers",
	 * and connecting nodes (randomly) to nodes in earlier (but never later)
	 * layers. Each layer has some random number of nodes in it 1 less than n
	 * less than maxNodesPerLayer.
	 * 
	 * @return the created graph
	 */
	public static Graph<String,DirectedEdge<String>> createDirectedAcyclicGraph(
		int layers,
		int maxNodesPerLayer,
		double linkprob) {
		DirectedGraph<String,DirectedEdge<String>> dag = 
            new SimpleDirectedSparseGraph<String,DirectedEdge<String>>();
//		StringLabeller sl = StringLabeller.getLabeller(dag);
		Set<String> previousLayers = new HashSet<String>();
		Set<String> inThisLayer = new HashSet<String>();
		for (int i = 0; i < layers; i++) {

			int nodesThisLayer = (int) (Math.random() * maxNodesPerLayer) + 1;
			for (int j = 0; j < nodesThisLayer; j++) {
                String v = i+":"+j;
				dag.addVertex(v);
				inThisLayer.add(v);
//				try {
//					sl.setLabel(v, i + ":" + j);
//				} catch (Exception e) {
//				}
				// for each previous node...
                for(String v2 : previousLayers) {
//				for (Iterator iter = previousLayers.iterator();
//					iter.hasNext();
//					) {
//					Vertex v2 = (Vertex) iter.next();
					if (Math.random() < linkprob) {
                        DirectedEdge<String> de = new DirectedSparseEdge<String>(v,v2);
						dag.addEdge(de);
					}
				}
			}

			previousLayers.addAll(inThisLayer);
			inThisLayer.clear();
		}
		return dag;
	}
	private static void createUndirectedEdge(
			final UndirectedGraph<String, UndirectedEdge<String>> g,
//			StringLabeller sl,
//			EdgeWeightLabeller el,
			String v1Label,
			String v2Label,
			int weight) {
			
			g.addEdge(new UndirectedSparseEdge<String>(v1Label, v2Label));
	}
	
	private static void createDirectedEdge(
		final DirectedGraph<String, DirectedEdge<String>> g,
//		StringLabeller sl,
//		EdgeWeightLabeller el,
		String v1Label,
		String v2Label,
		int weight) {
		
		g.addEdge(new DirectedSparseEdge<String>(v1Label, v2Label));

//		try {
//			Vertex v1 = sl.getVertex(v1Label);
//			if (v1 == null) {
//				v1 = g.addVertex(new SparseVertex());
//				sl.setLabel(v1, v1Label);
//			}
//			Vertex v2 = sl.getVertex(v2Label);
//			if (v2 == null) {
//				v2 = g.addVertex(new SparseVertex());
//				sl.setLabel(v2, v2Label);
//			}
//			Edge e = GraphUtils.addEdge(g, v1, v2);
//			el.setWeight(e, weight);
//		} catch (StringLabeller.UniqueLabelException e) {
//			throw new FatalException("This should not happen " + e);
//		}
	}

	/**
	 * Returns a bigger, undirected test graph with a just one component. This
	 * graph consists of a clique of ten edges, a partial clique (randomly
	 * generated, with edges of 0.6 probability), and one series of edges
	 * running from the first node to the last.
	 * 
	 * @return the testgraph
	 */
	public static Graph<String,UndirectedEdge<String>> getOneComponentGraph() {
		SimpleUndirectedSparseGraph<String, UndirectedEdge<String>> g = 
			new SimpleUndirectedSparseGraph<String, UndirectedEdge<String>>();
//		StringLabeller sl = StringLabeller.getLabeller(g);
//		EdgeWeightLabeller el = EdgeWeightLabeller.getLabeller(g);

		// let's throw in a clique, too
		for (int i = 1; i <= 10; i++) {
			for (int j = i + 1; j <= 10; j++) {
				String i1 = "" + i;
				String i2 = "" + j;
				createUndirectedEdge(g, i1, i2, i + j);
			}
		}

		// and, last, a partial clique
		for (int i = 11; i <= 20; i++) {
			for (int j = i + 1; j <= 20; j++) {
				if (Math.random() > 0.6)
					continue;
				String i1 = "" + i;
				String i2 = "" + j;
				createUndirectedEdge(g, i1, i2, i + j);
			}
		}

		List<String> index = new ArrayList<String>();
		index.addAll(g.getVertices());
		// and one edge to connect them all
//		Indexer ind = Indexer.getIndexer(g);
		for (int i = 0; i < index.size() - 1; i++) {
			try {
				g.addEdge(new UndirectedSparseEdge<String>(index.get(i), index.get(i+1)));
//				GraphUtils.addEdge(g, (Vertex)ind.getVertex(i), (Vertex) ind.getVertex(i + 1));
			} catch (IllegalArgumentException fe) {
			}
		}

		return g;
	}

	/**
	 * Returns a bigger test graph with a clique, several components, and other
	 * parts.
	 * 
	 * @return a demonstration graph of type <tt>UndirectedSparseGraph</tt>
	 *         with 28 vertices.
	 */
	public static Graph<String, UndirectedEdge<String>> getDemoGraph() {
		UndirectedGraph<String, UndirectedEdge<String>> g = 
            new SimpleUndirectedSparseGraph<String, UndirectedEdge<String>>();
//		StringLabeller sl = StringLabeller.getLabeller(g);
//		EdgeWeightLabeller el = EdgeWeightLabeller.getLabeller(g);

		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i];
			createUndirectedEdge(g, pair[0], pair[1], Integer.parseInt(pair[2]));
		}

		// let's throw in a clique, too
		for (int i = 1; i <= 10; i++) {
			for (int j = i + 1; j <= 10; j++) {
				String i1 = "" + i;
				String i2 = "" + j;
				createUndirectedEdge(g, i1, i2, i + j);
			}
		}

		// and, last, a partial clique
		for (int i = 11; i <= 20; i++) {
			for (int j = i + 1; j <= 20; j++) {
				if (Math.random() > 0.6)
					continue;
				String i1 = "" + i;
				String i2 = "" + j;
				createUndirectedEdge(g, i1, i2, i + j);
			}
		}
		return g;
	}

	/**
	 * Equivalent to <code>generateMixedRandomGraph(edge_weight, num_vertices, true)</code>.
	 */
	public static Graph<Integer, Edge<Integer>> generateMixedRandomGraph(Map<Edge, Number> edge_weight, int num_vertices)
	{
		return generateMixedRandomGraph(edge_weight, num_vertices, true);
	}

    /**
     * Returns a random mixed-mode graph.  Starts with a randomly generated 
     * Barabasi-Albert (preferential attachment) generator 
     * (4 initial vertices, 3 edges added at each step, and num_vertices - 4 evolution steps).
     * Then takes the resultant graph, replaces random undirected edges with directed
     * edges, and assigns random weights to each edge.
     */
    public static Graph<Integer, Edge<Integer>> generateMixedRandomGraph(Map<Edge,Number> edge_weights, int num_vertices, boolean parallel)
    {
        int seed = (int)(Math.random() * 10000);
        BarabasiAlbertGenerator bag = new BarabasiAlbertGenerator(4, 3, false, parallel, seed);
        bag.evolveGraph(num_vertices - 4);
        Graph<Integer, Edge<Integer>> ug = bag.generateGraph();
//        SettableVertexMapper svm = new HashSettableVertexMapper();
//        Map<Integer,Integer> svm = new HashMap<Integer,Integer>();

        // create a SparseGraph version of g
        Graph<Integer, Edge<Integer>> g = new SimpleSparseGraph<Integer, Edge<Integer>>();
        for(Integer v : ug.getVertices()) {
        	g.addVertex(v);
        }
//        for(Integer v : ug.getVertices()) {
////        for (Iterator iter = ug.getVertices().iterator(); iter.hasNext(); )
////        {
////            Vertex v = (Vertex)iter.next();
//        	Integer w = new Integer(ug.getVertices().size());
////            Vertex w = new SparseVertex();
//            g.addVertex(w);
////            if (v.containsUserDatumKey(BarabasiAlbertGenerator.SEED))
////                w.addUserDatum(BarabasiAlbertGenerator.SEED, BarabasiAlbertGenerator.SEED, UserData.REMOVE);
//            svm.put(v, w);
//        }
        
        // randomly replace some of the edges by directed edges to 
        // get a mixed-mode graph, add random weights
        
        for(Edge<Integer> e : ug.getEdges()) {
            Integer v1 = e.getEndpoints().getFirst();
            Integer v2 = e.getEndpoints().getSecond();

            Edge<Integer> me;
            if (Math.random() < 0.5)
                me = new DirectedSparseEdge<Integer>(v1, v2);
            else
                me = new UndirectedSparseEdge<Integer>(v1, v2);
            g.addEdge(me);
            edge_weights.put(me, new Double(Math.random()));
        }
        
        return g;
    }
    
    public static Graph<Integer, Edge<Integer>> getSmallGraph() {
        Graph<Integer, Edge<Integer>> graph = 
            new SimpleSparseGraph<Integer, Edge<Integer>>();
        Integer[] v = new Integer[3];
        for (int i = 0; i < 3; i++) {
            v[i] = new Integer(i);
            graph.addVertex(v[i]);
        }
        graph.addEdge(new DirectedSparseEdge<Integer>(v[0], v[1]));
        graph.addEdge(new DirectedSparseEdge<Integer>(v[0], v[1]));
        graph.addEdge(new DirectedSparseEdge<Integer>(v[0], v[1]));
        graph.addEdge(new DirectedSparseEdge<Integer>(v[1], v[0]));
        graph.addEdge(new DirectedSparseEdge<Integer>(v[1], v[0]));
        graph.addEdge(new UndirectedSparseEdge<Integer>(v[1], v[2]));
        graph.addEdge(new UndirectedSparseEdge<Integer>(v[1], v[2]));

        return graph;

    }

    
}
