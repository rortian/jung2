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

import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.*;
import edu.uci.ics.jung.io.graphml.GraphMetadata.EdgeDefault;

/**
 * Parses graph elements.
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public class GraphElementParser extends AbstractElementParser {

    final private KeyMap keyMap;
    
    public GraphElementParser(ElementParserRegistry registry, KeyMap keyMap) {
        super(registry);
        
        this.keyMap = keyMap;
    }

    @SuppressWarnings("unchecked")
    public GraphMetadata parse(XMLEventReader xmlEventReader, StartElement start)
            throws GraphIOException {

        try {
            // Create the new graph.
            GraphMetadata graph = new GraphMetadata();

            // Parse the attributes.
            Iterator iterator = start.getAttributes();
            while (iterator.hasNext()) {
                Attribute attribute = (Attribute) iterator.next();
                String name = attribute.getName().getLocalPart();
                String value = attribute.getValue();
                if (graph.getId() == null
                        && GraphMLConstants.ID_NAME.equals(name)) {
                    
                    graph.setId(value);
                } else if (graph.getEdgeDefault() == null
                        && GraphMLConstants.EDGEDEFAULT_NAME.equals(name)) {
                    
                    graph.setEdgeDefault(GraphMLConstants.DIRECTED_NAME
                            .equals(value) ? EdgeDefault.DIRECTED
                            : EdgeDefault.UNDIRECTED);
                } else {
                    graph.setProperty(name, value);
                }
            }

            // Make sure the graphdefault has been set.
            if (graph.getEdgeDefault() == null) {
                throw new GraphIOException(
                        "Element 'graph' is missing attribute 'edgedefault'");
            }

            while (xmlEventReader.hasNext()) {

                XMLEvent event = xmlEventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = (StartElement) event;

                    String name = element.getName().getLocalPart();
                    if (GraphMLConstants.DESC_NAME.equals(name)) {
                        String desc = (String) getParser(name).parse(
                                xmlEventReader, element);
                        graph.setDescription(desc);
                    } else if (GraphMLConstants.DATA_NAME.equals(name)) {
                        DataMetadata data = (DataMetadata) getParser(name).parse(
                                xmlEventReader, element);
                        graph.addData(data);
                    } else if (GraphMLConstants.NODE_NAME.equals(name)) {
                        NodeMetadata node = (NodeMetadata) getParser(name).parse(
                                xmlEventReader, element);
                        graph.addNode(node);
                    } else if (GraphMLConstants.EDGE_NAME.equals(name)) {
                        EdgeMetadata edge = (EdgeMetadata) getParser(name).parse(
                                xmlEventReader, element);
                        
                        // Set the directed property if not overridden.
                        if (edge.isDirected() == null) {
                            edge.setDirected(graph.getEdgeDefault() == EdgeDefault.DIRECTED);
                        }
                        
                        graph.addEdge(edge);                        
                    } else if (GraphMLConstants.HYPEREDGE_NAME.equals(name)) {
                        HyperEdgeMetadata edge = (HyperEdgeMetadata) getParser(name).parse(
                                xmlEventReader, element);
                        graph.addHyperEdge(edge);
                    } else {

                        // Treat anything else as unknown
                        getUnknownParser().parse(xmlEventReader, element);
                    }

                }
                if (event.isEndElement()) {
                    EndElement end = (EndElement) event;
                    verifyMatch(start, end);
                    break;
                }
            }
            
            // Apply the keys to this object.
            keyMap.applyKeys(graph);

            return graph;

        } catch (Exception e) {
            ExceptionConverter.convert(e);
        }

        return null;
    }
}
