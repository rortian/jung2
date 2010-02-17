package edu.uci.ics.jung.visualization.control;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A plugin that can create vertices, undirected edges, and directed edges using
 * mouse gestures.
 * 
 * @author Tom Nelson
 * 
 */
public class EditingGraphMousePlugin<V, E> extends AbstractGraphMousePlugin
		implements MouseListener, MouseMotionListener {

	protected V startVertex;
	protected Point2D down;
	protected EdgeEffects<V, E> edgeEffects;
	protected EdgeType edgeIsDirected;
	protected Factory<V> vertexFactory;
	protected Factory<E> edgeFactory;

	public EditingGraphMousePlugin(Factory<V> vertexFactory,
			Factory<E> edgeFactory) {
		this(MouseEvent.BUTTON1_MASK, vertexFactory, edgeFactory);
	}

	/**
	 * create instance and prepare shapes for visual effects
	 * 
	 * @param modifiers
	 */
	public EditingGraphMousePlugin(int modifiers, Factory<V> vertexFactory,
			Factory<E> edgeFactory) {
		super(modifiers);
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
		this.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		this.edgeEffects = new CubicCurveEdgeEffects<V, E>();
	}

	/**
	 * Overridden to be more flexible, and pass events with key combinations.
	 * The default responds to both ButtonOne and ButtonOne+Shift
	 */
	@Override
	public boolean checkModifiers(MouseEvent e) {
		return (e.getModifiers() & modifiers) != 0;
	}

	/**
	 * If the mouse is pressed in an empty area, create a new vertex there. If
	 * the mouse is pressed on an existing vertex, prepare to create an edge
	 * from that vertex to another
	 */
	@SuppressWarnings("unchecked")
	public void mousePressed(MouseEvent e) {
		if (checkModifiers(e)) {
			final VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e
					.getSource();
			final Point2D p = e.getPoint();
			GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
			if (pickSupport != null) {
				Graph<V, E> graph = vv.getModel().getGraphLayout().getGraph();
				// set default edge type
				if (graph instanceof DirectedGraph) {
					edgeIsDirected = EdgeType.DIRECTED;
				} else {
					edgeIsDirected = EdgeType.UNDIRECTED;
				}

				final V vertex = pickSupport.getVertex(vv.getModel()
						.getGraphLayout(), p.getX(), p.getY());
				if (vertex != null) { // get ready to make an edge
					startVertex = vertex;
					down = e.getPoint();
					edgeEffects.startEdgeEffects(down, down, vv);
					if ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0
							&& vv.getModel().getGraphLayout().getGraph() instanceof UndirectedGraph == false) {
						edgeIsDirected = EdgeType.DIRECTED;
					}
					if (edgeIsDirected == EdgeType.DIRECTED) {
						edgeEffects.startArrowEffects(down, e.getPoint(), vv);
					}
				} else { // make a new vertex

					V newVertex = vertexFactory.create();
					Layout<V, E> layout = vv.getModel().getGraphLayout();
					graph.addVertex(newVertex);
					layout.setLocation(newVertex, vv.getRenderContext()
							.getMultiLayerTransformer().inverseTransform(
									e.getPoint()));
				}
			}
			vv.repaint();
		}
	}

	/**
	 * If startVertex is non-null, and the mouse is released over an existing
	 * vertex, create an undirected edge from startVertex to the vertex under
	 * the mouse pointer. If shift was also pressed, create a directed edge
	 * instead.
	 */
	@SuppressWarnings("unchecked")
	public void mouseReleased(MouseEvent e) {
		if (checkModifiers(e)) {
			final VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e
					.getSource();
			final Point2D p = e.getPoint();
			Layout<V, E> layout = vv.getModel().getGraphLayout();
			GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
			if (pickSupport != null) {
				final V vertex = pickSupport.getVertex(layout, p.getX(), p
						.getY());
				if (vertex != null && startVertex != null) {
					Graph<V, E> graph = vv.getGraphLayout().getGraph();
					graph.addEdge(edgeFactory.create(), startVertex, vertex,
							edgeIsDirected);
					vv.repaint();
				}
			}
			startVertex = null;
			down = null;
			edgeIsDirected = EdgeType.UNDIRECTED;
			edgeEffects.endEdgeEffects(vv);
			edgeEffects.endArrowEffects(vv);
		}
	}

	/**
	 * If startVertex is non-null, stretch an edge shape between startVertex and
	 * the mouse pointer to simulate edge creation
	 */
	@SuppressWarnings("unchecked")
	public void mouseDragged(MouseEvent e) {
		if (checkModifiers(e)) {
			if (startVertex != null) {
				edgeEffects.transformEdgeShape(down, e.getPoint());
				if (edgeIsDirected == EdgeType.DIRECTED) {
					edgeEffects.transformArrowShape(down, e.getPoint());
				}
			}
			VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e
					.getSource();
			vv.repaint();
		}
	}

	public void setEdgeEffects(EdgeEffects<V, E> edgeEffects) {
		this.edgeEffects = edgeEffects;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		JComponent c = (JComponent) e.getSource();
		c.setCursor(cursor);
	}

	public void mouseExited(MouseEvent e) {
		JComponent c = (JComponent) e.getSource();
		c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void mouseMoved(MouseEvent e) {
	}
}
