/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
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
