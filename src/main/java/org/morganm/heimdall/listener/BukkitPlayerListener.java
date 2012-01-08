/**
 * 
 */
package org.morganm.heimdall.listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.player.PlayerTracker;

/**
 * @author morganm
 *
 */
public class BukkitPlayerListener extends PlayerListener {
	private final Heimdall plugin;
	private final PlayerStateManager playerStateManager;
	private final PlayerTracker playerTracker;
	
	public BukkitPlayerListener(final Heimdall plugin) {
		this.plugin = plugin;
		this.playerStateManager = this.plugin.getPlayerStateManager();
		this.playerTracker = this.playerStateManager.getPlayerTracker();
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerTracker.playerLogin(event.getPlayer().getName());
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		playerTracker.removeTrackedPlayer(event.getPlayer().getName());
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if( event.isCancelled() )
			return;
		playerTracker.removeTrackedPlayer(event.getPlayer().getName());
	}
}
