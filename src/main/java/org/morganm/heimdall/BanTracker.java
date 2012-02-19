/**
 * 
 */
package org.morganm.heimdall;

import java.util.HashMap;


/**
 * @author morganm
 *
 */
public class BanTracker {
	@SuppressWarnings("unused")
	private final Heimdall plugin;
	private final HashMap<String, Ban> bans = new HashMap<String, Ban>(10);
	
	public BanTracker(final Heimdall plugin) {
		this.plugin = plugin;
	}
	
	public void addCommand(final String bannedPlayer, final String banCommand, final String commandSender) {
		bans.put(bannedPlayer, new Ban(banCommand, commandSender));
	}
	
	public void unBan(final String bannedPlayer) {
		bans.remove(bannedPlayer);
	}
	
	public String getBanCommand(String bannedPlayer) {
		Ban ban = bans.get(bannedPlayer);
		if( ban != null )
			return ban.banCommand;
		else
			return null;
	}
	
	public String getBanSender(String bannedPlayer) {
		Ban ban = bans.get(bannedPlayer);
		if( ban != null )
			return ban.commandSender;
		else
			return null;
	}
	
	private class Ban {
		public String banCommand;
		public String commandSender;
		
		public Ban(String banCommand, String commandSender) {
			this.banCommand = banCommand;
			this.commandSender = commandSender;
		}
	}
}
