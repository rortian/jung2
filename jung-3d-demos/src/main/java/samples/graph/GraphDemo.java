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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
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
	Map<String,Graph<String,Number>> graphMap = new HashMap<String,Graph<String,Number>>();
//	Map<String,Class> layoutMap = new HashMap<String,Class>();
	JComboBox layoutBox, graphBox;
	
	public GraphDemo() {
		super(new BorderLayout());
		final VisualizationViewer<String,Number> vv = new VisualizationViewer<String,Number>();
		Graph<String,Number> graph = //TestGraphs.getOneComponentGraph();
			TestGraphs.getDemoGraph();
		vv.getRenderContext().setVertexStringer(new ToStringLabeller<String>());
		Layout<String,Number> layout = new SpringLayout<String,Number>(graph);
		vv.setGraphLayout(layout);
		
		add(vv);
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

