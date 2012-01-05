/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.PlayerGriefState;
import org.morganm.heimdall.engine.ActionEngine;
import org.morganm.heimdall.engine.ProcessEngine;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.EventHandler;
import org.morganm.util.Debug;

/**
 * @author morganm
 *
 */
public class HeimdallEventHandler extends EventHandler {
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	@SuppressWarnings("unused")
	private final Debug debug;
	private final ProcessEngine processEngine;
	private final ActionEngine actionEngine;
	private final PlayerGriefState playerState;
	
	public HeimdallEventHandler(final JavaPlugin plugin, final ProcessEngine engine,
			final ActionEngine actionEngine, final PlayerGriefState playerState) {
		this.plugin = plugin;
		this.processEngine = engine;
		this.actionEngine = actionEngine;
		this.playerState = playerState;
		this.debug = Debug.getInstance();
	}
	
	@Override
	public void processEvent(BlockChangeEvent bce) {
		float griefValue = processEngine.calculateBlockChange(bce);
		if( griefValue != 0 ) {
			float totalGriefValue = playerState.incrementGriefState(bce.playerName, griefValue);
			actionEngine.processGriefValue(bce.playerName, totalGriefValue);
		}
	}
}
