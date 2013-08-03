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
