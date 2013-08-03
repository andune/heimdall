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
import org.bukkit.Location;

import java.util.HashMap;

/**
 * Engine that keeps track of the most recent grief event per-player.
 *
 * @author andune
 */
public class LastGriefTrackingEngine extends AbstractEngine {
    @SuppressWarnings("unused")
    private final Heimdall plugin;
    private final HashMap<String, Location> lastGriefLocation = new HashMap<String, Location>(10);
    private String lastGriefPlayerName = null;

    public LastGriefTrackingEngine(final Heimdall plugin) {
        this.plugin = plugin;
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE};
    }

    /* Return the name of the most recent person to get a grief alert.
     *
     */
    public String getLastGriefPlayerName() {
        return lastGriefPlayerName;
    }

    public Location getLastGriefLocation(final String playerName) {
        return lastGriefLocation.get(playerName);
    }

    private void processEvent(final Event event) {
        String playerName = event.getPlayerName();
        Location location = event.getLocation();
        if (playerName != null && location != null) {
            lastGriefLocation.put(playerName, location);
            lastGriefPlayerName = playerName;
        }
    }

    @Override
    public void processBlockChange(final BlockChangeEvent event) {
        processEvent(event);
    }

    @Override
    public void processInventoryChange(final InventoryChangeEvent event) {
        processEvent(event);
    }

}
