/**
 * 
 */
package org.morganm.heimdall;

/**
 * @author morganm
 *
 */
public interface Engine {
	public int calculateBlockChange(BlockChange bc);
	public int calculateInventoryChange(InventoryChange ic);
	public int calculateChatMessage(String message);
}
