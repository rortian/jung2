package edu.uci.ics.jung.visualization.annotations;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.basic.BasicIconFactory;

import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class AnnotatingModalGraphMouse<V,E> extends AbstractModalGraphMouse 
	implements ModalGraphMouse, ItemSelectable {

	protected AnnotatingGraphMousePlugin annotatingPlugin;
	protected MultiLayerTransformer basicTransformer;
	protected RenderContext rc;

	/**
	 * create an instance with default values
	 *
	 */
	public AnnotatingModalGraphMouse(RenderContext rc, 
			AnnotatingGraphMousePlugin annotatingPlugin) {
		this(rc, annotatingPlugin, 1.1f, 1/1.1f);
	}

	/**
	 * create an instance with passed values
	 * @param in override value for scale in
	 * @param out override value for scale out
	 */
	public AnnotatingModalGraphMouse(RenderContext rc,
			AnnotatingGraphMousePlugin annotatingPlugin,
			float in, float out) {
		super(in,out);
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		this.annotatingPlugin = annotatingPlugin;
		loadPlugins();
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	/**
	 * create the plugins, and load the plugins for TRANSFORMING mode
	 *
	 */
	protected void loadPlugins() {
		this.pickingPlugin = new PickingGraphMousePlugin();
		this.animatedPickingPlugin = new AnimatedPickingGraphMousePlugin();
		this.translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
		this.scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		this.rotatingPlugin = new RotatingGraphMousePlugin();
		this.shearingPlugin = new ShearingGraphMousePlugin();
		add(scalingPlugin);
		setMode(Mode.TRANSFORMING);
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
			} else if(mode == Mode.ANNOTATING) {
				setAnnotatingMode();
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
		remove(annotatingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#setTransformingMode()
	 */
	protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(annotatingPlugin);
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
		remove(annotatingPlugin);
	}

	protected void setAnnotatingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		add(annotatingPlugin);
	}


	/**
	 * @return Returns the modeBox.
	 */
	public JComboBox getModeComboBox() {
		if(modeBox == null) {
			modeBox = new JComboBox(new Mode[]{Mode.TRANSFORMING, Mode.PICKING, Mode.ANNOTATING});
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

			ButtonGroup radio = new ButtonGroup();
			radio.add(transformingButton);
			radio.add(pickingButton);
			transformingButton.setSelected(true);
			modeMenu.add(transformingButton);
			modeMenu.add(pickingButton);
			modeMenu.setToolTipText("Menu for setting Mouse Mode");
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						if(e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if(e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						}
					}
				}});
		}
		return modeMenu;
	}
	
    public static class ModeKeyAdapter extends KeyAdapter {
    	private char t = 't';
    	private char p = 'p';
    	private char a = 'a';
    	protected ModalGraphMouse graphMouse;

    	public ModeKeyAdapter(ModalGraphMouse graphMouse) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter(char t, char p, char a, ModalGraphMouse graphMouse) {
			this.t = t;
			this.p = p;
			this.a = a;
			this.graphMouse = graphMouse;
		}
		
		public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			if(keyChar == t) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				graphMouse.setMode(Mode.TRANSFORMING);
			} else if(keyChar == p) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				graphMouse.setMode(Mode.PICKING);
			} else if(keyChar == a) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.ANNOTATING);
			}
		}
    }
    
    public JComboBox getShapeBox() {
    	JComboBox shapeBox = new JComboBox(
    			new Shape[] {
    					new Rectangle2D.Double(),
    					new RoundRectangle2D.Double(0,0,0,0,50,50),
    					new Ellipse2D.Double()
    			});
    	shapeBox.setRenderer(new DefaultListCellRenderer() {
    		public Component getListCellRendererComponent(JList list, Object value,
    			int index, boolean isSelected, boolean hasFocus) {
    			String valueString = value.toString();
    			valueString = valueString.substring(0,valueString.indexOf("2D"));
    			valueString = valueString.substring(valueString.lastIndexOf('.')+1);
    			return super.getListCellRendererComponent(list, valueString, index,
    					isSelected, hasFocus);
    		}
    	});
    	shapeBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					annotatingPlugin.setRectangularShape((RectangularShape)e.getItem());
				}
				
			}});
    	return shapeBox;
    }
    
    public JButton getColorChooserButton() {
    	final JButton colorChooser = new JButton("Color");
    	colorChooser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(colorChooser, "Annotation Color", 
						colorChooser.getForeground());
				annotatingPlugin.setAnnotationColor(color);
				colorChooser.setForeground(color);
			}});
    	return colorChooser;
    }

}

