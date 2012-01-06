/**
 * 
 */
package org.morganm.heimdall.blockhistory;

import org.bukkit.plugin.Plugin;

/**
 * @author morganm
 *
 */
public class BlockHistoryFactory {
	private static final BlockHistoryCache cache = new BlockHistoryCache();
	
	public static BlockHistoryManager getBlockHistoryManager(final Plugin plugin) {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p != null )
			return new BlockHistoryLogBlock(plugin, cache);
		
		return null;
	}
	
	public static BlockHistoryCache getBlockHistoryCache() { return cache; }
}
