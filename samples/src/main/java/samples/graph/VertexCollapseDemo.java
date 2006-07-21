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

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
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
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;


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
    
//    PickedState ps;
    
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
//        ps = vv.getPickedVertexState();
        pr.setVertexPaintFunction(new PickableVertexPaintFunction(vv.getPickedVertexState(), 
                Color.black, Color.red, Color.yellow));
        pr.setEdgePaintFunction(new PickableEdgePaintFunction(vv.getPickedEdgeState(), 
                Color.black, Color.red));
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
//                Map pickedMap = new HashMap();
//                for(Object v : picked) {
//                    pickedMap.put(v, layout.getGraph().getIncidentEdges(v));
//                }
                if(picked.size() > 1) {
                    Graph inGraph = layout.getGraph();
                    Graph clusterGraph = collapser.getClusterGraph(inGraph, picked);
//                    try {
//                        clusterGraph = (Graph)inGraph.getClass().newInstance();
//                    } catch (Exception e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                        return;
//                    }
//                    for(Object v : picked) {
//                      Collection edges = inGraph.getIncidentEdges(v);
//                      for(Object edge : edges) {
//                          Pair endpoints = inGraph.getEndpoints(edge);
//                          Object v1 = endpoints.getFirst();
//                          Object v2 = endpoints.getSecond();
//                          if(picked.containsAll(endpoints)) {
//                          if(inGraph.isDirected(edge)) {
//                              clusterGraph.addDirectedEdge(edge, v1, v2);
//                          } else {
//                              clusterGraph.addEdge(edge, v1, v2);
//                          }
//                          }
//                      }
//                  }

                    Graph g = collapser.collapse(layout.getGraph(), clusterGraph);
                    Point2D p = layout.getLocation(picked.iterator().next());
//                    System.err.("location for "+clusterGraph+" will be "+p);
                    vv.stop();
                    layout.setGraph(g);
                    layout.forceMove(clusterGraph, p.getX(), p.getY());
                }
            }});
        
        JButton expand = new JButton("Expand");
        expand.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Collection picked = vv.getPickedVertexState().getPicked();
                for(Object v : picked) {
                    if(v instanceof Graph) {
                        
                        Graph g = collapser.expand(layout.getGraph(), (Graph)v);
                        
                        vv.stop();
                        layout.setGraph(g);

                        
                        // remove v from the graph and add its components instead
//                        Graph g = layout.getGraph();
                    }
                    vv.repaint();
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
        JPanel collapseControls = new JPanel(new GridLayout(3,1));
        collapseControls.setBorder(BorderFactory.createTitledBorder("Picked"));
        collapseControls.add(collapse);
        collapseControls.add(expand);
        collapseControls.add(reset);
        controls.add(collapseControls);
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    class ClusterVertexShapeFunction extends EllipseVertexShapeFunction {

        @Override
        public Shape getShape(Object v) {
            if(v instanceof Graph) {
                int size = ((Graph)v).getVertices().size();
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
            if(v instanceof Graph) {
                return 30;
            }
            return super.getSize(v);
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


