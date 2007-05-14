package samples.graph;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;

public class SWTUtils {
	public static Control createHorizontalZoomControls(Composite parent, 
			final ScalingControl scaler, final VisualizationViewer vv) {
		Group zoom = new Group(parent, SWT.NONE);
		GridLayout zcl = new GridLayout();
		zcl.numColumns = 2;
		zoom.setLayout(zcl);
		zoom.setText("Zoom");

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
		
		return zoom;
	}
	
	public static Control createSimpleMouseControl(Composite parent, 
			final AbstractModalGraphMouse graphMouse,
			final VisualizationViewer vv) {
		Group mouse = new Group(parent, SWT.NONE);
		GridLayout mcl = new GridLayout();
		mcl.numColumns = 1;
		mouse.setLayout(mcl);
		mouse.setText("Mouse Mode");
		
        final Combo combo = new Combo(mouse, SWT.READ_ONLY);
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
		
		return mouse;
	}
}
