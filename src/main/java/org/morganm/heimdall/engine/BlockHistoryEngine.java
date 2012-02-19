/**
 * 
 */
package org.morganm.heimdall.engine;

import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.blockhistory.BlockHistory;
import org.morganm.heimdall.blockhistory.BlockHistoryManager;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.Event.Type;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.util.Debug;

/**
 * @author morganm
 *
 */
public class BlockHistoryEngine extends AbstractEngine {
	private final Heimdall plugin;
	private final BlockHistoryManager blockHistoryManager;
	private final PlayerStateManager playerStateManager;
	
	public BlockHistoryEngine(final Heimdall plugin) {
		this.plugin = plugin;
		this.blockHistoryManager = this.plugin.getBlockHistoryManager();
		this.playerStateManager = this.plugin.getPlayerStateManager();
	}
	
	@Override
	public Type[] getRegisteredEventTypes() {
		return new Event.Type[] { Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE };
	}

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		PlayerState ps = playerStateManager.getPlayerState(event.playerName);
		if( ps.isExemptFromChecks() )
			return;

		Debug.getInstance().debug("BlockHistoryEngine:processEvent() bc=",event);
		if( event.bukkitEventType == Event.BukkitType.BLOCK_BREAK ) {
//		if( event.bukkitEventType == Type.BLOCK_BREAK ) {
			BlockHistory bh = blockHistoryManager.getBlockHistory(event.getLocation());
			
			if( bh != null ) {
				event.blockOwner = bh.getOwner();
				event.ownerTypeId = bh.getTypeId();
			}
		}
	}
	
	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		PlayerState ps = playerStateManager.getPlayerState(event.playerName);
		if( ps.isExemptFromChecks() )
			return;

		if( event.type == InventoryEventType.CONTAINER_ACCESS ) {
			BlockHistory bh = blockHistoryManager.getBlockHistory(event.getLocation());

			if( bh != null ) {
				event.blockOwner = bh.getOwner();
			}
		}
	}

}
