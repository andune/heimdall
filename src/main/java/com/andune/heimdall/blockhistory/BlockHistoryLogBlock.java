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
import com.andune.heimdall.util.Debug;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

/**
 * @author andune
 */
public class BlockHistoryLogBlock implements BlockHistoryManager {
    private final static HashSet<String> ignoredOwners = new HashSet<String>(15);
    private final Heimdall plugin;
    private final Debug debug;
    private final BlockHistoryCache bhCache;
    private LogBlock logBlock;

    static {
        ignoredOwners.add("WaterFlow");
        ignoredOwners.add("LavaFlow");
        ignoredOwners.add("TNT");
        ignoredOwners.add("Creeper");
        ignoredOwners.add("Fire");
        ignoredOwners.add("Ghast");
        ignoredOwners.add("Environment");
        ignoredOwners.add("Enderman");
        ignoredOwners.add("LeavesDecay");
    }

    public BlockHistoryLogBlock(final Heimdall plugin, final BlockHistoryCache bhCache) {
        this.plugin = plugin;
        this.debug = Debug.getInstance();
        this.bhCache = bhCache;

        Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LogBlock");
        if (p instanceof LogBlock)
            this.logBlock = (LogBlock) p;
        else
            this.logBlock = null;
    }

    @Override
    public void pluginLoaded(final Plugin p) {
        if (p instanceof LogBlock)
            this.logBlock = (LogBlock) p;
    }

    @Override
    public void pluginUnloaded(final Plugin p) {
        if (p instanceof LogBlock)
            this.logBlock = null;
    }

    @Override
    public BlockHistory getBlockHistory(final Location l) {
        if (l == null)
            return null;

        // check the cache to see if we already have the history for this location
        BlockHistory bh = bhCache.getCacheObject(l);
        if (bh != null)
            return bh;

        // don't run a lookup if this world is disabled
        if (plugin.isDisabledWorld(l.getWorld().getName()))
            return null;

        // if it's a broken block and we have logBlock, lookup the owner
        if (logBlock != null) {
            debug.debug("running logBlock query");
            QueryParams params = new QueryParams(logBlock);
            params.bct = BlockChangeType.CREATED;
//			params.since = 43200;		// 30 days
            params.since = 107373;        // roughly 3 months
            params.loc = l;
            params.world = l.getWorld();
            params.silent = true;
//			params.needDate = true;
            params.needType = true;
            params.needPlayer = true;
            params.radius = 0;
            // order descending and limit 1, we just want the most recent blockChange
            params.limit = 1;
            params.order = QueryParams.Order.DESC;
            try {
                if (debug.isDevDebug()) {
                    debug.devDebug("logBlock query = ", params.getQuery());
                }
                for (de.diddiz.LogBlock.BlockChange lbChange : logBlock.getBlockChanges(params)) {
                    // skip ignored owners
                    if (ignoredOwners.contains(lbChange.playerName))
                        continue;

                    bh = new BlockHistory(lbChange.playerName, lbChange.type, l);
                    debug.debug("got logBlock result, lbOwner=", lbChange.playerName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // store it in the cache
        if (bh != null)
            bhCache.storeCacheObject(bh);

        return bh;
    }

}
