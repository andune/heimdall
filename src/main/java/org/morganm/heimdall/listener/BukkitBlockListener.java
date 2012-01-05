/**
 * 
 */
package org.morganm.heimdall.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.EventCircularBuffer;
import org.morganm.heimdall.event.EventManager;
import org.morganm.util.Debug;

/**
 * @author morganm
 *
 */
public class BukkitBlockListener extends BlockListener {
	private final static int ERROR_FLOOD_PREVENTION_LIMIT = 3;
	
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final EventManager eventManager;
	private final EventCircularBuffer<BlockChangeEvent> buffer;
	private final Debug debug;
	private int errorFloodPreventionCount = 0;
	
	public BukkitBlockListener(final JavaPlugin plugin, final EventManager eventManager) {
		this.plugin = plugin;
		this.eventManager = eventManager;
		this.debug = Debug.getInstance();
		
		buffer = new EventCircularBuffer<BlockChangeEvent>(BlockChangeEvent.class, 1000, false);
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if( event.isCancelled() )
			return;
		
		Block b = event.getBlock();
		BlockChangeEvent bc = getNextBlockChangeEvent();
		
		if( bc != null ) {
			bc.playerName = event.getPlayer().getName();
			bc.time = System.currentTimeMillis();
		    bc.bukkitEventType = event.getType();
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
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if( event.isCancelled() )
			return;
		
		Block b = event.getBlock();
		BlockChangeEvent bc = getNextBlockChangeEvent();
		
		if( bc != null ) {
			bc.playerName = event.getPlayer().getName();
			bc.time = System.currentTimeMillis();
		    bc.bukkitEventType = event.getType();
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
	
	@Override
	public void onSignChange(SignChangeEvent event) {
		if( event.isCancelled() )
			return;
		
		Block b = event.getBlock();
		BlockChangeEvent bc = getNextBlockChangeEvent();

		if( bc != null ) {
			bc.playerName = event.getPlayer().getName();
			bc.time = System.currentTimeMillis();
		    bc.bukkitEventType = event.getType();
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
		
		return bce;
	}
}
