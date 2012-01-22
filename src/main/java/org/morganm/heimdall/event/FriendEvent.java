/**
 * 
 */
package org.morganm.heimdall.event;

import org.bukkit.Location;
import org.morganm.heimdall.event.handlers.EventHandler;

/** Event used when a friend relationship has been created from a player
 * to a new friend.
 * 
 * @author morganm
 *
 */
public class FriendEvent implements Event {

	private String player;
	private String friend;
	private long time;
	
	public FriendEvent(String player, String friend) {
		this.player = player;
		this.friend = friend;
		this.time = System.currentTimeMillis();
	}
	
	public String getFriend() { return friend; }

	@Override
	public Type getType() {
		return Type.HEIMDALL_FRIEND_EVENT;
	}

	@Override
	public String getEventTypeString() {
		return Type.HEIMDALL_FRIEND_EVENT.toString();
	}

	@Override
	public String getPlayerName() {
		return player;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public void accept(EventHandler visitor) {
		visitor.processEvent(this);
	}

	// we don't re-use these events, so do nothing on clear
	@Override
	public void clear() {
	}

	@Override
	public boolean isCleared() {
		return true;
	}
}
