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
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.graph.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.AbstractLayout;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.layout.StaticLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.HyperbolicTransformer;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.MagnifyTransformer;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;

/**
 * Demonstrates the use of <code>HyperbolicTransform</code>
 * and <code>MagnifyTransform</code>
 * applied to either the model (graph layout) or the view
 * (VisualizationViewer)
 * The hyperbolic transform is applied in an elliptical lens
 * that affects that part of the visualization.
 * 
 * @author Tom Nelson
 * 
 */
public class LensDemo extends JApplet {

    /**
     * the graph
     */
    Graph<String,Number> graph;
    
    Layout<String,Number> graphLayout;
    
    /**
     * a grid shaped graph
     */
    Graph<String,Number> grid;
    
    Layout<String,Number> gridLayout;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,Number> vv;

    /**
     * provides a Hyperbolic lens for the view
     */
    LensSupport hyperbolicViewSupport;
    /**
     * provides a magnification lens for the view
     */
    LensSupport magnifyViewSupport;
    
    /**
     * provides a Hyperbolic lens for the model
     */
    LensSupport hyperbolicLayoutSupport;
    /**
     * provides a magnification lens for the model
     */
    LensSupport magnifyLayoutSupport;
    
    ScalingControl scaler;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public LensDemo() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();
        
        graphLayout = new FRLayout<String,Number>(graph);
        ((FRLayout)graphLayout).setMaxIterations(1000);

        Dimension preferredSize = new Dimension(400,400);
        DefaultSettableVertexLocationFunction<String> vlf =
            new DefaultSettableVertexLocationFunction<String>();
        grid = this.generateVertexGrid(vlf, preferredSize, 25);
        gridLayout = new StaticLayout<String,Number>(grid);
        ((AbstractLayout<String,Number>)gridLayout).initialize(preferredSize, vlf);
        
        final VisualizationModel<String,Number> visualizationModel = 
            new DefaultVisualizationModel<String,Number>(graphLayout, preferredSize);
        vv =  new VisualizationViewer<String,Number>(visualizationModel, preferredSize);

        PickedState<String> ps = vv.getPickedVertexState();
        PickedState<Number> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setVertexFillPaintFunction(new PickableVertexPaintTransformer<String>(ps, Color.red, Color.yellow));
        vv.getRenderContext().setEdgeDrawPaintFunction(new PickableEdgePaintTransformer<String,Number>(pes, Color.black, Color.cyan));
        vv.setBackground(Color.white);
        
        vv.getRenderContext().setVertexStringer(new ToStringLabeller());
        
