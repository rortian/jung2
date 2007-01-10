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

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.functors.OrPredicate;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.EdgeType;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedGraph;
import edu.uci.ics.graph.util.Pair;


/**
 * Reads a <code>Graph</code> from a Pajek NET formatted source.
 * 
 * <p>If the edge constraints specify that the graph is strictly undirected,
 * and an "*Arcs" section is encountered, or if the edge constraints specify that the 
 * graph is strictly directed, and an "*Edges" section is encountered,
 * an <code>IllegalArgumentException</code> is thrown.</p>
 * 
 * <p>If the edge constraints do not permit parallel edges, only the first encountered
 * of a set of parallel edges will be read; subsequent edges in that set will be ignored.</p>
 * 
 * <p>More restrictive edge constraints will cause vertices to be generated
 * that are more time- and space-efficient.</p>
 * 
 * At the moment, only supports the 
 * part of the specification that defines: 
 * <ul>
 * <li> vertex ids (each must have a value from 1 to n, where n is the number of vertices)
 * <li> vertex labels (must be in quotes if interrupted by whitespace)
 * <li> directed edge connections (single or list)
 * <li> undirected edge connections (single or list)
 * <li> edge weights (not compatible with edges specified in list form)
 * <br><b>note</b>: this version of PajekNetReader does not support multiple edge 
 * weights, as PajekNetFile does; this behavior is consistent with the NET format. 
 * <li/> vertex locations (x and y; z coordinate is ignored)
 * </ul> <p>
 *
 * Here is an example format for a directed graph without edge weights 
 * and edges specified in list form: <br>
 * <pre>
 * *vertices <# of vertices> 
 * 1 "a" 
 * 2 "b" 
 * 3 "c" 
 * *arcslist 
 * 1 2 3 
 * 2 3  
 * </pre>
 *
 * Here is an example format for an undirected graph with edge weights 
 * and edges specified in non-list form: <br>
 * <pre>
 * *vertices <# of vertices> 
 * 1 "a" 
 * 2 "b" 
 * 3 "c" 
 * *edges 
 * 1 2 0.1 
 * 1 3 0.9 
 * 2 3 1.0 
 * </pre> 
 * 
 * @author Joshua O'Madadhain
 * @see "'Pajek - Program for Analysis and Visualization of Large Networks', Vladimir Batagelj and Andrej Mrvar, http://vlado.fmf.uni-lj.si/pub/networks/pajek/doc/pajekman.pdf"
 * @author Tom Nelson - converted to jung2
 */
public class PajekNetReader<V,E> {
	
	protected Factory<? extends Graph<V,E>> graphFactory;
	protected Factory<V> vertexFactory;
	protected Factory<E> edgeFactory;
    protected boolean unique_labels;

    /**
     * The map for vertex labels (if any) created by this class.
     */
    protected Map<V,String> vertexLabeller = new HashMap<V,String>();
    /**
     * The map for vertex locations (if any) defined by this class.
     */
    protected Map<V,Point2D> vertexLocationTransformer;
    protected boolean get_locations = false;
    
    /**
     * Used to specify whether the most recently read line is a 
     * Pajek-specific tag.
     */
    private static final Predicate<String> v_pred = new StartsWithPredicate("*vertices");
    private static final Predicate<String> a_pred = new StartsWithPredicate("*arcs");
    private static final Predicate<String> e_pred = new StartsWithPredicate("*edges");
    private static final Predicate<String> t_pred = new StartsWithPredicate("*");
    private static final Predicate<String> c_pred = OrPredicate.getInstance(a_pred, e_pred);
    protected static final Predicate<String> l_pred = ListTagPred.getInstance();
    protected Predicate<E> p_pred;// = ParallelEdgePredicate.getInstance();
    
    /**
     * Creates a PajekNetReader with the specified labeling behavior, which does not 
     * read location information (if any).
     * 
     * @see #PajekNetReader(boolean, boolean)
     */
    public PajekNetReader(boolean unique_labels) {
        this(unique_labels, false);
    }
    
