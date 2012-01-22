/**
 * 
 */
package org.morganm.heimdall.engine;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.FriendEvent;
import org.morganm.heimdall.event.FriendInviteEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.PlayerEvent;

/** Abstract class to implement do-nothing methods so that Engine implementations
 * can only listen to events they are interested in and just ignore the rest.
 * 
 * @author morganm
 *
 */
public abstract class AbstractEngine implements Engine {

	@Override
	public void processBlockChange(BlockChangeEvent event) {}
	@Override
	public void processInventoryChange(InventoryChangeEvent event) {}
	@Override
	public void processChatMessage(String message) {}
	@Override
	public void processPlayerEvent(PlayerEvent event) {}
	@Override
	public void processHeimdallFriendEvent(FriendEvent event) {}
	@Override
	public void processHeimdallFriendInvite(FriendInviteEvent event) {}
}
