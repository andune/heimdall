/**
 * 
 */
package org.morganm.heimdall;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;

/**
 * @author morganm
 *
 */
public class LWCBridge {
	private final Heimdall plugin;
	private LWC lwc;
	
	public LWCBridge(final Heimdall plugin) {
		this.plugin = plugin;
		
		Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LWC");
		if( p != null ) {
			lwc = ((LWCPlugin) p).getLWC();
			plugin.getLogger().info(plugin.getLogPrefix()+"LWC version "+p.getDescription().getVersion()+" found and will be used to identify public chests");
		}
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
