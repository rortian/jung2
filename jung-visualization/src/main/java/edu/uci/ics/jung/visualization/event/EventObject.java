package edu.uci.ics.jung.visualization.event;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A Base class for any input events to be handled by the visualization.
 * @author Jason A Wrang
 *
 */
public class EventObject<E> {
	/**
	 * The object on which the event initially occurred.
	 */
	Object source;
	
	/**
	 * The UI Toolkit specific event object
	 */
	E uiEvent;
	
	public EventObject(EventObject<E> event) {
		this(event.uiEvent, event.source);
	}

	public EventObject(E uiEvent, Object source) {
		this.source = source;
		this.uiEvent = uiEvent;
		
//		System.err.println("source 1: " + System.currentTimeMillis() + " " + System.identityHashCode(source) + " " + source);
		if ( !(this.source instanceof VisualizationViewer) ) {
			this.source = VisualizationViewer.eventSourceToViewer.get(source);
		}
//		System.err.println("source 2: " + System.currentTimeMillis() + " " + System.identityHashCode(source) + " " + source);
	}
	
	/**
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * @return the uiEvent
	 */
	public E getUiEvent() {
		return uiEvent;
	}
}
