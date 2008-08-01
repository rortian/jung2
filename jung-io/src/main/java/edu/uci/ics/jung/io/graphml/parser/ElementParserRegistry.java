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

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.io.graphml.GraphMLConstants;
import edu.uci.ics.jung.io.graphml.KeyMap;

/**
 * Registry for all element parsers.
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public class ElementParserRegistry {

    final private Map<String, ElementParser> parserMap = new HashMap<String, ElementParser>();
    
    final private ElementParser unknownElementParser = new UnknownElementParser();
    
    public ElementParserRegistry(KeyMap keyMap) {
    
        parserMap.put(GraphMLConstants.DEFAULT_NAME, new StringElementParser(this));
        parserMap.put(GraphMLConstants.DESC_NAME, new StringElementParser(this));
        parserMap.put(GraphMLConstants.KEY_NAME, new KeyElementParser(this));
        parserMap.put(GraphMLConstants.DATA_NAME, new DataElementParser(this));
        parserMap.put(GraphMLConstants.PORT_NAME, new PortElementParser(this, keyMap));
        parserMap.put(GraphMLConstants.NODE_NAME, new NodeElementParser(this, keyMap));
        parserMap.put(GraphMLConstants.GRAPH_NAME, new GraphElementParser(this, keyMap));
        parserMap.put(GraphMLConstants.ENDPOINT_NAME, new EndpointElementParser(this, keyMap));
        parserMap.put(GraphMLConstants.EDGE_NAME, new EdgeElementParser(this, keyMap));
        parserMap.put(GraphMLConstants.HYPEREDGE_NAME, new HyperEdgeElementParser(this, keyMap));
    }
    
    public ElementParser getUnknownElementParser() {
        return unknownElementParser;
    }
    
    public ElementParser getParser(String localName) {
        ElementParser parser = parserMap.get(localName);
        if( parser == null ) {
            parser = unknownElementParser;
        }
        
        return parser;
    }
}
