package edu.uci.ics.jung.visualization.control;

import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.basic.BasicIconFactory;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.cursor.Cursor;
import edu.uci.ics.jung.visualization.event.Event;
import edu.uci.ics.jung.visualization.event.KeyEvent;
import edu.uci.ics.jung.visualization.event.KeyListener;

public class EditingModalGraphMouse<V,E> extends AbstractModalGraphMouse 
	implements ModalGraphMouse, ItemSelectable {

	protected Factory<V> vertexFactory;
	protected Factory<E> edgeFactory;
	protected EditingGraphMousePlugin<V,E> editingPlugin;
	protected LabelEditingGraphMousePlugin<V,E> labelEditingPlugin;
	protected EditingPopupGraphMousePlugin<V,E> popupEditingPlugin;
	protected AnnotatingGraphMousePlugin annotatingPlugin;
	protected MultiLayerTransformer basicTransformer;
	protected RenderContext rc;

	/**
	 * create an instance with default values
	 *
	 */
	public EditingModalGraphMouse(RenderContext rc, 
			Factory<V> vertexFactory, Factory<E> edgeFactory) {
		this(rc, vertexFactory, edgeFactory, 1.1f, 1/1.1f);
	}

	/**
	 * create an instance with passed values
	 * @param in override value for scale in
	 * @param out override value for scale out
	 */
	public EditingModalGraphMouse(RenderContext rc,
			Factory<V> vertexFactory, Factory<E> edgeFactory, float in, float out) {
		super(in,out);
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		loadPlugins();
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	/**
	 * create the plugins, and load the plugins for TRANSFORMING mode
	 *
	 */
	protected void loadPlugins() {
		pickingPlugin = new PickingGraphMousePlugin();
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin();
		translatingPlugin = new TranslatingGraphMousePlugin(Event.BUTTON1_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();
		editingPlugin = new EditingGraphMousePlugin<V,E>(vertexFactory, edgeFactory);
		labelEditingPlugin = new LabelEditingGraphMousePlugin<V,E>();
		annotatingPlugin = new AnnotatingGraphMousePlugin(rc);
		popupEditingPlugin = new EditingPopupGraphMousePlugin<V,E>(vertexFactory, edgeFactory);
		add(scalingPlugin);
//		add(labelEditingPlugin);
		setMode(Mode.EDITING);
	}
	public void setVertexLocations(Map<V,Point2D> vertexLocations) {
		((EditingGraphMousePlugin<V,E>)editingPlugin).setVertexLocations(vertexLocations);
		((EditingPopupGraphMousePlugin<V,E>)popupEditingPlugin).setVertexLocations(vertexLocations);
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
		remove(editingPlugin);
		remove(annotatingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.control.ModalGraphMouse#setTransformingMode()
	 */
	protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(editingPlugin);
		remove(annotatingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	protected void setEditingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(annotatingPlugin);
		add(editingPlugin);
		add(popupEditingPlugin);
	}

	protected void setAnnotatingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(editingPlugin);
		remove(popupEditingPlugin);
		add(annotatingPlugin);
	}


	/**
	 * @return Returns the modeBox.
	 */
	public JComboBox getModeComboBox() {
		if(modeBox == null) {
			modeBox = new JComboBox(new Mode[]{Mode.TRANSFORMING, Mode.PICKING, Mode.EDITING, Mode.ANNOTATING});
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
	
    public static class ModeKeyAdapter implements KeyListener {
    	private char t = 't';
    	private char p = 'p';
    	private char e = 'e';
    	private char a = 'a';
    	protected ModalGraphMouse graphMouse;

    	public ModeKeyAdapter(ModalGraphMouse graphMouse) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter(char t, char p, char e, char a, ModalGraphMouse graphMouse) {
			this.t = t;
			this.p = p;
			this.e = e;
			this.a = a;
			this.graphMouse = graphMouse;
		}
		
		public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			if(keyChar == t) {
				((VisualizationViewer)event.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				graphMouse.setMode(Mode.TRANSFORMING);
			} else if(keyChar == p) {
				((VisualizationViewer)event.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
				graphMouse.setMode(Mode.PICKING);
			} else if(keyChar == e) {
				((VisualizationViewer)event.getSource()).setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.EDITING);
			} else if(keyChar == a) {
				((VisualizationViewer)event.getSource()).setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.ANNOTATING);
			}
		}

		public void keyPressed(KeyEvent keyEvent) {}
		public void keyReleased(KeyEvent keyEvent) {}
    }

	/**
	 * @return the annotatingPlugin
	 */
	public AnnotatingGraphMousePlugin getAnnotatingPlugin() {
		return annotatingPlugin;
	}

	/**
	 * @return the editingPlugin
	 */
	public EditingGraphMousePlugin<V, E> getEditingPlugin() {
		return editingPlugin;
	}

	/**
	 * @return the labelEditingPlugin
	 */
	public LabelEditingGraphMousePlugin<V, E> getLabelEditingPlugin() {
		return labelEditingPlugin;
	}

	/**
	 * @return the popupEditingPlugin
	 */
	public EditingPopupGraphMousePlugin<V, E> getPopupEditingPlugin() {
		return popupEditingPlugin;
	}
}