        final Transformer<String,Shape> ovals = vv.getRenderContext().getVertexShapeFunction();
        final Transformer<String,Shape> squares = 
        	new ConstantTransformer(new Rectangle2D.Float(-10,-10,20,20));

        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        hyperbolicViewSupport = 
            new ViewLensSupport<String,Number>(vv, new HyperbolicShapeTransformer(vv), 
                    new ModalLensGraphMouse());
        hyperbolicLayoutSupport = 
            new LayoutLensSupport<String,Number>(vv, new HyperbolicTransformer(vv, vv.getLayoutTransformer()),
                    new ModalLensGraphMouse());
        magnifyViewSupport = 
            new ViewLensSupport<String,Number>(vv, new MagnifyShapeTransformer(vv),
                    new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
        magnifyLayoutSupport = 
            new LayoutLensSupport<String,Number>(vv, new MagnifyTransformer(vv, vv.getLayoutTransformer()),
                    new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
        hyperbolicLayoutSupport.getLensTransformer().setEllipse(hyperbolicViewSupport.getLensTransformer().getEllipse());
        magnifyViewSupport.getLensTransformer().setEllipse(hyperbolicLayoutSupport.getLensTransformer().getEllipse());
        magnifyLayoutSupport.getLensTransformer().setEllipse(magnifyViewSupport.getLensTransformer().getEllipse());
        
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
        
        ButtonGroup radio = new ButtonGroup();
        JRadioButton normal = new JRadioButton("None");
        normal.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    if(hyperbolicViewSupport != null) {
                        hyperbolicViewSupport.deactivate();
                    }
                    if(hyperbolicLayoutSupport != null) {
                        hyperbolicLayoutSupport.deactivate();
                    }
                    if(magnifyViewSupport != null) {
                        magnifyViewSupport.deactivate();
                    }
                    if(magnifyLayoutSupport != null) {
                        magnifyLayoutSupport.deactivate();
                    }
                    scaler = crossoverScaler;
                }
            }
        });

        final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
        hyperView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });
        final JRadioButton hyperModel = new JRadioButton("Hyperbolic Layout");
        hyperModel.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });
        final JRadioButton magnifyView = new JRadioButton("Magnified View");
        magnifyView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                magnifyViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });
        final JRadioButton magnifyModel = new JRadioButton("Magnified Layout");
        magnifyModel.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                magnifyLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
                scaler = layoutScaler;
            }
        });


        radio.add(normal);
        radio.add(hyperModel);
        radio.add(hyperView);
        radio.add(magnifyModel);
        radio.add(magnifyView);
        normal.setSelected(true);
        
        graphMouse.addItemListener(hyperbolicLayoutSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(magnifyLayoutSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(magnifyViewSupport.getGraphMouse().getModeListener());
        
        
        ButtonGroup graphRadio = new ButtonGroup();
        JRadioButton graphButton = new JRadioButton("Graph");
        graphButton.setSelected(true);
        graphButton.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    visualizationModel.setGraphLayout(graphLayout);
                    vv.getRenderContext().setVertexShapeFunction(ovals);
                    vv.getRenderContext().setVertexStringer(new ToStringLabeller());
                    vv.repaint();
                }
            }});
        JRadioButton gridButton = new JRadioButton("Grid");
        gridButton.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    visualizationModel.setGraphLayout(gridLayout);
                    vv.getRenderContext().setVertexShapeFunction(squares);
                    vv.getRenderContext().setVertexStringer(new ConstantTransformer(null));
                    vv.repaint();
                }
            }});
        graphRadio.add(graphButton);
        graphRadio.add(gridButton);
        
        JPanel modePanel = new JPanel(new GridLayout(3,1));
        modePanel.setBorder(BorderFactory.createTitledBorder("Display"));
        modePanel.add(graphButton);
        modePanel.add(gridButton);
        JRadioButton centerLabels = new JRadioButton("Center Labels");
        centerLabels.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				vv.getRenderContext().setCenterVertexLabel(e.getStateChange() == ItemEvent.SELECTED);
				vv.repaint();
			}});
        modePanel.add(centerLabels);
        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);
        

        Box controls = Box.createHorizontalBox();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel hyperControls = new JPanel(new GridLayout(3,2));
        hyperControls.setBorder(BorderFactory.createTitledBorder("Examiner Lens"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        
        hyperControls.add(normal);
        hyperControls.add(new JLabel());

        hyperControls.add(hyperModel);
        hyperControls.add(magnifyModel);
        
        hyperControls.add(hyperView);
        hyperControls.add(magnifyView);
        
        controls.add(zoomControls);
        controls.add(hyperControls);
        controls.add(modePanel);
        content.add(controls, BorderLayout.SOUTH);
    }

    private Graph<String,Number> generateVertexGrid(DefaultSettableVertexLocationFunction<String> vlf,
            Dimension d, int interval) {
        int count = d.width/interval * d.height/interval;
        Graph<String,Number> graph = new SimpleSparseGraph<String,Number>();
        for(int i=0; i<count; i++) {
            int x = interval*i;
            int y = x / d.width * interval;
            x %= d.width;
            
            Point2D location = new Point2D.Float(x, y);
            String vertex = "v"+i;
            vlf.setLocation(vertex, location);
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
        f.getContentPane().add(new LensDemo());
        f.pack();
        f.setVisible(true);
    }
}
