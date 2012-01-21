/**
 * 
 */
package org.morganm.heimdall;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.morganm.heimdall.util.Debug;
import org.morganm.heimdall.util.PermissionSystem;

/** Things related to Heimdall's personality are here in one easy spot, to allow
 * for turning him off if desired.
 * 
 * @author morganm
 *
 */
public class HeimdallPersonality {
	private final Heimdall plugin;
	private final PermissionSystem perm;

	public HeimdallPersonality(final Heimdall plugin) {
		this.plugin = plugin;
		this.perm = this.plugin.getPermissionSystem();
	}
	
	public boolean isEnabled() {
		return plugin.getConfig().getBoolean("core.personality.enabled", true);
	}
	
	public void announcePossibleGriefer(final String playerName) {
		List<String> silentPerms = plugin.getConfig().getStringList("core.personality.silentPerms");
		
		Player[] players = Bukkit.getOnlinePlayers();
		for(int i=0; i < players.length; i++) {
			// don't send the message to the griefer
			if( players[i].getName().equalsIgnoreCase(playerName) )
				continue;
			
			// look to see if player is has a "silentPerm", in which case Heimdall is silent to them
			boolean silentPerm=false;
			if( silentPerms != null ) {
				for(String permission : silentPerms) {
					if( perm.has(players[i], permission) ) {
						silentPerm=true;
						break;
					}
				}
			}
			
			if( !silentPerm ) {
				players[i].sendMessage(ChatColor.RED+"Heimdall bellows: What have we here? I'm going to keep my eye on this one..");
				Debug.getInstance().debug("Heimdall \"possible grief\" bellow sent to player ",players[i]);
			}
		}
	}
}
