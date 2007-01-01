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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.generators.random.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.subLayout.CircularSubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

/**
 * Demonstrates the Cluster, CircularCluster, and ClusteringLayout
 * classes. In this demo, vertices are visually clustered as they
 * are selected. The cluster is formed in a circle centered at the
 * location of the first vertex selected.
 * 
 * @author Tom Nelson
 * 
 */
public class SubLayoutDemo extends JApplet {

    String instructions =
        "<html>Use the mouse to select multiple vertices"+
        "<p>either by dragging a region, or by shift-clicking"+
        "<p>on multiple vertices."+
        "<p>As you select vertices, they become part of a"+
        "<p>cluster, centered at the location of the first"+
        "<p>selected vertex." +
        "<p>You can drag the cluster with the mouse." +
        "<p>Use the 'Picking'/'Transforming' combo-box to switch"+
        "<p>between picking and transforming mode.</html>";
    /**
     * the graph
     */
    Graph<String,Number> graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,Number> vv;

    SubLayoutDecorator<String,Number> clusteringLayout;
    
    PickedState<String> ps;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public SubLayoutDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();

        // ClusteringLayout is a decorator class that delegates
        // to another layout, but can also sepately manage the
        // layout of sub-sets of vertices in circular clusters.
        clusteringLayout = new SubLayoutDecorator<String,Number>(new FRLayout<String,Number>(graph));

        Dimension preferredSize = new Dimension(400,400);
        final VisualizationModel<String,Number> visualizationModel = 
            new DefaultVisualizationModel<String,Number>(clusteringLayout, preferredSize);
        vv =  new VisualizationViewer<String,Number>(visualizationModel, preferredSize);
        
        vv.setPickedVertexState(new ClusterListener<String,Number>(clusteringLayout));
        ps = vv.getPickedVertexState();
        vv.getRenderContext().setEdgeDrawPaintFunction(new PickableEdgePaintTransformer<String,Number>(vv.getPickedEdgeState(), Color.black, Color.red));
        vv.getRenderContext().setVertexFillPaintFunction(new PickableVertexPaintTransformer<String>(vv.getPickedVertexState(), 
                Color.red, Color.yellow));
        vv.setBackground(Color.white);
        
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        
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
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    class ClusterListener<V,E> extends MultiPickedState<V> {

        SubLayoutDecorator<V,E> layout;
        Point2D center;
        /* (non-Javadoc)
         * @see edu.uci.ics.jung.visualization.MultiPickedState#pick(edu.uci.ics.jung.graph.ArchetypeVertex, boolean)
         */
        public boolean pick(V v, boolean picked) {
            boolean result = super.pick(v, picked);
            if(picked) {
                vertexPicked(v);
            } else {
                vertexUnpicked(v);
            }
            return result;
        }
        public ClusterListener(SubLayoutDecorator<V,E> layout) {
            this.layout = layout;
        }
        public void vertexPicked(V v) {
            if(center == null) {
                center = layout.transform(v);
            }
            layout.removeAllSubLayouts();
            Transformer<V,Point2D> subLayout = new CircularSubLayout<V>(getPicked(), 20, center);
            layout.addSubLayout(subLayout);
        }

        public void vertexUnpicked(V v) {
            layout.removeAllSubLayouts();
            if(this.getPicked().isEmpty() == false) {
                Transformer<V,Point2D> subLayout = new CircularSubLayout<V>(getPicked(), 20, center);
                layout.addSubLayout(subLayout);
            } else {
                center = null;
            }
        }
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new SubLayoutDemo());
        f.pack();
        f.setVisible(true);
    }
}
