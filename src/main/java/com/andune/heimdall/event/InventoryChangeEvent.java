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
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * @author andune
 */
public class InventoryChangeEvent implements Event {
    public enum InventoryEventType {
        UNDEFINED,
        CONTAINER_ACCESS,
        CRAFTED
    }

    public String playerName;
    public long time;        // time of the event

    public World world;
    public int x;
    public int y;
    public int z;
    public transient Location location;
    public ItemStack[] diff;

    public InventoryEventType type;

    public boolean isLwcPublic = false;
    public String blockOwner;    // enriched data: the name of the owner of the block
    public float griefValue;    // grief value associated with this event, if any

    public transient boolean cleared = true;

    @Override
    public Location getLocation() {
        if (location == null)
            location = new Location(world, x, y, z);
        return location;
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
        playerName = null;
        time = 0;
        world = null;
        x = 0;
        y = 0;
        z = 0;
        location = null;
        diff = null;
        type = InventoryEventType.UNDEFINED;
        isLwcPublic = false;
        blockOwner = null;
        griefValue = 0;

        cleared = true;
    }

    @Override
    public boolean isCleared() {
        return cleared;
    }

    @Override
    public Type getType() {
        return Type.INVENTORY_CHANGE;
    }

    @Override
    public String getEventTypeString() {
        switch (type) {
            case UNDEFINED:
                return "Unknown Inventory Change";
            default:
                return type.toString();
        }
    }

    /**
     * Visitor pattern.
     */
    @Override
    public void accept(EventHandler visitor) {
        visitor.processEvent(this);
    }

}
