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
package com.andune.heimdall.blockhistory;

import com.andune.heimdall.Heimdall;
import org.bukkit.plugin.Plugin;

/**
 * @author andune
 */
public class BlockHistoryFactory {
    private static final BlockHistoryCache cache = new BlockHistoryCache();

    public static BlockHistoryManager getBlockHistoryManager(final Heimdall plugin) {
        Plugin p = plugin.getServer().getPluginManager().getPlugin("LogBlock");
        if (p != null)
            return new BlockHistoryLogBlock(plugin, cache);

        p = plugin.getServer().getPluginManager().getPlugin("Prism");
        if (p != null)
            return new BlockHistoryPrism(plugin, cache);

        if (plugin.getConfig().getBoolean("blockHistoryTest", false))
            return new BlockHistoryTest(plugin, cache);

        return null;
    }

    public static BlockHistoryCache getBlockHistoryCache() {
        return cache;
    }
}
