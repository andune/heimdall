/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.FriendEvent;
import org.morganm.heimdall.event.FriendInviteEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.PlayerEvent;


/**
 * @author morganm
 *
 */
public abstract class EventHandler {
	private static final Event.Type[] noTypes = new Event.Type[] {};
	public Event.Type[] getRegisteredEventTypes() {
		return noTypes;
	}
	
	public void processEvent(BlockChangeEvent event) {}
	
	public void processEvent(InventoryChangeEvent event) {}
	
	public void processEvent(PlayerEvent event) {}

	public void processEvent(FriendEvent event) {}
	public void processEvent(FriendInviteEvent event) {}
}
