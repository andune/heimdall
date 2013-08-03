/**
 * 
 */
package com.andune.heimdall.event.handlers;

import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.PlayerEvent;


/**
 * @author andune
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
