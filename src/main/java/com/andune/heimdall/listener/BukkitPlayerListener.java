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
package com.andune.heimdall.listener;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.event.EventCircularBuffer;
import com.andune.heimdall.event.EventManager;
import com.andune.heimdall.event.PlayerEvent;
import com.andune.heimdall.event.PlayerEvent.Type;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.player.PlayerTracker;
import com.andune.heimdall.util.Debug;
import com.andune.heimdall.util.General;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author andune
 */
public class BukkitPlayerListener implements Listener {
    private final Heimdall plugin;
    private final PlayerStateManager playerStateManager;
    private final PlayerTracker playerTracker;
    private final EventCircularBuffer<PlayerEvent> buffer;
    private final EventManager eventManager;

    public BukkitPlayerListener(final Heimdall plugin, final EventManager eventManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.playerStateManager = this.plugin.getPlayerStateManager();
        this.playerTracker = this.playerStateManager.getPlayerTracker();
        buffer = new EventCircularBuffer<PlayerEvent>(PlayerEvent.class, 1000, false, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerTracker.playerLogin(event.getPlayer().getName());

        PlayerEvent pe = getNextEventObject();

        if (pe != null) {
            if (General.getInstance().isNewPlayer(event.getPlayer())) {
                pe.eventType = Type.NEW_PLAYER_JOIN;
                Debug.getInstance().debug("New player join: ", event.getPlayer());
            }
            else
                pe.eventType = Type.PLAYER_JOIN;

            pe.playerName = event.getPlayer().getName();
            pe.location = event.getPlayer().getLocation();
            pe.x = pe.location.getBlockX();
            pe.y = pe.location.getBlockY();
            pe.z = pe.location.getBlockZ();
            pe.world = pe.location.getWorld();
            pe.time = System.currentTimeMillis();

            eventManager.pushEvent(pe);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerTracker.removeTrackedPlayer(event.getPlayer().getName());

        PlayerEvent pe = getNextEventObject();

        if (pe != null) {
            pe.eventType = Type.PLAYER_QUIT;
            pe.playerName = event.getPlayer().getName();
            pe.location = event.getPlayer().getLocation();
            pe.x = pe.location.getBlockX();
            pe.y = pe.location.getBlockY();
            pe.z = pe.location.getBlockZ();
            pe.world = pe.location.getWorld();
            pe.time = System.currentTimeMillis();

            eventManager.pushEvent(pe);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled())
            return;
        playerTracker.removeTrackedPlayer(event.getPlayer().getName());

        PlayerEvent pe = getNextEventObject();

        if (pe != null) {
            pe.eventType = Type.PLAYER_KICK;
            pe.playerName = event.getPlayer().getName();
            pe.location = event.getPlayer().getLocation();
            pe.x = pe.location.getBlockX();
            pe.y = pe.location.getBlockY();
            pe.z = pe.location.getBlockZ();
            pe.world = pe.location.getWorld();
            pe.time = System.currentTimeMillis();

            eventManager.pushEvent(pe);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/")) {
            if (event.getMessage().startsWith("/ban")) {
                String args = event.getMessage().substring(5);
                if (args != null) {
                    int index = args.indexOf(' ');
                    if (index != -1) {
                        String bannedPlayer = args.substring(0, index - 1);
                        Debug.getInstance().debug("ban command for player ", bannedPlayer, " from player ", event.getPlayer());
//						plugin.getBanTracker().addCommand(bannedPlayer, event.getMessage(), event.getPlayer().getName());

                        PlayerEvent pe = getNextEventObject();
                        if (pe != null) {
                            pe.eventType = Type.PLAYER_BANNED;
                            pe.playerName = bannedPlayer;
                            pe.location = event.getPlayer().getLocation();
                            pe.x = pe.location.getBlockX();
                            pe.y = pe.location.getBlockY();
                            pe.z = pe.location.getBlockZ();
                            pe.world = pe.location.getWorld();
                            pe.time = System.currentTimeMillis();
                            pe.extraData = new String[]{event.getMessage(), event.getPlayer().getName()};

                            eventManager.pushEvent(pe);
                        }
                    }
                }
            }
            else if (event.getMessage().startsWith("/unban")) {
                String args = event.getMessage().substring(7);
                if (args != null) {
                    int index = args.indexOf(' ');
                    if (index != -1) {
                        String unbannedPlayer = args.substring(0, index - 1);
                        Debug.getInstance().debug("unban command for player ", unbannedPlayer, " from player ", event.getPlayer());
//						plugin.getBanTracker().unBan(unbannedPlayer);
                        PlayerEvent pe = getNextEventObject();

                        if (pe != null) {
                            pe.eventType = Type.PLAYER_UNBANNED;
                            pe.playerName = unbannedPlayer;
                            pe.location = event.getPlayer().getLocation();
                            pe.x = pe.location.getBlockX();
                            pe.y = pe.location.getBlockY();
                            pe.z = pe.location.getBlockZ();
                            pe.world = pe.location.getWorld();
                            pe.time = System.currentTimeMillis();
                            pe.extraData = new String[]{event.getMessage(), event.getPlayer().getName()};

                            eventManager.pushEvent(pe);
                        }
                    }
                }
            }
        }
    }

    private final static int ERROR_FLOOD_PREVENTION_LIMIT = 3;
    private int errorFloodPreventionCount = 0;

    private PlayerEvent getNextEventObject() {
        PlayerEvent event = null;
        try {
            event = buffer.getNextObject();
            errorFloodPreventionCount = 0;
        } catch (InstantiationException e) {
            errorFloodPreventionCount++;
            if (errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT)
                e.printStackTrace();
        } catch (IllegalAccessException e) {
            errorFloodPreventionCount++;
            if (errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT)
                e.printStackTrace();
        }

        event.cleared = false;    // change clear flag since we are going to use this object
        return event;
    }
}
