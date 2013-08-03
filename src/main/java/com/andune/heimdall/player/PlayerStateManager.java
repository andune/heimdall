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

import com.andune.heimdall.Heimdall;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class for tracking and storing PlayerState objects.
 *
 * @author andune
 */
public class PlayerStateManager {
    private final Heimdall plugin;
    private final Logger log;
    private final String logPrefix;
    private final Map<String, PlayerState> playerStateMap;
    //	private File friendDataFile = new File("plugins/Heimdall/friends.yml");
//	private YamlConfiguration friendData;
    private final PlayerTracker playerTracker;

    public PlayerStateManager(final Heimdall plugin) {
        this.plugin = plugin;
        this.playerStateMap = new HashMap<String, PlayerState>(20);
        this.log = this.plugin.getLogger();
        this.logPrefix = this.plugin.getLogPrefix();
        this.playerTracker = new PlayerTracker(this);
    }

    public PlayerTracker getPlayerTracker() {
        return playerTracker;
    }

    /**
     * Get the PlayerState object for a player.
     *
     * @param playerName
     * @return the PlayerState object, guaranteed to be non-null
     */
    public PlayerState getPlayerState(final String playerName) {
        if (playerName == null)
            throw new NullPointerException("playerName is null");

        PlayerState ps = playerStateMap.get(playerName.toLowerCase());
        if (ps == null) {
            ps = loadPlayerState(playerName);        // intentionally not .toLowerCase()
            playerStateMap.put(playerName.toLowerCase(), ps);
        }
        return ps;
    }

    private PlayerState loadPlayerState(final String playerName) {
        PlayerState ps = new PlayerStateImpl(plugin, playerName, this);
        try {
            ps.load();
        } catch (Exception e) {
            log.warning(logPrefix + "error loading PlayerState for player " + playerName + ", error: " + e.getMessage());
            e.printStackTrace();
        }
        return ps;
    }

    /**
     * Package visibility method.
     *
     * @param ps
     */
    void removePlayerState(final PlayerState ps) {
        playerStateMap.remove(ps);
    }

    public void save() throws Exception {
//		saveFriends();

        for (PlayerState ps : playerStateMap.values()) {
            ps.save();
        }
    }
}
