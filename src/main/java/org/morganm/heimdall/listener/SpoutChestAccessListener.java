/**
 * 
 */
package org.morganm.heimdall.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.morganm.util.General;
import org.morganm.util.JavaPluginExtensions;

import com.mysql.jdbc.log.Log;

/** Code originally copied from @Diddiz's LogBlock plugin.
 * 
 * @author morganm, Diddiz (original Logblock code)
 *
 */
public class SpoutChestAccessListener extends InventoryListener {
	private final JavaPluginExtensions plugin;
	private final General util;
	private BlockHistoryManager blockHistoryManager;
	private final Map<Player, ItemStack[]> containers = new HashMap<Player, ItemStack[]>();

	public SpoutChestAccessListener(final JavaPluginExtensions plugin) {
		this.plugin = plugin;
		this.blockHistoryManager = BlockHistoryFactory.getBlockHistoryManager(plugin);
		this.util = General.getInstance();
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
			BlockHistory bh = blockHistoryManager.getBlockHistory(l);
			String blockOwner = null;
			if( bh != null )
				blockOwner = bh.getOwner();
			String ownerString = null;
			
			if( player.getName().equals(blockOwner) )
				ownerString = "owner="+blockOwner;
			else
				ownerString = "owner="+blockOwner+" ** NOT BLOCK OWNER **";

			final ItemStack[] after = util.compressInventory(event.getInventory().getContents());
			final ItemStack[] diff = util.compareInventories(before, after);
			for (final ItemStack item : diff) {
				if( item.getAmount() < 0 )
					log.logMessage("item "+item+" removed from container at {"+util.shortLocationString(l)+"}, "+ownerString);
				else
					log.logMessage("item "+item+" added to container at {"+util.shortLocationString(l)+"}, "+ownerString);
			}
			containers.remove(player);
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
	
	/** Log crafting events as well.
	 * 
	 * @author morganm
	 */
	@Override
	public void onInventoryCraft(InventoryCraftEvent event) {
		final Location l = event.getLocation();
		if(!event.isCancelled() && l != null) {
			final Player player = event.getPlayer();
			if( !trackerManager.isTracked(player) )
				return;
			
			ItemStack[] contents = event.getInventory().getContents();
			if( contents != null ) {
				final Log log = logManager.getLog(player);
				for(ItemStack item : contents) {
					if( item != null )
						log.logMessage("crafted item "+item+" at location {"+util.shortLocationString(l)+"}");
				}
			}
		}
	}
}
