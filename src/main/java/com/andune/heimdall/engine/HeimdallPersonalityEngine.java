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
import com.andune.heimdall.HeimdallPersonality;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;

import java.util.HashMap;

/**
 * @author andune
 */
public class HeimdallPersonalityEngine extends AbstractEngine {
    @SuppressWarnings("unused")
    private final Heimdall plugin;
    private final PlayerStateManager playerStateManager;
    private final HeimdallPersonality personality;
    private final HashMap<String, Integer> announceLevel = new HashMap<String, Integer>(10);

    public HeimdallPersonalityEngine(final Heimdall plugin) {
        this.plugin = plugin;
        this.playerStateManager = plugin.getPlayerStateManager();
        this.personality = new HeimdallPersonality(plugin);
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE};
    }

    private void processEvent(final Event event) {
        if (!personality.isEnabled())
            return;

        final String playerName = event.getPlayerName();
        final PlayerState ps = playerStateManager.getPlayerState(playerName);

        int level = 0;
        Integer intLevel = announceLevel.get(playerName);
        if (intLevel != null)
            level = intLevel;

        // keep track of the grief level we announce at, so we don't do the same
        // announcements multiple times
        if (ps.getGriefPoints() > 25 && level < 25) {
            announceLevel.put(playerName, 25);
            personality.announcePossibleGriefer(playerName);
        }
    }

    @Override
    public void processBlockChange(final BlockChangeEvent event) {
        processEvent(event);
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
        processEvent(event);
    }
}
