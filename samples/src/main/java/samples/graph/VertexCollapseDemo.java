/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.uci.ics.graph.DirectedEdge;
import edu.uci.ics.graph.Edge;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.UndirectedEdge;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.graph.DirectedSparseEdge;
import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseEdge;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.visualization.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.Layout;


/**
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class VertexCollapseDemo extends JApplet {

    String instructions =
        "<html>Use the mouse to select multiple vertices"+
        "<p>either by dragging a region, or by shift-clicking"+
        "<p>on multiple vertices."+
        "<p>After you select vertices, use the Collapse button"+
        "<p>to combine them into a single vertex."+
        "<p>You can drag the vertices with the mouse." +
        "<p>Use the 'Picking'/'Transforming' combo-box to switch"+
        "<p>between picking and transforming mode.</html>";
    /**
     * the graph
     */
    Graph graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;
    
    PickedState ps;
    
    Layout layout;
    
    GraphCollapser collapser;
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public VertexCollapseDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();
        collapser = new GraphCollapser(graph);
        
        PluggableRenderer pr = new PluggableRenderer();
        layout = new FRLayout(graph);

        Dimension preferredSize = new Dimension(400,400);
        final VisualizationModel visualizationModel = 
            new DefaultVisualizationModel(layout, preferredSize);
        vv =  new VisualizationViewer(visualizationModel, pr, preferredSize);
        vv.setPickSupport(new ShapePickSupport());
        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        pr.setVertexShapeFunction(new ClusterVertexShapeFunction());
        ((AbstractVertexShapeFunction)pr.getVertexShapeFunction()).setSizeFunction(new ClusterVertexSizeFunction(20));
        
//        vv.setPickedVertexState(new ClusterListener(layout));
        ps = vv.getPickedVertexState();
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(ps, Color.black, Color.red));
        vv.setBackground(Color.white);
        
        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        
        JButton collapse = new JButton("Collapse");
        collapse.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
                if(picked.size() > 1) {
                    Graph g = collapser.collapse(layout.getGraph(), picked);
                    Point2D p = layout.getLocation(picked.iterator().next());
                    vv.stop();
                    layout.setGraph(g);
                    layout.forceMove(picked, p.getX(), p.getY());
                }
            }});
        
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                vv.stop();
                layout.setGraph(graph);
                vv.restart();
            }});
        
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog((JComponent)e.getSource(), instructions, "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        controls.add(zoomControls);
        JPanel collapseControls = new JPanel(new GridLayout(2,1));
        collapseControls.setBorder(BorderFactory.createTitledBorder("Picked"));
        collapseControls.add(collapse);
        collapseControls.add(reset);
        controls.add(collapseControls);
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    class ClusterVertexShapeFunction extends EllipseVertexShapeFunction {

        @Override
        public Shape getShape(Object v) {
            if(v instanceof Collection) {
                int size = ((Collection)v).size();
                if (size < 5) {   
                    int sides = Math.max(size, 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else
                    return factory.getRegularStar(v, size);

//                return factory.getRegularStar(v, ((Collection)v).size());
            }
            return super.getShape(v);
        }
        
    }
    
    class ClusterVertexSizeFunction extends ConstantVertexSizeFunction {

        public ClusterVertexSizeFunction(int size) {
            super(size);
            // TODO Auto-generated constructor stub
        }

        @Override
        public int getSize(Object v) {
            if(v instanceof Collection) {
                return 30;
            }
            return super.getSize(v);
        }
        
    }
    
    public static class GraphCollapser  {
        
        private Graph originalGraph;
        
        public GraphCollapser(Graph originalGraph) {
            this.originalGraph = originalGraph;
        }
        
        public Graph collapse(Graph inGraph, Collection cluster) {
            Graph graph = new SimpleSparseGraph();
            
            if(cluster.size() < 2) return inGraph;
            // add all vertices in the delegate, unless the vertex is in the
            // cluster.
            for(Object v : inGraph.getVertices()) {
                if(cluster.contains(v) == false) {
                        graph.addVertex(v);
                }
            }
            // add the cluster as a vertex
            graph.addVertex(cluster);

            //add all edges from the delegate, unless both endpoints of
            // the edge are in the vertices that are to be collapsed
            for(Edge e : (Collection<Edge>)inGraph.getEdges()) {
                Pair endpoints = e.getEndpoints();
                // don't add edges whose endpoints are both in the cluster
                if(cluster.containsAll(endpoints) == false) {

                    if(cluster.contains(endpoints.getFirst())) {
                        Edge edge = null;
                        if(e instanceof UndirectedEdge) {
                            edge = new UndirectedSparseEdge(cluster,endpoints.getSecond());
                            graph.addEdge(edge);
                        } else if(e instanceof DirectedEdge) {
                            edge = new DirectedSparseEdge(cluster,endpoints.getSecond());
                            graph.addEdge(edge);
                        }
                    } else if(cluster.contains(endpoints.getSecond())) {
                        Edge edge = null;
                        if(e instanceof UndirectedEdge) {
                            edge = new UndirectedSparseEdge<Object>(endpoints.getFirst(), cluster);
                            graph.addEdge(edge);
                        } else if(e instanceof DirectedEdge) {
                            edge = new DirectedSparseEdge<Object>(endpoints.getFirst(), cluster);
                            graph.addEdge(edge);
                        }
                    } else {
                        graph.addEdge(e);
                    }
                }
            }
            return graph;
        }
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new VertexCollapseDemo());
        f.pack();
        f.setVisible(true);
    }
}


