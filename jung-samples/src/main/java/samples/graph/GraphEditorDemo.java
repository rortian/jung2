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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicIconFactory;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.visualization.ArrowFactory;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.GraphElementAccessor;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.SettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.visualization.layout.AbstractLayout;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.layout.StaticLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Shows how easy it is to create a graph editor with JUNG.
 * Mouse modes and actions are explained in the help text.
 * The application version of GraphEditorDemo provides a
 * File menu with an option to save the visible graph as
 * a jpeg file.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class GraphEditorDemo extends JApplet implements Printable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2023243689258876709L;

	/**
     * the graph
     */
    SimpleSparseGraph<Number,Number> graph;
    
    AbstractLayout<Number,Number> layout;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Number,Number> vv;
    
    DefaultSettableVertexLocationFunction<Number> vertexLocations;
    
    String instructions =
        "<html>"+
        "<h3>All Modes:</h3>"+
        "<ul>"+
        "<li>Right-click an empty area for <b>Create Vertex</b> popup"+
        "<li>Right-click on a Vertex for <b>Delete Vertex</b> popup"+
        "<li>Right-click on a Vertex for <b>Add Edge</b> menus <br>(if there are selected Vertices)"+
        "<li>Right-click on an Edge for <b>Delete Edge</b> popup"+
        "<li>Mousewheel scales with a crossover value of 1.0.<p>"+
        "     - scales the graph layout when the combined scale is greater than 1<p>"+
        "     - scales the graph view when the combined scale is less than 1"+

        "</ul>"+
        "<h3>Editing Mode:</h3>"+
        "<ul>"+
        "<li>Left-click an empty area to create a new Vertex"+
        "<li>Left-click on a Vertex and drag to another Vertex to create an Undirected Edge"+
        "<li>Shift+Left-click on a Vertex and drag to another Vertex to create a Directed Edge"+
        "</ul>"+
        "<h3>Picking Mode:</h3>"+
        "<ul>"+
        "<li>Mouse1 on a Vertex selects the vertex"+
        "<li>Mouse1 elsewhere unselects all Vertices"+
        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
        "</ul>"+
        "<h3>Transforming Mode:</h3>"+
        "<ul>"+
        "<li>Mouse1+drag pans the graph"+
        "<li>Mouse1+Shift+drag rotates the graph"+
        "<li>Mouse1+CTRL(or Command)+drag shears the graph"+
        "</ul>"+
        "</html>";
    
    /**
     * create an instance of a simple graph with popup controls to
     * create a graph.
     * 
     */
    public GraphEditorDemo() {
        
        // allows the precise setting of initial vertex locations
        vertexLocations = new DefaultSettableVertexLocationFunction<Number>();
        
        // create a simple graph for the demo
        graph = new SimpleSparseGraph<Number,Number>();

        this.layout = new StaticLayout<Number,Number>(graph);
        layout.initialize(new Dimension(600,600), vertexLocations);
        
        vv =  new VisualizationViewer<Number,Number>(layout);
        vv.setBackground(Color.white);
//        vv.setPickSupport(new ShapePickSupport());
//        pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
        vv.getRenderContext().setVertexStringer(new Transformer<Number, String>() {

            public String transform(Number v) {
                return v.toString();
            }});

        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        final EditingModalGraphMouse graphMouse = new EditingModalGraphMouse();
        
        // the EditingGraphMouse will pass mouse event coordinates to the
        // vertexLocations function to set the locations of the vertices as
        // they are created
        graphMouse.setVertexLocations(vertexLocations);
        vv.setGraphMouse(graphMouse);
        graphMouse.add(new EditingPopupGraphMousePlugin(vertexLocations));
        graphMouse.setMode(ModalGraphMouse.Mode.EDITING);
        
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
                JOptionPane.showMessageDialog(vv, instructions);
            }});

        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        JComboBox modeBox = graphMouse.getModeComboBox();
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * copy the visible part of the graph to a file as a jpeg image
     * @param file
     */
    public void writeJPEGImage(File file) {
        int width = vv.getWidth();
        int height = vv.getHeight();

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        vv.paint(graphics);
        graphics.dispose();
        
        try {
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int print(java.awt.Graphics graphics,
            java.awt.print.PageFormat pageFormat, int pageIndex)
            throws java.awt.print.PrinterException {
        if (pageIndex > 0) {
            return (Printable.NO_SUCH_PAGE);
        } else {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
            vv.setDoubleBuffered(false);
            g2d.translate(pageFormat.getImageableX(), pageFormat
                    .getImageableY());

            vv.paint(g2d);
            vv.setDoubleBuffered(true);

            return (Printable.PAGE_EXISTS);
        }
    }

    /**
     * a driver for this demo
     */
    @SuppressWarnings("serial")
	public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final GraphEditorDemo demo = new GraphEditorDemo();
        
        JMenu menu = new JMenu("File");
        menu.add(new AbstractAction("Make Image") {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser  = new JFileChooser();
                int option = chooser.showSaveDialog(demo);
                if(option == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    demo.writeJPEGImage(file);
                }
            }});
        menu.add(new AbstractAction("Print") {
            public void actionPerformed(ActionEvent e) {
                    PrinterJob printJob = PrinterJob.getPrinterJob();
                    printJob.setPrintable(demo);
                    if (printJob.printDialog()) {
                        try {
                            printJob.print();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            }});
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(demo);
        frame.pack();
        frame.setVisible(true);
    }
}

class EditingModalGraphMouse extends PluggableGraphMouse 
	implements ModalGraphMouse, ItemSelectable {

	/**
	 * used by the scaling plugins for zoom in
	 */
	protected float in;
	/**
	 * used by the scaling plugins for zoom out
	 */
	protected float out;
	/**
	 * a listener for mode changes
	 */
	protected ItemListener modeListener;
	/**
	 * a JComboBox control available to set the mode
	 */
	protected JComboBox modeBox;
	/**
	 * a menu available to set the mode
	 */
	protected JMenu modeMenu;
	/**
	 * the current mode
	 */
	protected Mode mode;
	/**
	 * listeners for mode changes
	 */
	protected EventListenerList listenerList = new EventListenerList();

	protected GraphMousePlugin pickingPlugin;
	protected GraphMousePlugin translatingPlugin;
	protected GraphMousePlugin animatedPickingPlugin;
	protected GraphMousePlugin scalingPlugin;
	protected GraphMousePlugin rotatingPlugin;
	protected GraphMousePlugin shearingPlugin;
	protected GraphMousePlugin editingPlugin;

	/**
	 * create an instance with default values
	 *
	 */
	public EditingModalGraphMouse() {
		this(1.1f, 1/1.1f);
	}

	/**
	 * create an instance with passed values
	 * @param in override value for scale in
	 * @param out override value for scale out
	 */
	public EditingModalGraphMouse(float in, float out) {
		this.in = in;
		this.out = out;
		loadPlugins();
	}

	/**
	 * create the plugins, and load the plugins for TRANSFORMING mode
	 *
	 */
	protected void loadPlugins() {
		pickingPlugin = new PickingGraphMousePlugin();
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin();
		translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();
		editingPlugin = new EditingGraphMousePlugin();

		add(scalingPlugin);
		setMode(Mode.EDITING);
	}
	public void setVertexLocations(SettableVertexLocationFunction vertexLocations) {
		((EditingGraphMousePlugin)editingPlugin).setVertexLocations(vertexLocations);
	}

	/**
	 * setter for the Mode.
	 */
	public void setMode(Mode mode) {
		if(this.mode != mode) {
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
					this.mode, ItemEvent.DESELECTED));
			this.mode = mode;
			if(mode == Mode.TRANSFORMING) {
				setTransformingMode();
			} else if(mode == Mode.PICKING) {
				setPickingMode();
			} else if(mode == Mode.EDITING) {
				setEditingMode();
			}
			if(modeBox != null) {
				modeBox.setSelectedItem(mode);
			}
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
		}
	}
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#setPickingMode()
	 */
	protected void setPickingMode() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(editingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#setTransformingMode()
	 */
	protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(editingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
	}

	protected void setEditingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		add(editingPlugin);
	}

	/**
	 * @param zoomAtMouse The zoomAtMouse to set.
	 */
	public void setZoomAtMouse(boolean zoomAtMouse) {
		((ScalingGraphMousePlugin) scalingPlugin).setZoomAtMouse(zoomAtMouse);
	}

	/**
	 * listener to set the mode from an external event source
	 */
	class ModeListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			setMode((Mode) e.getItem());
		}
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#getModeListener()
	 */
	public ItemListener getModeListener() {
		if (modeListener == null) {
			modeListener = new ModeListener();
		}
		return modeListener;
	}

	/**
	 * @return Returns the modeBox.
	 */
	public JComboBox getModeComboBox() {
		if(modeBox == null) {
			modeBox = new JComboBox(new Mode[]{Mode.TRANSFORMING, Mode.PICKING, Mode.EDITING});
			modeBox.addItemListener(getModeListener());
		}
		modeBox.setSelectedItem(mode);
		return modeBox;
	}

	/**
	 * create (if necessary) and return a menu that will change
	 * the mode
	 * @return the menu
	 */
	public JMenu getModeMenu() {
		if(modeMenu == null) {
			modeMenu = new JMenu();// {
			Icon icon = BasicIconFactory.getMenuArrowIcon();
			modeMenu.setIcon(BasicIconFactory.getMenuArrowIcon());
			modeMenu.setPreferredSize(new Dimension(icon.getIconWidth()+10, 
					icon.getIconHeight()+10));

			final JRadioButtonMenuItem transformingButton = 
				new JRadioButtonMenuItem(Mode.TRANSFORMING.toString());
			transformingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.TRANSFORMING);
					}
				}});

			final JRadioButtonMenuItem pickingButton =
				new JRadioButtonMenuItem(Mode.PICKING.toString());
			pickingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.PICKING);
					}
				}});

			final JRadioButtonMenuItem editingButton =
				new JRadioButtonMenuItem(Mode.EDITING.toString());
			editingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.EDITING);
					}
				}});

			ButtonGroup radio = new ButtonGroup();
			radio.add(transformingButton);
			radio.add(pickingButton);
			radio.add(editingButton);
			transformingButton.setSelected(true);
			modeMenu.add(transformingButton);
			modeMenu.add(pickingButton);
			modeMenu.add(editingButton);
			modeMenu.setToolTipText("Menu for setting Mouse Mode");
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						if(e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if(e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						} else if(e.getItem() == Mode.EDITING) {
							editingButton.setSelected(true);
						}
					}
				}});
		}
		return modeMenu;
	}

	/**
	 * add a listener for mode changes
	 */
	public void addItemListener(ItemListener aListener) {
		listenerList.add(ItemListener.class,aListener);
	}

	/**
	 * remove a listener for mode changes
	 */
	public void removeItemListener(ItemListener aListener) {
		listenerList.remove(ItemListener.class,aListener);
	}

	/**
	 * Returns an array of all the <code>ItemListener</code>s added
	 * to this JComboBox with addItemListener().
	 *
	 * @return all of the <code>ItemListener</code>s added or an empty
	 *         array if no listeners have been added
	 * @since 1.4
	 */
	public ItemListener[] getItemListeners() {
		return (ItemListener[])listenerList.getListeners(ItemListener.class);
	}

	public Object[] getSelectedObjects() {
		if ( mode == null )
			return new Object[0];
		else {
			Object result[] = new Object[1];
			result[0] = mode;
			return result;
		}
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.
	 * @param e  the event of interest
	 *  
	 * @see EventListenerList
	 */
	protected void fireItemStateChanged(ItemEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = listeners.length-2; i>=0; i-=2 ) {
			if ( listeners[i]==ItemListener.class ) {
				((ItemListener)listeners[i+1]).itemStateChanged(e);
			}
		}
	}   
}

