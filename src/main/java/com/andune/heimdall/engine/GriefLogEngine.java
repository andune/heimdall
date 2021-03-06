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
import com.andune.heimdall.event.PlayerEvent;
import com.andune.heimdall.log.GriefEntry;
import com.andune.heimdall.log.GriefEntry.Type;
import com.andune.heimdall.log.GriefLog;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Engine that logs grief events per-player.
 *
 * @author andune
 */
public class GriefLogEngine extends AbstractEngine {
    private final Debug debug;
    private final Heimdall plugin;
    private final PlayerStateManager playerStateManager;
    private final Logger logger;
    private final String logPrefix;

    public GriefLogEngine(final Heimdall plugin) {
        this.plugin = plugin;
        this.playerStateManager = plugin.getPlayerStateManager();
        this.logger = this.plugin.getLogger();
        this.logPrefix = this.plugin.getLogPrefix();
        this.debug = Debug.getInstance();
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE, Event.Type.PLAYER_EVENT};
    }

    /* (non-Javadoc)
     * @see com.andune.heimdall.engine.Engine#processBlockChange(com.andune.heimdall.event.BlockChangeEvent)
     */
    @Override
    public void processBlockChange(BlockChangeEvent event) {
        Debug.getInstance().debug("griefLogEngine: processing event: ", event);
        // do nothing if no grief value or blockOwner
        if (event.griefValue == 0 || event.blockOwner == null)
            return;

        String additionalData = "blockType: " + event.type.toString();

        logEvent(event, Type.BLOCK_BREAK_NOT_OWNER, event.griefValue, event.blockOwner, additionalData);
    }

    /* (non-Javadoc)
     * @see com.andune.heimdall.engine.Engine#processInventoryChange(com.andune.heimdall.event.InventoryChangeEvent)
     */
    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
        Debug.getInstance().debug("griefLogEngine: processing event: ", event);
        // do nothing if no grief value or blockOwner
        if (event.griefValue == 0 || event.blockOwner == null)
            return;

        StringBuilder sb = new StringBuilder(40);
        sb.append("Items: ");
        int baseLength = sb.length();
        for (int i = 0; i < event.diff.length; i++) {
            if (sb.length() > baseLength)
                sb.append(",");
            sb.append(event.diff[i].getType());
            sb.append(":");
            sb.append(event.diff[i].getAmount());
        }

        logEvent(event, Type.CHEST_ACCESS_NOT_OWNER, event.griefValue, event.blockOwner, sb.toString());
    }

    @Override
    public void processPlayerEvent(PlayerEvent event) {
        if (!playerStateManager.getPlayerTracker().isTrackedPlayer(event.playerName))
            return;

        debug.debug("GriefLogEngine::processPlayerEvent event=", event);
        if (event.eventType == PlayerEvent.Type.NEW_PLAYER_JOIN) {
            logEvent(event, Type.NEW_PLAYER, 0, null, null);
        }
        else if (event.eventType == PlayerEvent.Type.PLAYER_BANNED) {
            String banCommand = null;
            String banSender = null;
            if (event.extraData.length == 2) {
                banCommand = event.extraData[0];
                banSender = event.extraData[1];
            }

            logEvent(event, Type.BANNED_PLAYER, 0, null,
                    "BanSender: " + banSender + ", BanCommand: " + banCommand);
        }
        else if (event.eventType == PlayerEvent.Type.PLAYER_UNBANNED) {
            String unbanCommand = null;
            String unbanSender = null;
            if (event.extraData.length == 2) {
                unbanCommand = event.extraData[0];
                unbanSender = event.extraData[1];
            }
            logEvent(event, Type.UNBANNED_PLAYER, 0, null,
                    "UnBanSender: " + unbanSender + ", UnBanCommand: " + unbanCommand);
        }
        else if (event.eventType == PlayerEvent.Type.PLAYER_KICK) {
            logEvent(event, Type.PLAYER_KICKED, 0, null, null);
        }
    }

    private void logEvent(Event event, Type type, float griefValue, String blockOwner, String additionalData) {
        PlayerState ps = playerStateManager.getPlayerState(event.getPlayerName());
        GriefLog log = ps.getGriefLog();
        if (log != null) {
            Debug.getInstance().debug("GriefLogEngine:logEvent:", event);
            GriefEntry entry = new GriefEntry(type, event.getTime(), event.getPlayerName(),
                    griefValue, ps.getGriefPoints(), event.getLocation(), blockOwner, additionalData);
            try {
                log.writeEntry(entry);
            } catch (IOException e) {
                logger.warning(logPrefix + "error writing to player grief log: " + e.getMessage());
            }
        }

    }
}
