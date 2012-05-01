/**
 * 
 */
package org.morganm.heimdall.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.EventCircularBuffer;
import org.morganm.heimdall.event.EventManager;
import org.morganm.heimdall.event.PlayerEvent;
import org.morganm.heimdall.event.PlayerEvent.Type;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.player.PlayerTracker;
import org.morganm.heimdall.util.Debug;
import org.morganm.heimdall.util.General;

/**
 * @author morganm
 *
 */
public class BukkitPlayerListener implements Listener {
	private final Heimdall plugin;
	private final PlayerStateManager playerStateManager;
	private final PlayerTracker playerTracker;
	private final EventCircularBuffer<PlayerEvent> buffer;
	private final EventManager eventManager;
	
	public BukkitPlayerListener(final Heimdall plugin, final EventManager eventManager) {
		this.plugin = plugin;
		this.eventManager = eventManager;
		this.playerStateManager = this.plugin.getPlayerStateManager();
		this.playerTracker = this.playerStateManager.getPlayerTracker();
		buffer = new EventCircularBuffer<PlayerEvent>(PlayerEvent.class, 1000, false, true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerTracker.playerLogin(event.getPlayer().getName());
		
		PlayerEvent pe = getNextEventObject();
		
		if( pe != null ) {
			if( General.getInstance().isNewPlayer(event.getPlayer()) ) {
				pe.eventType = Type.NEW_PLAYER_JOIN;
				Debug.getInstance().debug("New player join: ",event.getPlayer());
			}
			else
				pe.eventType = Type.PLAYER_JOIN;

			pe.playerName = event.getPlayer().getName();
			pe.location = event.getPlayer().getLocation();
			pe.x = pe.location.getBlockX();
			pe.y = pe.location.getBlockY();
			pe.z = pe.location.getBlockZ();
			pe.world = pe.location.getWorld();
			pe.time = System.currentTimeMillis();
			
			eventManager.pushEvent(pe);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		playerTracker.removeTrackedPlayer(event.getPlayer().getName());

		PlayerEvent pe = getNextEventObject();
		
		if( pe != null ) {
			pe.eventType = Type.PLAYER_QUIT;
			pe.playerName = event.getPlayer().getName();
			pe.location = event.getPlayer().getLocation();
			pe.x = pe.location.getBlockX();
			pe.y = pe.location.getBlockY();
			pe.z = pe.location.getBlockZ();
			pe.world = pe.location.getWorld();
			pe.time = System.currentTimeMillis();
			
			eventManager.pushEvent(pe);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		if( event.isCancelled() )
			return;
		playerTracker.removeTrackedPlayer(event.getPlayer().getName());

		PlayerEvent pe = getNextEventObject();
		
		if( pe != null ) {
			pe.eventType = Type.PLAYER_KICK;
			pe.playerName = event.getPlayer().getName();
			pe.location = event.getPlayer().getLocation();
			pe.x = pe.location.getBlockX();
			pe.y = pe.location.getBlockY();
			pe.z = pe.location.getBlockZ();
			pe.world = pe.location.getWorld();
			pe.time = System.currentTimeMillis();
			
			eventManager.pushEvent(pe);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if( event.getMessage().startsWith("/") ) {
			if( event.getMessage().startsWith("/ban") ) {
				String args = event.getMessage().substring(5);
				if( args != null ) {
					int index = args.indexOf(' ');
					if( index != -1 ) {
						String bannedPlayer = args.substring(0, index-1);
						Debug.getInstance().debug("ban command for player ",bannedPlayer," from player ",event.getPlayer());
//						plugin.getBanTracker().addCommand(bannedPlayer, event.getMessage(), event.getPlayer().getName());
						
						PlayerEvent pe = getNextEventObject();
						if( pe != null ) {
							pe.eventType = Type.PLAYER_BANNED;
							pe.playerName = bannedPlayer;
							pe.location = event.getPlayer().getLocation();
							pe.x = pe.location.getBlockX();
							pe.y = pe.location.getBlockY();
							pe.z = pe.location.getBlockZ();
							pe.world = pe.location.getWorld();
							pe.time = System.currentTimeMillis();
							pe.extraData = new String[] {event.getMessage(), event.getPlayer().getName()};
							
							eventManager.pushEvent(pe);
						}
					}
				}
			}
			else if( event.getMessage().startsWith("/unban") ) {
				String args = event.getMessage().substring(7);
				if( args != null ) {
					int index = args.indexOf(' ');
					if( index != -1 ) {
						String unbannedPlayer = args.substring(0, index-1);
						Debug.getInstance().debug("unban command for player ",unbannedPlayer," from player ",event.getPlayer());
//						plugin.getBanTracker().unBan(unbannedPlayer);
						PlayerEvent pe = getNextEventObject();
						
						if( pe != null ) {
							pe.eventType = Type.PLAYER_UNBANNED;
							pe.playerName = unbannedPlayer;
							pe.location = event.getPlayer().getLocation();
							pe.x = pe.location.getBlockX();
							pe.y = pe.location.getBlockY();
							pe.z = pe.location.getBlockZ();
							pe.world = pe.location.getWorld();
							pe.time = System.currentTimeMillis();
							pe.extraData = new String[] {event.getMessage(), event.getPlayer().getName()};
							
							eventManager.pushEvent(pe);
						}
					}
				}
			}
		}
	}
	
	private final static int ERROR_FLOOD_PREVENTION_LIMIT = 3;
	private int errorFloodPreventionCount = 0;
	private PlayerEvent getNextEventObject() {
		PlayerEvent event = null;
		try {
			event = buffer.getNextObject();
			errorFloodPreventionCount = 0;
		} catch (InstantiationException e) {
			errorFloodPreventionCount++;
			if( errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT )
				e.printStackTrace();
		} catch (IllegalAccessException e) {
			errorFloodPreventionCount++;
			if( errorFloodPreventionCount < ERROR_FLOOD_PREVENTION_LIMIT )
				e.printStackTrace();
		}
		
		event.cleared = false;	// change clear flag since we are going to use this object
		return event;
	}
}