    /**
     * Creates a PajekNetReader with the specified labeling behavior and 
     * location assignment behavior.
     * 
     * <p>If <code>unique_labels</code> is true, vertices will be labelled 
     * using a <code>StringLabeller</code> with key <code>jung.io.PajekNetReader.LABEL</code>.
     * Otherwise, they will be labeled with a user data <code>String</code> with key 
     * <code>PajekNetReader.LABEL</code>.  (Vertices that have no apparent label
     * information will not be labelled.)</p>
     * 
     * <p>If <code>get_locations</code> is true, each vertex line in the file
     * will be assumed to contain (x,y) coordinates in the range [0,1]; if any line
     * lacks this data, an <code>IllegalArgumentException</code> will be thrown.  (The Pajek
     * format assumes coordinates are (x,y,z) but we ignore the z-coordinate.)  Location
     * data will be stored in a <code>SettabelVertexLocationDecorator</code> instance
     * in the graph's user data with key<code>jung.io.PajekNetReader.LOCATIONS</code>.</p>
     */
    public PajekNetReader(boolean unique_labels, boolean get_locations) {
        this.unique_labels = unique_labels;
        this.get_locations = get_locations;
        if (get_locations) {
            this.vertexLocationTransformer = new HashMap<V,Point2D>();
        }
    }
    
    /**
     * Creates a PajekNetReader with the specified labeling behavior and 
     * location assignment behavior.
     * 
     * <p>If <code>unique_labels</code> is true, vertices will be labelled 
     * using a <code>StringLabeller</code> with key <code>jung.io.PajekNetReader.LABEL</code>.
     * Otherwise, they will be labeled with a user data <code>String</code> with key 
     * <code>PajekNetReader.LABEL</code>.  (Vertices that have no apparent label
     * information will not be labelled.)</p>
     * 
     * <p>If <code>get_locations</code> is true, each vertex line in the file
     * will be assumed to contain (x,y) coordinates in the range [0,1]; if any line
     * lacks this data, an <code>IllegalArgumentException</code> will be thrown.  (The Pajek
     * format assumes coordinates are (x,y,z) but we ignore the z-coordinate.)  Location
     * data will be stored in <code>vertexLocationTransformer</code>, a reference to which will be
     * stored in the graph's user data with key <code>jung.io.PajekNetReader.LOCATIONS</code>.</p>
     */
    public PajekNetReader(boolean unique_labels, Map<V,Point2D> v_locations) {
        this.unique_labels = unique_labels;
        this.get_locations = true;
        this.vertexLocationTransformer = v_locations;
    }
    
    /**
     * Creates a PajekNetReader whose labels are not required to be unique.
     */
    public PajekNetReader() {
        this(false, false);
    }
    
    /**
     * Returns <code>load(filename, new SparseGraph(), null)</code>.
     * @throws IOException
     */
    public Graph<V,E> load(String filename) throws IOException
    {
    	Graph<V,E> graph = graphFactory.create();
    	this.p_pred = new ParallelEdgePredicate<V,E>(graph);
        return load(filename, graph, null);
    }

    /**
     * Returns <code>load(filename, new SparseGraph(), nev)</code>.
     * @throws IOException
     */
    public Graph<V,E> load(String filename, Map<E,Number> nev) throws IOException
    {
    	Graph<V,E> graph = graphFactory.create();
    	this.p_pred = new ParallelEdgePredicate<V,E>(graph);
        return load(filename, graph, nev);
    }
    
    /**
     * Returns <code>load(filename, g, null)</code>.
     * @throws IOException
     */
    public Graph<V,E> load(String filename, Graph<V,E> g) throws IOException
    {
        return load(filename, g, null);
    }
    
    /**
     * Creates a <code>FileReader</code> from <code>filename</code>, calls
     * <code>load(reader, g, nev)</code>, closes the reader, and returns
     * the resultant graph.
     * @throws IOException
     */
    public Graph<V,E> load(String filename, Graph<V,E> g, Map<E,Number> nev) throws IOException
    {
        Reader reader = new FileReader(filename);
        Graph<V,E> graph = load(reader, g, nev);
        reader.close();
        return graph;
    }
    
    /**
     * Returns <code>load(reader, g, null)</code>.
     * @throws IOException
     */
    public Graph<V,E> load(Reader reader, Graph<V,E> g) throws IOException
    {
        return load(reader, g, null);
    }
    
    /**
     * Returns <code>load(reader, new SparseGraph(), nev)</code>.
     * @throws IOException
     */
    public Graph<V,E> load(Reader reader, Map<E,Number> nev) throws IOException
    {
    	Graph<V,E> graph = graphFactory.create();
    	this.p_pred = new ParallelEdgePredicate<V,E>(graph);
        return load(reader, graph, nev);
    }
    
