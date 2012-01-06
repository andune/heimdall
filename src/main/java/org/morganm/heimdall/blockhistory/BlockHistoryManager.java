/**
 * 
 */
package org.morganm.heimdall.blockhistory;

import org.bukkit.Location;

/**
 * @author morganm
 *
 */
public interface BlockHistoryManager {
	public BlockHistory getBlockHistory(Location l);
}
