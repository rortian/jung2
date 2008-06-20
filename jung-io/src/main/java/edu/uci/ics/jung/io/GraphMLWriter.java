/*
 * Created on June 16, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Writes graphs out in GraphML format.
 *
 * Known issues: does not indent lines for readability.
 */
public class GraphMLWriter<G extends Hypergraph<V,E>, V,E> 
{
    protected BidiMap<V, String> vertex_ids;
    protected BidiMap<E, String> edge_ids;
    protected Map<String, String> graph_data_descriptions;
    protected Map<String, String> edge_data_descriptions;
    protected Map<String, String> vertex_data_descriptions;
    protected Map<String, Transformer<G, String>> graph_data;
    protected Map<String, Transformer<E, String>> edge_data;
    protected Map<String, Transformer<V, String>> vertex_data;
    protected Transformer<V, String> vertex_desc;
    protected Transformer<E, String> edge_desc;
    protected Transformer<G, String> graph_desc;
	protected Map<String, String> defaults;
	protected boolean directed;
    
	/**
	 * 
	 */
	public GraphMLWriter() 
	{
		
	}
	
	
	/**
	 * 
	 * @param graph
	 * @param w
	 * @return
	 * @throws IOException 
	 */
	public boolean save(G graph, Writer w) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(w);

		// write out boilerplate header
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bw.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"  " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  \n");
		bw.write("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml\">\n");
		
		// write out data specifiers, including defaults
		if (graph_data != null)
		{
			for (String key : graph_data.keySet())
				writeKeySpecification(key, "graph", graph_data_descriptions, bw);
		}
		if (vertex_data != null)
		{
			for (String key : vertex_data.keySet())
				writeKeySpecification(key, "node", vertex_data_descriptions, bw);
			
		}
		if (edge_data != null)
		{
			for (String key : edge_data.keySet())
				writeKeySpecification(key, "edge", edge_data_descriptions, bw);
		}

		// write out graph-level information
		// set edge default direction
		bw.write("<graph edgedefault=\"");
		directed = !(graph instanceof UndirectedGraph);
        if (directed)
            bw.write("directed\">\n");
        else 
            bw.write("undirected\">\n");

        // write graph description, if any
		String desc = graph_desc.transform(graph);
		if (desc != null)
			w.write("<desc>" + desc + "</desc>\n");
		
		// write graph data out if any
		for (String key : graph_data.keySet())
		{
			Transformer<G, String> t = graph_data.get(key);
			String value = t.transform(graph);
			if (value != null)
				w.write(format("data", "key", key, value) + "\n");
		}
        
		// write vertex information
        writeVertexData(graph, bw);
		
		// write edge information
        writeEdgeData(graph, bw);

        // close graph
        w.write("</graph>\n");
        w.write("</graphml>\n");
        
        w.close();

		return true;
	}

