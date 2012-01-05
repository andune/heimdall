/**
 * 
 */
package org.morganm.heimdall.event.enrichers;

import org.bukkit.Location;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.EventHandler;
import org.morganm.util.Debug;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;

/** Class which uses LogBlock to enrich BlockChangeEvents with block owner information.
 * 
 * @author morganm
 *
 */
public class LogBlockEnricher extends EventHandler {
	private final JavaPlugin plugin;
	private final Debug debug;
	private final LogBlock logBlock;
	
	public LogBlockEnricher(final JavaPlugin plugin) {
		this.plugin = plugin;
		this.debug = Debug.getInstance();
		
		Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p instanceof LogBlock )
			this.logBlock = (LogBlock) p;
		else
			this.logBlock = null;
}
	
	@Override
	public void processEvent(BlockChangeEvent bc) {
		if( bc.bukkitEventType == Type.BLOCK_BREAK ) {
			String lbOwner = "(none)";

			// if it's a broken block and we have logBlock, lookup the owner
			if( logBlock != null ) {
				debug.debug("running logBlock query");
				QueryParams params = new QueryParams(logBlock);
				params.bct = BlockChangeType.CREATED;
				//				params.since = 43200;		// 30 days
				params.since = 107373;		// roughly 3 months
				params.loc = new Location(bc.world, bc.x, bc.y, bc.z);
				params.world = bc.world;
				params.silent = true;
				//				params.needDate = true;
				params.needType = true;
				params.needPlayer = true;
				params.radius = 0;
				// order descending and limit 1, we just want the most recent blockChange
				params.limit = 1;
				params.order = QueryParams.Order.DESC;
				try {
					if( debug.isDevDebug() ) {
						debug.devDebug("logBlock query = ",params.getQuery());
					}
					for (de.diddiz.LogBlock.BlockChange lbChange : logBlock.getBlockChanges(params)) {
						bc.blockOwner = lbChange.playerName;
						bc.ownerTypeId = lbChange.type;

						debug.debug("got logBlock result, lbOwner=",lbOwner);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
