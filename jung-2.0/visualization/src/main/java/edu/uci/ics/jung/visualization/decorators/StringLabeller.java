/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Jun 13, 2003
 *
 */
package edu.uci.ics.jung.visualization.decorators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * A StringLabeller applies a set of labels to a Graph. The Labeller,
 * specifically, attaches itself to a Graph's UserData, and maintains an index
 * of Strings that are labels. Note that the strings must be unique so that
 * getVertex( label ) will work.
 * 
 * @author danyelf
 *  
 */
public class StringLabeller<V> implements VertexStringer<V> {

	/**
	 * The key that hasLabeller() and getLabeller() use.
	 */
	public static final Object DEFAULT_STRING_LABELER_KEY = "StringLabeller.LabelDefaultKey";
	protected Map<String, V> labelToVertex = new HashMap<String, V>();
	protected Map<V, String> vertexToLabel = new HashMap<V, String>();

	/**
	 * Gets the String label associated with a particular Vertex.
	 * 
	 * @param v
	 *            a Vertex inside the Graph.
	 * @throws FatalException
	 *             if the Vertex is not in the Graph associated with this
	 *             Labeller.
	 */
	public String getLabel(V v) {
			return vertexToLabel.get(v);
	}

	/**
	 * Gets the Vertex from the graph associated with this label.
	 * 
	 * @param label
	 */
	public V getVertex(String label) {
		return labelToVertex.get(label);
	}

	/**
	 * Associates a Vertex with a Label, overrwriting any previous labels on
	 * this vertex.
	 * 
	 * @param v
	 *            a Vertex in the labeller's graph
	 * @param l
	 *            a Label to be associated with this vertex
	 * @throws FatalException
	 *             thrown if this vertex isn't in the Labeller's graph
	 * @throws UniqueLabelException
	 *             thrown if this label is already associated with some other
	 *             vertex.
	 */
	public void setLabel(V v, String l) throws UniqueLabelException {

			if (labelToVertex.containsKey(l)) {
				// we already have a vertex with this label
				throw new UniqueLabelException(l + " is already on vertex "
						+ labelToVertex.get(l));
			}
			// ok, we know we don't have this label anywhere yet
			if (vertexToLabel.containsKey(v)) {
				Object junk = vertexToLabel.get(v);
				labelToVertex.remove(junk);
			}
			vertexToLabel.put(v, l);
			labelToVertex.put(l, v);

	}

	/**
	 * Assigns textual labels to every vertex passed in. Walks through the graph
	 * in iterator order, assigning labels "offset", "offset+1" "offset+2". The
	 * count starts at offset.
	 * 
	 * @param vertices
	 *            The set of Vertices to label. All must be part of this graph.
	 * @param offset
	 *            The starting value to number vertices from
	 * @throws UniqueLabelException
	 *             Is thrown if some other vertexc is already numbered.
	 * @throws FatalException
	 *             if any Vertex is not part of the Graph.
	 */
	public void assignDefaultLabels(Set<V> vertices, int offset)
			throws UniqueLabelException {
		int labelIdx = offset;
		for (V v : vertices) {
			String label = String.valueOf(labelIdx);
			setLabel(v, label);
			labelIdx++;
		}
	}

	/**
	 * A minor class to store exceptions from duplicate labels in the Graph.
	 * 
	 * @author danyelf
	 */
	public static class UniqueLabelException extends Exception {

		public UniqueLabelException(String string) {
			super(string);
		}

	}

	/**
	 * @param string
	 */
	public V removeLabel(String string) {
		if (labelToVertex.containsKey(string)) {
			V v = labelToVertex.get(string);
			labelToVertex.remove(string);
			vertexToLabel.remove(v);
			return v;
		} else {
			return null;
		}

	}

	/**
	 * Wipes the entire table. Resets everything.
	 */
	public void clear() {
		vertexToLabel.clear();
		labelToVertex.clear();
	}

}