//	public boolean save(Collection<G> graphs, Writer w)
//	{
//		return true;
//	}

	protected void writeVertexData(G graph, BufferedWriter w) throws IOException
	{
		for (V v: graph.getVertices())
		{
			String id = (vertex_ids != null) ? vertex_ids.get(v) : v.toString();
			String v_string = String.format("<node id=\"%s\"", id);
			boolean closed = false;
			// write description out if any
			String desc = vertex_desc.transform(v);
			if (desc != null)
			{
				w.write(v_string + ">\n");
				closed = true;
				w.write("<desc>" + desc + "</desc>\n");
			}
			// write data out if any
			for (String key : vertex_data.keySet())
			{
				Transformer<V, String> t = vertex_data.get(key);
				String value = t.transform(v);
				if (value != null)
				{
					if (!closed)
					{
						w.write(v_string + ">\n");
						closed = true;
					}
					w.write(format("data", "key", key, value) + "\n");
				}
			}
			if (!closed)
				w.write(v_string + "/>\n"); // no contents; close the node with "/>"
		}
	}

	protected void writeEdgeData(G g, Writer w) throws IOException
	{
		for (E e: g.getEdges())
		{
			Collection<V> vertices = g.getIncidentVertices(e);
			String id = (edge_ids != null) ? edge_ids.get(e) : null;
			EdgeType edge_type = g.getEdgeType(e);
			String e_string;
			if (edge_type == EdgeType.HYPER)
			{
				e_string = "<hyperedge ";
				// add ID if present
				if (id != null)
					e_string += "id=\"" + id + "\" ";
			}
			else
			{
				Pair<V> endpoints = (Pair<V>)vertices;
				V v1 = endpoints.getFirst();
				V v2 = endpoints.getSecond();
				e_string = "<edge ";
				// add ID if present
				if (id != null)
					e_string += "id=\"" + id + "\" ";
				// add edge type if doesn't match default
				if (directed && edge_type == EdgeType.UNDIRECTED)
					e_string += "directed=\"false\" ";
				if (!directed && edge_type == EdgeType.DIRECTED)
					e_string += "directed=\"true\" ";
				e_string += "source=\"" + vertex_ids.get(v1) + 
					" target = \"" + vertex_ids.get(v2);
			}
			
			boolean closed = false;
			// write description out if any
			String desc = edge_desc.transform(e);
			if (desc != null)
			{
				w.write(e_string + ">\n");
				closed = true;
				w.write("<desc>" + desc + "</desc>\n");
			}
			// write data out if any
			for (String key : edge_data.keySet())
			{
				Transformer<E, String> t = edge_data.get(key);
				String value = t.transform(e);
				if (value != null)
				{
					if (!closed)
					{
						w.write(e_string + ">\n");
						closed = true;
					}
					w.write(format("data", "key", key, value) + "\n");
				}
			}
			// if this is a hyperedge, write endpoints out if any
			if (edge_type == EdgeType.HYPER)
			{
				for (V v : vertices)
				{
					if (!closed)
					{
						w.write(e_string + ">\n");
						closed = true;
					}
					w.write("<endpoint node=\"" + vertex_ids.get(v) + "\"/>\n");
				}
			}
			
			if (!closed)
				w.write(e_string + "/>\n"); // no contents; close the edge with "/>"
		}
	}

	protected void writeKeySpecification(String key, String type, 
			Map<String,String> descriptions, BufferedWriter bw) throws IOException
	{
		bw.write("<key id=\"" + key + "\" for=\"" + type + "\"");
		boolean closed = false;
		// write out description if any
		String desc = descriptions.get(key);
		if (desc != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<desc>" + desc + "</desc>\n");
		}
		// write out default if any
		String def = defaults.get(key);
		if (def != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<default>" + def + "</default>\n");
		}
	}
	
	protected String format(String type, String attr, String value, String contents)
	{
		return String.format("<%s %s=\"%s\">%s</%s>", 
				type, attr, value, contents, type);
	}
	
	/**
	 * Provides an ID that will be used to identify a vertex in the output file.
	 * If the vertex IDs are not set, the ID for each vertex will default to
	 * the output of <code>toString</code> 
	 * (and thus not guaranteed to be unique).
	 * 
	 * @param vertex_ids
	 */
	public void setVertexIDs(BidiMap<V, String> vertex_ids) 
	{
		this.vertex_ids = vertex_ids;
	}



	/**
	 * Provides an ID that will be used to identify an edge in the output file.
	 * If any edge ID is missing, no ID will be written out for the
	 * corresponding edge.
	 * 
	 * @param edge_ids
	 */
	public void setEdgeIDs(BidiMap<E, String> edge_ids) 
	{
		this.edge_ids = edge_ids;
	}



	public void setGraphDataDescriptions(
			Map<String, String> graph_data_descriptions) 
	{
		this.graph_data_descriptions = graph_data_descriptions;
	}



	public void setEdgeDataDescriptions(Map<String, String> edge_data_descriptions) 
	{
		this.edge_data_descriptions = edge_data_descriptions;
	}



	public void setVertexDataDescriptions(
			Map<String, String> vertex_data_descriptions) 
	{
		this.vertex_data_descriptions = vertex_data_descriptions;
	}



	public void setGraphData(Map<String, Transformer<G, String>> graph_data) 
	{
		this.graph_data = graph_data;
	}



	public void setEdgeData(Map<String, Transformer<E, String>> edge_data) 
	{
		this.edge_data = edge_data;
	}



	public void setVertexData(Map<String, Transformer<V, String>> vertex_data) 
	{
		this.vertex_data = vertex_data;
	}

	public void setVertexDescriptions(Transformer<V, String> vertex_desc) 
	{
		this.vertex_desc = vertex_desc;
	}

	public void setEdgeDescriptions(Transformer<E, String> edge_desc) 
	{
		this.edge_desc = edge_desc;
	}

	public void setGraphDescriptions(Transformer<G, String> graph_desc) 
	{
		this.graph_desc = graph_desc;
	}
	
	public void setDefaults(Map<String, String> defaults)
	{
		this.defaults = defaults;
	}
}
