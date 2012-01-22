/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.FriendEvent;
import org.morganm.heimdall.event.FriendInviteEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;

/** Simple engine to log griefer actions.
 * 
 * @author morganm
 *
 */
public class SimpleLogActionEngine extends AbstractEngine {
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
//	private static long TIME_BETWEEN_FLUSH = 5000;	// 5 seconds
	
	private final Heimdall plugin;
	private EngineLog log;
	private final PlayerStateManager playerStateManager;
	
	public SimpleLogActionEngine(final Heimdall plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;
		
		// TODO: drive file location from config file
		this.log = new EngineLog(plugin, new File("plugins/Heimdall/logs/simpleLogActionEngine.log"));
		try {
			log.init();
		}
		catch(IOException e) {
			log = null;
			e.printStackTrace();
		}
	}

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		logEvent(event, event.griefValue);
	}

	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		logEvent(event, event.griefValue);
	}
	
	@Override
	public void processHeimdallFriendEvent(FriendEvent event) {
		if( log != null ) {
			try {
				StringBuilder sb = new StringBuilder(160);
				sb.append("[");
				sb.append(dateFormat.format(new Date()));
				sb.append("] ");
				sb.append("Player ");
				sb.append(event.getPlayerName());
				sb.append(" friended player ");
				sb.append(event.getFriend());
				log.log(sb.toString());				
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void processHeimdallFriendInvite(FriendInviteEvent event) {
		if( log != null ) {
			try {
				StringBuilder sb = new StringBuilder(160);
				sb.append("[");
				sb.append(dateFormat.format(new Date()));
				sb.append("] ");
				sb.append("Player ");
				sb.append(event.getPlayerName());
				sb.append(" sent automated friend invite for player ");
				sb.append(event.getInvitedFriend());
				log.log(sb.toString());				
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void logEvent(final Event event, final float griefValue) {
//		Debug.getInstance().debug("SimpleLogActionEngine:processGriefValue(): playerName=",event.getPlayerName(),", griefvalue=",griefValue);
		if( griefValue == 0 )
			return;

		PlayerState ps = playerStateManager.getPlayerState(event.getPlayerName());
		if( ps.isExemptFromChecks() )
			return;

		if( log != null ) {
			try {
				StringBuilder sb = new StringBuilder(160);
				sb.append("[");
				sb.append(dateFormat.format(new Date()));
				sb.append("] ");
				sb.append(event.getPlayerName());
				sb.append(" event grief points ");
				sb.append(griefValue);
				sb.append(", total grief now is ");
				sb.append(ps.getGriefPoints());
				log.log(sb.toString());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
