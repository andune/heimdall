/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.util.Debug;
import org.morganm.util.JavaPluginExtensions;

/**
 * @author morganm
 *
 */
public class MainProcessEngine implements ProcessEngine {
	private final JavaPluginExtensions plugin;
	private final Debug debug;
	private final YamlConfiguration config;
	
	public MainProcessEngine(final JavaPluginExtensions plugin) {
		this.plugin = plugin;
		this.debug = Debug.getInstance();
		String configFile = this.plugin.getConfig().getString("engine.main.configfile");
		File file = new File(configFile);
		Debug.getInstance().debug("loading file ",file);
		this.config = YamlConfiguration.loadConfiguration(file);
		Debug.getInstance().debug("config.getInt(\"blockpoints.4\") = ",config.getInt("blockpoints.4"));
	}

	@Override
	public float calculateBlockChange(BlockChangeEvent bc) {
		float value = 0;
		
		if( bc.bukkitEventType == org.bukkit.event.Event.Type.BLOCK_BREAK ) {
			int typeId = bc.type.getId();
			/*
			if( bc.blockOwner != null && !bc.playerName.equals(bc.blockOwner) && (bc.ownerTypeId == 0 || typeId == bc.ownerTypeId) ) {
				debug.debug("grief penalty: owner and player don't match, owner=",bc.blockOwner,", player=",bc.playerName);
				value = getBlockValue(typeId);
			}
			*/
			value = getBlockValue(typeId);	// testing;
		}
		else if( bc.bukkitEventType == org.bukkit.event.Event.Type.BLOCK_PLACE ) {
			value = - getBlockValue(bc.type.getId());
			value /= 4;		// block place is worth 1/4 the points as a grief destroy
							// TODO: move ratio to config file
		}
		
		return value;
	}

	@Override
	public float calculateInventoryChange(InventoryChangeEvent ic) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float calculateChatMessage(String message) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public float getBlockValue(int id) {
		return config.getInt("blockpoints."+id, 1);
	}

}
