/**
 * 
 */
package com.andune.heimdall;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;

/** Class that provides a simplified wrapper to LWC.
 * 
 * @author andune
 *
 */
public class LWCBridge {
	private final Heimdall plugin;
	private LWC lwc;
	
	public LWCBridge(final Heimdall plugin) {
		this.plugin = plugin;
		
		Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LWC");
		if( p != null )
			updateLWC((LWCPlugin) p);
	}
	
	public void updateLWC(final LWCPlugin lwcPlugin) {
		if( lwcPlugin != null ) {
			this.lwc = lwcPlugin.getLWC();
			this.plugin.getLogger().info(this.plugin.getLogPrefix()+"LWC version "+lwcPlugin.getDescription().getVersion()+" found and will be used to identify public chests");
		}
		else
			this.lwc = null;
	}
	
	public boolean isEnabled() {
		// TODO: should include config option as well
		return (lwc != null);
	}
	
	public boolean isPublic(Block b) {
		if( !isEnabled() )
			return false;
		
		Protection protection = lwc.findProtection(b);
		if( protection != null && protection.getType() == Protection.Type.PUBLIC )
			return true;
		else
			return false;
	}
}
