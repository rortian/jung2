/*
 * Created on Sep 21, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;
import edu.uci.ics.jung.algorithms.util.SettableTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * 
 * @author Joshua O'Madadhain
 * 
 * @link http://graphml.graphdrawing.org/specification.html
 */
public class GraphMLReader<G extends Hypergraph<V,E>, V, E> extends DefaultHandler
{
    protected enum State {NO_TAG, VERTEX, EDGE, HYPEREDGE, ENDPOINT, GRAPH, DATA, KEY, DESC, DEFAULT_KEY, GRAPHML, OTHER};
    
    protected SAXParser saxp;
    protected EdgeType default_edgetype;
    protected G current_graph;
    protected V current_vertex;
    protected E current_edge;
    protected String current_key;
    protected LinkedList<State> current_states;
    protected BidiMap<String, State> tag_state;
    protected Factory<G> graph_factory;
    protected Factory<V> vertex_factory;
    protected Factory<E> edge_factory;
    protected BidiMap<V, String> vertex_ids;
    protected BidiMap<E, String> edge_ids;
    protected Map<String, String> graph_data_descriptions;
    protected Map<String, String> edge_data_descriptions;
    protected Map<String, String> vertex_data_descriptions;
    protected Map<String, SettableTransformer<G, String>> graph_data;
    protected Map<String, SettableTransformer<E, String>> edge_data;
    protected Map<String, SettableTransformer<V, String>> vertex_data;
    protected Map<V, String> vertex_desc;
    protected Map<E, String> edge_desc;
    protected Map<G, String> graph_desc;
    protected Map<String, String> defaults;
    protected Collection<V> hyperedge_vertices;

    protected List<G> graphs;
    
    public GraphMLReader(Factory<V> vertex_factory, Factory<E> edge_factory) 
        throws ParserConfigurationException, SAXException
    {
        current_vertex = null;
        current_edge = null;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        saxp = factory.newSAXParser();

        current_states = new LinkedList<State>();
        
//        current_states.add(State.NO_TAG);
        tag_state = new DualHashBidiMap<String, State>();
        tag_state.put("node", State.VERTEX);
        tag_state.put("edge", State.EDGE);
        tag_state.put("hyperedge", State.HYPEREDGE);
        tag_state.put("endpoint", State.ENDPOINT);
        tag_state.put("graph", State.GRAPH);
        tag_state.put("data", State.DATA);
        tag_state.put("key", State.KEY);
        tag_state.put("desc", State.DESC);
        tag_state.put("default", State.DEFAULT_KEY);
        tag_state.put("graphml", State.GRAPHML);
        
        this.vertex_factory = vertex_factory;
        this.edge_factory = edge_factory;
    }
    
    public List<G> loadMultiple(Reader reader, Factory<G> graph_factory) 
        throws IOException
    {
        this.graph_factory = graph_factory;
        initializeData();
        clearData();
        parse(reader);
      
        return graphs;
    }

    public List<G> loadMultiple(String filename, Factory<G> graph_factory) throws IOException
    {
        return loadMultiple(new FileReader(filename), graph_factory);
    }
    
    /**
     * @see edu.uci.ics.jung.io.GraphReader#load(java.io.Reader)
     */
    public void load(Reader reader, G g) throws IOException
    {
        this.current_graph = g;
        this.graph_factory = null;
        initializeData();
        clearData();
        
        parse(reader);
    }

    public void load(String filename, G g) throws IOException
    {
        load(new FileReader(filename), g);
    }
    
    protected void clearData()
    {
        this.vertex_ids.clear();
        this.vertex_desc.clear();

        this.edge_ids.clear();
        this.edge_desc.clear();

        this.graph_desc.clear();

        this.hyperedge_vertices.clear();
    }