/**
 * a plugin that uses popup menus to create vertices, undirected edges,
 * and directed edges.
 * 
 * @author Tom Nelson - RABA Technologies
 *
 */
class EditingPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin {
    
    SettableVertexLocationFunction vertexLocations;
    
    public EditingPopupGraphMousePlugin(SettableVertexLocationFunction vertexLocations) {
        this.vertexLocations = vertexLocations;
    }

    @SuppressWarnings({ "unchecked", "serial", "serial" })
	protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<Number,Number> vv =
            (VisualizationViewer<Number,Number>)e.getSource();
        final Layout<Number,Number> layout = vv.getGraphLayout();
        final SimpleSparseGraph<Number,Number> graph = (SimpleSparseGraph<Number,Number>)layout.getGraph();
        final Point2D p = e.getPoint();
        final Point2D ivp = vv.inverseViewTransform(e.getPoint());
        GraphElementAccessor<Number,Number> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            
            final Number vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            final Number edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            final PickedState<Number> pickedVertexState = vv.getPickedVertexState();
            final PickedState<Number> pickedEdgeState = vv.getPickedEdgeState();
            JPopupMenu popup = new JPopupMenu();
            
            if(vertex != null) {
                Set<Number> picked = pickedVertexState.getPicked();
                if(picked.size() > 0) {
                    JMenu directedMenu = new JMenu("Create Directed Edge");
                    popup.add(directedMenu);
                    for(final Number other : picked) {
//                        final Number other = iterator.next();
                        directedMenu.add(new AbstractAction("["+other+","+vertex+"]") {
                            public void actionPerformed(ActionEvent e) {
//                                Number newEdge = new Number(other, vertex);
                                graph.addEdge(graph.getEdges().size(), other, vertex);
                                vv.repaint();
                            }
                        });
                    }
                    JMenu undirectedMenu = new JMenu("Create Undirected Edge");
                    popup.add(undirectedMenu);
                    for(final Number other : picked) {
                        undirectedMenu.add(new AbstractAction("[" + other+","+vertex+"]") {
                            public void actionPerformed(ActionEvent e) {
                                graph.addUndirectedEdge(graph.getEdges().size(), other, vertex);
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
                        for(Number vertex : graph.getVertices()) {
//                        		Iterator iterator=graph.getVertices().iterator(); iterator.hasNext(); ) {
                            layout.lockVertex(vertex);
                        }
                        graph.addVertex(newVertex);
                        vv.getModel().restart();
                        for(Number vertex : graph.getVertices()) {
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

/**
 * A plugin that can create vertices, undirected edges, and directed edges
 * using mouse gestures.
 * 
 * @author Tom Nelson - RABA Technologies
 *
 */
class EditingGraphMousePlugin extends AbstractGraphMousePlugin implements
    MouseListener, MouseMotionListener {
    
    SettableVertexLocationFunction vertexLocations;
    Number startVertex;
    Point2D down;
    
    CubicCurve2D rawEdge = new CubicCurve2D.Float();
    Shape edgeShape;
    Shape rawArrowShape;
    Shape arrowShape;
    VisualizationServer.Paintable edgePaintable;
    VisualizationServer.Paintable arrowPaintable;
    boolean edgeIsDirected;
    
    public EditingGraphMousePlugin() {
        this(MouseEvent.BUTTON1_MASK);
    }

    /**
     * create instance and prepare shapes for visual effects
     * @param modifiers
     */
    public EditingGraphMousePlugin(int modifiers) {
        super(modifiers);
        rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50,
                1.0f, 0.0f);
        rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
        edgePaintable = new EdgePaintable();
        arrowPaintable = new ArrowPaintable();
    }
    
    /**
     * sets the vertex locations. Needed to place new vertices
     * @param vertexLocations
     */
    public void setVertexLocations(SettableVertexLocationFunction vertexLocations) {
        this.vertexLocations = vertexLocations;
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
            final VisualizationViewer<Number,Number> vv =
                (VisualizationViewer<Number,Number>)e.getSource();
            final Point2D p = vv.inverseViewTransform(e.getPoint());
            GraphElementAccessor<Number,Number> pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                final Number vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
                if(vertex != null) { // get ready to make an edge
                    startVertex = vertex;
                    down = e.getPoint();
                    transformEdgeShape(down, down);
                    vv.addPostRenderPaintable(edgePaintable);
                    if((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) {
                        edgeIsDirected = true;
                        transformArrowShape(down, e.getPoint());
                        vv.addPostRenderPaintable(arrowPaintable);
                    } 
                } else { // make a new vertex
                    SimpleSparseGraph<Number,Number> graph = 
                    	(SimpleSparseGraph<Number,Number>)vv.getGraphLayout().getGraph();
                    Number newVertex = new Integer(graph.getVertices().size());
                    vertexLocations.setLocation(newVertex, vv.inverseTransform(e.getPoint()));
                    Layout<Number,Number> layout = vv.getModel().getGraphLayout();
                    for(Number lockVertex : graph.getVertices()) {
                        layout.lockVertex(lockVertex);
                    }
                    graph.addVertex(newVertex);
                    vv.getModel().restart();
                    for(Number lockVertex : graph.getVertices()) {
                        layout.unlockVertex(lockVertex);
                    }
                    vv.repaint();
                }
            }
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
            final VisualizationViewer<Number,Number> vv =
                (VisualizationViewer<Number,Number>)e.getSource();
            final Point2D p = vv.inverseViewTransform(e.getPoint());
            Layout<Number,Number> layout = vv.getModel().getGraphLayout();
            GraphElementAccessor<Number,Number> pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                final Number vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                if(vertex != null && startVertex != null) {
                    SimpleSparseGraph<Number,Number> graph = 
                    	(SimpleSparseGraph<Number,Number>)vv.getGraphLayout().getGraph();
                    if(edgeIsDirected) {
                        graph.addDirectedEdge(graph.getEdges().size(), startVertex, vertex);
                    } else {
                        graph.addEdge(graph.getEdges().size(), startVertex, vertex);
                    }
                    vv.repaint();
                }
            }
            startVertex = null;
            down = null;
            edgeIsDirected = false;
            vv.removePostRenderPaintable(edgePaintable);
            vv.removePostRenderPaintable(arrowPaintable);
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
                if(edgeIsDirected) {
                    transformArrowShape(down, e.getPoint());
                }
            }
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
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
