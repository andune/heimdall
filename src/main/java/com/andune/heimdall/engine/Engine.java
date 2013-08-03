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

import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.PlayerEvent;

/**
 * @author andune
 */
public interface Engine {
    /* Additional interface contract; the Engine MUST implement a constructor of the form:
     *
	 *   Engine(Heimdall plugin);
	 * 
	 * This will be called to instantiate the engine, passing in the Heimdall plugin as
	 * a reference. Failure to do so will result in a failure to instantiate the engine
	 * when the plugin starts.
	 * 
	 * Alternately, if the Engine wants to take a config file as input, it can instead
	 * implement the constructor:
	 * 
	 *   Engine(Heimdall plugin, String configFile);
	 */

    /**
     * Return all Heimdall event types that this engine wants to process.
     */
    public Event.Type[] getRegisteredEventTypes();

    public void processBlockChange(BlockChangeEvent event);

    public void processInventoryChange(InventoryChangeEvent event);

    public void processChatMessage(String message);

    public void processPlayerEvent(PlayerEvent event);

    public void processHeimdallFriendEvent(FriendEvent event);

    public void processHeimdallFriendInvite(FriendInviteEvent event);
}
