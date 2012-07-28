/**
 * 
 */
package org.morganm.heimdall.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.EventCircularBuffer;
import org.morganm.heimdall.event.EventManager;
import org.morganm.heimdall.player.PlayerTracker;
import org.morganm.heimdall.util.Debug;

/**
 * @author morganm
 *
 */
public class BukkitBlockListener implements Listener {
	private final static int ERROR_FLOOD_PREVENTION_LIMIT = 3;
	
	private final Heimdall plugin;
	private final EventManager eventManager;
	private final EventCircularBuffer<BlockChangeEvent> buffer;
	private final Debug debug;
	private final PlayerTracker tracker;
	
	public BukkitBlockListener(final Heimdall plugin, final EventManager eventManager) {
		this.plugin = plugin;
		this.eventManager = eventManager;
		this.debug = Debug.getInstance();
		this.tracker = this.plugin.getPlayerStateManager().getPlayerTracker();
		
		buffer = new EventCircularBuffer<BlockChangeEvent>(BlockChangeEvent.class, 1000, false, true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		
		Block b = event.getBlock();
		if( plugin.isDisabledWorld(b.getWorld().getName()) )
			return;
			
		BlockChangeEvent bc = getNextBlockChangeEvent();
		
		if( bc != null ) {
			bc.playerName = event.getPlayer().getName();
			bc.time = System.currentTimeMillis();
			bc.bukkitEventType = Event.BukkitType.BLOCK_BREAK;
			bc.x = b.getX();
			bc.y = b.getY();
			bc.z = b.getZ();
			bc.world = b.getWorld();
			bc.type = b.getType();
			bc.data = b.getData();
			bc.signData = null;
			
			eventManager.pushEvent(bc);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		
		Block b = event.getBlock();
		if( plugin.isDisabledWorld(b.getWorld().getName()) )
			return;

		BlockChangeEvent bc = getNextBlockChangeEvent();
		
		if( bc != null ) {
			bc.playerName = event.getPlayer().getName();
			bc.time = System.currentTimeMillis();
			bc.bukkitEventType = Event.BukkitType.BLOCK_PLACE;
			bc.x = b.getX();
			bc.y = b.getY();
			bc.z = b.getZ();
			bc.world = b.getWorld();
			bc.type = b.getType();
			bc.data = b.getData();
			
			bc.signData = null;

			eventManager.pushEvent(bc);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event) {
		if( event.isCancelled() || !tracker.isTrackedPlayer(event.getPlayer().getName()) )
			return;
		
		Block b = event.getBlock();
		if( plugin.isDisabledWorld(b.getWorld().getName()) )
			return;

		BlockChangeEvent bc = getNextBlockChangeEvent();

		if( bc != null ) {
			bc.playerName = event.getPlayer().getName();
			bc.time = System.currentTimeMillis();
			bc.bukkitEventType = Event.BukkitType.SIGN_CHANGE;
			bc.x = b.getX();
			bc.y = b.getY();
			bc.z = b.getZ();
			bc.world = b.getWorld();
			bc.type = b.getType();
			bc.data = b.getData();
			
			// should always be true, this is a SIGN_CHANGE event, after all..
			if( bc.type == Material.SIGN || bc.type == Material.SIGN_POST ) {
				debug.debug("onSignChange: sign placed");
				BlockState bs = b.getState();
				if( bs instanceof Sign ) {
					debug.debug("onSignChange: recording sign data");
					Sign sign = (Sign) bs;
					bc.signData = sign.getLines();
				}
				else
					bc.signData = null;
			}
			else
				bc.signData = null;

			eventManager.pushEvent(bc);
		}
	}
	
	private int errorFloodPreventionCount = 0;
	private BlockChangeEvent getNextBlockChangeEvent() {
		BlockChangeEvent bce = null;
		try {
			bce = buffer.getNextObject();
			errorFloodPreventionCount = 0;
		} catch (InstantiationException e) {
			errorFloodPreventionCount++;
			if( errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT )
				e.printStackTrace();
		} catch (IllegalAccessException e) {
			errorFloodPreventionCount++;
			if( errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT )
				e.printStackTrace();
		}
		
		bce.cleared = false;	// change clear flag since we are going to use this object
		return bce;
	}
}
