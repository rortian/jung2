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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicLabelUI;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.SparseForest;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
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
public class RadialTreeLensDemo extends JApplet {
	
    Graph<String,Integer> graph;
    
    Factory<DirectedGraph<String,Integer>> graphFactory = 
    	new Factory<DirectedGraph<String,Integer>>() {

			public DirectedGraph<String, Integer> create() {
				return new DirectedSparseGraph<String,Integer>();
			}};
			
	Factory<Integer> edgeFactory = new Factory<Integer>() {
		int i=0;
		public Integer create() {
			return i++;
		}};
    
    Factory<String> vertexFactory = new Factory<String>() {
    	int i=0;
		public String create() {
			return "V"+i++;
		}};

		VisualizationServer.Paintable rings;
	    
	    String root;
	    
	    TreeLayout<String,Integer> layout;
	    
	    RadialTreeLayout<String,Integer> radialLayout;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,Integer> vv;

    /**
     * provides a Hyperbolic lens for the view
     */
    LensSupport hyperbolicViewSupport;
    /**
     * provides a magnification lens for the view
     */
//    LensSupport magnifyViewSupport;
    
    /**
     * provides a Hyperbolic lens for the model
     */
//    LensSupport hyperbolicLayoutSupport;
    /**
     * provides a magnification lens for the model
     */
//    LensSupport magnifyLayoutSupport;
    
    ScalingControl scaler;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public RadialTreeLensDemo() {
        
        // create a simple graph for the demo
        // create a simple graph for the demo
        graph = new SparseForest<String,Integer>(graphFactory, edgeFactory);

        createTree();
        
        layout = new TreeLayout<String,Integer>(graph);
        radialLayout = new RadialTreeLayout<String,Integer>(graph);
        radialLayout.setSize(new Dimension(600,600));

        Dimension preferredSize = new Dimension(600,600);
        Map<String,Point2D> map = new HashMap<String,Point2D>();
        Transformer<String,Point2D> vlf =
        	TransformerUtils.mapTransformer(map);
        
        final VisualizationModel<String,Integer> visualizationModel = 
            new DefaultVisualizationModel<String,Integer>(radialLayout, preferredSize);
        vv =  new VisualizationViewer<String,Integer>(visualizationModel, preferredSize);

        PickedState<String> ps = vv.getPickedVertexState();
        PickedState<Integer> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<String>(ps, Color.red, Color.yellow));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<String,Integer>(pes, Color.black, Color.cyan));
        vv.setBackground(Color.white);
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        
        final Transformer<String,Shape> ovals = vv.getRenderContext().getVertexShapeTransformer();
        final Transformer<String,Shape> squares = 
        	new ConstantTransformer(new Rectangle2D.Float(-10,-10,20,20));

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
        rings = new Rings();
		vv.addPostRenderPaintable(rings);

        hyperbolicViewSupport = 
            new ViewLensSupport<String,Integer>(vv, new HyperbolicShapeTransformer(vv, 
            		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)), 
                    new ModalLensGraphMouse());
//        hyperbolicLayoutSupport = 
//            new LayoutLensSupport<String,Integer>(vv, new HyperbolicTransformer(vv, 
//            		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)),
//                    new ModalLensGraphMouse());
//        magnifyViewSupport = 
//            new ViewLensSupport<String,Integer>(vv, new MagnifyShapeTransformer(vv,
//            		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)),
//                    new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
//        magnifyLayoutSupport = 
//            new LayoutLensSupport<String,Integer>(vv, new MagnifyTransformer(vv, 
//            		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)),
//                    new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
//        hyperbolicLayoutSupport.getLensTransformer().setEllipse(hyperbolicViewSupport.getLensTransformer().getEllipse());
//        magnifyViewSupport.getLensTransformer().setEllipse(hyperbolicLayoutSupport.getLensTransformer().getEllipse());
//        magnifyLayoutSupport.getLensTransformer().setEllipse(magnifyViewSupport.getLensTransformer().getEllipse());
        
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
        
//        ButtonGroup radio = new ButtonGroup();
//        JRadioButton normal = new JRadioButton("None");
//        normal.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                if(e.getStateChange() == ItemEvent.SELECTED) {
//                    if(hyperbolicViewSupport != null) {
//                        hyperbolicViewSupport.deactivate();
//                    }
//                    if(hyperbolicLayoutSupport != null) {
//                        hyperbolicLayoutSupport.deactivate();
//                    }
//                    if(magnifyViewSupport != null) {
//                        magnifyViewSupport.deactivate();
//                    }
//                    if(magnifyLayoutSupport != null) {
//                        magnifyLayoutSupport.deactivate();
//                    }
//                }
//            }
//        });

        final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
        hyperView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
//        final JRadioButton hyperModel = new JRadioButton("Hyperbolic Layout");
//        hyperModel.addItemListener(new ItemListener(){
//            public void itemStateChanged(ItemEvent e) {
//                hyperbolicLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
//            }
//        });
//        final JRadioButton magnifyView = new JRadioButton("Magnified View");
//        magnifyView.addItemListener(new ItemListener(){
//            public void itemStateChanged(ItemEvent e) {
//                magnifyViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
//            }
//        });
//        final JRadioButton magnifyModel = new JRadioButton("Magnified Layout");
//        magnifyModel.addItemListener(new ItemListener(){
//            public void itemStateChanged(ItemEvent e) {
//                magnifyLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
//            }
//        });
        JLabel modeLabel = new JLabel("     Mode Menu >>");
        modeLabel.setUI(new VerticalLabelUI(false));
