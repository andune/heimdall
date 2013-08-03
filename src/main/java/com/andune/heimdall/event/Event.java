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
 * General event interface that all Heimdall Event types will implement.
 *
 * @author andune
 */
public interface Event {
    public enum Type {
        BLOCK_CHANGE,
        INVENTORY_CHANGE,
        CHAT_MESSAGE,
        PLAYER_EVENT,
        HEIMDALL_FRIEND_EVENT,
        HEIMDALL_FRIEND_INVITE_SENT,
    }

    /* Bukkit has deprecated their event types. While the new event system is
     * much better, event types are still useful for a plugin like Heimdall
     * where event data is stored and processed asynchronously. Heimdall's
     * event types were designed to be less granular, so this enum is used
     * to track the original Bukkit event type for places where that might
     * be useful (such as logging).
     */
    public enum BukkitType {
        BLOCK_PLACE,
        BLOCK_BREAK,
        SIGN_CHANGE
    }

    /**
     * Clear the event object of any data.
     */
    public void clear();

    public boolean isCleared();

    public Type getType();

    /**
     * Not specifically related to getType(): this method should return a human-readable
     * String that explains the event type. For example this might be "BLOCK_PLACE" or
     * "BLOCK_DESTROY" or "ITEM_CRAFTED", etc.
     *
     * @return
     */
    public String getEventTypeString();

    /**
     * Return the player whose actions caused this event to be generated.
     *
     * @return
     */
    public String getPlayerName();

    /**
     * Return the time that the event took place.
     *
     * @return
     */
    public long getTime();

    /**
     * For events that have a location.
     *
     * @return the location of the event, or null if no location
     */
    public Location getLocation();

    /**
     * Visitor pattern.
     */
    public void accept(EventHandler visitor);
}
