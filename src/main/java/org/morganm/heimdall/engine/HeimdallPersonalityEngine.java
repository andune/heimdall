/**
 * 
 */
package org.morganm.heimdall.engine;

import java.util.HashMap;

import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.HeimdallPersonality;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.PlayerEvent;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;

/**
 * @author morganm
 *
 */
public class HeimdallPersonalityEngine extends AbstractEngine {
	@SuppressWarnings("unused")
	private final Heimdall plugin;
	final PlayerStateManager playerStateManager;
	final HeimdallPersonality personality;
	final HashMap<String, Integer> announceLevel = new HashMap<String, Integer>(10);
	
	public HeimdallPersonalityEngine(final Heimdall plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;
		this.personality = new HeimdallPersonality(plugin);
	}
	
	private void processEvent(final Event event) {
		final String playerName = event.getPlayerName();
		final PlayerState ps = playerStateManager.getPlayerState(playerName);
		
		int level = 0;
		Integer intLevel = announceLevel.get(playerName);
		if( intLevel != null )
			level = intLevel;
		
		// keep track of the grief level we announce at, so we don't do the same
		// announcements multiple times
		if( ps.getGriefPoints() > 25 && level < 25 ) {
			announceLevel.put(playerName, 25);
			personality.announcePossibleGriefer(playerName);
		}
	}
	
	@Override
	public void processBlockChange(final BlockChangeEvent event) {
		processEvent(event);
	}
	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		processEvent(event);
	}
	@Override
	public void processPlayerEvent(PlayerEvent event) {
		processEvent(event);
	}
}
