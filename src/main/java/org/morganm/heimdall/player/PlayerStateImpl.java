/**
 * 
 */
package org.morganm.heimdall.player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.morganm.heimdall.log.GriefLog;
import org.morganm.util.JavaPluginExtensions;

/**
 * @author morganm
 *
 */
public class PlayerStateImpl implements PlayerState {
	private final transient JavaPluginExtensions plugin;  
	private final String name;
	private float griefPoints=0;
	private Map<String, Float> pointsByOwner;
	
	private final transient File dataFile;
	private final transient PlayerStateManager playerStateManager;
	private transient GriefLog griefLog;
	private transient YamlConfiguration dataStore;
	
	public PlayerStateImpl(final JavaPluginExtensions plugin, final String name, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.name = name;
		this.playerStateManager = playerStateManager;
		this.dataFile = new File("plugins/Heimdall/playerData/"+name+".yml");
	}
	
	@Override
	public String getName() { return name; }

	@Override
	public float incrementGriefPoints(final float f, final String owner) {
		griefPoints += f;
		
		// track owner points, if owner was given
		if( owner != null ) {
			Float ownerPoints = pointsByOwner.get(owner);
			if( ownerPoints == null )
				ownerPoints = Float.valueOf(f);
			else
				ownerPoints += f;
			pointsByOwner.put(owner, ownerPoints);
		}
		
		return griefPoints;
	}

	@Override
	public float getGriefPoints() {
		return griefPoints;
	}

	@Override
	public boolean isExemptFromChecks() {
		// TODO: something intelligent later
		return false;
	}

	@Override
	public boolean isFriend(PlayerState p) {
		// TODO: something intelligent later (with playerStateManager)
		return false;
	}

	@Override
	public float getPointsByOwner(PlayerState p) {
		return pointsByOwner.get(p.getName());
	}

	@Override
	public GriefLog getGriefLog() {
		if( griefLog == null ) {
			File logFile = new File("plugins/Heimdall/griefLog/"+name+".dat");
			griefLog = new GriefLog(plugin, logFile);
		}
		
		return griefLog;
	}
	
	public void save() throws IOException {
		// make parent directories if necessary
		if( !dataFile.exists() ) {
			File path = new File(dataFile.getParent());
			if( !path.exists() )
				path.mkdirs();
		}
		
		if( dataStore == null )
			dataStore = new YamlConfiguration();
		
		dataStore.set("griefPoints", griefPoints);
		dataStore.set("pointsByOwner", pointsByOwner);

		dataStore.save(dataFile);
	}
	
	@SuppressWarnings("unchecked")
	public void load() throws IOException, InvalidConfigurationException {
		// if there's no data file, do nothing
		if( !dataFile.exists() )
			return;
		
		if( dataStore == null )
			dataStore = YamlConfiguration.loadConfiguration(dataFile);
		else
			dataStore.load(dataFile);
		
		griefPoints = (float) dataStore.getDouble("griefPoints");
		pointsByOwner = (Map<String, Float>) dataStore.get("pointsByOwner");
		
		if( pointsByOwner == null )
			pointsByOwner = new HashMap<String, Float>();
	}
}
