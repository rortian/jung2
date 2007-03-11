package edu.uci.ics.jung.visualization.event;

import java.util.EventListener;

public interface GraphEventListener<V,E> extends EventListener {
	
	void handleGraphEvent(GraphEvent<V,E> evt);

}