    /**
     * Returns <code>load(reader, new SparseGraph(), null)</code>.
     * @throws IOException
     */
    public Graph<V,E> load(Reader reader) throws IOException
    {
    	Graph<V,E> graph = graphFactory.create();
    	this.p_pred = new ParallelEdgePredicate<V,E>(graph);
        return load(reader, graph, null);
    }
    
    /**
     * Returns <code>load(reader, g, nev, new TypedVertexGenerator(g))</code>.
     * @throws IOException
     * @see edu.uci.ics.jung.utils.TypedVertexGenerator
     */
    public Graph<V,E> load(Reader reader, Graph<V,E> g, Map<E,Number> nev) throws IOException
    {
        return load(reader, g, nev, vertexFactory);
    }
    
    /**
     * Populates the graph <code>g</code> with the graph represented by the
     * Pajek-format data supplied by <code>reader</code>.  Stores edge weights,
     * if any, according to <code>nev</code> (if non-null).
     * Any existing vertices/edges of <code>g</code>, if any, are unaffected.
     * The edge data are filtered according to <code>g</code>'s constraints, if any; thus, if 
     * <code>g</code> only accepts directed edges, any undirected edges in the 
     * input are ignored.
     * Vertices are created with the generator <code>vg</code>.  The user is responsible
     * for supplying a generator whose output is compatible with this graph and its contents;
     * users that don't want to deal with this issue may use a <code>TypedVertexGenerator</code>
     * or call <code>load(reader, g, nev)</code> for a default generator.
     * @throws IOException
     */
    public Graph<V,E> load(Reader reader, Graph<V,E> g, Map<E,Number> nev, Factory<V> vg) throws IOException
    {
        BufferedReader br = new BufferedReader(reader);
                
        // ignore everything until we see '*Vertices'
        String curLine = skip(br, v_pred);
        
        if (curLine == null) // no vertices in the graph; return empty graph
            return g;
        
        // create appropriate number of vertices
        StringTokenizer st = new StringTokenizer(curLine);
        st.nextToken(); // skip past "*vertices";
        int num_vertices = Integer.parseInt(st.nextToken());
        for (int i = 1; i <= num_vertices; i++)
            g.addVertex(vg.create());
        List<V> id = new ArrayList<V>(g.getVertices());//Indexer.getIndexer(g);

        // read vertices until we see any Pajek format tag ('*...')
        curLine = null;
        while (br.ready())
        {
            curLine = br.readLine();
            if (curLine == null || t_pred.evaluate(curLine))
                break;
            if (curLine == "") // skip blank lines
                continue;
            
            try
            {
                readVertex(curLine, id, num_vertices);
            }
            catch (IllegalArgumentException iae)
            {
                br.close();
                reader.close();
                throw iae;
            }
        }   

        // skip over the intermediate stuff (if any) 
        // and read the next arcs/edges section that we find
        curLine = readArcsOrEdges(curLine, br, g, nev);

        // ditto
        readArcsOrEdges(curLine, br, g, nev);
        
        br.close();
        reader.close();
        
        return g;
    }

