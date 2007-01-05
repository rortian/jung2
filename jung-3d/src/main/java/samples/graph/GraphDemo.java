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
package samples.graph;

/**
 * 
 */
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.generators.random.TestGraphs;
import edu.uci.ics.jung3d.algorithms.layout.FRLayout;
import edu.uci.ics.jung3d.algorithms.layout.Layout;
import edu.uci.ics.jung3d.algorithms.layout.SpringLayout;
import edu.uci.ics.jung3d.visualization.VisualizationViewer;

/**
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public class GraphDemo extends JPanel {

	Graph<String,Number> demoGraph = TestGraphs.getDemoGraph();
	Graph<String,Number> oneComponentGraph = TestGraphs.getOneComponentGraph();
	Map<String,Graph> graphMap = new HashMap<String,Graph>();
	Map<String,Class> layoutMap = new HashMap<String,Class>();
	JComboBox layoutBox, graphBox;
	
	public GraphDemo() {
		super(new BorderLayout());
		final VisualizationViewer<String,Number> vv = new VisualizationViewer<String,Number>();
		Graph<String,Number> graph = //TestGraphs.getOneComponentGraph();
			TestGraphs.getDemoGraph();
		
		graphBox = new JComboBox(new String[]{"ONE","TWO"});
		graphMap.put("ONE", demoGraph);
		graphMap.put("TWO", oneComponentGraph);
		graphBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				Class clazz = layoutMap.get(layoutBox.getSelectedItem());
				Constructor ctor;
				try {
					ctor = clazz.getConstructor(new Class[]{Graph.class});
					Layout layout = (Layout) ctor.newInstance(graphMap.get(e.getItem()));
					vv.setGraphLayout(layout);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}});

		layoutBox = new JComboBox(new String[]{"SpringLayout","FRLayout"});
		layoutMap.put("SpringLayout", SpringLayout.class);
		layoutMap.put("FRLayout", FRLayout.class);
		layoutBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				Class clazz = layoutMap.get(e.getItem());
				Constructor ctor;
				try {
					ctor = clazz.getConstructor(new Class[]{Graph.class});
					Layout layout = (Layout) ctor.newInstance(graphMap.get(graphBox.getSelectedItem()));
					vv.setGraphLayout(layout);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}});
		Layout<String,Number> layout = new SpringLayout<String,Number>(graph);
		vv.setGraphLayout(layout);
		JPanel controls = new JPanel();
		controls.add(graphBox);
		controls.add(layoutBox);
		
		add(vv);
		add(controls, BorderLayout.SOUTH);
	}

	public static void main(String argv[])
	{
		final GraphDemo demo = new GraphDemo();
		JFrame f = new JFrame();
		f.add(demo);
		f.setSize(600,600);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}

