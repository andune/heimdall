/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent.InventoryEventType;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.util.Debug;
import org.morganm.util.JavaPluginExtensions;

/**
 * @author morganm
 *
 */
public class MainProcessEngine implements Engine {
	private final JavaPluginExtensions plugin;
	private final PlayerStateManager playerStateManager;
	private final Debug debug;
	private final YamlConfiguration config;
	private final EngineLog log;
	private final boolean isLogging;
	
	public MainProcessEngine(final JavaPluginExtensions plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;
		this.debug = Debug.getInstance();
		
		String configFile = this.plugin.getConfig().getString("engine.main.configfile");
		File file = new File(configFile);
//		Debug.getInstance().debug("loading file ",file);
		this.config = YamlConfiguration.loadConfiguration(file);
//		Debug.getInstance().debug("config.getInt(\"blockpoints.4\") = ",config.getInt("blockpoints.4"));
		
		this.isLogging = this.config.getBoolean("engine.main.writeEngineLog", false);
		if( this.isLogging ) {
			File logFile = new File(this.config.getString("engine.main.logfile"));
			Debug.getInstance().debug("Main engine opening engine logfile ",logFile);
			log = new EngineLog(logFile);
		}
		else
			log = null;
	}

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		if( event.bukkitEventType == org.bukkit.event.Event.Type.BLOCK_BREAK ) {
			int typeId = event.type.getId();
			/*
			if( bc.blockOwner != null && !bc.playerName.equals(bc.blockOwner) && (bc.ownerTypeId == 0 || typeId == bc.ownerTypeId) ) {
				debug.debug("block grief penalty: owner and player don't match, owner=",bc.blockOwner,", player=",bc.playerName);
				value = getBlockValue(typeId);
			}
			*/
			event.griefValue = getBlockValue(typeId);	// testing;
			
			Debug.getInstance().debug("MainProcessEngine:processBlockChange event.griefValue = ",event.griefValue);
			if( isLogging && log != null )
				log.logIgnoreError("assessing grief value of "+event.griefValue+" to player "+event.playerName);
		}
		else if( event.bukkitEventType == org.bukkit.event.Event.Type.BLOCK_PLACE ) {
			event.griefValue = - getBlockValue(event.type.getId());
			event.griefValue /= 4;		// block place is worth 1/4 the points as a grief destroy
							// TODO: move ratio to config file
		}
		
		if( event.griefValue != 0 )
			playerStateManager.getPlayerState(event.playerName).incrementGriefPoints(event.griefValue, event.blockOwner);
	}
	
	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		if( event.type == InventoryEventType.CONTAINER_ACCESS ) {
			if( event.blockOwner != null && !event.playerName.equals(event.blockOwner) ) {
				debug.debug("inventory grief penalty: owner and player don't match, owner=",event.blockOwner,", player=",event.playerName);
				for(int i=0; i < event.diff.length; i++) {
					event.griefValue += getInventoryValue(event.diff[i]);
				}
			}
		}
		
		if( event.griefValue != 0 )
			playerStateManager.getPlayerState(event.playerName).incrementGriefPoints(event.griefValue, event.blockOwner);
	}

	@Override
	public void processChatMessage(String message) {
		// TODO Auto-generated method stub
	}
	
	public float getBlockValue(int id) {
		return config.getInt("blockpoints."+id, 1);
	}

	public float getInventoryValue(ItemStack is) {
		return config.getInt("inventorypoints."+is.getTypeId(), 1) * is.getAmount();
	}
}
