/**
 * 
 */
package com.andune.heimdall.blockhistory;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * @author andune
 *
 */
public interface BlockHistoryManager {
	public BlockHistory getBlockHistory(Location l);
	
	/** Can be called externally to respond to plugin load events, so that we
	 * can update our dependencies if a block manager is reloaded.
	 * 
	 * @param plugin
	 */
	public void pluginLoaded(Plugin plugin);

	/** Can be called externally to respond to plugin unload events, so that we
	 * can update our dependencies if a block manager is unloaded.
	 * 
	 * @param plugin
	 */
	public void pluginUnloaded(Plugin plugin);
}
