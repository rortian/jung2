package edu.uci.ics.jung.graph;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public class UndirectedSparseMultigraphTest 
	extends AbstractUndirectedSparseMultigraphTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graph = new UndirectedSparseMultigraph<Integer,Number>();
        // FIXME: note that the edges below _should not work_: USG doesn't accept parallel edges.
        graph.addEdge(e01, v0, v1);
        graph.addEdge(e10, v1, v0);
        graph.addEdge(e12, v1, v2);
        graph.addEdge(e21, v2, v1);

    }
}
