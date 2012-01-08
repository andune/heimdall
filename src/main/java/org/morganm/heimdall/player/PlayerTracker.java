/**
 * 
 */
package org.morganm.heimdall.player;

import java.util.HashSet;

import org.morganm.heimdall.util.Debug;

/** Class which keeps track of which players are being tracked by Heimdall.
 * 
 * @author morganm
 *
 */
public class PlayerTracker {
	private final HashSet<String> trackedPlayers = new HashSet<String>(10);
	private final PlayerStateManager playerStateManager;
	private final Debug debug;
	
	public PlayerTracker(final PlayerStateManager playerStateManager) {
		this.playerStateManager = playerStateManager;
		this.debug = Debug.getInstance();
	}
	
	public void playerLogin(final String playerName) {
		PlayerState ps = playerStateManager.getPlayerState(playerName);
		if( !ps.isExemptFromChecks() ) {
			trackedPlayers.add(playerName);
			debug.debug("Player ",playerName," logged in and now being tracked by Heimdall");
		}
		else
			debug.debug("Player ",playerName," logged in and is exempt from Heimdall tracking");
	}
	
	public void addTrackedPlayer(final String playerName) {
		trackedPlayers.add(playerName);
	}
	public void removeTrackedPlayer(final String playerName) {
		trackedPlayers.remove(playerName);
	}
	public boolean isTrackedPlayer(final String playerName) {
		return trackedPlayers.contains(playerName);
	}
}