    /**
     * This is separate from initialize() because these data structures are shared among all 
     * graphs loaded (i.e., they're defined inside <code>graphml</code> rather than <code>graph</code>.
     */
    protected void initializeData()
    {
        this.vertex_ids = new DualHashBidiMap<V, String>();
        this.vertex_desc = new HashMap<V, String>();
        this.vertex_data = new HashMap<String, SettableTransformer<V, String>>();
        this.vertex_data_descriptions = new HashMap<String, String>();
        
        this.edge_ids = new DualHashBidiMap<E, String>();
        this.edge_desc = new HashMap<E, String>();
        this.edge_data = new HashMap<String, SettableTransformer<E, String>>();
        this.edge_data_descriptions = new HashMap<String, String>();

        this.graph_desc = new HashMap<G, String>();
        this.graph_data = new HashMap<String, SettableTransformer<G, String>>();
        this.graph_data_descriptions = new HashMap<String, String>();

        this.defaults = new HashMap<String, String>();
        this.hyperedge_vertices = new ArrayList<V>();
    }
    
    protected void parse(Reader reader) throws IOException
    {
        try
        {
            saxp.parse(new InputSource(reader), this);
            reader.close();
        }
        catch (SAXException saxe)
        {
            throw new IOException(saxe.getMessage());
        }
    }
    
    public void startElement(String uri, String name, String qName, Attributes atts) throws SAXNotSupportedException
    {
        String tag = qName.toLowerCase();
        State state = tag_state.get(tag);
        if (state == null)
            state = State.OTHER;

        System.out.println("opening: " + tag);
        System.out.println("elements: " + current_states);
        
        switch (state)
        {
            case GRAPHML:
                break;
                
            case VERTEX:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge != null || this.current_vertex != null)
                    throw new SAXNotSupportedException("Nesting elements not supported");

                this.current_vertex = createVertex(atts);

                // make sure vertex gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (vertex_data.containsKey(s))
                        vertex_data.get(s).set(this.current_vertex, defaults.get(s));
                break;
                
            case ENDPOINT:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge == null)
                    throw new SAXNotSupportedException("No edge defined for endpoint");
                if (this.current_states.getFirst() != State.HYPEREDGE)
                    throw new SAXNotSupportedException("Endpoints must be defined inside hyperedge");
                Map<String, String> endpoint_atts = getAttributeMap(atts);
                String node = endpoint_atts.remove("node");
                if (node == null)
                    throw new SAXNotSupportedException("Endpoint must include an 'id' attribute");
                V v = vertex_ids.getKey(node);
                if (v == null)
                    throw new SAXNotSupportedException("Endpoint refers to nonexistent node ID: " + node);
//                V v = getVertex(id, "Endpoint refers to nonexistent node ID: " + id);
               
                this.current_vertex = v;
                hyperedge_vertices.add(v);
                break;
                
