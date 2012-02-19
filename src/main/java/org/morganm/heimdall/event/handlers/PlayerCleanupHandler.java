/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.PlayerEvent;
import org.morganm.heimdall.event.PlayerEvent.Type;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.util.Debug;


/**
 * @author morganm
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
