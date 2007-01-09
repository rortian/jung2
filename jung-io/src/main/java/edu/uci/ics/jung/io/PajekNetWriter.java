/*
 * Created on May 4, 2004
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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Edges;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedGraph;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.util.ConstantMap;

/**
 * Writes graphs in the Pajek NET format.
 * 
 * <p>Labels for vertices may optionally be specified by implementations of 
 * <code>VertexStringer</code>.  Edges weights are optionally specified by 
 * implementations of <code>NumberEdgeValue</code>.  Vertex locations
 * are optionally specified by implementations of 
 * <code>VertexLocationFunction</code>.  Note that vertex location coordinates 
 * must be normalized to the interval [0, 1] on each axis in order to conform to the 
 * Pajek specification.</p>
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson - converted to jung2
 */
public class PajekNetWriter<V,E>
{
    public PajekNetWriter()
    {
    }

    /**
     * Saves <code>g</code> to <code>filename</code>.  Labels for vertices may
     * be supplied by <code>vs</code>.  Edges weights are specified by <code>nev</code>.
     * 
     * @see #save(Graph, Writer, VertexStringer, NumberEdgeValue, VertexLocationFunction)
     * @throws IOException
     */
    public void save(Graph<V,E> g, String filename, Transformer<V,String> vs, 
            Map<E,Number> nev, Transformer<V,Point2D> vld) throws IOException
    {
        save(g, new FileWriter(filename), vs, nev, vld);
    }
    
    public void save(Graph<V,E> g, String filename, Transformer<V,String> vs, 
            Map<E,Number> nev) throws IOException
    {
        save(g, new FileWriter(filename), vs, nev, null);
    }
    
    /**
     * Saves <code>g</code> to <code>filename</code>; no vertex labels are written out,
     * and the edge weights are written as 1.0.
     * 
     * @throws IOException
     */
    public void save(Graph<V,E> g, String filename) throws IOException
    {
        save(g, filename, null, null, null);
    }

    /**
     * Saves <code>g</code> to <code>w</code>; no vertex labels are written out,
     * and the edge weights are written as 1.0.
     * 
     * @throws IOException
     */
    public void save(Graph<V,E> g, Writer w) throws IOException
    {
        save(g, w, null, null, null);
    }

    public void save(Graph<V,E> g, Writer w, Transformer<V,String> vs, 
            Map<E,Number> nev) throws IOException
    {
        save(g, w, vs, nev, null);
    }
    
    /**
     * Writes <code>graph</code> to <code>w</code>.  Labels for vertices may
     * be supplied by <code>vs</code> (defaults to no labels if null), 
     * edge weights may be specified by <code>nev</code>
     * (defaults to weights of 1.0 if null), 
     * and vertex locations may be specified by <code>vld</code> (defaults
     * to no locations if null). 
     */
    @SuppressWarnings("unchecked")
	public void save(Graph<V,E> graph, Writer w, Transformer<V,String> vs, 
    		Map<E,Number> nev, Transformer<V,Point2D> vld) throws IOException
    {
        /*
         * TODO: Changes we might want to make:
         * - optionally writing out in list form
         */
        
        BufferedWriter writer = new BufferedWriter(w);
        if (nev == null)
            nev = new ConstantMap(1);
        writer.write("*Vertices " + graph.getVertices().size());
        writer.newLine();
        
        List<V> id = new ArrayList<V>(graph.getVertices());//Indexer.getIndexer(graph);
        for (V currentVertex : graph.getVertices())
        {
            // convert from 0-based to 1-based index
            int v_id = id.indexOf(currentVertex) + 1;
            writer.write(""+v_id); 
            if (vs != null)
            { 
                String label = vs.transform(currentVertex);
                if (label != null)
                    writer.write (" \"" + label + "\"");
            }
            if (vld != null)
            {
                Point2D location = vld.transform(currentVertex);
                if (location != null)
                    writer.write (" " + location.getX() + " " + location.getY() + " 0.0");
            }
            writer.newLine();
        }

        Collection<E> d_set = new HashSet<E>();
        Collection<E> u_set = new HashSet<E>();

        boolean directed = graph instanceof DirectedGraph;

        boolean undirected = graph instanceof UndirectedGraph;

        // if it's strictly one or the other, no need to create extra sets
        if (directed)
            d_set.addAll(graph.getEdges());
        if (undirected)
            u_set.addAll(graph.getEdges());
        if (!directed && !undirected) // mixed-mode graph
        {
        	u_set.addAll(graph.getEdges());
        	d_set.addAll(graph.getEdges());
        	for(E e : graph.getEdges()) {
        		if(graph.getDirectedness(e) == Edges.UNDIRECTED) {
        			d_set.remove(e);
        		} else {
        			u_set.remove(e);
        		}
        	}
        }

        // write out directed edges
        if (!d_set.isEmpty())
        {
            writer.write("*Arcs");
            writer.newLine();
        }
        for (E e : d_set)
        {
            int source_id = id.indexOf(graph.getEndpoints(e).getFirst()) + 1;
            int target_id = id.indexOf(graph.getEndpoints(e).getSecond()) + 1;
            float weight = nev.get(e).floatValue();
            writer.write(source_id + " " + target_id + " " + weight);
            writer.newLine();
        }

        // write out undirected edges
        if (!u_set.isEmpty())
        {
            writer.write("*Edges");
            writer.newLine();
        }
        for (E e : u_set)
        {
            Pair<V> endpoints = graph.getEndpoints(e);
            int v1_id = id.indexOf(endpoints.getFirst()) + 1;
            int v2_id = id.indexOf(endpoints.getSecond()) + 1;
            float weight = nev.get(e).floatValue();
            writer.write(v1_id + " " + v2_id + " " + weight);
            writer.newLine();
        }
        writer.close();
    }
}
