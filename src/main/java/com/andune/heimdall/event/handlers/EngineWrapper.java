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

import com.andune.heimdall.engine.Engine;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.PlayerEvent;

/**
 * Enricher which wraps an Engine and passes it events.
 *
 * @author andune
 */
public class EngineWrapper extends EventHandler {
    private final Engine engine;

    public EngineWrapper(final Engine engine) {
        this.engine = engine;
    }

    public Event.Type[] getRegisteredEventTypes() {
        return engine.getRegisteredEventTypes();
    }

    @Override
    public void processEvent(BlockChangeEvent bc) {
        engine.processBlockChange(bc);
    }

    @Override
    public void processEvent(InventoryChangeEvent ice) {
        engine.processInventoryChange(ice);
    }

    @Override
    public void processEvent(PlayerEvent pe) {
        engine.processPlayerEvent(pe);
    }

    @Override
    public void processEvent(FriendEvent event) {
        engine.processHeimdallFriendEvent(event);
    }

    @Override
    public void processEvent(FriendInviteEvent event) {
        engine.processHeimdallFriendInvite(event);
    }
}
