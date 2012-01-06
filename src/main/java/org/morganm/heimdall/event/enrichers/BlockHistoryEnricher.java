/**
 * 
 */
package org.morganm.heimdall.event.enrichers;

import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.morganm.heimdall.blockhistory.BlockHistory;
import org.morganm.heimdall.blockhistory.BlockHistoryManager;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.EventHandler;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;

/** Class which enriches an event with block history information.
 * 
 * @author morganm
 *
 */
public class BlockHistoryEnricher extends EventHandler {
	@SuppressWarnings("unused")
	private final Plugin plugin;
	private final BlockHistoryManager blockHistoryManager;
	
	public BlockHistoryEnricher(final Plugin plugin, final BlockHistoryManager blockHistoryManager) {
		this.plugin = plugin;
		this.blockHistoryManager = blockHistoryManager;
	}
	
	@Override
	public void processEvent(BlockChangeEvent bc) {
		if( bc.bukkitEventType == Type.BLOCK_BREAK ) {
			BlockHistory bh = blockHistoryManager.getBlockHistory(bc.getLocation());
			
			if( bh != null ) {
				bc.blockOwner = bh.getOwner();
				bc.ownerTypeId = bh.getTypeId();
			}
		}
	}
	
	@Override
	public void processEvent(InventoryChangeEvent ice) {
		if( ice.type == InventoryEventType.CONTAINER_ACCESS ) {
			BlockHistory bh = blockHistoryManager.getBlockHistory(ice.getLocation());

			if( bh != null ) {
				ice.blockOwner = bh.getOwner();
			}
		}
	}
}
