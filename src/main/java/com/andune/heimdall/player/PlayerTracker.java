/**
 *
 */
package com.andune.heimdall.player;

import com.andune.heimdall.util.Debug;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Class which keeps track of which players are being tracked by Heimdall.
 *
 * @author andune
 */
public class PlayerTracker {
    private final HashSet<String> trackedPlayers = new HashSet<String>(10);
    private final PlayerStateManager playerStateManager;
    private final Debug debug;

    public PlayerTracker(final PlayerStateManager playerStateManager) {
        this.playerStateManager = playerStateManager;
        this.debug = Debug.getInstance();
    }

    /**
     * Clear trackedPlayers and re-check against currently online people.
     */
    public void reset() {
        trackedPlayers.clear();
        Player[] players = Bukkit.getOnlinePlayers();
        debug.debug("Tracker reset; running all ", players.length, " online players through PlayerTracker login check");
        for (int i = 0; i < players.length; i++)
            playerStateManager.getPlayerTracker().playerLogin(players[i].getName());
    }

    public void playerLogin(final String playerName) {
        PlayerState ps = playerStateManager.getPlayerState(playerName);
        if (!ps.isExemptFromChecks()) {
            trackedPlayers.add(playerName);
            debug.debug("Player ", playerName, " logged in and now being tracked by Heimdall");
        }
        else
            debug.debug("Player ", playerName, " logged in and is exempt from Heimdall tracking");
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
