/**
 * 
 */
package org.morganm.heimdall.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.morganm.heimdall.event.handlers.EventHandler;

/**
 * @author morganm
 *
 */
public class PlayerEvent implements Event {
	public enum Type {
		NEW_PLAYER_JOIN,
		PLAYER_JOIN,
		PLAYER_QUIT,
		PLAYER_KICK,
		PLAYER_BANNED,
		PLAYER_UNBANNED
	}
	public Type eventType;
	
	public String playerName;
	public long time;		// time of the event
	
	public World world;
	public int x;
	public int y;
	public int z;
	public transient Location location;
	
	public String[] extraData;
	
	public transient boolean cleared = true;
	
	@Override
	public void clear() {
		eventType = null;
		playerName = null;
		time = 0;
		world = null;
		x = 0;
		y = 0;
		z = 0;
		location = null;
		extraData = null;

		cleared = true;
	}

	@Override
	public boolean isCleared() {
		return cleared;
	}

	@Override
	public Event.Type getType() {
		return Event.Type.PLAYER_EVENT;
	}

	@Override
	public String getEventTypeString() {
		return eventType.toString();
	}

	@Override
	public String getPlayerName() { return playerName; }
	@Override
	public long getTime() { return time; }

	@Override
	public Location getLocation() {
		if( location == null )
			location = new Location(world, x, y, z);
		return location;
	}

	/** Visitor pattern.
	 * 
	 */
	@Override
	public void accept(EventHandler visitor) {
		visitor.processEvent(this);
	}
	
	public String toString() {
		return "PlayerEvent:["
				+"eventType="+eventType
				+",playerName="+playerName
				+",time="+time
				+",world="+world
				+",x="+x
				+",y="+y
				+",z="+z
				+",location="+location
				+"]";
	}
}
