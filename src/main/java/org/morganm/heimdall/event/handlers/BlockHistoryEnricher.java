/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.morganm.heimdall.blockhistory.BlockHistory;
import org.morganm.heimdall.blockhistory.BlockHistoryManager;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.util.Debug;

/** Class which enriches an event with block history information.
 * 
 * @author morganm
 *
 */
public class BlockHistoryEnricher extends EventHandler {
	@SuppressWarnings("unused")
	private final Plugin plugin;
	private final BlockHistoryManager blockHistoryManager;
	private final PlayerStateManager playerStateManager;
	
	public BlockHistoryEnricher(final Plugin plugin, final BlockHistoryManager blockHistoryManager,
			final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.blockHistoryManager = blockHistoryManager;
		this.playerStateManager = playerStateManager;
	}
	
	@Override
	public void processEvent(BlockChangeEvent event) {
		PlayerState ps = playerStateManager.getPlayerState(event.playerName);
		if( ps.isExemptFromChecks() )
			return;

		Debug.getInstance().debug("BlockHistoryEnricher:processEvent() bc=",event);
		if( event.bukkitEventType == Type.BLOCK_BREAK ) {
			BlockHistory bh = blockHistoryManager.getBlockHistory(event.getLocation());
			
			if( bh != null ) {
				event.blockOwner = bh.getOwner();
				event.ownerTypeId = bh.getTypeId();
			}
		}
	}
	
	@Override
	public void processEvent(InventoryChangeEvent event) {
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
