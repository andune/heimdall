/**
 * 
 */
package org.morganm.heimdall;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

/** Class to keep track of the current "grief state" of players.
 * 
 * @author morganm
 *
 */
public class PlayerGriefState {
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final HashMap<String, Float> state = new HashMap<String, Float>();

	public PlayerGriefState(final JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	/** Increment the player's current grief value and return the new value.
	 * 
	 * @param player
	 * @param value
	 * @return
	 */
	public float incrementGriefState(final String player, final float value) {
		Float curValue = state.get(player);
		if( curValue == null )
			curValue = new Float(value);
		else
			curValue += value;
		state.put(player, curValue);
		
		return curValue;
	}
	
	public void save() {}
	public void load() {}
}
