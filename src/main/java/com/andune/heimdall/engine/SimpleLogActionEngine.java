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
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Simple engine to log griefer actions.
 *
 * @author andune
 */
public class SimpleLogActionEngine extends AbstractEngine {
    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
//	private static long TIME_BETWEEN_FLUSH = 5000;	// 5 seconds

    private final Heimdall plugin;
    private EngineLog log;
    private final PlayerStateManager playerStateManager;

    public SimpleLogActionEngine(final Heimdall plugin) {
        this.plugin = plugin;
        this.playerStateManager = this.plugin.getPlayerStateManager();

        // TODO: drive file location from config file
        this.log = new EngineLog(plugin, new File("plugins/Heimdall/logs/simpleLogActionEngine.log"));
        try {
            log.init();
        } catch (IOException e) {
            log = null;
            e.printStackTrace();
        }
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE,
                Event.Type.HEIMDALL_FRIEND_EVENT, Event.Type.HEIMDALL_FRIEND_INVITE_SENT};
    }

    @Override
    public void processBlockChange(BlockChangeEvent event) {
        logEvent(event, event.griefValue);
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
        logEvent(event, event.griefValue);
    }

    @Override
    public void processHeimdallFriendEvent(FriendEvent event) {
        if (log != null) {
            try {
                StringBuilder sb = new StringBuilder(160);
                sb.append("[");
                sb.append(dateFormat.format(new Date()));
                sb.append("] ");
                sb.append("Player ");
                sb.append(event.getPlayerName());
                sb.append(" friended player ");
                sb.append(event.getFriend());
                log.log(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processHeimdallFriendInvite(FriendInviteEvent event) {
        if (log != null) {
            try {
                StringBuilder sb = new StringBuilder(160);
                sb.append("[");
                sb.append(dateFormat.format(new Date()));
                sb.append("] ");
                sb.append("Player ");
                sb.append(event.getPlayerName());
                sb.append(" sent automated friend invite for player ");
                sb.append(event.getInvitedFriend());
                log.log(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logEvent(final Event event, final float griefValue) {
//		Debug.getInstance().debug("SimpleLogActionEngine:processGriefValue(): playerName=",event.getPlayerName(),", griefvalue=",griefValue);
        if (griefValue < 0)
            return;

        PlayerState ps = playerStateManager.getPlayerState(event.getPlayerName());
        if (ps.isExemptFromChecks())
            return;

        if (log != null) {
            try {
                StringBuilder sb = new StringBuilder(160);
                sb.append("[");
                sb.append(dateFormat.format(new Date()));
                sb.append("] ");
                sb.append(event.getPlayerName());
                sb.append(" event grief points ");
                sb.append(griefValue);
                sb.append(", total grief now is ");
                sb.append(ps.getGriefPoints());
                log.log(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
