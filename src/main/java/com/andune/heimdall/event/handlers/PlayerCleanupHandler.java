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
package com.andune.heimdall.event.handlers;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.PlayerEvent;
import com.andune.heimdall.event.PlayerEvent.Type;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;


/**
 * @author andune
 */
public class PlayerCleanupHandler extends EventHandler {
    private final Heimdall plugin;
    private final PlayerStateManager playerStateManager;

    public PlayerCleanupHandler(final Heimdall plugin, final PlayerStateManager playerStateManager) {
        this.plugin = plugin;
        this.playerStateManager = playerStateManager;
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.PLAYER_EVENT};
    }

    @Override
    public void processEvent(PlayerEvent event) {
        Debug.getInstance().debug("PlayerCleanupHandler::processEvent event=", event);
        if (event.eventType == Type.PLAYER_QUIT) {
            PlayerState ps = playerStateManager.getPlayerState(event.playerName);
            if (ps != null) {
                Debug.getInstance().debug("PlayerCleanupHandler::processEvent saving playerState object for ", event.playerName);
                try {
                    ps.close();
                } catch (Exception e) {
                    plugin.getLogger().warning(plugin.getLogPrefix() + "Error saving player state for player " + event.playerName);
                    e.printStackTrace();
                }
            }
        }
    }
}