            case EDGE:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge != null || this.current_vertex != null)
                    throw new SAXNotSupportedException("Nesting elements not supported");

                this.current_edge = createEdge(atts, false);
                
                // make sure edge gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (edge_data.containsKey(s))
                        edge_data.get(s).set(this.current_edge, defaults.get(s));
                break;

            case HYPEREDGE:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge != null || this.current_vertex != null)
                    throw new SAXNotSupportedException("Nesting elements not supported");
                
                this.current_edge = createEdge(atts, true);
                
                // make sure edge gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (edge_data.containsKey(s))
                        edge_data.get(s).set(this.current_edge, defaults.get(s));
                break;
            
            case GRAPH:
                if (this.current_graph != null && graph_factory != null)
                    throw new SAXNotSupportedException("Nesting graphs not currently supported");
                
                // graph factory is null if there's only one graph
                if (graph_factory != null)
                    current_graph = graph_factory.create();

                // reset all non-key data structures (to avoid collisions between different graphs)
                clearData();

                // set up default direction of edges
                Map<String, String> graph_atts = getAttributeMap(atts);
                String default_direction = graph_atts.remove("edgedefault");
                if (default_direction == null)
                    throw new SAXNotSupportedException("All graphs must specify a default edge direction");
                if (default_direction.equals("directed"))
                    this.default_edgetype = EdgeType.DIRECTED;
                else if (default_direction.equals("undirected"))
                    this.default_edgetype = EdgeType.UNDIRECTED;
                else
                    throw new SAXNotSupportedException("Invalid or unrecognized default edge direction: " + default_direction);
                
                // make sure graph gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (graph_data.containsKey(s))
                        graph_data.get(s).set(this.current_graph, defaults.get(s));

                // put remaining attribute/value pairs in graph_data
                for (Map.Entry<String, String> entry : graph_atts.entrySet())
                {
                    SettableTransformer<G, String> st = this.graph_data.get(entry.getKey());
                    if (st == null)
                    {
                        st = new MapSettableTransformer<G, String>(new HashMap<G, String>());
                        this.graph_data.put(entry.getKey(), st);
                    }
                    st.set(current_graph, entry.getValue());
                }
                
                break;
                
            case DATA:
                if (this.current_states.contains(State.DATA))
                    throw new SAXNotSupportedException("Nested data not supported");
                handleData(atts);
                break;
                
            case KEY:
                createKey(atts);
                break;
                
                
            default:
                break;
        }
        
        current_states.addFirst(state);
    }

    public void characters(char[] ch, int start, int length) throws SAXNotSupportedException
    {
        String text = new String(ch, start, length);

        System.out.println("inside: " + text);
        
        switch (this.current_states.getFirst())
        {
            case DESC:
                switch (this.current_states.get(1)) // go back one
                {
                    case GRAPH:
                        graph_desc.put(current_graph, text);
                        break;
                    case VERTEX:
                    case ENDPOINT:
                        vertex_desc.put(current_vertex, text);
                        break;
                    case EDGE:
                    case HYPEREDGE:
                        edge_desc.put(current_edge, text);
                        break;
                    case DATA:
                        break;
                    default:
                        break;
                }
                break;
            case DATA:
                switch (this.current_states.get(1))
                {
                    case GRAPH:
                        if (graph_data.containsKey(this.current_key))
                            graph_data.get(this.current_key).set(current_graph, text);
                        else
                            throw new SAXNotSupportedException("key " + this.current_key + " not valid for graphs");
                        break;
                    case VERTEX:
                    case ENDPOINT:
                        if (vertex_data.containsKey(this.current_key))
                            vertex_data.get(this.current_key).set(current_vertex, text);
                        else
                            throw new SAXNotSupportedException("key " + this.current_key + " not valid for nodes");
                        break;
                    case EDGE:
                    case HYPEREDGE:
                        if (edge_data.containsKey(this.current_key))
                            edge_data.get(this.current_key).set(current_edge, text);
                        else
                            throw new SAXNotSupportedException("key " + this.current_key + " not valid for edges/hyperedges");
                        break;
                    default:
                        break;
                }
                break;
            case DEFAULT_KEY:
                if (this.current_states.get(1) != State.KEY)
                    throw new SAXNotSupportedException("'default' only defined in context of 'key' tag: " +
                            "stack: " + current_states.toString());
                defaults.put(this.current_key, text);
                break;
            default:
                break;
        }
    }
    
    public void endElement(String uri, String name, String qName) throws SAXNotSupportedException
    {
        String tag = qName.toLowerCase();
        State state = tag_state.get(tag);
        if (state == null)
            state = State.OTHER;
        if (state == State.OTHER) // || state == State.NO_TAG)
        {
//            state = State.NO_TAG;
            return;
        }

        System.out.println("closing: " + tag);
        System.out.println("elements: " + current_states);
        
        if (state != current_states.getFirst())
            throw new SAXNotSupportedException("Unbalanced tags: opened " + tag_state.getKey(current_states.getFirst()) + 
                    ", closed " + tag);
        
        switch(state)
        {
            case VERTEX:
            case ENDPOINT:
                current_vertex = null;
                break;
                
            case EDGE:
                current_edge = null;
                break;
                
            case HYPEREDGE:
                current_graph.addEdge(current_edge, hyperedge_vertices);
                hyperedge_vertices.clear();
                current_edge = null;
            
            case DATA:
                break;
                
            case GRAPH: 
                current_graph = null;
                break;
                
            case KEY:
                current_key = null;
                break;
                
            default:
                break;
        }
        
        current_states.removeFirst();
    }
    
    protected Map<String, String> getAttributeMap(Attributes atts)
    {
        Map<String,String> att_map = new HashMap<String,String>();
        for (int i = 0; i < atts.getLength(); i++)
            att_map.put(atts.getQName(i), atts.getValue(i));

        return att_map;
    }

    protected void handleData(Attributes atts) throws SAXNotSupportedException
    {
        switch (this.current_states.getFirst())
        {
            case GRAPH:
                break;
            case VERTEX:
            case ENDPOINT:
                break;
            case EDGE:
                break;
            case HYPEREDGE:
                break;
            default:
                throw new SAXNotSupportedException("'data' tag only defined if immediately containing tag is 'graph', 'node', " +
                        "'edge', or 'hyperedge'");
        }
        this.current_key = getAttributeMap(atts).get("key");
        if (this.current_key == null)
            throw new SAXNotSupportedException("'data' tag requires a key specification");
        if (this.current_key.equals(""))
            throw new SAXNotSupportedException("'data' tag requires a non-empty key");
        if (!getGraphData().containsKey(this.current_key) &&
            !getVertexData().containsKey(this.current_key) &&
            !getEdgeData().containsKey(this.current_key))
        {
            throw new SAXNotSupportedException("'data' tag's key specification must reference a defined key");
        }

    }
    
    protected void createKey(Attributes atts)
    {
        Map<String, String> key_atts = getAttributeMap(atts);
        String id = key_atts.remove("id");
        String for_type = key_atts.remove("for");

        if (for_type != null && !for_type.equals(""))
        {
            State type = tag_state.get(for_type);
            switch (type)
            {
                case VERTEX:
                    vertex_data.put(id, new MapSettableTransformer<V, String>(new HashMap<V, String>()));
                    break;
                case EDGE:
                case HYPEREDGE:
                    edge_data.put(id, new MapSettableTransformer<E, String>(new HashMap<E, String>()));
                    break;
                case GRAPH:
                    graph_data.put(id, new MapSettableTransformer<G, String>(new HashMap<G, String>()));
                    break;
                default:
                    if (for_type.equals("all"))
                    {
                        vertex_data.put(id, new MapSettableTransformer<V, String>(new HashMap<V, String>()));
                        edge_data.put(id, new MapSettableTransformer<E, String>(new HashMap<E, String>()));
                        graph_data.put(id, new MapSettableTransformer<G, String>(new HashMap<G, String>()));
                    }
                    break;
            }
        }
        else
        {
            vertex_data.put(id, new MapSettableTransformer<V, String>(new HashMap<V, String>()));
            edge_data.put(id, new MapSettableTransformer<E, String>(new HashMap<E, String>()));
            graph_data.put(id, new MapSettableTransformer<G, String>(new HashMap<G, String>()));
        }
        
        this.current_key = id;
        
    }

    protected V createVertex(Attributes atts) throws SAXNotSupportedException
    {
        Map<String, String> vertex_atts = getAttributeMap(atts);
        String id = vertex_atts.remove("id");
        if (id == null)
            throw new SAXNotSupportedException("node attribute list missing 'source': " + atts.toString());
        V v = vertex_ids.getKey(id);
        
//        V v = getVertex(id, "vertex attribute list missing 'id': " + atts.toString());
        if (v == null)
        {
            v = vertex_factory.create();
            vertex_ids.put(v, id);
            this.current_graph.addVertex(v);

            // put remaining attribute/value pairs in vertex_data
            for (Map.Entry<String, String> entry : vertex_atts.entrySet())
            {
                SettableTransformer<V, String> st = this.vertex_data.get(entry.getKey());
                if (st == null)
                {
                    st = new MapSettableTransformer<V, String>(new HashMap<V, String>());
                    this.vertex_data.put(entry.getKey(), st);
                }
                st.set(v, entry.getValue());
            }
        }
        else
            throw new SAXNotSupportedException("Node id \"" + id + " is a duplicate of an existing node ID");
        return v;
        
    }
    
  
    protected E createEdge(Attributes atts, boolean is_hyperedge) throws SAXNotSupportedException
    {
        Map<String,String> edge_atts = getAttributeMap(atts);

        E e = edge_factory.create();
        String id = edge_atts.remove("id");
        if (id != null)
        {
            if (edge_ids.containsKey(e))
                throw new SAXNotSupportedException("Edge id \"" + id + " is a duplicate of an existing edge ID");
            edge_ids.put(e, id);
        }
        
        if (!is_hyperedge)
        {
            String source_id = edge_atts.remove("source");
            if (source_id == null)
                throw new SAXNotSupportedException("edge attribute list missing 'source': " + atts.toString());
            V source = vertex_ids.getKey(source_id);
            if (source == null)
                throw new SAXNotSupportedException("specified 'source' attribute \"" + id + 
                      "\" does not match any node ID");
            
            String target_id = edge_atts.remove("target");
            if (target_id == null)
                throw new SAXNotSupportedException("edge attribute list missing 'target': " + atts.toString());
            V target = vertex_ids.getKey(target_id);
            if (source == null)
                throw new SAXNotSupportedException("specified 'target' attribute \"" + id + 
                      "\" does not match any node ID");
            
            String direction = edge_atts.remove("directed");
            EdgeType edge_type;
            if (direction == null)
                edge_type = default_edgetype;
            else if (direction.equals("directed"))
                edge_type = EdgeType.DIRECTED;
            else if (direction.equals("undirected"))
                edge_type = EdgeType.UNDIRECTED;
            else
                throw new SAXNotSupportedException("Unrecognized edge direction specifier: " + direction + 
                        ": " + atts.toString());
            
            if (current_graph instanceof Graph)
                ((Graph<V,E>)this.current_graph).addEdge(e, source, target, edge_type);
            else
                this.current_graph.addEdge(e, new Pair<V>(source, target));
        }
        
        // put remaining attribute/value pairs in edge_data
        for (Map.Entry<String, String> entry : edge_atts.entrySet())
        {
            SettableTransformer<E, String> st = this.edge_data.get(entry.getKey());
            if (st == null)
            {
                st = new MapSettableTransformer<E, String>(new HashMap<E, String>());
                this.edge_data.put(entry.getKey(), st);
            }
            st.set(e, entry.getValue());
        }

        return e;
    }
    
