/**
 * 
 */
package org.morganm.heimdall.event;

import org.bukkit.Material;
import org.bukkit.World;

/**
 * @author morganm
 *
 */
public class BlockChangeEvent implements Event {
	// should only be BLOCK_PLACE or BLOCK_BREAK
	public org.bukkit.event.Event.Type bukkitEventType;
	
	public String playerName;
	public long time;		// time of the event
	
	public World world;
	public int x;
	public int y;
	public int z;
	
	public Material type;
	public byte data;
	
	public String[] signData;
	
	public String blockOwner;	// enriched data: the name of the owner of the block
	public int ownerTypeId;		// enriched data: the id of the "owned" block (which may be different
								// than the current id: a planted sapling becomes wood, etc)
	
	public String locationString() {
		return "{"+world.getName()+",x="+x+",y="+y+",z="+z+"}";
	}

	@Override
	public void clear() {
		bukkitEventType = null;
		playerName = null;
		time = 0;
		world = null;
		x = 0;
		y = 0;
		z = 0;
		type = null;
		data = 0;
		signData = null;
		blockOwner = null;
		ownerTypeId = 0;
	}

	@Override
	public Type getType() {
		return Event.Type.BLOCK_CHANGE;
	}

	/** Visitor pattern.
	 * 
	 */
	@Override
	public void accept(EventHandler visitor) {
		visitor.processEvent(this);
	}
}
