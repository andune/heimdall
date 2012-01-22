/**
 * 
 */
package org.morganm.heimdall.engine;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.FriendEvent;
import org.morganm.heimdall.event.FriendInviteEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.PlayerEvent;

/**
 * @author morganm
 *
 */
public interface Engine {
	public void processBlockChange(BlockChangeEvent event);
	public void processInventoryChange(InventoryChangeEvent event);
	public void processChatMessage(String message);
	public void processPlayerEvent(PlayerEvent event);
	public void processHeimdallFriendEvent(FriendEvent event);
	public void processHeimdallFriendInvite(FriendInviteEvent event);
}
