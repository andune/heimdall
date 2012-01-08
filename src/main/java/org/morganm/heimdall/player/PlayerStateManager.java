/**
 * 
 */
package org.morganm.heimdall.player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.morganm.heimdall.Heimdall;

/** Class for tracking and storing PlayerState objects.
 * 
 * @author morganm
 *
 */
public class PlayerStateManager {
	private final Heimdall plugin;
	private final Logger log;
	private final String logPrefix;
	private final Map<String, PlayerState> playerStateMap;
	private File friendDataFile = new File("plugins/Heimdall/friends.yml");
	private YamlConfiguration friendData;
	private final PlayerTracker playerTracker;
	
	public PlayerStateManager(final Heimdall plugin) {
		this.plugin = plugin;
		this.playerStateMap = new HashMap<String, PlayerState>(20);
		this.log = this.plugin.getLogger();
		this.logPrefix = this.plugin.getLogPrefix();
		this.playerTracker = new PlayerTracker(this);
	}
	
	public PlayerTracker getPlayerTracker() { return playerTracker; }
	
	/** Get the PlayerState object for a player.
	 * 
	 * @param playerName
	 * @return the PlayerState object, guaranteed to be non-null
	 */
	public PlayerState getPlayerState(final String playerName) {
		if( friendData == null )
			loadFriends();
		
		if( playerName == null )
			throw new NullPointerException("playerName is null");

		PlayerState ps = playerStateMap.get(playerName);
		if( ps == null ) {
			ps = loadPlayerState(playerName);
			playerStateMap.put(playerName, ps);
		}
		return ps;
	}
	
	private PlayerState loadPlayerState(final String playerName) {
		PlayerState ps = new PlayerStateImpl(plugin, playerName, this);
		try {
			ps.load();
		}
		catch(Exception e) {
			log.warning(logPrefix+"error loading PlayerState for player "+playerName+", error: "+e.getMessage());
			e.printStackTrace();
		}
		return ps;
	}
	
	private void loadFriends() {
//		friendData = YamlConfiguration.loadConfiguration(friendDataFile);
//		friendData.getKeys(false);
	}
	private void saveFriends() {
		
	}
	public void save() throws Exception {
		saveFriends();

		for(PlayerState ps : playerStateMap.values()) {
			ps.save();
		}
	}
}
