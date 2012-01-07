/**
 * 
 */
package org.morganm.heimdall.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.morganm.heimdall.event.handlers.EventHandler;

/**
 * @author morganm
 *
 */
public class InventoryChangeEvent implements Event {
	public enum InventoryEventType {
		UNDEFINED,
		CONTAINER_ACCESS,
		CRAFTED
	}
	
	public String playerName;
	public long time;		// time of the event
	
	public World world;
	public int x;
	public int y;
	public int z;
	public transient Location location;
	public ItemStack[] diff;
	
	public InventoryEventType type;
	
	public String blockOwner;	// enriched data: the name of the owner of the block
	public float griefValue;	// grief value associated with this event, if any

	public transient boolean cleared = true;
	
	@Override
	public Location getLocation() {
		if( location == null )
			location = new Location(world, x, y, z);
		return location;
	}
	
	@Override
	public String getPlayerName() { return playerName; }
	@Override
	public long getTime() { return time; }
	
	@Override
	public void clear() {
		playerName = null;
		time = 0;
		world = null;
		x=0;
		y=0;
		z=0;
		location=null;
		diff=null;
		type = InventoryEventType.UNDEFINED;
		blockOwner = null;
		griefValue = 0;
		
		cleared=true;
	}
	
	@Override
	public boolean isCleared() {
		return cleared;
	}

	@Override
	public Type getType() {
		return Type.INVENTORY_CHANGE;
	}

	@Override
	public String getEventTypeString()
	{
		switch(type) {
		case UNDEFINED:
			return "Unknown Inventory Change";
		default:
			return type.toString();
		}
	}
	
	/** Visitor pattern.
	 * 
	 */
	@Override
	public void accept(EventHandler visitor) {
		visitor.processEvent(this);
	}

}
