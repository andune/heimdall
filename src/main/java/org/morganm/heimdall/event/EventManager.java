/**
 * 
 */
package org.morganm.heimdall.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.morganm.util.JavaPluginExtensions;

/** Class to manage events. Events are added as they happen and are processed asynchronously
 * by an event processing engine.
 * 
 * Goal for this class is to maintain a single time-indexed array of events, that point to
 * actual event objects. The event objects then can be stored in whatever way is most
 * efficient, so high traffic events can use a circular buffer where event objects are never
 * created/destroyed, thereby minimizing the amount of CPU spent on having to create/GC
 * thousands of objects all the time.
 * 
 * @author morganm
 *
 */
public class EventManager implements Runnable {
	private final static int CIRCULAR_BUFFER_SIZE = 10000;
	
	@SuppressWarnings("unused")
	private JavaPluginExtensions plugin;
	private boolean running = false;
	
	// maybe implementation itself should use a circular buffer, backed by an ArrayList
	// to handle any possible overflow?
	
	private EventCircularBuffer<Event> eventBuffer;
	/* Event enrichers are called before any handlers and their job is to enrich
	 * the information related to the event. This might include looking up the block
	 * owner in a database, or adding information based on knowledge of previous
	 * events, etc.  The enriched event is then passed on to the handlers.
	 */
	private Map<Event.Type, Set<EventHandler>> eventEnrichers;
	private Map<Event.Type, Set<EventHandler>> eventHandlers;
	
	public EventManager(final JavaPluginExtensions plugin) {
		this.plugin = plugin;
		this.eventBuffer = new EventCircularBuffer<Event>(Event.class, CIRCULAR_BUFFER_SIZE, true);
		this.eventHandlers = new HashMap<Event.Type, Set<EventHandler>>();
		this.eventEnrichers = new HashMap<Event.Type, Set<EventHandler>>();
	}
	
	public void registerHandler(Event.Type type, EventHandler handler) {
		Set<EventHandler> list = eventHandlers.get(type);
		if( list == null ) {
			list = new HashSet<EventHandler>();
			eventHandlers.put(type, list);
		}
		
		list.add(handler);
	}
	
	public void unregisterHandler(Event.Type type, EventHandler handler) {
		Set<EventHandler> list = eventHandlers.get(type);
		if( list != null )
			list.remove(handler);
	}
	
	public void pushEvent(Event e) {
		eventBuffer.push(e);
	}
	
	public void run() {
		// don't allow us to run twice
		if( running )
			return;
		
		running = true;
		try {
			Event event = null;
			while( (event = eventBuffer.pop()) != null ) {
				Event.Type type = event.getType();
				
				Set<EventHandler> enrichers = eventEnrichers.get(type);
				if( enrichers != null ) {
					for(EventHandler handler : enrichers) {
						event.accept(handler);
					}
				}
				
				Set<EventHandler> handlers = eventHandlers.get(type);
				if( handlers != null ) {
					for(EventHandler handler : handlers) {
						event.accept(handler);
					}
				}
			}
		}
		finally {
			running = false;
		}
	}
}
