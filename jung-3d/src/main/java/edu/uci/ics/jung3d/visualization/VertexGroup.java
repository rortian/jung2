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
import javax.media.j3d.Node;
import javax.media.j3d.TransformGroup;

import com.sun.j3d.utils.geometry.Primitive;

/**
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public class VertexGroup<V> extends TransformGroup {

	 V vertex;
	 Node shape;

	 public VertexGroup(V vertex, Node shape) {
		 this.vertex = vertex;
		 this.shape = shape;
		 setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		 addChild(shape);
	 }


	 /**
	  * @return the shape
	  */
	 public Node getShape() {
		 return shape;
	 }


	 /**
	  * @param shape the shape to set
	  */
	 public void setShape(Node shape) {
		 this.shape = shape;
	 }


	 public String toString() { return vertex.toString(); }

 }
