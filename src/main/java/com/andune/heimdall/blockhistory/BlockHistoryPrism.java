/**
 * 
 */
package com.andune.heimdall.blockhistory;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.actions.Handler;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.util.Debug;

import java.util.List;

/**
 * Block history implementation to find block history using Prism
 * block logging plugin.
 * 
 * @author andune
 *
 */
public class BlockHistoryPrism implements BlockHistoryManager {
    private final Heimdall plugin;
    private final Debug debug;
    private final BlockHistoryCache bhCache;
    private Prism prism;

    public BlockHistoryPrism(final Heimdall plugin, final BlockHistoryCache bhCache) {
        this.plugin = plugin;
        this.debug = Debug.getInstance();
        this.bhCache = bhCache;
        
        Plugin p = this.plugin.getServer().getPluginManager().getPlugin("Prism");
        if( p instanceof Prism )
            this.prism = (Prism) p;
        else
            this.prism = null;
    }

    @Override
    public void pluginLoaded(final Plugin p) {
        if( p instanceof Prism )
            this.prism = (Prism) p;
    }
    
    @Override
    public void pluginUnloaded(final Plugin p) {
        if( p instanceof Prism )
            this.prism = null;
    }

    @Override
    public BlockHistory getBlockHistory(final Location l) {
        if( l == null )
            return null;
        
        // check the cache to see if we already have the history for this location
        BlockHistory bh = bhCache.getCacheObject(l);
        if( bh != null )
            return bh;
        
        // don't run a lookup if this world is disabled
        if( plugin.isDisabledWorld(l.getWorld().getName()) )
            return null;

        // if it's a broken block and we have prism, lookup the owner
        if( prism != null ) {
            QueryParameters parameters = new QueryParameters();
            parameters.setWorld(l.getWorld().getName());
            parameters.addActionType("block-place");
            parameters.setMinMaxVectorsFromPlayerLocation(l);
            // default sort order is most recent action first, so we only
            // need one row
            parameters.setLimit(1);
            
            ActionsQuery aq = new ActionsQuery(prism);
            QueryResult lookupResult = aq.lookup( parameters );
            List<Handler> results = lookupResult.getActionResults();
            if( results != null && !results.isEmpty() ) {
                for(Handler h : results) {
                    final String name = h.getPlayerName();
                    bh = new BlockHistory(name, h.getBlockId(), l);
                    debug.debug("got prism result, owner=", name);
                }
            }
        }
        
        // store it in the cache
        if( bh != null )
            bhCache.storeCacheObject(bh);
        
        return bh;
    }
    
}
