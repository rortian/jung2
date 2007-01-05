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
 * 
 */
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.util.Pair;
import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung3d.visualization.PluggableRenderContext;
import edu.uci.ics.jung3d.visualization.RenderContext;
import edu.uci.ics.jung.visualization.decorators.EdgeContext;
import edu.uci.ics.jung.visualization.layout.VisRunner;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung3d.algorithms.layout.Layout;
import edu.uci.ics.jung3d.visualization.layout.LayoutEventBroadcaster;

/**
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public class VisualizationViewer<V,E> extends JPanel {

	BranchGroup objRoot;
	TransformGroup objTrans;
//	Appearance vertexLook;
//	Appearance edgeLook;
    /**
     * a listener used to cause pick events to result in
     * repaints, even if they come from another view
     */
    protected ItemListener pickEventListener;
	/**
	 * holds the state of which vertices of the graph are
	 * currently 'picked'
	 */
	protected PickedState<V> pickedVertexState;
	
	/**
	 * holds the state of which edges of the graph are
	 * currently 'picked'
	 */
    protected PickedState<E> pickedEdgeState;
    
    protected RenderContext<V,E> renderContext = new PluggableRenderContext<V,E>();

	Map<V,VertexGroup> vertexMap = new HashMap<V,VertexGroup>();
	Map<E,EdgeGroup> edgeMap = new HashMap<E,EdgeGroup>();
	Graph<V,E> graph;
	Layout<V,E> layout;

	public VisualizationViewer() {
//		controls = createControls();
		setLayout(new BorderLayout());
		
		renderContext.setPickedVertexState(new MultiPickedState<V>());
		renderContext.setPickedEdgeState(new MultiPickedState<E>());
		GraphicsConfiguration config = 
			SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add(c, BorderLayout.CENTER);
		setPickedVertexState(new MultiPickedState<V>());
		setPickedEdgeState(new MultiPickedState<E>());

		// Create a SpringGraph scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph(c);
		SimpleUniverse u = new SimpleUniverse(c);
		u.getViewer().getView().setUserHeadToVworldEnable(true);	

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();

		u.addBranchGraph(scene);
	}
	
	public Layout<V,E> getGraphLayout() {
		return layout;
	}


	public BranchGroup createSceneGraph(Canvas3D canvas) {

		objRoot = new BranchGroup();
		objRoot.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(Group.ALLOW_CHILDREN_WRITE);

		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(0.01);
		objScale.setTransform(t3d);
		objRoot.addChild(objScale);

		Transform3D tt = new Transform3D();
		tt.setTranslation(new Vector3f(0, 0, -300.f));
		objTrans = new TransformGroup(tt);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ );
		objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objScale.addChild(objTrans);

		// Create Colors, Materials,  and Appearances.
		Appearance look = new Appearance();
		Color3f objColor = new Color3f(0.7f, 0.7f, 0.7f);
		Color3f black = new Color3f(0.f, 0.f, 0.f);
		Color3f white = new Color3f(1.0f, 1.0f, 0.6f);
		Color3f gray  = new Color3f(.2f, .2f, .2f);
		Color3f red = new Color3f(1.0f, 0, 0);
		Color3f yellow = new Color3f(1,1,0);
		
		Material objMaterial = new Material(objColor, black,
				objColor, white, 100.0f);
		Material blackMaterial = new Material(objColor, black,
				black, objColor, 10.0f);
		Material whiteMaterial = new Material(white, white,
				white, white, 100.0f);
		Material grayMaterial = new Material(gray, black,
				gray, gray, 100.0f);

		Material redMaterial = new Material(red, black,
				red, red, 100.0f);
		Material yellowMaterial = new Material(yellow, black, 
				yellow, yellow, 100);

		look.setMaterial(new Material(objColor, black,
				objColor, white, 100.0f));
		Appearance blackLook = new Appearance();
		blackLook.setMaterial(blackMaterial);

		Appearance whiteLook = new Appearance();
		whiteLook.setMaterial(whiteMaterial);

		Appearance grayLook = new Appearance();
		grayLook.setMaterial(grayMaterial);
		grayLook.setCapability(Appearance.ALLOW_MATERIAL_READ);
		grayLook.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

		final Appearance redLook = new Appearance();
		redLook.setMaterial(redMaterial);
//		vertexLook = redLook;

		Appearance objLook = new Appearance();
		objLook.setMaterial(objMaterial);
