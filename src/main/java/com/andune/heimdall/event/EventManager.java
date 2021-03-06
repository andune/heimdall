/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 *
 */
package com.andune.heimdall.event;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.engine.EngineLog;
import com.andune.heimdall.event.Event.Type;
import com.andune.heimdall.event.handlers.EventHandler;
import com.andune.heimdall.util.Debug;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to manage events. Events are added as they happen and are processed asynchronously
 * by an event processing engine.
 * <p/>
 * Goal for this class is to maintain a single time-indexed array of events, that point to
 * actual event objects. The event objects then can be stored in whatever way is most
 * efficient, so high traffic events can use a circular buffer where event objects are never
 * created/destroyed, thereby minimizing the amount of CPU spent on having to create/GC
 * thousands of objects all the time.
 *
 * @author andune
 */
public class EventManager implements Runnable {
    private final static int CIRCULAR_BUFFER_SIZE = 30000;

    private final Heimdall plugin;
    @SuppressWarnings("unused")
    private final EngineLog eventDebugLog;
    private final Logger log;
    private final String logPrefix;
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

    public EventManager(final Heimdall plugin) {
        this.plugin = plugin;
        this.eventBuffer = new EventCircularBuffer<Event>(Event.class, CIRCULAR_BUFFER_SIZE, true);
        this.eventHandlers = new HashMap<Event.Type, Map<Plugin, Set<EventHandler>>>();
        this.eventEnrichers = new HashMap<Event.Type, Map<Plugin, Set<EventHandler>>>();
        this.log = this.plugin.getLogger();
        this.logPrefix = this.plugin.getLogPrefix();

        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 100, 40);
        eventDebugLog = new EngineLog(this.plugin, new File("plugins/Heimdall/logs/eventDebug.log"));
    }

    public void registerHandler(final Plugin plugin, final EventHandler handler,
                                Map<Event.Type, Map<Plugin, Set<EventHandler>>> handlersMap) {
        final Event.Type[] types = handler.getRegisteredEventTypes();

        for (int i = 0; i < types.length; i++) {
            final Event.Type type = types[i];
            Map<Plugin, Set<EventHandler>> map = handlersMap.get(type);
            if (map == null) {
                map = new HashMap<Plugin, Set<EventHandler>>();
                handlersMap.put(type, map);
            }
            Set<EventHandler> list = map.get(plugin);
            if (list == null) {
                list = new LinkedHashSet<EventHandler>();
                map.put(plugin, list);
            }

            list.add(handler);
        }
    }

    public void registerHandler(final Plugin plugin, final EventHandler handler) {
        registerHandler(plugin, handler, eventHandlers);
    }

    public void registerEnricher(final Plugin plugin, final EventHandler enricher) {
        registerHandler(plugin, enricher, eventEnrichers);
    }

    public void unregisterAllPluginHandlers(final Plugin plugin) {
        // loop through the double-keyed map looking for any matches to this plugin and remove them
        for (Entry<Type, Map<Plugin, Set<EventHandler>>> entry : eventHandlers.entrySet()) {
            for (Iterator<Plugin> i = entry.getValue().keySet().iterator(); i.hasNext(); ) {
                if (plugin.equals(i.next()))
                    i.remove();
            }
        }
    }

    public void unregisterAllPluginEnrichers(final Plugin plugin) {
        // loop through the double-keyed map looking for any matches to this plugin and remove them
        for (Entry<Type, Map<Plugin, Set<EventHandler>>> entry : eventEnrichers.entrySet()) {
            for (Iterator<Plugin> i = entry.getValue().keySet().iterator(); i.hasNext(); ) {
                if (plugin.equals(i.next()))
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

    //	private HashMap<Event, Integer> eventDebugMap = new HashMap<Event, Integer>(1000);
//	private int lastProcessedEvent=0;
    public void pushEvent(Event e) {
        eventBuffer.push(e);
//		eventDebugMap.put(e, EventDebug.incrementEventNumber());
    }

    public void run() {
        // don't allow us to run twice
        if (running)
            return;

        running = true;
        try {
            Event event = null;
//			try {
//				eventDebugLog.log("["+new Date()+"] starting while loop, size="+eventBuffer.size());
//			}catch(Exception e) {}

            while ((event = eventBuffer.pop()) != null) {
                // DEBUGGING
//				Integer eventNumber = eventDebugMap.remove(event);
//				if( eventNumber != lastProcessedEvent ) {
//					lastProcessedEvent = eventNumber;
//					try {
//						eventDebugLog.log("got eventNumber "+eventNumber+" when lastProcessEvent="+lastProcessedEvent);
//					}catch(IOException e) {}
//				}
//				else {
//					if( (lastProcessedEvent % 10) == 0 )
//						try {
//							eventDebugLog.log("["+new Date()+"] lastProcessedEvent = "+lastProcessedEvent+", buffer size="+eventBuffer.size());
//						}catch(IOException e) {}
////					Debug.getInstance().devDebug("eventManager event counts align: ",lastProcessedEvent);
//					lastProcessedEvent++;
//				}
                // DEBUGGING

                Debug.getInstance().devDebug("Begin processing event ", event);
                Event.Type type = event.getType();

                // first process any event enrichers
                Map<Plugin, Set<EventHandler>> enrichersMap = eventEnrichers.get(type);
                if (enrichersMap != null) {
                    for (Entry<Plugin, Set<EventHandler>> entry : enrichersMap.entrySet()) {
                        Set<EventHandler> enrichers = enrichersMap.get(entry.getKey());
                        if (enrichers != null) {
                            for (EventHandler handler : enrichers) {
                                try {
                                    event.accept(handler);                    // visitor pattern
                                } catch (Throwable t) {
                                    log.log(Level.WARNING, logPrefix + " Caught exception processing event for enricher " + handler + ", error: " + t.getMessage(), t);
                                }
                            }
                        }
                    }
                }

                // now process the event handlers
                Map<Plugin, Set<EventHandler>> handlersMap = eventHandlers.get(type);
                if (handlersMap != null) {
                    for (Entry<Plugin, Set<EventHandler>> entry : handlersMap.entrySet()) {
                        Set<EventHandler> handlers = handlersMap.get(entry.getKey());
                        if (handlers != null) {
                            for (EventHandler handler : handlers) {
                                try {
                                    event.accept(handler);                    // visitor pattern
                                } catch (Throwable t) {
                                    log.log(Level.WARNING, logPrefix + " Caught exception processing event for handler " + handler + ", error: " + t.getMessage(), t);
                                }
                            }
                        }
                    }
                }

                Debug.getInstance().devDebug("Finished processing event " + event);
                // once we are done processing the event, clear out the object
                event.clear();
            } // end while
        } finally {
            running = false;
        }
    }
}
