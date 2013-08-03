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
package com.andune.heimdall.engine;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.InventoryChangeEvent.InventoryEventType;
import com.andune.heimdall.player.FriendTracker;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;

/**
 * Engine responsible for determining the grief point value for a given
 * action (if any).
 *
 * @author andune
 */
public class GriefPointEngine extends AbstractEngine {
    private static final String DEFAULT_CONFIG_FILE = "engine/main.yml";

    private final Heimdall plugin;
    private final PlayerStateManager playerStateManager;
    private final Debug debug;
    private final YamlConfiguration config;
    private final EngineLog log;
    private final boolean isLogging;
    private final FriendTracker friendTracker;

    public GriefPointEngine(final Heimdall plugin, final String configFile) {
        if (configFile == null)
            throw new NullPointerException("configFile is null");

        this.plugin = plugin;
        this.playerStateManager = this.plugin.getPlayerStateManager();
        this.friendTracker = this.plugin.getFriendTracker();
        this.debug = Debug.getInstance();

        this.config = loadConfig(plugin, configFile, DEFAULT_CONFIG_FILE);

        this.isLogging = this.config.getBoolean("engine.main.writeEngineLog", false);
        if (this.isLogging) {
            File logFile = new File(this.config.getString("engine.main.logfile"));
            debug.debug("Main engine opening engine logfile ", logFile);
            log = new EngineLog(plugin, logFile);
        }
        else
            log = null;
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE};
    }

    @Override
    public void processBlockChange(BlockChangeEvent event) {
        PlayerState ps = playerStateManager.getPlayerState(event.playerName);
        if (ps.isExemptFromChecks())
            return;

        if (event.bukkitEventType == Event.BukkitType.BLOCK_BREAK) {
            int typeId = event.type.getId();
            if (event.blockOwner != null
                    && !event.playerName.equals(event.blockOwner)
                    && (event.ownerTypeId == 0 || typeId == event.ownerTypeId)) {

                // ignore any broken blocks between friends
                if (!friendTracker.isFriend(event.blockOwner, event.playerName)) {
                    debug.debug("block grief penalty: owner and player don't match, owner=", event.blockOwner, ", player=", event.playerName);
                    event.griefValue = getBlockValue(typeId);
                }
                else
                    debug.debug("player ", event.blockOwner, " has claimed player ", event.playerName, " as friend: no grief penalty");
            }
//			event.griefValue = getBlockValue(typeId);	// testing;

            debug.debug("MainProcessEngine:processBlockChange event.griefValue = ", event.griefValue);
            if (isLogging && log != null)
                log.logIgnoreError("assessing grief value of " + event.griefValue + " to player " + event.playerName);
        }
        else if (event.bukkitEventType == Event.BukkitType.BLOCK_PLACE) {
            event.griefValue = -getBlockValue(event.type.getId());
            event.griefValue /= 4;        // block place is worth 1/4 the points as a grief destroy
            // TODO: move ratio to config file
        }

        if (event.griefValue != 0)
            playerStateManager.getPlayerState(event.playerName).incrementGriefPoints(event.griefValue, event.blockOwner);
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
        PlayerState ps = playerStateManager.getPlayerState(event.playerName);
        if (ps.isExemptFromChecks())
            return;

        debug.debug("MainProcessEngine:processInventoryChange event.type = ", event.type);
        if (event.type == InventoryEventType.CONTAINER_ACCESS) {
            if (event.blockOwner != null && !event.playerName.equals(event.blockOwner)) {
                if (!event.isLwcPublic) {
                    // ignore any chest access between friends
                    if (!friendTracker.isFriend(event.blockOwner, event.playerName)) {
                        debug.debug("MainProcessEngine:processInventoryChange inventory grief penalty: owner and player don't match, owner=", event.blockOwner, ", player=", event.playerName);
                        for (int i = 0; i < event.diff.length; i++) {
                            event.griefValue += getInventoryValue(event.diff[i]);
                            debug.debug("MainProcessEngine:processInventoryChange event grief value = ", event.griefValue);
                        }
                    }
                    else
                        debug.debug("player ", event.blockOwner, " has claimed player ", event.playerName, " as friend: no grief penalty");
                }
                else
                    debug.debug("MainProcessEngine:processInventoryChange block is flagged as LWC public, no grief penalty");
            }
        }

        if (event.griefValue != 0)
            playerStateManager.getPlayerState(event.playerName).incrementGriefPoints(event.griefValue, event.blockOwner);
    }

    public float getBlockValue(int id) {
        return (float) config.getDouble("blockpoints." + id, 1);
    }

    public float getInventoryValue(ItemStack is) {
        // multiply by - amount, since a negative amount is items taken, which should accumulate
        // positive grief value
        return (float) config.getDouble("inventorypoints." + is.getTypeId(), 1) * (-is.getAmount());
    }
}
