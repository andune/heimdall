/**
 * 
 */
package org.morganm.heimdall.event;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.morganm.heimdall.engine.EngineLog;
import org.morganm.heimdall.event.Event.Type;
import org.morganm.heimdall.event.handlers.EventHandler;
import org.morganm.heimdall.util.Debug;
import org.morganm.heimdall.util.JavaPluginExtensions;

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
	private Map<Event.Type, Map<Plugin, Set<EventHandler>>> eventEnrichers;
	private Map<Event.Type, Map<Plugin, Set<EventHandler>>> eventHandlers;
	
	public EventManager(final JavaPluginExtensions plugin) {
		this.plugin = plugin;
		this.eventBuffer = new EventCircularBuffer<Event>(Event.class, CIRCULAR_BUFFER_SIZE, true);
		this.eventHandlers = new HashMap<Event.Type, Map<Plugin, Set<EventHandler>>>();
		this.eventEnrichers = new HashMap<Event.Type, Map<Plugin, Set<EventHandler>>>();
		
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 100, 100);
	}
	
	public void registerHandler(final Plugin plugin, final Event.Type type, final EventHandler handler) {
		Map<Plugin, Set<EventHandler>> map = eventHandlers.get(type);
		if( map == null ) {
			map = new HashMap<Plugin, Set<EventHandler>>();
			eventHandlers.put(type, map);
		}
		Set<EventHandler> list = map.get(plugin);
		if( list == null ) {
			list = new HashSet<EventHandler>();
			map.put(plugin, list);
		}
		
		list.add(handler);
	}
	
	public void registerEnricher(final Plugin plugin, final Event.Type type, final EventHandler enricher) {
		Map<Plugin, Set<EventHandler>> map = eventEnrichers.get(type);
		if( map == null ) {
			map = new HashMap<Plugin, Set<EventHandler>>();
			eventEnrichers.put(type, map);
		}
		Set<EventHandler> list = map.get(plugin);
		if( list == null ) {
			list = new HashSet<EventHandler>();
			map.put(plugin, list);
		}
		
		list.add(enricher);
	}
	
	public void unregisterAllPluginHandlers(final Plugin plugin) {
		// loop through the double-keyed map looking for any matches to this plugin and remove them
		for (Entry<Type, Map<Plugin, Set<EventHandler>>> entry : eventHandlers.entrySet()) {
			for (Iterator<Plugin> i = entry.getValue().keySet().iterator(); i.hasNext();) {
				if( plugin.equals(i.next()) )
					i.remove();
			}
		}
	}
	public void unregisterAllPluginEnrichers(final Plugin plugin) {
		// loop through the double-keyed map looking for any matches to this plugin and remove them
		for (Entry<Type, Map<Plugin, Set<EventHandler>>> entry : eventEnrichers.entrySet()) {
			for (Iterator<Plugin> i = entry.getValue().keySet().iterator(); i.hasNext();) {
				if( plugin.equals(i.next()) )
					i.remove();
			}
		}
	}
	/*
	public void unregisterHandler(Event.Type type, EventHandler handler) {
		Set<EventHandler> list = eventHandlers.get(type);
		if( list != null )
			list.remove(handler);
	}
	*/
	
	private EngineLog eventDebugLog = new EngineLog(new File("plugins/Heimdall/eventDebug.log"));
	private HashMap<Event, Integer> eventDebugMap = new HashMap<Event, Integer>(1000);
	private int lastProcessedEvent=0;
	public void pushEvent(Event e) {
		eventBuffer.push(e);
		eventDebugMap.put(e, EventDebug.incrementEventNumber());
	}
	
	public void run() {
		// don't allow us to run twice
		if( running )
			return;
		
		running = true;
		try {
			Event event = null;
			while( (event = eventBuffer.pop()) != null ) {
				// DEBUGGING
				Integer eventNumber = eventDebugMap.remove(event);
				if( eventNumber != lastProcessedEvent ) {
					lastProcessedEvent = eventNumber;
					try {
						eventDebugLog.log("got eventNumber "+eventNumber+" when lastProcessEvent="+lastProcessedEvent);
					}catch(IOException e) {}
				}
				else {
					if( (lastProcessedEvent % 10) == 0 )
						try {
							eventDebugLog.log("lastProcessedEvent = "+lastProcessedEvent);
						}catch(IOException e) {}
//					Debug.getInstance().devDebug("eventManager event counts align: ",lastProcessedEvent);
					lastProcessedEvent++;
				}
				// DEBUGGING
				
				Event.Type type = event.getType();
				
				// first process any event enrichers
				Map<Plugin, Set<EventHandler>> enrichersMap = eventEnrichers.get(type);
				if( enrichersMap != null ) {
					for(Entry<Plugin, Set<EventHandler>> entry : enrichersMap.entrySet()) {
						Set<EventHandler> enrichers = enrichersMap.get(entry.getKey());
						if( enrichers != null ) {
							for(EventHandler handler : enrichers) {
								event.accept(handler);					// visitor pattern
							}
						}
					}
				}
				
				// now process the event handlers
				Map<Plugin, Set<EventHandler>> handlersMap = eventHandlers.get(type);
				if( handlersMap != null ) {
					for(Entry<Plugin, Set<EventHandler>> entry : handlersMap.entrySet()) {
						Set<EventHandler> handlers = handlersMap.get(entry.getKey());
						if( handlers != null ) {
							for(EventHandler handler : handlers) {
								event.accept(handler);					// visitor pattern
							}
						}
					}
				}
				
				// once we are done processing the event, clear out the object
				event.clear();
			} // end while
		}
		finally {
			running = false;
		}
	}
}
