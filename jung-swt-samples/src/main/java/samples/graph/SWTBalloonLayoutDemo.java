/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.SparseForest;
import edu.uci.ics.jung.graph.SparseTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.swt.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.swt.VisualizationComposite;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformerDecorator;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;

/**
 * Demonstrates the visualization of a Tree using TreeLayout
 * and BalloonLayout. An examiner lens performing a hyperbolic
 * transformation of the view is also included.
 * 
 * @author Tom Nelson
 * 
 */
public class SWTBalloonLayoutDemo extends Composite {

	/**
	 * the graph
	 */
	Forest<String,Integer> graph;

	Factory<DirectedGraph<String,Integer>> graphFactory = 
		new Factory<DirectedGraph<String,Integer>>() {

		public DirectedGraph<String, Integer> create() {
			return new DirectedSparseMultigraph<String,Integer>();
		}
	};

	Factory<Tree<String,Integer>> treeFactory =
		new Factory<Tree<String,Integer>> () {

		public Tree<String, Integer> create() {
			return new SparseTree<String,Integer>(graphFactory);
		}
	};

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

			/**
			 * the visual component and renderer for the graph
			 */
			VisualizationComposite<String,Integer> vv;

			VisualizationServer.Paintable rings;

			String root;

			TreeLayout<String,Integer> layout;

			BalloonLayout<String,Integer> radialLayout;
			/**
			 * provides a Hyperbolic lens for the view
			 */
			LensSupport hyperbolicViewSupport;

			public SWTBalloonLayoutDemo(Composite parent, int style) {
				super(parent, style);
				setLayout(new GridLayout());

				// create a simple graph for the demo
				graph = new SparseForest<String,Integer>();

				createTree();

				layout = new TreeLayout<String,Integer>(graph);
				layout.setSize(new Dimension(900,900));
				radialLayout = new BalloonLayout<String,Integer>(graph);
				radialLayout.setSize(new Dimension(900,900));
				

				final GraphZoomScrollPane<String,Integer> panel = new GraphZoomScrollPane<String,Integer>(this, SWT.NONE, layout, new Dimension(600,600));
				GridData gridData = new GridData();
		        gridData.grabExcessHorizontalSpace = true;
		        gridData.grabExcessVerticalSpace = true;
		        gridData.horizontalAlignment = GridData.FILL;
		        gridData.verticalAlignment = GridData.FILL;
		        panel.setLayoutData(gridData);
				
				vv =  panel.vv;
				vv.setBackground(Color.white);
				vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<String,Integer>());
				vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
				// add a listener for ToolTips
				vv.setVertexToolTipTransformer(new ToStringLabeller<String>());
				vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
				rings = new Rings(radialLayout);
				
				GridData gd = new GridData();
				gd.grabExcessHorizontalSpace = true;
				gd.grabExcessVerticalSpace = true;
				gd.horizontalAlignment = GridData.FILL;
				gd.verticalAlignment = GridData.FILL;
				vv.getComposite().setLayoutData(gd);

				final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

				vv.setGraphMouse(graphMouse);
				vv.addKeyListener(graphMouse.getModeKeyListener());

