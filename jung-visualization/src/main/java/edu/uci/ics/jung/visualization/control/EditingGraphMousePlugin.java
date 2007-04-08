package edu.uci.ics.jung.visualization.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.ArrowFactory;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.cursor.Cursor;
import edu.uci.ics.jung.visualization.event.Event;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;
import edu.uci.ics.jung.visualization.event.MouseMotionListener;

/**
 * A plugin that can create vertices, undirected edges, and directed edges
 * using mouse gestures.
 * 
 * @author Tom Nelson
 *
 */
public class EditingGraphMousePlugin<V,E> extends AbstractGraphMousePlugin implements
    MouseListener, MouseMotionListener {
    
    protected Layout<V,E> layout;
    protected V startVertex;
    protected Point2D down;
    
    protected CubicCurve2D rawEdge = new CubicCurve2D.Float();
    protected Shape edgeShape;
    protected Shape rawArrowShape;
    protected Shape arrowShape;
    protected VisualizationServer.Paintable edgePaintable;
    protected VisualizationServer.Paintable arrowPaintable;
    protected EdgeType edgeIsDirected;
    protected Factory<V> vertexFactory;
    protected Factory<E> edgeFactory;
    
    public EditingGraphMousePlugin(Layout<V,E> layout, Factory<V> vertexFactory, Factory<E> edgeFactory) {
        this(Event.BUTTON1_MASK, layout, vertexFactory, edgeFactory);
    }

    /**
     * create instance and prepare shapes for visual effects
     * @param modifiers
     */
    public EditingGraphMousePlugin(int modifiers, Layout<V,E> layout, Factory<V> vertexFactory, Factory<E> edgeFactory) {
        super(modifiers);
        this.layout = layout;
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50,
                1.0f, 0.0f);
        rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
        edgePaintable = new EdgePaintable();
        arrowPaintable = new ArrowPaintable();
		this.cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
    }
    
    /**
     * sets the vertex locations. Needed to place new vertices
     * @param vertexLocations
     */
    public void setLayout(Layout<V,E> layout) {
        this.layout = layout;
    }
    
    /**
     * overrided to be more flexible, and pass events with
     * key combinations. The default responds to both ButtonOne
     * and ButtonOne+Shift
     */
    public boolean checkModifiers(MouseEvent e) {
        return (e.getModifiers() & modifiers) != 0;
    }

    /**
     * If the mouse is pressed in an empty area, create a new vertex there.
     * If the mouse is pressed on an existing vertex, prepare to create
     * an edge from that vertex to another
     */
    @SuppressWarnings("unchecked")
	public void mousePressed(MouseEvent e) {
        if(checkModifiers(e)) {
            final VisualizationViewer<V,E> vv =
                (VisualizationViewer<V,E>)e.getSource();
            final Point2D p = e.getPoint();//vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
            GraphElementAccessor<V,E> pickSupport = vv.getServer().getPickSupport();
            if(pickSupport != null) {
            	Graph<V,E> graph = vv.getServer().getModel().getGraphLayout().getGraph();
            	// set default edge type
            	if(graph instanceof DirectedGraph) {
            		edgeIsDirected = EdgeType.DIRECTED;
            	} else {
            		edgeIsDirected = EdgeType.UNDIRECTED;
            	}
            	
                final V vertex = pickSupport.getVertex(vv.getServer().getModel().getGraphLayout(), p.getX(), p.getY());
                if(vertex != null) { // get ready to make an edge
                    startVertex = vertex;
                    down = e.getPoint();
                    transformEdgeShape(down, down);
                    vv.getServer().addPostRenderPaintable(edgePaintable);
                    if((e.getModifiers() & Event.SHIFT_MASK) != 0
                    		&& vv.getServer().getModel().getGraphLayout().getGraph() instanceof UndirectedGraph == false) {
                        edgeIsDirected = EdgeType.DIRECTED;
                    }
                    if(edgeIsDirected == EdgeType.DIRECTED) {
                        transformArrowShape(down, e.getPoint());
                        vv.getServer().addPostRenderPaintable(arrowPaintable);
                    }
                } else { // make a new vertex

                    V newVertex = vertexFactory.create();
                    layout.setLocation(newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
                    Layout<V,E> layout = vv.getServer().getModel().getGraphLayout();
                    for(V lockVertex : graph.getVertices()) {
                        layout.lock(lockVertex,true);
                    }
                    graph.addVertex(newVertex);
                    for(V lockVertex : graph.getVertices()) {
                        layout.lock(lockVertex, false);
                    }
                }
            }
            vv.repaint();
        }
    }
    
    /**
     * If startVertex is non-null, and the mouse is released over an
     * existing vertex, create an undirected edge from startVertex to
     * the vertex under the mouse pointer. If shift was also pressed,
     * create a directed edge instead.
     */
    @SuppressWarnings("unchecked")
	public void mouseReleased(MouseEvent e) {
        if(checkModifiers(e)) {
            final VisualizationViewer<V,E> vv =
                (VisualizationViewer<V,E>)e.getSource();
            final Point2D p = e.getPoint();//vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
            Layout<V,E> layout = vv.getServer().getModel().getGraphLayout();
            GraphElementAccessor<V,E> pickSupport = vv.getServer().getPickSupport();
            if(pickSupport != null) {
                final V vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                if(vertex != null && startVertex != null) {
                    Graph<V,E> graph = 
                    	(Graph<V,E>)vv.getGraphLayout().getGraph();
                        graph.addEdge(edgeFactory.create(),
                        		startVertex, vertex, edgeIsDirected);
                    vv.repaint();
                }
            }
            startVertex = null;
            down = null;
            edgeIsDirected = EdgeType.UNDIRECTED;
            vv.getServer().removePostRenderPaintable(edgePaintable);
            vv.getServer().removePostRenderPaintable(arrowPaintable);
        }
    }

    /**
     * If startVertex is non-null, stretch an edge shape between
     * startVertex and the mouse pointer to simulate edge creation
     */
    public void mouseDragged(MouseEvent e) {
        if(checkModifiers(e)) {
            if(startVertex != null) {
                transformEdgeShape(down, e.getPoint());
                if(edgeIsDirected == EdgeType.DIRECTED) {
                    transformArrowShape(down, e.getPoint());
                }
            }
            VisualizationViewer<V,E> vv =
                (VisualizationViewer<V,E>)e.getSource();
            vv.repaint();
        }
    }
    
    /**
     * code lifted from PluggableRenderer to move an edge shape into an
     * arbitrary position
     */
    private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
        
        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        xform.scale(dist / rawEdge.getBounds().getWidth(), 1.0);
        edgeShape = xform.createTransformedShape(rawEdge);
    }
    
    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);
        
        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        arrowShape = xform.createTransformedShape(rawArrowShape);
    }
    
    /**
     * Used for the edge creation visual effect during mouse drag
     */
    class EdgePaintable implements VisualizationServer.Paintable {
        
        public void paint(Graphics g) {
            if(edgeShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D)g).draw(edgeShape);
                g.setColor(oldColor);
            }
        }
        
        public boolean useTransform() {
            return false;
        }
    }
    
    /**
     * Used for the directed edge creation visual effect during mouse drag
     */
    class ArrowPaintable implements VisualizationServer.Paintable {
        
        public void paint(Graphics g) {
            if(arrowShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D)g).fill(arrowShape);
                g.setColor(oldColor);
            }
        }
        
        public boolean useTransform() {
            return false;
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {
        VisualizationViewer c = (VisualizationViewer)e.getSource();
        c.setCursor(cursor);
    }
    public void mouseExited(MouseEvent e) {
    	VisualizationViewer c = (VisualizationViewer)e.getSource();
        c.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    public void mouseMoved(MouseEvent e) {}

	public void mouseDoubleClicked(MouseEvent mouseEvent) {}
}
