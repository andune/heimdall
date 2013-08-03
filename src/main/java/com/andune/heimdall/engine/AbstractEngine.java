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
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.PlayerEvent;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Abstract class to implement do-nothing methods so that Engine implementations
 * can only listen to events they are interested in and just ignore the rest.
 *
 * @author andune
 */
public abstract class AbstractEngine implements Engine {
    protected YamlConfiguration loadConfig(final Heimdall plugin, final String configFile, final String defaultConfigFile) {
        File file = new File(configFile);
        // copy default into place if file doesn't exist
        if (!file.exists())
            plugin.getJarUtils().copyConfigFromJar(defaultConfigFile, file);

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // shouldn't ever happen according to Bukkit contract, but better to be paranoid
        if (config == null)
            throw new NullPointerException("Yaml config is null: " + configFile);

        return config;
    }

    @Override
    public void processBlockChange(BlockChangeEvent event) {
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
    }

    @Override
    public void processChatMessage(String message) {
    }

    @Override
    public void processPlayerEvent(PlayerEvent event) {
    }

    @Override
    public void processHeimdallFriendEvent(FriendEvent event) {
    }

    @Override
    public void processHeimdallFriendInvite(FriendInviteEvent event) {
    }
}
