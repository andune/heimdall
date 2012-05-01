/**
 * 
 */
package org.morganm.heimdall.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.LWCBridge;
import org.morganm.heimdall.event.EventCircularBuffer;
import org.morganm.heimdall.event.EventManager;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;
import org.morganm.heimdall.player.PlayerTracker;
import org.morganm.heimdall.util.General;

/** Inventory tracking; class heavily borrowed from @Diddiz's LogBlock plugin.
 * 
 * @author morganm
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
	
	public void checkInventoryClose(Player player) {
		final ContainerState cont = containers.get(player);
		if (cont != null) {
			final ItemStack[] before = cont.items;
			final BlockState state = cont.loc.getBlock().getState();
			if (!(state instanceof InventoryHolder))
				return;
			final ItemStack[] after = util.compressInventory(((InventoryHolder)state).getInventory().getContents());
			final ItemStack[] diff = util.compareInventories(before, after);

			pushInventoryChangeEvent(player, cont.loc, diff);
			
//				for (final ItemStack item : diff)
//				consumer.queueChestAccess(player.getName(), cont.loc, state.getTypeId(), (short)item.getTypeId(), (short)item.getAmount(), rawData(item));
			containers.remove(player);
		}
	}

	public void checkInventoryOpen(Player player, Block block) {
		final BlockState state = block.getState();
		if (!(state instanceof InventoryHolder))
			return;
		containers.put(player, new ContainerState(block.getLocation(), util.compressInventory(((InventoryHolder)state).getInventory().getContents())));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(PlayerChatEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		checkInventoryClose(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		checkInventoryClose(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if( !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		checkInventoryClose(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		checkInventoryClose(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		final Player player = event.getPlayer();
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
