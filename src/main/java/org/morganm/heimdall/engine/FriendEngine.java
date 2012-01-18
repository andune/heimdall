/**
 * 
 */
package org.morganm.heimdall.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.player.FriendTracker;
import org.morganm.heimdall.util.CircularBuffer;
import org.morganm.heimdall.util.Debug;

/** Engine which watches events to establish friend relationships.
 * 
 * @author morganm
 *
 */
public class FriendEngine implements Engine {
	private static final long SECONDS_60 = 60000;	// 60 seconds * 1000 milliseconds
	
	private final Heimdall plugin;
	private final FriendTracker friendTracker;
	private final Debug debug;
	private final HashMap<String, PlayerActivity> playerActivity = new HashMap<String, PlayerActivity>(20);
	
	public FriendEngine(final Heimdall plugin) {
		this.plugin = plugin;
		this.friendTracker = this.plugin.getFriendTracker();
		this.debug = Debug.getInstance();
	}

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		debug.debug("FriendEngine processing block change by player ",event.playerName);
		PlayerActivity activity = getPlayerActivityObject(event.playerName);
		int chunkX = event.getLocation().getChunk().getX();
		int chunkZ = event.getLocation().getChunk().getZ();
		
		// update timestamp of existing activity for this player & chunk, or create
		// a new chunkActivity object with the current timestamp
		ChunkActivity ca = activity.getChunkActivity(chunkX,  chunkZ);
		if( ca != null )
			ca.timestamp = event.time;
		else
			activity.addNewChunkActivity(chunkX, chunkZ, event.time);
		
		updateActivityPoints(event.playerName, event.time, 1);
	}

	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
	}

	@Override
	public void processChatMessage(String message) {
	}

	/** Return players that are nearby the given player. Players are considered to be
	 * nearby if the are in the same or immediately adjacent chunks. This is a bit crude,
	 * but a very fast/efficient check that guarantees a player is considered nearby
	 * if they are within 16 blocks (1 chunk) of the player or at most up to 32 blocks
	 * away (if both players are at the edge of adjacent chunks).
	 * 
	 * @param p
	 * @return
	 */
	private List<Player> findNearbyPlayers(final Player p) {
		ArrayList<Player> nearbyPlayers = new ArrayList<Player>(5);
		
		final Chunk playerChunk = p.getLocation().getChunk();
		final int chunkX = playerChunk.getX();
		final int chunkZ = playerChunk.getZ();
		
		Player[] players = Bukkit.getOnlinePlayers();
		for(int i=0; i < players.length; i++) {
			if( players[i] == p )
				continue;
			
			final Chunk chunk = players[i].getLocation().getChunk();
			final int x = chunk.getX();
			final int z = chunk.getZ();

			int deltaX = chunkX-x;
			int deltaZ = chunkZ-z;
			if( (deltaX >= -1 && deltaX <= 1) && (deltaZ >= -1 && deltaZ <= 1) ) {
				nearbyPlayers.add(players[i]);
			}
		}
		
		debug.devDebug("count of nearby players to player ",p," is ",nearbyPlayers.size());
		
		return nearbyPlayers;
	}
	
	/** Get or create a PlayerActivity object for a given player.
	 * 
	 * @param playerName
	 * @return
	 */
	private PlayerActivity getPlayerActivityObject(final String playerName) {
		PlayerActivity pa = playerActivity.get(playerName);
		if( pa == null ) {
			pa = new PlayerActivity(playerName);
			playerActivity.put(playerName, pa);
		}
		return pa;
	}
	
	private void updateActivityPoints(String playerName, long eventTime, float points) {
		final Player player = Bukkit.getPlayer(playerName);
		if( player == null )
			return;
		
		final int chunkX = player.getLocation().getChunk().getX();
		final int chunkZ = player.getLocation().getChunk().getZ();
		
		final List<Player> nearbyPlayers = findNearbyPlayers(player);
		for(Player p : nearbyPlayers) {
			debug.devDebug("updateActivityPoints: checking nearby player ",p," due to activity from player ",playerName);
			PlayerActivity pa = getPlayerActivityObject(p.getName());
			
			// Look for recent activity from the nearby player. If they were recently
			// active, then increment the friendship points
			for(int i=0; i < pa.chunkActivity.length; i++) {
				if( pa.chunkActivity[i] != null ) {
					debug.devDebug("checking active chunk for player activity from ",playerName);
					// activty has to have been in the last 60 seconds
					if( (eventTime - pa.chunkActivity[i].timestamp) < SECONDS_60 ) {
						
						// if the event happened within 1 chunk of the player's location, then
						// we count this player as close enough to be associated
						int deltaX = pa.chunkActivity[i].chunkX - chunkX;
						int deltaZ = pa.chunkActivity[i].chunkZ - chunkZ;
						if( (deltaX >= -1 && deltaX <= 1) && (deltaZ >= -1 && deltaZ <= 1) ) {
							friendTracker.addFriendPoints(playerName, p.getName(), points);
							debug.debug("updateActivityPoints: ",points," point(s) added between player ",playerName," and ",p);
						}
					}
				}
			}
		}
	}

	private class PlayerActivity {
		private final String playerName;
		private final ChunkActivity[] chunkActivity = new ChunkActivity[5];
		private int chunkActivityPtr=0;
//		final CircularBuffer<ChunkActivity> activityChunks = new CircularBuffer<ChunkActivity>(ChunkActivity.class, 5, false, true);
		
		public PlayerActivity(final String playerName) {
			this.playerName = playerName;
		}
		public String getPlayerName() { return playerName; }
		
		public ChunkActivity getChunkActivity(int x, int z) {
			for(int i=0; i < chunkActivity.length; i++) {
				if( chunkActivity[i] != null ) {
					if( chunkActivity[i].chunkX == x && chunkActivity[i].chunkZ == z )
						return chunkActivity[i];
				}
			}
			return null;
		}
		public void addNewChunkActivity(int x, int z, long timestamp) {
			ChunkActivity current = chunkActivity[chunkActivityPtr];
			if( current == null ) {
				current = new ChunkActivity();
				chunkActivity[chunkActivityPtr] = current;
			}
			
			// wrap around to beginning
			if( ++chunkActivityPtr >= chunkActivity.length )
				chunkActivityPtr = 0;
			
			current.chunkX = x;
			current.chunkZ = z;
			current.timestamp = timestamp;
		}
	}
	private class ChunkActivity {
		int chunkX;
		int chunkZ;
		long timestamp;
	}
}
