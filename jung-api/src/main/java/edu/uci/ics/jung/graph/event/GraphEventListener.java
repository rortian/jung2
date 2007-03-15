package edu.uci.ics.jung.graph.event;

import java.util.EventListener;

public interface GraphEventListener<V,E> extends EventListener {
	
	void handleGraphEvent(GraphEvent<V,E> evt);

}