//        radio.add(normal);
//        radio.add(hyperModel);
//        radio.add(hyperView);
//        radio.add(magnifyModel);
//        radio.add(magnifyView);
//        normal.setSelected(true);
        
//        graphMouse.addItemListener(hyperbolicLayoutSupport.getGraphMouse().getModeListener());
        graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());
//        graphMouse.addItemListener(magnifyLayoutSupport.getGraphMouse().getModeListener());
//        graphMouse.addItemListener(magnifyViewSupport.getGraphMouse().getModeListener());
        
        
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
        
//        hyperControls.add(normal);
//        hyperControls.add(new JLabel());
//
//        hyperControls.add(hyperModel);
//        hyperControls.add(magnifyModel);
        
        hyperControls.add(hyperView);
//        hyperControls.add(magnifyView);
        
        controls.add(zoomControls);
        controls.add(hyperControls);
        controls.add(modeLabel);
        content.add(controls, BorderLayout.SOUTH);
    }

    static class VerticalLabelUI extends BasicLabelUI
    {
    	static {
    		labelUI = new VerticalLabelUI(false);
    	}
    	
    	protected boolean clockwise;
    	VerticalLabelUI( boolean clockwise )
    	{
    		super();
    		this.clockwise = clockwise;
    	}
    	

        public Dimension getPreferredSize(JComponent c) 
        {
        	Dimension dim = super.getPreferredSize(c);
        	return new Dimension( dim.height, dim.width );
        }	

        private static Rectangle paintIconR = new Rectangle();
        private static Rectangle paintTextR = new Rectangle();
        private static Rectangle paintViewR = new Rectangle();
        private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

    	public void paint(Graphics g, JComponent c) 
        {

        	
            JLabel label = (JLabel)c;
            String text = label.getText();
            Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

            if ((icon == null) && (text == null)) {
                return;
            }

            FontMetrics fm = g.getFontMetrics();
            paintViewInsets = c.getInsets(paintViewInsets);

            paintViewR.x = paintViewInsets.left;
            paintViewR.y = paintViewInsets.top;
        	
        	// Use inverted height & width
            paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
            paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

            paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
            paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

            String clippedText = 
                layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

        	Graphics2D g2 = (Graphics2D) g;
        	AffineTransform tr = g2.getTransform();
        	if( clockwise )
        	{
    	    	g2.rotate( Math.PI / 2 ); 
        		g2.translate( 0, - c.getWidth() );
        	}
        	else
        	{
    	    	g2.rotate( - Math.PI / 2 ); 
        		g2.translate( - c.getHeight(), 0 );
        	}

        	if (icon != null) {
                icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
            }

            if (text != null) {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fm.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                }
                else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }
        	
        	
        	g2.setTransform( tr );
        }
    }

    private void createTree() {
    	graph.addVertex("V0");
    	graph.addEdge(edgeFactory.create(), "V0", "V1");
    	graph.addEdge(edgeFactory.create(), "V0", "V2");
    	graph.addEdge(edgeFactory.create(), "V1", "V4");
    	graph.addEdge(edgeFactory.create(), "V2", "V3");
    	graph.addEdge(edgeFactory.create(), "V2", "V5");
    	graph.addEdge(edgeFactory.create(), "V4", "V6");
    	graph.addEdge(edgeFactory.create(), "V4", "V7");
    	graph.addEdge(edgeFactory.create(), "V3", "V8");
    	graph.addEdge(edgeFactory.create(), "V6", "V9");
    	graph.addEdge(edgeFactory.create(), "V4", "V10");
    	
       	graph.addVertex("A0");
       	graph.addEdge(edgeFactory.create(), "A0", "A1");
       	graph.addEdge(edgeFactory.create(), "A0", "A2");
       	graph.addEdge(edgeFactory.create(), "A0", "A3");
       	
       	graph.addVertex("B0");
    	graph.addEdge(edgeFactory.create(), "B0", "B1");
    	graph.addEdge(edgeFactory.create(), "B0", "B2");
    	graph.addEdge(edgeFactory.create(), "B1", "B4");
    	graph.addEdge(edgeFactory.create(), "B2", "B3");
    	graph.addEdge(edgeFactory.create(), "B2", "B5");
    	graph.addEdge(edgeFactory.create(), "B4", "B6");
    	graph.addEdge(edgeFactory.create(), "B4", "B7");
    	graph.addEdge(edgeFactory.create(), "B3", "B8");
    	graph.addEdge(edgeFactory.create(), "B6", "B9");
       	
    }

    class Rings implements VisualizationServer.Paintable {
    	
    	Collection<Double> depths;
    	
    	public Rings() {
    		depths = getDepths();
    	}
    	
    	private Collection<Double> getDepths() {
    		Set<Double> depths = new HashSet<Double>();
    		Map<String,PolarPoint> polarLocations = radialLayout.getPolarLocations();
    		for(String v : graph.getVertices()) {
    			PolarPoint pp = polarLocations.get(v);
    			depths.add(pp.getRadius());
    		}
    		return depths;
    	}

		public void paint(Graphics g) {
			g.setColor(Color.gray);
			Graphics2D g2d = (Graphics2D)g;
			Point2D center = radialLayout.getCenter();

			Ellipse2D ellipse = new Ellipse2D.Double();
			for(double d : depths) {
				ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
						center.getX()+d, center.getY()+d);
				Shape shape = 
					vv.getRenderContext().getMultiLayerTransformer().transform(ellipse);
//					vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
				g2d.draw(shape);
			}
		}

		public boolean useTransform() {
			return true;
		}
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new RadialTreeLensDemo());
        f.pack();
        f.setVisible(true);
    }
}
