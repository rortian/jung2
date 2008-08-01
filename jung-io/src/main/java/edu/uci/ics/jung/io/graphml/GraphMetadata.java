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

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata structure for the 'graph' GraphML element.
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 * 
 * @see "http://graphml.graphdrawing.org/specification.html"
 */
public class GraphMetadata extends AbstractMetadata {

    public enum EdgeDefault {
        DIRECTED,
        UNDIRECTED
    }
    
    private String id;
    private EdgeDefault edgeDefault;
    private String description;
    private Object graphObject;
    final private List<NodeMetadata> nodes = new ArrayList<NodeMetadata>();
    final private List<EdgeMetadata> edges = new ArrayList<EdgeMetadata>();
    final private List<HyperEdgeMetadata> hyperEdges = new ArrayList<HyperEdgeMetadata>();
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public EdgeDefault getEdgeDefault() {
        return edgeDefault;
    }
    
    public void setEdgeDefault(EdgeDefault edgeDefault) {
        this.edgeDefault = edgeDefault;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    public void addNode(NodeMetadata node) {
        nodes.add(node);
    }
    
    public List<NodeMetadata> getNodes() {
        return nodes;
    }
    
    public void addEdge(EdgeMetadata edge) {
        edges.add(edge);
    }
    
    public List<EdgeMetadata> getEdges() {
        return edges;
    }
    
    public void addHyperEdge(HyperEdgeMetadata hyperEdge) {
        hyperEdges.add(hyperEdge);
    }
    
    public List<HyperEdgeMetadata> getHyperEdges() {
        return hyperEdges;
    }
    
    public Object getGraphObject() {
        return graphObject;
    }

    public void setGraphObject(Object graphObject) {
        this.graphObject = graphObject;
    }

    public MetadataType getMetadataType() {
        return MetadataType.GRAPH;
    }

}
