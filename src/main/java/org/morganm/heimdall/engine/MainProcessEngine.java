/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;
import org.morganm.heimdall.player.FriendTracker;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.util.Debug;

/**
 * @author morganm
 *
 */
public class MainProcessEngine extends AbstractEngine {
	private final Heimdall plugin;
	private final PlayerStateManager playerStateManager;
	private final Debug debug;
	private final YamlConfiguration config;
	private final EngineLog log;
	private final boolean isLogging;
	private final FriendTracker friendTracker;
	
	public MainProcessEngine(final Heimdall plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;
		this.friendTracker = plugin.getFriendTracker();
		this.debug = Debug.getInstance();
		
		String configFile = this.plugin.getConfig().getString("engine.main.configfile");
		File file = new File(configFile);
//		debug.debug("loading file ",file);
		this.config = YamlConfiguration.loadConfiguration(file);
//		debug.debug("config.getInt(\"blockpoints.4\") = ",config.getInt("blockpoints.4"));
		
		this.isLogging = this.config.getBoolean("engine.main.writeEngineLog", false);
		if( this.isLogging ) {
			File logFile = new File(this.config.getString("engine.main.logfile"));
			debug.debug("Main engine opening engine logfile ",logFile);
			log = new EngineLog(plugin, logFile);
		}
		else
			log = null;
	}

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		PlayerState ps = playerStateManager.getPlayerState(event.playerName);
		if( ps.isExemptFromChecks() )
			return;
		
		if( event.bukkitEventType == Event.BukkitType.BLOCK_BREAK ) {
			int typeId = event.type.getId();
			if( event.blockOwner != null
					&& !event.playerName.equals(event.blockOwner)
					&& (event.ownerTypeId == 0 || typeId == event.ownerTypeId) ) {
				
				// ignore any broken blocks between friends
				if( !friendTracker.isFriend(event.blockOwner, event.playerName) ) {
					debug.debug("block grief penalty: owner and player don't match, owner=",event.blockOwner,", player=",event.playerName);
					event.griefValue = getBlockValue(typeId);
				}
				else
					debug.debug("player ",event.blockOwner," has claimed player ",event.playerName," as friend: no grief penalty");
			}
//			event.griefValue = getBlockValue(typeId);	// testing;
			
			debug.debug("MainProcessEngine:processBlockChange event.griefValue = ",event.griefValue);
			if( isLogging && log != null )
				log.logIgnoreError("assessing grief value of "+event.griefValue+" to player "+event.playerName);
		}
		else if( event.bukkitEventType == Event.BukkitType.BLOCK_PLACE ) {
			event.griefValue = - getBlockValue(event.type.getId());
			event.griefValue /= 4;		// block place is worth 1/4 the points as a grief destroy
							// TODO: move ratio to config file
		}
		
		if( event.griefValue != 0 )
			playerStateManager.getPlayerState(event.playerName).incrementGriefPoints(event.griefValue, event.blockOwner);
	}
	
	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		PlayerState ps = playerStateManager.getPlayerState(event.playerName);
		if( ps.isExemptFromChecks() )
			return;

		debug.debug("MainProcessEngine:processInventoryChange event.type = ",event.type);
		if( event.type == InventoryEventType.CONTAINER_ACCESS ) {
			if( event.blockOwner != null && !event.playerName.equals(event.blockOwner) ) {
				if( !event.isLwcPublic ) {
					// ignore any chest access between friends
					if( !friendTracker.isFriend(event.blockOwner, event.playerName) ) {
						debug.debug("MainProcessEngine:processInventoryChange inventory grief penalty: owner and player don't match, owner=",event.blockOwner,", player=",event.playerName);
						for(int i=0; i < event.diff.length; i++) {
							event.griefValue += getInventoryValue(event.diff[i]);
							debug.debug("MainProcessEngine:processInventoryChange event grief value = ",event.griefValue);
						}
					}
					else
						debug.debug("player ",event.blockOwner," has claimed player ",event.playerName," as friend: no grief penalty");
				}
				else
					debug.debug("MainProcessEngine:processInventoryChange block is flagged as LWC public, no grief penalty");
			}
		}
		
		if( event.griefValue != 0 )
			playerStateManager.getPlayerState(event.playerName).incrementGriefPoints(event.griefValue, event.blockOwner);
	}

	public float getBlockValue(int id) {
		return (float) config.getDouble("blockpoints."+id, 1);
	}

	public float getInventoryValue(ItemStack is) {
		// multiply by - amount, since a negative amount is items taken, which should accumulate
		// positive grief value
		return (float) config.getDouble("inventorypoints."+is.getTypeId(), 1) * (- is.getAmount());
	}
}
