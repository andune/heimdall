/**
 * 
 */
package com.andune.heimdall.engine;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.blockhistory.BlockHistory;
import com.andune.heimdall.blockhistory.BlockHistoryManager;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.Event.Type;
import com.andune.heimdall.event.InventoryChangeEvent.InventoryEventType;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;

/**
 * @author andune
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