//    protected E createHyperedge(Attributes atts) throws SAXNotSupportedException
//    {
//        Map<String,String> edge_atts = getAttributeMap(atts);
//
//        E e = edge_factory.create();
//        String id = edge_atts.remove("id");
//        if (id != null)
//        {
//            if (edge_ids.containsKey(e))
//                throw new SAXNotSupportedException("Hyperedge id \"" + id + " is a duplicate of an existing edge ID");
//            edge_ids.put(e, id);
//        }
//        
//        return e;
//    }

    public BidiMap<V, String> getVertexIDs()
    {
        return vertex_ids;
    }
    
    public BidiMap<E, String> getEdgeIDs()
    {
        return edge_ids;
    }
    
    public Map<String, String> getGraphDataDescriptions()
    {
        return graph_data_descriptions;
    }
    
    public Map<String, String> getVertexDataDescriptions()
    {
        return vertex_data_descriptions;
    }
    
    public Map<String, String> getEdgeDataDescriptions()
    {
        return edge_data_descriptions;
    }
    
    public Map<String, SettableTransformer<G, String>> getGraphData()
    {
        return graph_data;
    }
    
    public Map<String, SettableTransformer<V, String>> getVertexData()
    {
        return vertex_data;
    }
    
    public Map<String, SettableTransformer<E, String>> getEdgeData()
    {
        return edge_data;
    }

    public Map<G, String> getGraphDesc()
    {
        return graph_desc;
    }
    
    public Map<V, String> getVertexDesc()
    {
        return vertex_desc;
    }
    
    public Map<E, String> getEdgeDesc()
    {
        return edge_desc;
    }
    

}
