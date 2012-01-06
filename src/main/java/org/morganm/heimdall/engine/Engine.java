/**
 * 
 */
package org.morganm.heimdall.engine;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;

/**
 * @author morganm
 *
 */
public interface Engine {
	public void processBlockChange(BlockChangeEvent event);
	public void processInventoryChange(InventoryChangeEvent event);
	public void processChatMessage(String message);
}
