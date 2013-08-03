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
import org.bukkit.Material;
import org.bukkit.World;

/**
 * @author andune
 */
public class BlockChangeEvent implements Event {
    // BLOCK_PLACE, BLOCK_BREAK, etc
    public Event.BukkitType bukkitEventType;

    public String playerName;
    public long time;        // time of the event

    public World world;
    public int x;
    public int y;
    public int z;
    public transient Location location;

    public Material type;
    public byte data;

    public String[] signData;

    public String blockOwner;    // enriched data: the name of the owner of the block
    public int ownerTypeId;        // enriched data: the id of the "owned" block (which may be different
    // than the current id: a planted sapling becomes wood, etc)
    public float griefValue;    // grief value associated with this event, if any

    public transient boolean cleared = true;

    @Override
    public Location getLocation() {
        if (location == null)
            location = new Location(world, x, y, z);
        return location;
    }

    public String locationString() {
        return "{" + world.getName() + ",x=" + x + ",y=" + y + ",z=" + z + "}";
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void clear() {
        bukkitEventType = null;
        playerName = null;
        time = 0;
        world = null;
        x = 0;
        y = 0;
        z = 0;
        location = null;
        type = null;
        data = 0;
        signData = null;
        blockOwner = null;
        ownerTypeId = 0;
        griefValue = 0;

        cleared = true;
    }

    @Override
    public boolean isCleared() {
        return cleared;
    }

    @Override
    public Type getType() {
        return Event.Type.BLOCK_CHANGE;
    }

    @Override
    public String getEventTypeString() {
        switch (bukkitEventType) {
            case BLOCK_BREAK:
            case BLOCK_PLACE:
            case SIGN_CHANGE:
                return bukkitEventType.toString();
            default:
                return "Unknown Block Change";
        }
    }

    /**
     * Visitor pattern.
     */
    @Override
    public void accept(EventHandler visitor) {
        visitor.processEvent(this);
    }

    public String toString() {
        return "BlockChangeEvent:["
                + "bukkitEventType=" + bukkitEventType
                + ",playerName=" + playerName
                + ",time=" + time
                + ",world=" + world
                + ",x=" + x
                + ",y=" + y
                + ",z=" + z
                + ",location=" + location
                + ",type=" + type
                + ",data=" + data
                + ",signData=" + signData
                + ",blockOwner=" + blockOwner
                + ",ownerTypeId=" + ownerTypeId
                + ",griefValue=" + griefValue
                + "]";
    }
}
