/**
 * 
 */
package com.andune.heimdall.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.LWCBridge;
import com.andune.heimdall.event.EventCircularBuffer;
import com.andune.heimdall.event.EventManager;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.InventoryChangeEvent.InventoryEventType;
import com.andune.heimdall.player.PlayerTracker;
import com.andune.heimdall.util.General;

/** Inventory tracking; class heavily borrowed from @Diddiz's LogBlock plugin.
 * 
 * @author andune
 *
 */
public class BukkitInventoryListener implements Listener {
	private final Heimdall plugin;
	private final Map<Player, ContainerState> containers = new HashMap<Player, ContainerState>();
	private final EventManager eventManager;
	private final General util;
	private final EventCircularBuffer<InventoryChangeEvent> buffer;
	private final PlayerTracker tracker;

	public BukkitInventoryListener(final Heimdall plugin, final EventManager eventManager) {
		this.plugin = plugin;
		this.eventManager = eventManager;
		this.util = General.getInstance();
		this.tracker = plugin.getPlayerStateManager().getPlayerTracker();
		
		this.buffer = new EventCircularBuffer<InventoryChangeEvent>(InventoryChangeEvent.class, 1000, false, true);
	}
	
	private void pushInventoryChangeEvent(final Player player, final Location l, final ItemStack[] diff) {
		InventoryChangeEvent ice = getNextInventoryChangeEvent();
		if( ice != null ) {
			ice.playerName = player.getName();
			ice.time = System.currentTimeMillis();
			ice.world = l.getWorld();
			ice.x = l.getBlockX();
			ice.y = l.getBlockY();
			ice.z = l.getBlockZ();
			ice.location = l;
			ice.type = InventoryEventType.CONTAINER_ACCESS;
			
			ice.diff = diff;
			
			final LWCBridge lwc = plugin.getDependencyManager().getLWCBridge();
			if( lwc != null && lwc.isEnabled() && lwc.isPublic(l.getBlock()) )
				ice.isLwcPublic = true;
			else
				ice.isLwcPublic = false;
			
			eventManager.pushEvent(ice);
		}
		
	}
	
	private void checkInventoryClose(Player player) {
		final ContainerState cont = containers.get(player);
		if (cont != null) {
			final ItemStack[] before = cont.items;
			final BlockState state = cont.loc.getBlock().getState();
			if (!(state instanceof InventoryHolder))
				return;
			final ItemStack[] after = util.compressInventory(((InventoryHolder)state).getInventory().getContents());
			final ItemStack[] diff = util.compareInventories(before, after);

			pushInventoryChangeEvent(player, cont.loc, diff);
			containers.remove(player);
		}
	}

	private void checkInventoryOpen(Player player, Block block) {
		final BlockState state = block.getState();
		if (!(state instanceof InventoryHolder))
			return;
		containers.put(player, new ContainerState(block.getLocation(), util.compressInventory(((InventoryHolder)state).getInventory().getContents())));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void inventoryCloseEvent(InventoryCloseEvent event) {
		if( !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		if( plugin.isDisabledWorld(event.getPlayer().getWorld().getName()) )
			return;

		HumanEntity entity = event.getPlayer();
		if( !(entity instanceof Player) )
			return;
		
		checkInventoryClose((Player) entity);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		final Player player = event.getPlayer();
		if( plugin.isDisabledWorld(player.getWorld().getName()) )
			return;
		
		checkInventoryClose(player);
		if (!event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Block block = event.getClickedBlock();
			final int type = block.getTypeId();
			if (type == 23 || type == 54 || type == 61 || type == 62)
				checkInventoryOpen(player, block);
		}
	}

	private static class ContainerState
	{
		public final ItemStack[] items;
		public final Location loc;

		private ContainerState(Location loc, ItemStack[] items) {
			this.items = items;
			this.loc = loc;
		}
	}
	
	private final static int ERROR_FLOOD_PREVENTION_LIMIT = 3;
	private int errorFloodPreventionCount = 0;
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