    /**
     * Parses <code>curLine</code> as a reference to a vertex, and optionally assigns 
     * label and location information.
     * @throws IOException
     */
    private void readVertex(String curLine, List<V> id, int num_vertices) throws IOException
    {
        V v;
        String[] parts = null;
        int coord_idx = -1;     // index of first coordinate in parts; -1 indicates no coordinates found
        String index;
        String label = null;
        // if there are quote marks on this line, split on them; label is surrounded by them
        if (curLine.indexOf('"') != -1)
        {
            String[] initial_split = curLine.trim().split("\"");
            // if there are any quote marks, there should be exactly 2
            if (initial_split.length < 2 || initial_split.length > 3)
                throw new IllegalArgumentException("Unbalanced (or too many) quote marks in " + curLine);
            index = initial_split[0].trim();
            label = initial_split[1].trim();
            if (initial_split.length == 3)
                parts = initial_split[2].trim().split("\\s+", -1);
            coord_idx = 0;
        }
        else // no quote marks, but are there coordinates?
        {
            parts = curLine.trim().split("\\s+", -1);
            index = parts[0];
            switch (parts.length)
            {
                case 1:         // just the ID; nothing to do, continue
                    break;  
                case 2:         // just the ID and a label
                    label = parts[1];
                    break;
                case 3:         // ID, no label, coordinates
                    coord_idx = 1;
                    break;
                case 4:         // ID, label, (x,y) coordinates
                    coord_idx = 2;
                    break;
            }
        }
        int v_id = Integer.parseInt(index) - 1; // go from 1-based to 0-based index
        if (v_id >= num_vertices || v_id < 0)
            throw new IllegalArgumentException("Vertex number " + v_id +
                    "is not in the range [1," + num_vertices + "]");
        v = id.get(v_id);
        // only attach the label if there's one to attach
        if (label != null && label.length() > 0)
        	vertexLabeller.put(v, label);

        // parse the rest of the line
        if (get_locations)
        {
            if (coord_idx == -1 || parts == null || parts.length < coord_idx+2)
                throw new IllegalArgumentException("Coordinates requested, but" +
                        curLine + " does not include coordinates");
            double x = Double.parseDouble(parts[coord_idx]);
            double y = Double.parseDouble(parts[coord_idx+1]);
//            if (x < 0 || x > 1 || y < 0 || y > 1)
//                throw new IllegalArgumentException("Coordinates in line " + 
//                        curLine + " are not all in the range [0,1]");
                
            vertexLocationTransformer.put(v, new Point2D.Double(x,y));
        }
    }

    
    
    private String readArcsOrEdges(String curLine, BufferedReader br, Graph<V,E> g,
            Map<E,Number> nev) 
        throws IOException
    {
        String nextLine = curLine;
        
        List<V> id = new ArrayList<V>(g.getVertices());//Indexer.getIndexer(g);
        
        // in case we're not there yet (i.e., format tag isn't arcs or edges)
        if (! c_pred.evaluate(curLine))
//            nextLine = skip(br, e_pred);
            nextLine = skip(br, c_pred);

        // in "*Arcs" and this graph is not strictly undirected
//        boolean reading_arcs = a_pred.evaluate(nextLine) && 
//            !PredicateUtils.enforcesUndirected(g);
//        // in "*EdgeType" and this graph is not strictly directed
//        boolean reading_edges = e_pred.evaluate(nextLine) && 
//            !PredicateUtils.enforcesDirected(g);

        boolean reading_arcs = false;
        boolean reading_edges = false;
        EdgeType directedness = null;
        if (a_pred.evaluate(nextLine))
        {
            if (g instanceof UndirectedGraph) {
                throw new IllegalArgumentException("Supplied undirected-only graph cannot be populated with directed edges");
            } else {
                reading_arcs = true;
                directedness = EdgeType.DIRECTED;
            }
        }
        if (e_pred.evaluate(nextLine))
        {
            if (g instanceof DirectedGraph)
                throw new IllegalArgumentException("Supplied directed-only graph cannot be populated with undirected edges");
            else
                reading_edges = true;
            directedness = EdgeType.UNDIRECTED;
        }
        
        if (!(reading_arcs || reading_edges))
            return nextLine;
        
        boolean is_list = l_pred.evaluate(nextLine);

        // default to always ok here
        // TODO: Do we need some way to assert that parallel edges are
        // not allowed in a graph? To do so here would require that it
        // be part of the graph interface....
        boolean parallel_ok = true;
        	//!PredicateUtils.enforcesNotParallel(g);

        while (br.ready())
        {
            nextLine = br.readLine();
            if (nextLine == null || t_pred.evaluate(nextLine))
                break;
            if (curLine == "") // skip blank lines
                continue;
            
            StringTokenizer st = new StringTokenizer(nextLine.trim());
            
            int vid1 = Integer.parseInt(st.nextToken()) - 1;
            V v1 = id.get(vid1);
            
            if (is_list) // one source, multiple destinations
            {
                do
                {
                    createAddEdge(st, v1, directedness, g, id, parallel_ok);
                } while (st.hasMoreTokens());
            }
            else // one source, one destination, at most one weight
            {
                E e = createAddEdge(st, v1, directedness, g, id, parallel_ok);
                // get the edge weight if we care
                if (nev != null)
                    nev.put(e, new Float(st.nextToken()));
            }
        }
        return nextLine;
    }

