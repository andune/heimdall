/**
 * 
 */
package org.morganm.heimdall.event;


/**
 * @author morganm
 *
 */
public abstract class EventHandler {
	public void processEvent(BlockChangeEvent bce) {}
	
	public void processEvent(InventoryChangeEvent ice) {}
}
