/**
 * 
 */
package com.andune.heimdall.engine;

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
public interface Engine {
	/* Additional interface contract; the Engine MUST implement a constructor of the form:
	 * 
	 *   Engine(Heimdall plugin);
	 * 
	 * This will be called to instantiate the engine, passing in the Heimdall plugin as
	 * a reference. Failure to do so will result in a failure to instantiate the engine
	 * when the plugin starts.
	 * 
	 * Alternately, if the Engine wants to take a config file as input, it can instead
	 * implement the constructor:
	 * 
	 *   Engine(Heimdall plugin, String configFile);
	 */

	/** Return all Heimdall event types that this engine wants to process.
	 */
	public Event.Type[] getRegisteredEventTypes();
	
	public void processBlockChange(BlockChangeEvent event);
	public void processInventoryChange(InventoryChangeEvent event);
	public void processChatMessage(String message);
	public void processPlayerEvent(PlayerEvent event);
	public void processHeimdallFriendEvent(FriendEvent event);
	public void processHeimdallFriendInvite(FriendInviteEvent event);
}
