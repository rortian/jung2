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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.uci.ics.graph.Edges;
import edu.uci.ics.graph.Graph;

/**
 * The default GraphML file handler to use to parse the xml file
 * @author Scott White
 * @author Tom Nelson - converted to jung2
 */
public class GraphMLFileHandler<V,E> extends DefaultHandler {
    private Graph<V,E> mGraph;
    private Map<String,V> mLabeller;
    private Edges default_directed;
    private Factory<V> vertexFactory;
    private Factory<E> edgeFactory;
    private Factory<Graph<V,E>> graphFactory;
    private Map<String,String> graphAttributes = new HashMap<String,String>();

    private Map<E,Map<String,String>> edgeAttributes =
    	LazyMap.decorate(new HashMap<E,Map<String,String>>(), new Factory<Map<String,String>>() {
			public Map<String, String> create() {
				return new HashMap<String,String>();
			}});
    private Map<V,Map<String,String>> vertexAttributes =
    	LazyMap.decorate(new HashMap<V,Map<String,String>>(), new Factory<Map<String,String>>() {
    		public Map<String, String> create() {
    			return new HashMap<String,String>();
    		}});

    /**
     * The default constructor
     */
    public GraphMLFileHandler(Factory<Graph<V,E>> graphFactory, Factory<V> vertexFactory, Factory<E> edgeFactory) {
    	this.graphFactory = graphFactory;
    	this.vertexFactory = vertexFactory;
    	this.edgeFactory = edgeFactory;
    
    }

    protected Graph<V,E> getGraph() {
        return mGraph;
    }

    protected Map<String,V> getLabeller() {
        return mLabeller;
    }

    private Map<String,String> getAttributeMap(Attributes attrs) {
        Map<String,String> map = new HashMap<String,String>();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                map.put(attrs.getQName(i), attrs.getValue(i));
            }
        }
        return map;
    }

    protected E createEdge(Map<String,String> attributeMap) {
        if (mGraph == null) {
            throw new RuntimeException("Error parsing graph. Graph element must be specified before edge element.");
        }

        String sourceId = attributeMap.remove("source");
        V sourceVertex =
                mLabeller.get(sourceId);

        String targetId = attributeMap.remove("target");
        V targetVertex =
                 mLabeller.get(targetId);

        String direction = attributeMap.remove("directed");
        Edges directed;
        if (direction == null)
        {
            // use default_directed
            directed = default_directed;
        }
        else
        {
            // use specified direction
            if (direction.equals("true"))
                directed = Edges.DIRECTED;
            else if (direction.equals("false"))
                directed = Edges.UNDIRECTED;
            else
                throw new RuntimeException("Error parsing graph: 'directed' tag has invalid value: " + direction);
        }
//        Edges e = GraphUtils.addEdge(mGraph, sourceVertex, targetVertex);
        E e = edgeFactory.create();
        mGraph.addEdge(e, sourceVertex, targetVertex, directed);
        
        edgeAttributes.get(e).putAll(attributeMap);
//        for(String key : attributeMap.keySet()) {
//            String value = attributeMap.get(key);
//            Map<String,String> edgeAttributeMap = edgeAttributes.get(e);
//            edgeAttributeMap.put(key, value);
//        }

        return e;
    }

    protected void createGraph(Map<String,String> attributeMap) {
        String edgeDefaultType = attributeMap.remove("edgedefault");
        mGraph = graphFactory.create();
        if (edgeDefaultType.equals("directed")) {
            default_directed = Edges.DIRECTED;
//          mGraph = new DirectedSparseGraph();
        } 
        else if (edgeDefaultType.equals("undirected")) 
        {
            default_directed = Edges.UNDIRECTED;
//            mGraph = new UndirectedSparseGraph();
        } 
        else {
            throw new RuntimeException("Error parsing graph. Edges default type not specified.");
        }

        mLabeller = new HashMap<String,V>();
        	//StringLabeller.getLabeller(mGraph);

        graphAttributes.putAll(attributeMap);
//        for (Iterator keyIt = attributeMap.keySet().iterator(); keyIt.hasNext();) {
//            Object key = keyIt.next();
//            Object value = attributeMap.get(key);
//            mGraph.setUserDatum(key, value, UserData.SHARED);
//        }

    }

    protected V createVertex(Map<String,String> attributeMap) {
        if (mGraph == null) {
            throw new RuntimeException("Error parsing graph. Graph element must be specified before node element.");
        }

        V vertex = vertexFactory.create();
        mGraph.addVertex(vertex);
        	//mGraph.addVertex(new SparseVertex());
        String idString = attributeMap.remove("id");

        if(mLabeller.put(idString, vertex) != null) {
        	throw new RuntimeException("Ids must be unique");
        }
//        try {
//            mLabeller.setLabel((Vertex) vertex,idString);
//        } catch (StringLabeller.UniqueLabelException ule) {
//            throw new FatalException("Ids must be unique");
//
//        }

        vertexAttributes.get(vertex).putAll(attributeMap);
//        for (Iterator keyIt = attributeMap.keySet().iterator();
//             keyIt.hasNext();
//                ) {
//            Object key = keyIt.next();
//            Object value = attributeMap.get(key);
//            vertex.setUserDatum(key, value, UserData.SHARED);
//        }
        return vertex;
    }

    public void startElement(
            String namespaceURI,
            String lName,
            // local name
            String qName, // qualified name
            Attributes attrs) throws SAXException {

        Map<String,String> attributeMap = getAttributeMap(attrs);

        if (qName.toLowerCase().equals("graph")) {
            createGraph(attributeMap);

        } else if (qName.toLowerCase().equals("node")) {
            createVertex(attributeMap);

        } else if (qName.toLowerCase().equals("edge")) {
            createEdge(attributeMap);

        }
    }

	/**
	 * @return the edgeAttributes
	 */
	public Map<E, Map<String, String>> getEdgeAttributes() {
		return edgeAttributes;
	}

	/**
	 * @return the graphAttributes
	 */
	public Map<String, String> getGraphAttributes() {
		return graphAttributes;
	}

	/**
	 * @return the vertexAttributes
	 */
	public Map<V, Map<String, String>> getVertexAttributes() {
		return vertexAttributes;
	}

}