    protected E createAddEdge(StringTokenizer st, V v1, 
            EdgeType directed, Graph<V,E> g, List<V> id, boolean parallel_ok)
    {
        int vid2 = Integer.parseInt(st.nextToken()) - 1;
        V v2 = id.get(vid2);
        E e = edgeFactory.create();

        // add this edge if parallel edges are OK,
        // or if this isn't one; otherwise ignore it
        if (parallel_ok || !p_pred.evaluate(e)) {
        	
        	g.addEdge(e, v1, v2, directed);
        }
        return e;
    }
    
    /**
     * Returns the first line read from <code>br</code> for which <code>p</code> 
     * returns <code>true</code>, or <code>null</code> if there is no
     * such line.
     * @throws IOException
     */
    protected String skip(BufferedReader br, Predicate<String> p) throws IOException
    {
        while (br.ready())
        {
            String curLine = br.readLine();
            if (curLine == null)
                break;
            curLine = curLine.trim();
            if (p.evaluate(curLine))
                return curLine;
        }
        return null;
    }
    
    /**
     * Sets or clears the <code>unique_labels</code> boolean.
     * @see #PajekNetReader(boolean, boolean)
     */
    public void setUniqueLabels(boolean unique_labels)
    {
        this.unique_labels = unique_labels;
    }

    /**
     * Sets or clears the <code>get_locations</code> boolean.
     * @see #PajekNetReader(boolean, boolean)
     */
    public void setGetLocations(boolean get_locations)
    {
        this.get_locations = get_locations;
    }
    
    /**
     * A Predicate which evaluates to <code>true</code> if the
     * argument starts with the constructor-specified String.
     * 
     * @author Joshua O'Madadhain
     */
    protected static class StartsWithPredicate implements Predicate<String> {
        private String tag;
        
        public StartsWithPredicate(String s) {
            this.tag = s;
        }
        
        public boolean evaluate(String str) {
            return (str != null && str.toLowerCase().startsWith(tag));
        }
    }
    
    public static class ParallelEdgePredicate<V,E> implements Predicate<E> {
    	Graph<V,E> graph;

		private ParallelEdgePredicate(Graph<V, E> graph) {
			this.graph = graph;
		}

		public boolean evaluate(E e) {
			Pair<V> endpoints = graph.getEndpoints(e);
			V start = endpoints.getFirst();
			V end = endpoints.getSecond();
			Collection<E> outgoing = graph.getOutEdges(start);
			for(E edge : outgoing) {
				if(graph.getEndpoints(edge).getSecond().equals(end)) {
					return true;
				}
			}
			return false;
		}
    	
    }
    
    /**
     * A Predicate which evaluates to <code>true</code> if the
     * argument ends with the string "list".
     * 
     * @author Joshua O'Madadhain
     */
    protected static class ListTagPred implements Predicate<String>
    {
        protected static ListTagPred instance;
        
        protected ListTagPred() {}
        
        public static ListTagPred getInstance()
        {
            if (instance == null)
                instance = new ListTagPred();
            return instance;
        }
        
        public boolean evaluate(String s)
        {
//            String s = (String)arg0;
            return (s != null && s.toLowerCase().endsWith("list"));
        }
    }

	/**
	 * @return the edgeFactory
	 */
	public Factory<E> getEdgeFactory() {
		return edgeFactory;
	}

	/**
	 * @param edgeFactory the edgeFactory to set
	 */
	public void setEdgeFactory(Factory<E> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}

	/**
	 * @return the graphFactory
	 */
	public Factory<? extends Graph<V, E>> getGraphFactory() {
		return graphFactory;
	}

	/**
	 * @param graphFactory the graphFactory to set
	 */
	public void setGraphFactory(Factory<? extends Graph<V, E>> graphFactory) {
		this.graphFactory = graphFactory;
	}

	/**
	 * @return the vertexFactory
	 */
	public Factory<V> getVertexFactory() {
		return vertexFactory;
	}

	/**
	 * @param vertexFactory the vertexFactory to set
	 */
	public void setVertexFactory(Factory<V> vertexFactory) {
		this.vertexFactory = vertexFactory;
	}

	/**
	 * @return the vertexLocationTransformer
	 */
	public Map<V, Point2D> getVertexLocationTransformer() {
		return vertexLocationTransformer;
	}

	/**
	 * @return the vertexLabeller
	 */
	public Map<V, String> getVertexLabeller() {
		return vertexLabeller;
	}
    

}
