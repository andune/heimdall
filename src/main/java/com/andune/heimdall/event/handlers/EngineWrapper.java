/**
 * 
 */
package com.andune.heimdall.event.handlers;

import com.andune.heimdall.engine.Engine;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.PlayerEvent;

/** Enricher which wraps an Engine and passes it events.
 * 
 * @author andune
 *
 */
public class EngineWrapper extends EventHandler {
	private final Engine engine;
	
	public EngineWrapper(final Engine engine) {
		this.engine = engine;
	}

	public Event.Type[] getRegisteredEventTypes() {
		return engine.getRegisteredEventTypes();
	}

	@Override
	public void processEvent(BlockChangeEvent bc) {
		engine.processBlockChange(bc);
	}

	@Override
	public void processEvent(InventoryChangeEvent ice) {
		engine.processInventoryChange(ice);
	}
	
	@Override
	public void processEvent(PlayerEvent pe) {
		engine.processPlayerEvent(pe);
	}
	
	@Override
	public void processEvent(FriendEvent event) {
		engine.processHeimdallFriendEvent(event);
	}
	
	@Override
	public void processEvent(FriendInviteEvent event) {
		engine.processHeimdallFriendInvite(event);
	}
}
