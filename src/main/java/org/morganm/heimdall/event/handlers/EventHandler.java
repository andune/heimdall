/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;


/**
 * @author morganm
 *
 */
public abstract class EventHandler {
	public void processEvent(BlockChangeEvent bce) {}
	
	public void processEvent(InventoryChangeEvent ice) {}
}
