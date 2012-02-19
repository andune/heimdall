/**
 * 
 */
package org.morganm.heimdall.engine;

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
