/**
 * 
 */
package org.morganm.heimdall.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.morganm.heimdall.event.EventCircularBuffer;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;
import org.morganm.util.General;
import org.morganm.util.JavaPluginExtensions;

/** Code originally copied from @Diddiz's LogBlock plugin.
 * 
 * @author morganm, Diddiz (original Logblock code)
 *
 */
public class SpoutChestAccessListener extends InventoryListener {
	private final static int ERROR_FLOOD_PREVENTION_LIMIT = 3;

	@SuppressWarnings("unused")
	private final JavaPluginExtensions plugin;
	private final General util;
	private final Map<Player, ItemStack[]> containers = new HashMap<Player, ItemStack[]>();
	private final EventCircularBuffer<InventoryChangeEvent> buffer;
	private int errorFloodPreventionCount = 0;

	public SpoutChestAccessListener(final JavaPluginExtensions plugin) {
		this.plugin = plugin;
		this.util = General.getInstance();
		
		this.buffer = new EventCircularBuffer<InventoryChangeEvent>(InventoryChangeEvent.class, 1000, false);
	}

	/** When an inventory is closed, check to see if we had a "before" snapshot recorded
	 * to compare against. If so, log the differences.
	 * 
	 * @author morganm, Diddiz (original LogBlock code)
	 */
	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		final Location l = event.getLocation();
		if (event.isCancelled() || l == null)
			return;

		final Player player = event.getPlayer();

		final ItemStack[] before = containers.get(player);
		if (before != null) {
			final ItemStack[] after = util.compressInventory(event.getInventory().getContents());
			final ItemStack[] diff = util.compareInventories(before, after);
			containers.remove(player);
			
			InventoryChangeEvent ice = getNextInventoryChangeEvent();
			if( ice != null ) {
				ice.playerName = player.getName();
				ice.world = l.getWorld();
				ice.x = l.getBlockX();
				ice.y = l.getBlockY();
				ice.z = l.getBlockZ();
				ice.location = l;
				ice.type = InventoryEventType.CONTAINER_ACCESS;
				
				ice.diff = diff;
			}
		}
	}

	/** Record the "before" inventory of the container in memory when the chest is opened,
	 * so that we can later compare before/after to see what has changed. -morganm
	 * 
	 * @author Diddiz
	 */
	@Override
	public void onInventoryOpen(InventoryOpenEvent event) {
		// blockid 58 == crafting table, so this code ignores those. -morganm
		if (!event.isCancelled() && event.getLocation() != null && event.getLocation().getBlock().getTypeId() != 58)
			containers.put(event.getPlayer(), util.compressInventory(event.getInventory().getContents()));
	}
	
	/** Capture crafting events as well.
	 * 
	 * @author morganm
	 */
	@Override
	public void onInventoryCraft(InventoryCraftEvent event) {
		final Location l = event.getLocation();
		if(!event.isCancelled() && l != null) {
			final Player player = event.getPlayer();
			
			ItemStack[] contents = event.getInventory().getContents();
			if( contents != null ) {
				InventoryChangeEvent ice = getNextInventoryChangeEvent();
				if( ice != null ) {
					ice.playerName = player.getName();
					ice.world = l.getWorld();
					ice.x = l.getBlockX();
					ice.y = l.getBlockY();
					ice.z = l.getBlockZ();
					ice.location = l;
					ice.type = InventoryEventType.CRAFTED;
					
					ice.diff = contents;
				}
			}
		}
	}
	
	private InventoryChangeEvent getNextInventoryChangeEvent() {
		InventoryChangeEvent ice = null;
		try {
			ice = buffer.getNextObject();
			errorFloodPreventionCount = 0;
		} catch (InstantiationException e) {
			errorFloodPreventionCount++;
			if( errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT )
				e.printStackTrace();
		} catch (IllegalAccessException e) {
			errorFloodPreventionCount++;
			if( errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT )
				e.printStackTrace();
		}
		
		ice.cleared = false;	// change clear flag since we are going to use this object
		return ice;
	}
}
