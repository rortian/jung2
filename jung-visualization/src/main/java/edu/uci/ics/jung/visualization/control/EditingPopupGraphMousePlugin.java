package edu.uci.ics.jung.visualization.control;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.GraphElementFactory;
import edu.uci.ics.jung.visualization.GraphElementAccessor;
import edu.uci.ics.jung.visualization.SettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * a plugin that uses popup menus to create vertices, undirected edges,
 * and directed edges.
 * 
 * @author Tom Nelson
 *
 */
public class EditingPopupGraphMousePlugin<V,E> extends AbstractPopupGraphMousePlugin {
    
    protected SettableVertexLocationFunction vertexLocations;
    protected GraphElementFactory<V,E> graphElementFactory;

    public EditingPopupGraphMousePlugin(SettableVertexLocationFunction vertexLocations,
    		GraphElementFactory<V,E> graphElementFactory) {
        this.vertexLocations = vertexLocations;
        this.graphElementFactory = graphElementFactory;
    }

    @SuppressWarnings({ "unchecked", "serial", "serial" })
	protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<V,E> vv =
            (VisualizationViewer<V,E>)e.getSource();
        final Layout<V,E> layout = vv.getGraphLayout();
        final Graph<V,E> graph = layout.getGraph();
        final Point2D p = e.getPoint();
        final Point2D ivp = vv.inverseViewTransform(e.getPoint());
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            
            final V vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            final E edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            final PickedState<V> pickedVertexState = vv.getPickedVertexState();
            final PickedState<E> pickedEdgeState = vv.getPickedEdgeState();
            JPopupMenu popup = new JPopupMenu();
            
            if(vertex != null) {
                Set<V> picked = pickedVertexState.getPicked();
                if(picked.size() > 0) {
                    JMenu directedMenu = new JMenu("Create Directed Edge");
                    popup.add(directedMenu);
                    for(final V other : picked) {
//                        final Number other = iterator.next();
                        directedMenu.add(new AbstractAction("["+other+","+vertex+"]") {
                            public void actionPerformed(ActionEvent e) {
//                                Number newEdge = new Number(other, vertex);
                                graph.addDirectedEdge(graphElementFactory.generateEdge(graph),
//                                		graph.getEdges().size(), 
                                		other, vertex);
                                vv.repaint();
                            }
                        });
                    }
                    JMenu undirectedMenu = new JMenu("Create Undirected Edge");
                    popup.add(undirectedMenu);
                    for(final V other : picked) {
                        undirectedMenu.add(new AbstractAction("[" + other+","+vertex+"]") {
                            public void actionPerformed(ActionEvent e) {
                                graph.addEdge(graphElementFactory.generateEdge(graph),
                                		//graph.getEdges().size(), 
                                		other, vertex);
                                vv.repaint();
                            }
                        });
                    }
                }
                popup.add(new AbstractAction("Delete Vertex") {
                    public void actionPerformed(ActionEvent e) {
                        pickedVertexState.pick(vertex, false);
                        graph.removeVertex(vertex);
                        vv.repaint();
                    }});
            } else if(edge != null) {
                popup.add(new AbstractAction("Delete Edge") {
                    public void actionPerformed(ActionEvent e) {
                        pickedEdgeState.pick(edge, false);
                        graph.removeEdge(edge);
                        vv.repaint();
                    }});
            } else {
                popup.add(new AbstractAction("Create Vertex") {
                    public void actionPerformed(ActionEvent e) {
                        Number newVertex = new Integer(graph.getVertices().size());
                        vertexLocations.setLocation(newVertex, vv.inverseTransform(p));
                        Layout layout = vv.getGraphLayout();
                        for(V vertex : graph.getVertices()) {
                            layout.lockVertex(vertex);
                        }
                        graph.addVertex(graphElementFactory.generateVertex(graph));
                        vv.getModel().restart();
                        for(V vertex : graph.getVertices()) {
                            layout.unlockVertex(vertex);
                        }
                        vv.repaint();
                    }
                });
            }
            if(popup.getComponentCount() > 0) {
                popup.show(vv, e.getX(), e.getY());
            }
        }
    }
}

