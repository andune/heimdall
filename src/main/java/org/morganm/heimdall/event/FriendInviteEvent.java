/**
 * 
 */
package org.morganm.heimdall.event;

import org.bukkit.Location;
import org.morganm.heimdall.event.Event.Type;
import org.morganm.heimdall.event.handlers.EventHandler;

/** Event created when a friend invite is sent.
 * 
 * @author morganm
 *
 */
public class FriendInviteEvent implements Event {

	private String player;			// the player the invite was sent to
	private String invitedFriend;	// the "friend" the invite is related to
	private long time;
	
	public FriendInviteEvent(String player, String friend) {
		this.player = player;
		this.invitedFriend = friend;
		this.time = System.currentTimeMillis();
	}
	
	public String getInvitedFriend() { return invitedFriend; }

	@Override
	public Type getType() {
		return Type.HEIMDALL_FRIEND_INVITE_SENT;
	}

	@Override
	public String getEventTypeString() {
		return Type.HEIMDALL_FRIEND_INVITE_SENT.toString();
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
