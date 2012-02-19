/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.IOException;
import java.util.logging.Logger;

import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.PlayerEvent;
import org.morganm.heimdall.log.GriefEntry;
import org.morganm.heimdall.log.GriefEntry.Type;
import org.morganm.heimdall.log.GriefLog;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.util.Debug;

/**
 * @author morganm
 *
 */
public class GriefLogEngine extends AbstractEngine {
	private final Debug debug;
	private final Heimdall plugin;
	private final PlayerStateManager playerStateManager;
	private final Logger logger;
	private final String logPrefix;
	
	public GriefLogEngine(final Heimdall plugin) {
		this.plugin = plugin;
		this.playerStateManager = plugin.getPlayerStateManager();
		this.logger = this.plugin.getLogger();
		this.logPrefix = this.plugin.getLogPrefix();
		this.debug = Debug.getInstance();
	}
	
	@Override
	public Event.Type[] getRegisteredEventTypes() {
		return new Event.Type[] { Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE, Event.Type.PLAYER_EVENT };
	}
	
	/* (non-Javadoc)
	 * @see org.morganm.heimdall.engine.Engine#processBlockChange(org.morganm.heimdall.event.BlockChangeEvent)
	 */
	@Override
	public void processBlockChange(BlockChangeEvent event) {
		Debug.getInstance().debug("griefLogEngine: processing event: ",event);
		// do nothing if no grief value or blockOwner
		if( event.griefValue == 0 || event.blockOwner == null )
			return;
		
		String additionalData = "blockType: "+event.type.toString();
		
		logEvent(event, Type.BLOCK_BREAK_NOT_OWNER, event.griefValue, event.blockOwner, additionalData);
	}

	/* (non-Javadoc)
	 * @see org.morganm.heimdall.engine.Engine#processInventoryChange(org.morganm.heimdall.event.InventoryChangeEvent)
	 */
	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		Debug.getInstance().debug("griefLogEngine: processing event: ",event);
		// do nothing if no grief value or blockOwner
		if( event.griefValue == 0 || event.blockOwner == null )
			return;
		
		StringBuilder sb = new StringBuilder(40);
		sb.append("Items: ");
		int baseLength = sb.length();
		for(int i=0; i < event.diff.length; i++) {
			if( sb.length() > baseLength )
				sb.append(",");
			sb.append(event.diff[i].getType());
			sb.append(":");
			sb.append(event.diff[i].getAmount());
		}
		
		logEvent(event, Type.CHEST_ACCESS_NOT_OWNER, event.griefValue, event.blockOwner, sb.toString());
	}
	
	@Override
	public void processPlayerEvent(PlayerEvent event) {
		debug.debug("GriefLogEngine::processPlayerEvent event=",event);
		if( event.eventType == PlayerEvent.Type.NEW_PLAYER_JOIN ) {
			logEvent(event, Type.NEW_PLAYER, 0, null, null);
		}
		else if( event.eventType == PlayerEvent.Type.PLAYER_BANNED ) {
			String banCommand = null;
			String banSender = null;
			if( event.extraData.length == 2 ) {
				banCommand = event.extraData[0];
				banSender = event.extraData[1];
			}

			logEvent(event, Type.BANNED_PLAYER, 0, null,
					"BanSender: "+banSender+", BanCommand: "+banCommand);
		}
		else if( event.eventType == PlayerEvent.Type.PLAYER_UNBANNED ) {
			String unbanCommand = null;
			String unbanSender = null;
			if( event.extraData.length == 2 ) {
				unbanCommand = event.extraData[0];
				unbanSender = event.extraData[1];
			}
			logEvent(event, Type.UNBANNED_PLAYER, 0, null,
					"UnBanSender: "+unbanSender+", UnBanCommand: "+unbanCommand);
		}
		else if( event.eventType == PlayerEvent.Type.PLAYER_KICK ) {
			logEvent(event, Type.PLAYER_KICKED, 0, null, null);
		}
	}

	private void logEvent(Event event, Type type, float griefValue, String blockOwner, String additionalData) {
		PlayerState ps = playerStateManager.getPlayerState(event.getPlayerName());
		GriefLog log = ps.getGriefLog();
		if( log != null ) {
			Debug.getInstance().debug("GriefLogEngine:logEvent:",event);
			GriefEntry entry = new GriefEntry(type, event.getTime(), event.getPlayerName(),
					griefValue, ps.getGriefPoints(), event.getLocation(), blockOwner, additionalData);
			try {
				log.writeEntry(entry);
			}
			catch(IOException e) {
				logger.warning(logPrefix+"error writing to player grief log: "+e.getMessage());
			}
		}

	}
}
