/**
 * 
 */
package org.morganm.heimdall.engine;


/**
 * @author morganm
 *
 */
public interface ActionEngine {
	/** Return true if nothing of note happened and action processing should continue,
	 * return false otherwise.
	 * 
	 * @param playerName
	 * @param griefValue
	 * @return
	 */
	public boolean processGriefValue(String playerName, float griefValue);
}
