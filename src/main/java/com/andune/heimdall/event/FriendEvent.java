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
package com.andune.heimdall.event;

import com.andune.heimdall.event.handlers.EventHandler;
import org.bukkit.Location;

/**
 * Event used when a friend relationship has been created from a player
 * to a new friend.
 *
 * @author andune
 */
public class FriendEvent implements Event {

    private String player;
    private String friend;
    private long time;

    public FriendEvent(String player, String friend) {
        this.player = player;
        this.friend = friend;
        this.time = System.currentTimeMillis();
    }

    public String getFriend() {
        return friend;
    }

    @Override
    public Type getType() {
        return Type.HEIMDALL_FRIEND_EVENT;
    }

    @Override
    public String getEventTypeString() {
        return Type.HEIMDALL_FRIEND_EVENT.toString();
    }

    @Override
    public String getPlayerName() {
        return player;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void accept(EventHandler visitor) {
        visitor.processEvent(this);
    }

    // we don't re-use these events, so do nothing on clear
    @Override
    public void clear() {
    }

    @Override
    public boolean isCleared() {
        return true;
    }
}
