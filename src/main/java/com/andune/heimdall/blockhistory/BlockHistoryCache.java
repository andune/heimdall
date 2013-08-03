/**
 *
 */
package com.andune.heimdall.blockhistory;

import org.bukkit.Location;

import java.util.HashMap;

/**
 * We keep a cache of lookups that we do, primarily so that if a player opens a chest,
 * when we get the event that they're finished, we can just grab the cached BlockHistory
 * rather than having to go back to the database again for it.
 * <p/>
 * It stores BlockHistory objects in a CircularBuffer that just wraps indefinitely (it
 * is never popped) and it stores locations as an index in the buffer, cleaning up
 * indexes as buffer indexes are re-used for new objects.
 *
 * @author andune
 */
public class BlockHistoryCache {
    private static final int BUFFER_MAX = 1000;

    private final BlockHistory[] blockHistoryCircularBuffer = new BlockHistory[BUFFER_MAX];

    /* This map maintains a location->index mapping for the circular buffer, so we can
     * quickly find an object in the buffer by its location.
     *
     */
    private final HashMap<Location, Integer> bufferIndexMap = new HashMap<Location, Integer>(BUFFER_MAX);
    private int bufferEnd = 0;

    /**
     * Return the cache object for the given location, or null if none found.
     *
     * @param l
     * @return
     */
    public BlockHistory getCacheObject(final Location l) {
        Integer index = bufferIndexMap.get(l);
        if (index != null)
            return blockHistoryCircularBuffer[index];
        else
            return null;
    }

    public void storeCacheObject(final BlockHistory bh) {
        if (bh != null && bh.getLocation() != null) {
            bufferEnd++;
            if (bufferEnd >= BUFFER_MAX)
                bufferEnd = 0;

            // clean up old blockHistory from the bufferIndexMap
            BlockHistory oldBlockHistory = blockHistoryCircularBuffer[bufferEnd];
            if (oldBlockHistory != null)
                bufferIndexMap.remove(oldBlockHistory.getLocation());

            blockHistoryCircularBuffer[bufferEnd] = bh;
            bufferIndexMap.put(bh.getLocation(), bufferEnd);
        }
    }

    public void clearCacheLocation(final Location l) {
        Integer index = bufferIndexMap.remove(l);
        if (index != null)
            blockHistoryCircularBuffer[index] = null;
    }
}
