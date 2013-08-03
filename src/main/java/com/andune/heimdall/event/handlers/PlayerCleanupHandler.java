/**
 * 
 */
package com.andune.heimdall.event.handlers;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.PlayerEvent;
import com.andune.heimdall.event.PlayerEvent.Type;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;


/**
 * @author andune
 *
 */
public class PlayerCleanupHandler extends EventHandler {
	private final Heimdall plugin;
	private final PlayerStateManager playerStateManager;
	
	public PlayerCleanupHandler(final Heimdall plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;
	}

	@Override
	public Event.Type[] getRegisteredEventTypes() {
		return new Event.Type[] { Event.Type.PLAYER_EVENT };
	}

	@Override
	public void processEvent(PlayerEvent event) {
		Debug.getInstance().debug("PlayerCleanupHandler::processEvent event=",event);
		if( event.eventType == Type.PLAYER_QUIT ) {
			PlayerState ps = playerStateManager.getPlayerState(event.playerName);
			if( ps != null ) {
				Debug.getInstance().debug("PlayerCleanupHandler::processEvent saving playerState object for ",event.playerName);
				try {
					ps.close();
				}
				catch(Exception e) {
					plugin.getLogger().warning(plugin.getLogPrefix()+"Error saving player state for player "+event.playerName);
					e.printStackTrace();
				}
			}
		}
	}
}
