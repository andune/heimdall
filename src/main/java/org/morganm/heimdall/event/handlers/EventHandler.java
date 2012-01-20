/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.PlayerEvent;


/**
 * @author morganm
 *
 */
public abstract class EventHandler {
	public void processEvent(BlockChangeEvent event) {}
	
	public void processEvent(InventoryChangeEvent event) {}
	
	public void processEvent(PlayerEvent event) {}
}
