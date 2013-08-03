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

import com.andune.heimdall.util.Debug;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Test class, to test griefers by returning another owner for all blocks, also
 * can be used to test the block history cache.
 *
 * @author andune
 */
public class BlockHistoryTest implements BlockHistoryManager {
    @SuppressWarnings("unused")
    private final Plugin plugin;
    private final BlockHistoryCache bhCache;

    public BlockHistoryTest(final Plugin plugin, final BlockHistoryCache bhCache) {
        this.plugin = plugin;
        this.bhCache = bhCache;
        Debug.getInstance().debug("using BlockHistoryTest block history manager");
    }

    @Override
    public BlockHistory getBlockHistory(Location l) {
        Debug.getInstance().debug("BlockHistoryTest:getBlockHistory() called l=", l);
        if (l == null)
            return null;

        // check the cache to see if we already have the history for this location
        BlockHistory bh = bhCache.getCacheObject(l);
        if (bh == null) {
            bh = new BlockHistory("testOwner", 0, l);        // type of "0"
            // store it in the cache
            bhCache.storeCacheObject(bh);
        }

        Debug.getInstance().debug("BlockHistoryTest:getBlockHistory() l=", l, ", bh =", bh);

        return bh;
    }

    @Override
    public void pluginLoaded(Plugin plugin) {
    }

    @Override
    public void pluginUnloaded(Plugin plugin) {
    }
}
