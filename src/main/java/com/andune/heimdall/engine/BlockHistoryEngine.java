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
import com.andune.heimdall.blockhistory.BlockHistory;
import com.andune.heimdall.blockhistory.BlockHistoryManager;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.Event.Type;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.InventoryChangeEvent.InventoryEventType;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;

/**
 * @author andune
 */
public class BlockHistoryEngine extends AbstractEngine {
    private final Heimdall plugin;
    private final BlockHistoryManager blockHistoryManager;
    private final PlayerStateManager playerStateManager;

    public BlockHistoryEngine(final Heimdall plugin) {
        this.plugin = plugin;
        this.blockHistoryManager = this.plugin.getBlockHistoryManager();
        this.playerStateManager = this.plugin.getPlayerStateManager();
    }

    @Override
    public Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE};
    }

    @Override
    public void processBlockChange(BlockChangeEvent event) {
        PlayerState ps = playerStateManager.getPlayerState(event.playerName);
        if (ps.isExemptFromChecks())
            return;

        Debug.getInstance().debug("BlockHistoryEngine:processEvent() bc=", event);
        if (event.bukkitEventType == Event.BukkitType.BLOCK_BREAK) {
//		if( event.bukkitEventType == Type.BLOCK_BREAK ) {
            BlockHistory bh = blockHistoryManager.getBlockHistory(event.getLocation());

            if (bh != null) {
                event.blockOwner = bh.getOwner();
                event.ownerTypeId = bh.getTypeId();
            }
        }
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
        PlayerState ps = playerStateManager.getPlayerState(event.playerName);
        if (ps.isExemptFromChecks())
            return;

        if (event.type == InventoryEventType.CONTAINER_ACCESS) {
            BlockHistory bh = blockHistoryManager.getBlockHistory(event.getLocation());

            if (bh != null) {
                event.blockOwner = bh.getOwner();
            }
        }
    }

}