				hyperbolicViewSupport = 
					new ViewLensSupport<String,Integer>(vv, new HyperbolicShapeTransformer(vv.getScreenDevice(), 
							vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)), 
							new ModalLensGraphMouse());


				graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());

				final ScalingControl scaler = new CrossoverScalingControl();
				vv.scaleToLayout(scaler);

				Group controls = new Group(this, SWT.NONE);
				GridData gdc = new GridData();
				gdc.horizontalAlignment = GridData.CENTER;
				controls.setLayoutData(gdc);
				
				GridLayout cl = new GridLayout();
				cl.numColumns = 4;
				controls.setLayout(cl);
				controls.setText("controls");

				Button radial = new Button(controls, SWT.TOGGLE);
				radial.setText("Balloon");
				radial.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {}
					public void widgetSelected(SelectionEvent e) {
						if( ((Button)e.widget).getSelection() ) {
							vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
							vv.setGraphLayout(radialLayout);
							vv.scaleToLayout(scaler);
							vv.getServer().addPreRenderPaintable(rings);
						} else {
							vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
							vv.setGraphLayout(layout);
							vv.scaleToLayout(scaler);
							vv.getServer().removePreRenderPaintable(rings);
						}
						vv.repaint();
					}});


				Group zoom = new Group(controls, SWT.NONE);
				GridLayout zcl = new GridLayout();
				zcl.numColumns = 2;
				zoom.setLayout(zcl);
				zoom.setText("zoom");

				Button plus = new Button(zoom, SWT.PUSH);
				plus.setText("+");
				plus.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {}
					public void widgetSelected(SelectionEvent e) {
						scaler.scale(vv.getServer(), 1.1f, vv.getCenter());
					}
				});
				Button minus = new Button(zoom, SWT.PUSH);
				minus.setText("-");
				minus.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {}
					public void widgetSelected(SelectionEvent e) {
						scaler.scale(vv.getServer(), 1/1.1f, vv.getCenter());
					}
				});

				final Combo combo = new Combo(controls, SWT.READ_ONLY);
				combo.setItems(new String[]{"Transforming", "Picking"});
				combo.select(0);
				combo.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {}
					public void widgetSelected(SelectionEvent e) {
						int i = combo.getSelectionIndex();
						switch (i) {
						case 0:
							graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
							break;
						case 1:
							graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
							break;
						}
					}
				});


				final Button hyperView = new Button(controls, SWT.TOGGLE);
				hyperView.setText("Hyperbolic View");
				hyperView.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {}
					public void widgetSelected(SelectionEvent e) {
						hyperbolicViewSupport.activate(hyperView.getSelection());
					}
				});
			}

			class Rings implements VisualizationServer.Paintable {

				BalloonLayout<String,Integer> layout;

				public Rings(BalloonLayout<String,Integer> layout) {
					this.layout = layout;
				}

				public void paint(GraphicsContext g) {
					g.setColor(Color.gray);

					Ellipse2D ellipse = new Ellipse2D.Double();
					for(String v : layout.getGraph().getVertices()) {
						Double radius = layout.getRadii().get(v);
						if(radius == null) continue;
						Point2D p = layout.transform(v);
						ellipse.setFrame(-radius, -radius, 2*radius, 2*radius);
						AffineTransform at = AffineTransform.getTranslateInstance(p.getX(), p.getY());
						Shape shape = at.createTransformedShape(ellipse);

						MutableTransformer viewTransformer =
							vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);

						if(viewTransformer instanceof MutableTransformerDecorator) {
							shape = vv.getRenderContext().getMultiLayerTransformer().transform(shape);
						} else {
							shape = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT,shape);
						}

						g.draw(shape);
					}
				}

				public boolean useTransform() {
					return true;
				}
			}

			/**
			 * 
			 */
			private void createTree() {

				graph.addVertex("A0");
				graph.addEdge(edgeFactory.create(), "A0", "B0");
				graph.addEdge(edgeFactory.create(), "A0", "B1");
				graph.addEdge(edgeFactory.create(), "A0", "B2");

				graph.addEdge(edgeFactory.create(), "B0", "C0");
				graph.addEdge(edgeFactory.create(), "B0", "C1");
				graph.addEdge(edgeFactory.create(), "B0", "C2");
				graph.addEdge(edgeFactory.create(), "B0", "C3");

				graph.addEdge(edgeFactory.create(), "C2", "H0");
				graph.addEdge(edgeFactory.create(), "C2", "H1");

				graph.addEdge(edgeFactory.create(), "B1", "D0");
				graph.addEdge(edgeFactory.create(), "B1", "D1");
				graph.addEdge(edgeFactory.create(), "B1", "D2");

				graph.addEdge(edgeFactory.create(), "B2", "E0");
				graph.addEdge(edgeFactory.create(), "B2", "E1");
				graph.addEdge(edgeFactory.create(), "B2", "E2");

				graph.addEdge(edgeFactory.create(), "D0", "F0");
				graph.addEdge(edgeFactory.create(), "D0", "F1");
				graph.addEdge(edgeFactory.create(), "D0", "F2");

				graph.addEdge(edgeFactory.create(), "D1", "G0");
				graph.addEdge(edgeFactory.create(), "D1", "G1");
				graph.addEdge(edgeFactory.create(), "D1", "G2");
				graph.addEdge(edgeFactory.create(), "D1", "G3");
				graph.addEdge(edgeFactory.create(), "D1", "G4");
				graph.addEdge(edgeFactory.create(), "D1", "G5");
				graph.addEdge(edgeFactory.create(), "D1", "G6");
				graph.addEdge(edgeFactory.create(), "D1", "G7");

				// uncomment this to make it a Forest:
//				graph.addVertex("K0");
//				graph.addEdge(edgeFactory.create(), "K0", "K1");
//				graph.addEdge(edgeFactory.create(), "K0", "K2");
//				graph.addEdge(edgeFactory.create(), "K0", "K3");

//				graph.addVertex("J0");
//				graph.addEdge(edgeFactory.create(), "J0", "J1");
//				graph.addEdge(edgeFactory.create(), "J0", "J2");
//				graph.addEdge(edgeFactory.create(), "J1", "J4");
//				graph.addEdge(edgeFactory.create(), "J2", "J3");
////				graph.addEdge(edgeFactory.create(), "J2", "J5");
////				graph.addEdge(edgeFactory.create(), "J4", "J6");
////				graph.addEdge(edgeFactory.create(), "J4", "J7");
////				graph.addEdge(edgeFactory.create(), "J3", "J8");
////				graph.addEdge(edgeFactory.create(), "J6", "B9");


			}

			/**
			 * a driver for this demo
			 */
			public static void main(String[] args) {
				Display display = new Display();
				Shell shell = new Shell(display);
				shell.setText("SWT Image");
				shell.setLayout(new FillLayout());

				new SWTBalloonLayoutDemo(shell, SWT.NONE);

				shell.open ();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch ()) display.sleep ();
				}
				display.dispose ();
			}
}
