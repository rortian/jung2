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

/**
 * 
 * @author Joshua O'Madadhain
 * 
 * @link http://graphml.graphdrawing.org/specification.html
 */
public class GraphMLReader<G extends Hypergraph<V,E>, V, E> extends DefaultHandler
{
    protected enum State {NO_TAG, VERTEX, EDGE, HYPEREDGE, ENDPOINT, GRAPH, DATA, KEY, DESC, DEFAULT_KEY, OTHER};
    
    protected SAXParser saxp;
    protected EdgeType default_directed;
    protected G current_graph;
    protected V current_vertex;
    protected E current_edge;
    protected String current_key;
    protected LinkedList<State> current_states;
    protected Map<String, State> tag_state;
    protected Factory<G> graph_factory;
    protected Factory<V> vertex_factory;
    protected Factory<E> edge_factory;
    protected BidiMap<V, String> vertex_labels;
    protected BidiMap<E, String> edge_labels;
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
        
        current_states.add(State.NO_TAG);
        tag_state = new HashMap<String, State>();
        tag_state.put("node", State.VERTEX);
        tag_state.put("edge", State.EDGE);
        tag_state.put("hyperedge", State.HYPEREDGE);
        tag_state.put("graph", State.GRAPH);
        tag_state.put("data", State.DATA);
        tag_state.put("key", State.KEY);
        tag_state.put("default", State.DEFAULT_KEY);
        
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
        this.vertex_labels.clear();
        this.vertex_desc.clear();

        this.edge_labels.clear();
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
        this.vertex_labels = new DualHashBidiMap<V, String>();
        this.vertex_desc = new HashMap<V, String>();
        this.vertex_data = new HashMap<String, SettableTransformer<V, String>>();
        this.vertex_data_descriptions = new HashMap<String, String>();
        
        this.edge_labels = new DualHashBidiMap<E, String>();
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
        
        switch (state)
        {
            case VERTEX:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge != null || this.current_vertex != null)
                    throw new SAXNotSupportedException("Nesting elements not supported");

                Map<String, String> vertex_atts = getAttributeMap(atts);
                this.current_vertex = getVertex(vertex_atts.remove("id"));

                // make sure vertex gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (vertex_data.containsKey(s))
                        vertex_data.get(s).set(this.current_vertex, defaults.get(s));
                
                break;
                
            case EDGE:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge != null || this.current_vertex != null)
                    throw new SAXNotSupportedException("Nesting elements not supported");

                this.current_edge = getEdge(atts);
                
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
                
                this.current_edge = getHyperedge(atts);
                
                // make sure edge gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (edge_data.containsKey(s))
                        edge_data.get(s).set(this.current_edge, defaults.get(s));
                break;
                
            case ENDPOINT:
                if (this.current_graph == null)
                    throw new SAXNotSupportedException("Graph must be defined prior to elements");
                if (this.current_edge == null)
                    throw new SAXNotSupportedException("No edge defined for endpoint");
                if (this.current_states.getFirst() != State.HYPEREDGE)
                    throw new SAXNotSupportedException("Endpoints must be defined immediately inside hyperedge");
                Map<String, String> endpoint_atts = getAttributeMap(atts);
                V v = getVertex(endpoint_atts.remove("id"));
                this.current_vertex = v;
                hyperedge_vertices.add(v);
                
            
            case GRAPH:
                if (this.current_graph != null && graph_factory != null)
                    throw new SAXNotSupportedException("Nesting graphs not currently supported");
                
                // graph factory is null if there's only one graph
                if (graph_factory != null)
                    current_graph = graph_factory.create();

                // reset all non-key data structures (to avoid accidental collisions between different graphs)
                clearData();
                
                // make sure graph gets a copy of any defaults
                for (String s: defaults.keySet())
                    if (graph_data.containsKey(s))
                        graph_data.get(s).set(this.current_graph, defaults.get(s));

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
            case DEFAULT_KEY:
                if (this.current_states.get(1) != State.KEY)
                    throw new SAXNotSupportedException("'default' only defined in context of 'key' tag");
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
        if (state == State.OTHER || state == State.NO_TAG)
        {
            state = State.NO_TAG;
            return;
        }
        
        if (state != current_states.getFirst())
            throw new SAXNotSupportedException("Unbalanced tags");
        
        switch(state)
        {
            case VERTEX:
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
                    break;
            }
        }
        else
        {
            vertex_data.put(id, new MapSettableTransformer<V, String>(new HashMap<V, String>()));
            edge_data.put(id, new MapSettableTransformer<E, String>(new HashMap<E, String>()));
            graph_data.put(id, new MapSettableTransformer<G, String>(new HashMap<G, String>()));
        }
        
        
    }
    
    protected V getVertex(String id)
    {
        V v = vertex_labels.getKey(id);
        if (v == null)
        {
            v = vertex_factory.create();
            vertex_labels.put(v, id);
            this.current_graph.addVertex(v);
        }
        return v;
    }
    
    protected E getEdge(Attributes atts) throws SAXNotSupportedException
    {
        Map<String,String> edge_atts = getAttributeMap(atts);
        V source = getVertex(edge_atts.remove("source"));
        V target = getVertex(edge_atts.remove("target"));
        String direction = edge_atts.remove("directed");
        EdgeType directed;
        if (direction == null)
            directed = default_directed;
        else if (direction.equals("directed"))
            directed = EdgeType.DIRECTED;
        else if (direction.equals("undirected"))
            directed = EdgeType.UNDIRECTED;
        else
            throw new SAXNotSupportedException("Unrecognized edge direction specifier: " + direction + 
                    ": " + atts.toString());
        
        E e = edge_factory.create();
        String id = edge_atts.remove("id");
        if (id != null)
            edge_labels.put(e, id);
       
        ((Graph<V,E>)this.current_graph).addEdge(e, source, target, directed);
        
        return e;
    }
    
    protected E getHyperedge(Attributes atts)
    {
        Map<String,String> edge_atts = getAttributeMap(atts);

        E e = edge_factory.create();
        String id = edge_atts.remove("id");
        if (id != null)
            edge_labels.put(e, id);
        
        return e;
    }

    public BidiMap<V, String> getVertexLabels()
    {
        return vertex_labels;
    }
    
    public BidiMap<E, String> getEdgeLabels()
    {
        return edge_labels;
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