//		edgeLook = objLook;
		final Appearance yellowLook = new Appearance();
		yellowLook.setMaterial(yellowMaterial);
		Bounds bounds =
			new BoundingSphere(new Point3d(),
					300);

		MouseRotate behavior1 = new MouseRotate();
		behavior1.setTransformGroup(objTrans);
		objTrans.addChild(behavior1);
		behavior1.setSchedulingBounds(bounds);

		MouseWheelZoom behavior2 = new MouseWheelZoom();
		behavior2.setTransformGroup(objTrans);
		behavior2.setFactor(10);
		objTrans.addChild(behavior2);
		behavior2.setSchedulingBounds(bounds);

		MouseTranslate behavior3 = new MouseTranslate();
		behavior3.setTransformGroup(objTrans);
		objTrans.addChild(behavior3);
		behavior3.setSchedulingBounds(bounds);

		PickVertexBehavior pvb = new PickVertexBehavior(objRoot,canvas,bounds,renderContext.getPickedVertexState());
		objTrans.addChild(pvb);
		pvb.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				for(V v : graph.getVertices()) {
					VertexGroup<V> vg = vertexMap.get(v);
					Appearance look = redLook;
					if(renderContext.getPickedVertexState().isPicked(v)) {
						look = yellowLook;
					}
					Node node = vg.getShape();
					if(node instanceof Primitive) {
						((Primitive)node).setAppearance(look);
					}
				}
				
			}});

		//Shine it with two colored lights.
		Color3f lColor1 = new Color3f(.5f, .5f, .5f);
		Color3f lColor2 = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f lDir2  = new Vector3f(-1.0f, 0.0f, -1.0f);
		DirectionalLight lgt2 = new DirectionalLight(lColor2, lDir2);
		AmbientLight ambient = new AmbientLight(lColor1);
		lgt2.setInfluencingBounds(bounds);
		ambient.setInfluencingBounds(bounds);
		objRoot.addChild(lgt2);
		objRoot.addChild(ambient);
		
		// Let Java 3D perform optimizations on this scene graph.
		objRoot.compile();

//		VisRunner runner = new VisRunner((IterativeContext)elayout);
//		runner.relax();

		return objRoot;
	}
	
	public void setGraphLayout(Layout<V,E> inLayout) {

//		this.layout = inLayout;
		this.graph = inLayout.getGraph();
		BranchGroup branch = new BranchGroup();
		LayoutEventBroadcaster<V,E> elayout =
			new LayoutEventBroadcaster<V,E>(inLayout);
		this.layout = elayout;
		for(V v : graph.getVertices()) {
			VertexGroup<V> vg = new VertexGroup<V>(v, renderContext.getVertexShapeTransformer().transform(v));
			vg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			vg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			vertexMap.put(v, vg);
			branch.addChild(vg);
		}

		for(E edge : graph.getEdges()) {
			EdgeGroup<E> eg = 
				new EdgeGroup<E>(edge, renderContext.getEdgeShapeTransformer().transform(new EdgeContext<V,E>(graph, edge)));
			eg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			eg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			edgeMap.put(edge, eg);
			branch.addChild(eg);
		}
		
//		System.err.println("branch is "+branch);
//		for(int i=0; i<branch.numChildren(); i++) {
//			System.err.println("branch child ["+i+"] is "+branch.getChild(i));
//		}

		objTrans.addChild(branch);
		elayout.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				for(V v : vertexMap.keySet()) {
					Point3f p = VisualizationViewer.this.layout.transform(v);
					Vector3f pv = new Vector3f(p.getX(), p.getY(), p.getZ());
					Transform3D tx = new Transform3D();
					tx.setTranslation(pv);
					vertexMap.get(v).setTransform(tx);
				}

				for(E edge : graph.getEdges()) {
					Pair<V> endpoints = graph.getEndpoints(edge);
					V start = endpoints.getFirst();
					V end = endpoints.getSecond();
					EdgeGroup eg = edgeMap.get(edge);
					eg.setEndpoints(layout.transform(start), layout.transform(end));
				}
			}});

		elayout.setSize(new BoundingSphere(new Point3d(), 200));
		elayout.initialize();
		VisRunner runner = new VisRunner((IterativeContext)elayout);
		runner.relax();

//		for(int i=0; i<objTrans.numChildren(); i++) {
//			System.err.println("objTrans child ["+i+"] is "+objTrans.getChild(i));
//		}

	}
	
    public void setPickedVertexState(PickedState<V> pickedVertexState) {
        if(pickEventListener != null && this.pickedVertexState != null) {
            this.pickedVertexState.removeItemListener(pickEventListener);
        }
        this.pickedVertexState = pickedVertexState;
        this.renderContext.setPickedVertexState(pickedVertexState);
        if(pickEventListener == null) {
            pickEventListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    System.err.println(e.getItem()+" was picked");
                }
            };
        }
        pickedVertexState.addItemListener(pickEventListener);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setPickedEdgeState(edu.uci.ics.jung.visualization.picking.PickedState)
     */
    public void setPickedEdgeState(PickedState<E> pickedEdgeState) {
        if(pickEventListener != null && this.pickedEdgeState != null) {
            this.pickedEdgeState.removeItemListener(pickEventListener);
        }
        this.pickedEdgeState = pickedEdgeState;
        this.renderContext.setPickedEdgeState(pickedEdgeState);
        if(pickEventListener == null) {
            pickEventListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    repaint();
                }
            };
        }
        pickedEdgeState.addItemListener(pickEventListener);
    }

	
//	public static void main(String argv[])
//	{
//		final VisualizationViewer enigma = new VisualizationViewer();
//		JFrame f = new JFrame();
//		f.add(enigma);
//		f.setSize(600,600);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////			new MainFrame(enigma, 500, 500);
////		f.pack();
//		f.setVisible(true);
//	}
}

