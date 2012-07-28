/**
 * 
 */
package org.morganm.heimdall.blockhistory;

import org.bukkit.plugin.Plugin;
import org.morganm.heimdall.Heimdall;

/**
 * @author morganm
 *
 */
public class BlockHistoryFactory {
	private static final BlockHistoryCache cache = new BlockHistoryCache();
	
	public static BlockHistoryManager getBlockHistoryManager(final Heimdall plugin) {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p != null )
			return new BlockHistoryLogBlock(plugin, cache);
		
		if( plugin.getConfig().getBoolean("blockHistoryTest", false) )
			return new BlockHistoryTest(plugin, cache);
		
		return null;
	}
	
	public static BlockHistoryCache getBlockHistoryCache() { return cache; }
}
