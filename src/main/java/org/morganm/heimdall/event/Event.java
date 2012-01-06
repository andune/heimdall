/**
 * 
 */
package org.morganm.heimdall.event;

import org.bukkit.Location;

/** General event interface that all Heimdall Event types will implement.
 * 
 * @author morganm
 *
 */
public interface Event {
	public enum Type {
		BLOCK_CHANGE,
		INVENTORY_CHANGE,
		CHAT_MESSAGE
	}
	
	/** Clear the event object of any data.
	 * 
	 */
	public void clear();
	
	public boolean isCleared();
	
	public Type getType();
	
	/** Not specifically related to getType(): this method should return a human-readable
	 * String that explains the event type. For example this might be "BLOCK_PLACE" or
	 * "BLOCK_DESTROY" or "ITEM_CRAFTED", etc.
	 * 
	 * @return
	 */
	public String getEventTypeString();
	
	/** Return the player whose actions caused this event to be generated.
	 * 
	 * @return
	 */
	public String getPlayerName();
	
	/** For events that have a location.
	 * 
	 * @return the location of the event, or null if no location
	 */
	public Location getLocation();
	
	/** Visitor pattern.
	 * 
	 */
	public void accept(EventHandler visitor);
}
