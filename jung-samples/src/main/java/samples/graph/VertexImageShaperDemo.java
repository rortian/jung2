/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;
import edu.uci.ics.jung.visualization.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.FourPassImageShaper;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconFunction;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeFunction;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Demonstrates the use of images to represent graph vertices.
 * The images are supplied via the VertexShapeFunction so that
 * both the image and its shape can be utilized.
 * 
 * The images used in this demo (courtesy of slashdot.org) are
 * rectangular but with a transparent background. When vertices
 * are represented by these images, it looks better if the actual
 * shape of the opaque part of the image is computed so that the
 * edge arrowheads follow the visual shape of the image. This demo
 * uses the FourPassImageShaper class to compute the Shape from
 * an image with transparent background.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class VertexImageShaperDemo extends JApplet {

    /**
     * the graph
     */
    SimpleDirectedSparseGraph<Number, Number> graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Number, Number> vv;
    
    /**
     * some icon names to use
     */
    String[] iconNames = {
            "apple",
            "os",
            "x",
            "linux",
            "inputdevices",
            "wireless",
            "graphics3",
            "gamespcgames",
            "humor",
            "music",
            "privacy"
    };
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * 
     */
    public VertexImageShaperDemo() {
        
        // create a simple graph for the demo
        graph = new SimpleDirectedSparseGraph<Number,Number>();
        Number[] vertices = createVertices(11);
        
        // a Map for the labels
        Map<Number,String> map = new HashMap<Number,String>();
        for(int i=0; i<vertices.length; i++) {
            map.put(vertices[i], iconNames[i%iconNames.length]);
        }
        
        // a Map for the Icons
        Map<Number,Icon> iconMap = new HashMap<Number,Icon>();
        for(int i=0; i<vertices.length; i++) {
            String name = "/images/topic"+iconNames[i]+".gif";
            try {
                Icon icon = 
                    new LayeredIcon(new ImageIcon(VertexImageShaperDemo.class.getResource(name)).getImage());
                iconMap.put(vertices[i], icon);
            } catch(Exception ex) {
                System.err.println("You need slashdoticons.jar in your classpath to see the image "+name);
            }
        }
        
        createEdges(vertices);
        
        FRLayout<Number, Number> layout = new FRLayout<Number, Number>(graph);
        layout.setMaxIterations(100);
        vv =  new VisualizationViewer<Number, Number>(layout, new Dimension(400,400));
        
        // This demo uses a special renderer to turn outlines on and off.
        // you do not need to do this in a real application. Just use a PluggableRender
        vv.getRenderer().setVertexRenderer(new DemoRenderer<Number,Number>());

        Transformer<Number,Paint> vpf = 
            new PickableVertexPaintFunction<Number>(vv.getPickedVertexState(), Color.white, Color.yellow);
        vv.getRenderContext().setVertexFillPaintFunction(vpf);
        vv.getRenderContext().setEdgeDrawPaintFunction(new PickableEdgePaintFunction<Number, Number>(vv.getPickedEdgeState(), Color.black, Color.cyan));

        vv.setBackground(Color.white);
        
        final Transformer<Number,String> vertexStringerImpl = 
            new VertexStringerImpl<Number,String>(map);
        vv.getRenderContext().setVertexStringer(vertexStringerImpl);
        vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.cyan));
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
        
        
        // For this demo only, I use a special class that lets me turn various
        // features on and off. For a real application, use VertexIconAndShapeFunction instead.
        final DemoVertexImageShapeFunction<Number> vertexImageShapeFunction =
            new DemoVertexImageShapeFunction<Number>(new EllipseVertexShapeFunction<Number>());
        
        final DemoVertexIconFunction<Number> vertexIconFunction =
        	new DemoVertexIconFunction<Number>();
        
        vertexImageShapeFunction.setIconMap(iconMap);
        vertexIconFunction.setIconMap(iconMap);
        
        vv.getRenderContext().setVertexShapeFunction(vertexImageShapeFunction);
        vv.getRenderContext().setVertexIconFunction(vertexIconFunction);

        
        // Get the pickedState and add a listener that will decorate the
        // Vertex images with a checkmark icon when they are picked
        PickedState<Number> ps = vv.getPickedVertexState();
        ps.addItemListener(new PickWithIconListener(vertexIconFunction));
        
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable(){
            int x;
            int y;
            Font font;
            FontMetrics metrics;
            int swidth;
            int sheight;
            String str = "Thank You, slashdot.org, for the images!";
            
            public void paint(Graphics g) {
                Dimension d = vv.getSize();
                if(font == null) {
                    font = new Font(g.getFont().getName(), Font.BOLD, 20);
                    metrics = g.getFontMetrics(font);
                    swidth = metrics.stringWidth(str);
                    sheight = metrics.getMaxAscent()+metrics.getMaxDescent();
                    x = (d.width-swidth)/2;
                    y = (int)(d.height-sheight*1.5);
                }
                g.setFont(font);
                Color oldColor = g.getColor();
                g.setColor(Color.lightGray);
                g.drawString(str, x, y);
                g.setColor(oldColor);
            }
            public boolean useTransform() {
                return false;
            }
        });

        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        
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

        JCheckBox shape = new JCheckBox("Shape");
        shape.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent e) {
                vertexImageShapeFunction.setShapeImages(e.getStateChange()==ItemEvent.SELECTED);
                vv.repaint();
            }
        });
        shape.setSelected(true);

        JCheckBox fill = new JCheckBox("Fill");
        fill.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent e) {
                vertexIconFunction.setFillImages(e.getStateChange()==ItemEvent.SELECTED);
                vv.repaint();
            }
        });
        fill.setSelected(true);
        
        JCheckBox drawOutlines = new JCheckBox("Outline");
        drawOutlines.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent e) {
                vertexIconFunction.setOutlineImages(e.getStateChange()==ItemEvent.SELECTED);
                vv.repaint();
            }
        });
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        JPanel modePanel = new JPanel();
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modePanel.add(modeBox);
        
        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel labelFeatures = new JPanel(new GridLayout(1,0));
        labelFeatures.setBorder(BorderFactory.createTitledBorder("Image Effects"));
        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(scaleGrid);
        labelFeatures.add(shape);
        labelFeatures.add(fill);
        labelFeatures.add(drawOutlines);

        controls.add(labelFeatures);
        controls.add(modePanel);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * When Vertices are picked, add a checkmark icon to the imager.
     * Remove the icon when a Vertex is unpicked
     * @author Tom Nelson - RABA Technologies
     *
     */
    public static class PickWithIconListener implements ItemListener {
        DefaultVertexIconFunction<Number> imager;
        Icon checked;
        
        public PickWithIconListener(DefaultVertexIconFunction<Number> imager) {
            this.imager = imager;
            checked = new Checkmark();
        }

        public void itemStateChanged(ItemEvent e) {
            Icon icon = imager.transform((Number)e.getItem());
            if(icon != null && icon instanceof LayeredIcon) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    ((LayeredIcon)icon).add(checked);
                } else {
                    ((LayeredIcon)icon).remove(checked);
                }
            }
        }
    }
    /**
     * A simple implementation of VertexStringer that
     * gets Vertex labels from a Map  
     * 
     * @author Tom Nelson - RABA Technologies
     *
     *
     */
    public static class VertexStringerImpl<V,S> implements Transformer<V,String> {
        
        Map<V,String> map = new HashMap<V,String>();
        
        boolean enabled = true;
        
        public VertexStringerImpl(Map<V,String> map) {
            this.map = map;
        }
        
        /* (non-Javadoc)
         * @see edu.uci.ics.jung.graph.decorators.VertexStringer#getLabel(edu.uci.ics.jung.graph.Vertex)
         */
        public String transform(V v) {
            if(isEnabled()) {
                return map.get(v);
            } else {
                return "";
            }
        }
        
        /**
         * @return Returns the enabled.
         */
        public boolean isEnabled() {
            return enabled;
        }
        
        /**
         * @param enabled The enabled to set.
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    private Number[] createVertices(int count) {
        Number[] v = new Number[count];
        for (int i = 0; i < count; i++) {
            v[i] = new Integer(i);
            graph.addVertex(v[i]);
        }
        return v;
    }

    /**
     * create edges for this demo graph
     * @param v an array of Vertices to connect
     */
    void createEdges(Number[] v) {
        graph.addDirectedEdge(new Double(Math.random()), v[0], v[1]);
        graph.addDirectedEdge(new Double(Math.random()), v[3], v[0]);
        graph.addDirectedEdge(new Double(Math.random()), v[0], v[4]);
        graph.addDirectedEdge(new Double(Math.random()), v[4], v[5]);
        graph.addDirectedEdge(new Double(Math.random()), v[5], v[3]);
        graph.addDirectedEdge(new Double(Math.random()), v[2], v[1]);
        graph.addDirectedEdge(new Double(Math.random()), v[4], v[1]);
        graph.addDirectedEdge(new Double(Math.random()), v[8], v[2]);
        graph.addDirectedEdge(new Double(Math.random()), v[3], v[8]);
        graph.addDirectedEdge(new Double(Math.random()), v[6], v[7]);
        graph.addDirectedEdge(new Double(Math.random()), v[7], v[5]);
        graph.addDirectedEdge(new Double(Math.random()), v[0], v[9]);
        graph.addDirectedEdge(new Double(Math.random()), v[9], v[8]);
        graph.addDirectedEdge(new Double(Math.random()), v[7], v[6]);
        graph.addDirectedEdge(new Double(Math.random()), v[6], v[5]);
        graph.addDirectedEdge(new Double(Math.random()), v[4], v[2]);
        graph.addDirectedEdge(new Double(Math.random()), v[5], v[4]);
        graph.addDirectedEdge(new Double(Math.random()), v[4], v[10]);
        graph.addDirectedEdge(new Double(Math.random()), v[10], v[4]);
    }

    /** 
     * this class exists only to provide settings to turn on/off shapes and image fill
     * in this demo.
     * In a real application, use VertexIconAndShapeFunction instead.
     * 
     */
    public static class DemoVertexIconFunction<V> extends DefaultVertexIconFunction<V>
    	implements Transformer<V,Icon> {
        
//        boolean shapeImages = true;
        boolean fillImages = true;
        boolean outlineImages = false;

        /**
         * @return Returns the fillImages.
         */
        public boolean isFillImages() {
            return fillImages;
        }
        /**
         * @param fillImages The fillImages to set.
         */
        public void setFillImages(boolean fillImages) {
            this.fillImages = fillImages;
        }
//        /**
//         * @return Returns the shapeImages.
//         */
//        public boolean isShapeImages() {
//            return shapeImages;
//        }
//        /**
//         * @param shapeImages The shapeImages to set.
//         */
//        public void setShapeImages(boolean shapeImages) {
//            shapeMap.clear();
//            this.shapeImages = shapeImages;
//        }
        public boolean isOutlineImages() {
            return outlineImages;
        }
        public void setOutlineImages(boolean outlineImages) {
            this.outlineImages = outlineImages;
        }
        
        public Icon transform(V v) {
            if(fillImages) {
                return (Icon)iconMap.get(v);
            } else {
                return null;
            }
        }
    }
    
    /** 
     * this class exists only to provide settings to turn on/off shapes and image fill
     * in this demo.
     * In a real application, use VertexIconAndShapeFunction instead.
     * 
     */
    public static class DemoVertexImageShapeFunction<V> extends VertexIconShapeFunction<V> {
        
        boolean shapeImages = true;
        boolean fillImages = true;
        boolean outlineImages = false;

        public DemoVertexImageShapeFunction(Transformer<V,Shape> delegate) {
            super(delegate);
        }
        /**
         * @return Returns the fillImages.
         */
        public boolean isFillImages() {
            return fillImages;
        }
        /**
         * @param fillImages The fillImages to set.
         */
        public void setFillImages(boolean fillImages) {
            this.fillImages = fillImages;
        }
        /**
         * @return Returns the shapeImages.
         */
        public boolean isShapeImages() {
            return shapeImages;
        }
        /**
         * @param shapeImages The shapeImages to set.
         */
        public void setShapeImages(boolean shapeImages) {
            shapeMap.clear();
            this.shapeImages = shapeImages;
        }
        public boolean isOutlineImages() {
            return outlineImages;
        }
        public void setOutlineImages(boolean outlineImages) {
            this.outlineImages = outlineImages;
        }
        public Shape transform(V v) {
			Icon icon = (Icon) iconMap.get(v);

			if (icon != null && icon instanceof ImageIcon) {

				Image image = ((ImageIcon) icon).getImage();

				Shape shape = (Shape) shapeMap.get(image);
				if (shape == null) {
					if (shapeImages) {
						shape = FourPassImageShaper.getShape(image, 30);
					} else {
						shape = new Rectangle2D.Float(0, 0, 
								image.getWidth(null), image.getHeight(null));
					}
                    if(shape.getBounds().getWidth() > 0 && 
                            shape.getBounds().getHeight() > 0) {
                        int width = image.getWidth(null);
                        int height = image.getHeight(null);
                        AffineTransform transform = 
                            AffineTransform.getTranslateInstance(-width / 2, -height / 2);
                        shape = transform.createTransformedShape(shape);
                        shapeMap.put(image, shape);
                    }
				}
				return shape;
			} else {
				return delegate.transform(v);
			}
		}
        
//        public Icon getIcon(V v) {
//            if(fillImages) {
//                return (Icon)iconMap.get(v);
//            } else {
//                return null;
//            }
//        }
    }
    
    /**
     * a special renderer that can turn outlines on and off
     * in this demo.
     * You won't need this for a real application.
     * Use PluggableRenderer instead
     * 
     * @author Tom Nelson - RABA Technologies
     *
     */
    class DemoRenderer<V,E> extends BasicVertexRenderer<V,E> {
        public void paintIconForVertex(RenderContext<V,E> rc, V v, int x, int y) {
            GraphicsDecorator g = rc.getGraphicsContext();
            boolean outlineImages = false;
            Transformer<V,Icon> vertexIconFunction = rc.getVertexIconFunction();
            
            if(vertexIconFunction instanceof DemoVertexIconFunction) {
                outlineImages = ((DemoVertexIconFunction)vertexIconFunction).isOutlineImages();
            }
            Icon icon = vertexIconFunction.transform(v);
            if(icon == null || outlineImages) {
                
                Shape s = AffineTransform.getTranslateInstance(x,y).
                    createTransformedShape(rc.getVertexShapeFunction().transform(v));
                paintShapeForVertex(rc, v, s);
                
            }
            if(icon != null) {
                int xLoc = x - icon.getIconWidth()/2;
                int yLoc = y - icon.getIconHeight()/2;
                icon.paintIcon(rc.getScreenDevice(), g.getDelegate(), xLoc, yLoc);
            }
        }
    }
    
    /**
	 * a simple Icon that draws a checkmark in the lower-right quadrant of its
	 * area. Used to draw a checkmark on Picked Vertices.
	 */
    public static class Checkmark implements Icon {

    		GeneralPath path = new GeneralPath();
    		AffineTransform highlight = AffineTransform.getTranslateInstance(-1,-1);
    		AffineTransform lowlight = AffineTransform.getTranslateInstance(1,1);
    		AffineTransform shadow = AffineTransform.getTranslateInstance(2,2);
    		Color color;
    		public Checkmark() {
    			this(Color.green);
    		}
    		public Checkmark(Color color) {
    			this.color = color;
    			path.moveTo(10,17);
    			path.lineTo(13,20);
    			path.lineTo(20,13);
    		}
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Shape shape = AffineTransform.getTranslateInstance(x, y).createTransformedShape(path);
			Graphics2D g2d = (Graphics2D)g;
			g2d.addRenderingHints(Collections.singletonMap(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON));
			g2d.setStroke(new BasicStroke(4));
			g2d.setColor(Color.darkGray);
			g2d.draw(shadow.createTransformedShape(shape));
            g2d.setColor(Color.black);
            g2d.draw(lowlight.createTransformedShape(shape));
			g2d.setColor(Color.white);
			g2d.draw(highlight.createTransformedShape(shape));
			g2d.setColor(color);
			g2d.draw(shape);
		}

		public int getIconWidth() {
			return 20;
		}

		public int getIconHeight() {
			return 20;
		}
    }
    /**
     * An icon that is made up of a collection of Icons.
     * They are rendered in layers starting with the first
     * Icon added (from the constructor).
     * 
     * @author Tom Nelson - RABA Technologies
     *
     */
    public static class LayeredIcon extends ImageIcon {

		Set<Icon> iconSet = new LinkedHashSet<Icon>();

		public LayeredIcon(Image image) {
		    super(image);
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
            super.paintIcon(c, g, x, y);
            Dimension d = new Dimension(getIconWidth(), getIconHeight());
			for (Iterator iterator = iconSet.iterator(); iterator.hasNext();) {
				Icon icon = (Icon) iterator.next();
                 Dimension id = new Dimension(icon.getIconWidth(), icon.getIconHeight());
                 int dx = (d.width - id.width)/2;
                 int dy = (d.height - id.height)/2;
				icon.paintIcon(c, g, x+dx, y+dy);
			}
		}

		public void add(Icon icon) {
			iconSet.add(icon);
		}

		public boolean remove(Icon icon) {
			return iconSet.remove(icon);
		}
	}

    /**
	 * a driver for this demo
	 */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new VertexImageShaperDemo());
        frame.pack();
        frame.setVisible(true);
    }
}