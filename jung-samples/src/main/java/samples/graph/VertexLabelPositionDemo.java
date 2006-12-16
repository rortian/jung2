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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.graph.generators.random.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * Demonstrates vertex label positioning 
 * controlled by the user.
 * In the AUTO setting, labels are placed according to
 * which quadrant the vertex is in
 * 
 * @author Tom Nelson
 * 
 */
public class VertexLabelPositionDemo extends JApplet {

    /**
     * the graph
     */
    Graph<String,Number> graph;
    
    Layout<String,Number> graphLayout;
    
    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,Number> vv;
    
    ScalingControl scaler;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public VertexLabelPositionDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();
        
        graphLayout = new FRLayout<String,Number>(graph);
        ((FRLayout)graphLayout).setMaxIterations(1000);

        Dimension preferredSize = new Dimension(600,600);
        Map<String,Point2D> map = new HashMap<String,Point2D>();
        Transformer<String,Point2D> vlf =
        	TransformerUtils.mapTransformer(map);
        
        final VisualizationModel<String,Number> visualizationModel = 
            new DefaultVisualizationModel<String,Number>(graphLayout, preferredSize);
        vv =  new VisualizationViewer<String,Number>(visualizationModel, preferredSize);

        PickedState<String> ps = vv.getPickedVertexState();
        PickedState<Number> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setVertexFillPaintFunction(new PickableVertexPaintTransformer<String>(ps, Color.red, Color.yellow));
        vv.getRenderContext().setEdgeDrawPaintFunction(new PickableEdgePaintTransformer<String,Number>(pes, Color.black, Color.cyan));
        vv.setBackground(Color.white);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.W);
        
        vv.getRenderContext().setVertexStringer(new ToStringLabeller());
        
        final Transformer<String,Shape> ovals = vv.getRenderContext().getVertexShapeFunction();

        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
        
        final ScalingControl crossoverScaler = new CrossoverScalingControl();
        final ScalingControl layoutScaler = new LayoutScalingControl();
        scaler = crossoverScaler;

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
        JPanel positionPanel = new JPanel();
        positionPanel.setBorder(BorderFactory.createTitledBorder("Label Position"));
        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);
        JComboBox cb = new JComboBox();
        cb.addItem(Renderer.VertexLabel.Position.N);
        cb.addItem(Renderer.VertexLabel.Position.NE);
        cb.addItem(Renderer.VertexLabel.Position.E);
        cb.addItem(Renderer.VertexLabel.Position.SE);
        cb.addItem(Renderer.VertexLabel.Position.S);
        cb.addItem(Renderer.VertexLabel.Position.SW);
        cb.addItem(Renderer.VertexLabel.Position.W);
        cb.addItem(Renderer.VertexLabel.Position.NW);
        cb.addItem(Renderer.VertexLabel.Position.N);
        cb.addItem(Renderer.VertexLabel.Position.CNTR);
        cb.addItem(Renderer.VertexLabel.Position.AUTO);
        cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Renderer.VertexLabel.Position position = 
					(Renderer.VertexLabel.Position)e.getItem();
				vv.getRenderer().getVertexLabelRenderer().setPosition(position);
				vv.repaint();
			}});
        cb.setSelectedItem(Renderer.VertexLabel.Position.SE);
        positionPanel.add(cb);
        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel hyperControls = new JPanel(new GridLayout(3,2));
        hyperControls.setBorder(BorderFactory.createTitledBorder("Examiner Lens"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        
        controls.add(zoomControls);
        controls.add(positionPanel);
        content.add(controls, BorderLayout.SOUTH);
    }

    private Graph<String,Number> generateVertexGrid(Map<String,Point2D> vlf,
            Dimension d, int interval) {
        int count = d.width/interval * d.height/interval;
        Graph<String,Number> graph = new SimpleSparseGraph<String,Number>();
        for(int i=0; i<count; i++) {
            int x = interval*i;
            int y = x / d.width * interval;
            x %= d.width;
            
            Point2D location = new Point2D.Float(x, y);
            String vertex = "v"+i;
            vlf.put(vertex, location);
            graph.addVertex(vertex);
        }
        return graph;
    }
    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new VertexLabelPositionDemo());
        f.pack();
        f.setVisible(true);
    }
}
