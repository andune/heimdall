/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.util.General;
import org.morganm.heimdall.util.JavaPluginExtensions;
import org.morganm.heimdall.util.PermissionSystem;

/**
 * @author morganm
 *
 */
public class NotifyEngine implements Engine {
	private final JavaPluginExtensions plugin; 
	private final PlayerStateManager playerStateManager;
	private final PermissionSystem perms;
	private final Map<String, Float> lastNotifyValues = new HashMap<String, Float>(20);
	private final Map<String, Set<String>> notifyIgnores = new HashMap<String, Set<String>>();
	private final FileConfiguration config;
	
	public NotifyEngine(final JavaPluginExtensions plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;
		this.perms = plugin.getPermissionSystem();
		
		String configFile = this.plugin.getConfig().getString("engine.notify.configfile");
		File file = new File(configFile);
		this.config = YamlConfiguration.loadConfiguration(file);
//		Debug.getInstance().debug("config.getInt(\"blockpoints.4\") = ",config.getInt("blockpoints.4"));
	}

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		processEvent(event, " (owner=",event.blockOwner,", Material=",event.type,")");
	}

	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		processEvent(event, " (owner=",event.blockOwner,")");
	}

	@Override
	public void processChatMessage(String message) {
		// TODO Auto-generated method stub
	}
	
	private void processEvent(Event event, Object...arg) {
		Float lastNotifyValue = lastNotifyValues.get(event.getPlayerName());
		if( lastNotifyValue == null )
			lastNotifyValue = Float.valueOf(0);
		float newValue = playerStateManager.getPlayerState(event.getPlayerName()).getGriefPoints();
		
		// if the number has gone up by at least a whole number, time to notify
		if( Math.ceil(newValue) > Math.ceil(lastNotifyValue) ) {
			doNotify(event, newValue, arg);
		}
		
		// we update no matter what, because if the value went down, we want to capture
		// the new value so that any future griefing will be notified on as well
		lastNotifyValues.put(event.getPlayerName(), newValue);
	}
	
	/** Called when an admin wants to ignore notifications from a given player.
	 * 
	 * @param playerAdmin
	 * @param playerIgnored
	 */
	public void addNotifyIgnore(final String playerAdmin, final String playerIgnored) {
		Set<String> ignores = notifyIgnores.get(playerAdmin);
		if( ignores == null ) {
			ignores = new HashSet<String>();
			notifyIgnores.put(playerAdmin, ignores);
		}
		ignores.add(playerIgnored);
	}
	/** Called to clear a previously set admin ignore.
	 * 
	 * @param playerAdmin
	 * @param playerIgnored
	 */
	public void removeNotifyIgnore(final String playerAdmin, final String playerIgnored) {
		Set<String> ignores = notifyIgnores.get(playerAdmin);
		if( ignores != null )
			ignores.remove(playerIgnored);
	}

	private void doNotify(final Event event, final float griefPoints, final Object...arg) {
		final List<Player> notifyTargets = getOnlineNotifyTargets();
		if( notifyTargets != null && notifyTargets.size() > 0 ) {
			final StringBuilder sb = new StringBuilder();
			for(int i=0; i < arg.length; i++) {
				sb.append(arg[i]);
			}
			final String additionalData = sb.toString();
			for(Player p : notifyTargets) {
				Set<String> ignores = notifyIgnores.get(p.getName());
				
				// notify if no ignores are set or if the player is not in the ignore list
				if( ignores == null || !ignores.contains(event.getPlayerName()) ) {
					p.sendMessage("Player "+event.getPlayerName()+" has accumulated "
							+griefPoints+" total grief points. Latest action "+event.getEventTypeString()
							+" at location {"+General.getInstance().shortLocationString(event.getLocation())+"}"
							+additionalData);
				}
			}
		}
	}
	
	private List<Player> getOnlineNotifyTargets() {
		List<Player> notifyTargets = new ArrayList<Player>(5);
		
		String permission = config.getString("notifyPermission");
		if( permission != null ) {
			Player[] players = Bukkit.getOnlinePlayers();
			for(int i=0; i < players.length; i++) {
				if( perms.has(players[i].getName(), permission) )
					notifyTargets.add(players[i]);
			}
		}
		
		return notifyTargets;
	}
}
