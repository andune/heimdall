/**
 * 
 */
package org.morganm.heimdall.player;

import java.util.HashMap;
import java.util.Map;

import org.morganm.util.JavaPluginExtensions;

/** Class for tracking and storing PlayerState objects.
 * 
 * @author morganm
 *
 */
public class PlayerStateManager {
	@SuppressWarnings("unused")
	private final JavaPluginExtensions plugin;
	private final Map<String, PlayerState> playerStateMap;
	
	public PlayerStateManager(final JavaPluginExtensions plugin) {
		this.plugin = plugin;
		this.playerStateMap = new HashMap<String, PlayerState>(20);
	}
	
	/** Get the PlayerState object for a player.
	 * 
	 * @param playerName
	 * @return the PlayerState object, guaranteed to be non-null
	 */
	public PlayerState getPlayerState(final String playerName) {
		PlayerState ps = playerStateMap.get(playerName);
		if( ps == null ) {
			ps = new PlayerStateImpl(playerName, 0);
			playerStateMap.put(playerName, ps);
		}
		return ps;
	}
	
	public void load() {}
	public void save() {}
}
