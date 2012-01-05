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
public interface ProcessEngine {
	public float calculateBlockChange(BlockChangeEvent bc);
	public float calculateInventoryChange(InventoryChangeEvent ic);
	public float calculateChatMessage(String message);
}
