/*
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package edu.uci.ics.jung3d.visualization;

/**
 */

import java.awt.event.MouseEvent;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.event.ChangeListener;

//import com.sun.j3d.utils.behaviors.picking.PickObject;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior;

import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

/**
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public class PickVertexBehavior<V,E> extends PickTranslateBehavior implements ChangeEventSupport {

	Bounds bounds;
	BranchGroup root;
	ChangeEventSupport support = new DefaultChangeEventSupport(this);
	PickedState<V> pickedState;

	public PickVertexBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds, PickedState<V> pickedState){
		super(root, canvas, bounds);
		this.setSchedulingBounds(bounds);
		this.bounds = bounds;
		this.root = root;
		this.pickedState = pickedState;
		pickCanvas.setMode(PickTool.GEOMETRY);
	}

	public void updateScene(int xpos, int ypos){
		
		if(mevent.getButton() == MouseEvent.BUTTON1) {  // ButtonOne
			int buttonOne = MouseEvent.BUTTON1_MASK;
			int shiftButtonOne = MouseEvent.BUTTON1_MASK | MouseEvent.SHIFT_MASK;
			int modifiers = mevent.getModifiers();
			if(modifiers == buttonOne) {
				// clear previous picked stuff
				pickedState.clear();
				doPick(xpos, ypos);
			} else if(modifiers == shiftButtonOne) {
				doPick(xpos, ypos);
			}
		}
	}
	
	private void doPick(int xpos, int ypos) {
		pickCanvas.setShapeLocation(xpos, ypos);
		PickResult result = pickCanvas.pickClosest();
		if(result != null) {

			TransformGroup tg = (TransformGroup)result.getNode(PickResult.TRANSFORM_GROUP);
			if(tg instanceof VertexGroup) {

				System.err.println("picked "+tg);
				pickedState.pick(((VertexGroup<V>)tg).vertex, true);
				fireStateChanged();
			}
		}
	}

	public void addChangeListener(ChangeListener l) {
		support.addChangeListener(l);

	}

	public void fireStateChanged() {
		support.fireStateChanged();

	}

	public ChangeListener[] getChangeListeners() {
		return support.getChangeListeners();
	}

	public void removeChangeListener(ChangeListener l) {
		support.removeChangeListener(l);
	}
}

