/**
 * 
 */
package org.morganm.heimdall;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.blockhistory.BlockHistoryFactory;
import org.morganm.heimdall.blockhistory.BlockHistoryManager;
import org.morganm.heimdall.command.CommandMapper;
import org.morganm.heimdall.engine.Engine;
import org.morganm.heimdall.engine.GriefLogEngine;
import org.morganm.heimdall.engine.LastGriefTrackingEngine;
import org.morganm.heimdall.engine.MainProcessEngine;
import org.morganm.heimdall.engine.NotifyEngine;
import org.morganm.heimdall.engine.SimpleLogActionEngine;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.EventManager;
import org.morganm.heimdall.event.handlers.BlockHistoryEnricher;
import org.morganm.heimdall.event.handlers.EngineWrapper;
import org.morganm.heimdall.listener.BukkitBlockListener;
import org.morganm.heimdall.listener.BukkitPlayerListener;
import org.morganm.heimdall.listener.SpoutChestAccessListener;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.player.PlayerTracker;
import org.morganm.heimdall.util.Debug;
import org.morganm.heimdall.util.JarUtils;
import org.morganm.heimdall.util.JavaPluginExtensions;
import org.morganm.heimdall.util.PermissionSystem;

/**
 * @author morganm
 *
 */
public class Heimdall extends JavaPlugin implements JavaPluginExtensions {
	public static final Logger log = Logger.getLogger(Heimdall.class.toString());
	public static final String logPrefix = "[Heimdall] ";

	private String version;
	private int buildNumber = -1;
	private boolean configLoaded = false;
	
	private PermissionSystem perm;
	private JarUtils jarUtil;
	private EventManager eventManager;
	private BukkitBlockListener blockListener;	// block listener to push block breaks into buffer
	private BukkitPlayerListener playerListener;
	private PlayerStateManager playerStateManager;
	private Engine griefEngine;
	private NotifyEngine notifyEngine;
	private LastGriefTrackingEngine lastGriefTrackingEngine;
	private BlockHistoryManager blockHistoryManager;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		jarUtil = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtil.getBuildNumber();

		loadConfig();
		Debug.getInstance().debug("onEnable() starting, config loaded");
		
		perm = new PermissionSystem(this, log, logPrefix);
		perm.setupPermissions();

		new CommandMapper(this).mapCommands();		// map our command objects
		
		// initialize various objects needed to get things going
		playerStateManager = new PlayerStateManager(this);
		eventManager = new EventManager(this);
		
		// register enricher to add block history information to events
		blockHistoryManager = BlockHistoryFactory.getBlockHistoryManager(this);
		if( blockHistoryManager != null ) {
			BlockHistoryEnricher bhe = new BlockHistoryEnricher(this, blockHistoryManager, playerStateManager);
			eventManager.registerEnricher(this, Event.Type.BLOCK_CHANGE, bhe);
			eventManager.registerEnricher(this, Event.Type.INVENTORY_CHANGE, bhe);
			Debug.getInstance().debug("BlockHistoryEnricher ",bhe," has been registered");
		}
		
		// TODO: at some point the engine definition will be driven out of a config
		// and this hard coded stuff cleaned up.
		
		// main grief engine, runs as an enricher (adds grief information to event)
		griefEngine = new MainProcessEngine(this, playerStateManager);
		EngineWrapper wrapper = new EngineWrapper(griefEngine);
		eventManager.registerEnricher(this, Event.Type.BLOCK_CHANGE, wrapper);
		eventManager.registerEnricher(this, Event.Type.INVENTORY_CHANGE, wrapper);
		
		wrapper = new EngineWrapper(new SimpleLogActionEngine(this, playerStateManager));
		eventManager.registerHandler(this, Event.Type.BLOCK_CHANGE, wrapper);
		eventManager.registerHandler(this, Event.Type.INVENTORY_CHANGE, wrapper);
		
		notifyEngine = new NotifyEngine(this, playerStateManager);
		wrapper = new EngineWrapper(notifyEngine);
		eventManager.registerHandler(this, Event.Type.BLOCK_CHANGE, wrapper);
		eventManager.registerHandler(this, Event.Type.INVENTORY_CHANGE, wrapper);
		
		wrapper = new EngineWrapper(new GriefLogEngine(this, playerStateManager));
		eventManager.registerHandler(this, Event.Type.BLOCK_CHANGE, wrapper);
		eventManager.registerHandler(this, Event.Type.INVENTORY_CHANGE, wrapper);
		
		lastGriefTrackingEngine = new LastGriefTrackingEngine(this);
		wrapper = new EngineWrapper(lastGriefTrackingEngine);
		eventManager.registerHandler(this, Event.Type.BLOCK_CHANGE, wrapper);
		eventManager.registerHandler(this, Event.Type.INVENTORY_CHANGE, wrapper);

		/* old code
		final ArrayList<Engine> actionEngines = new ArrayList<Engine>();
		actionEngines.add(new SimpleLogActionEngine(this, playerStateManager));
		notifyEngine = new NotifyEngine(this, playerStateManager);
		actionEngines.add(notifyEngine);
		
		// main handler for passing events to anti-grief engine
		eventManager.registerHandler(this, Event.Type.BLOCK_CHANGE,
				new HeimdallEventHandler(this,  griefEngine, actionEngines));
		*/
		
		PluginManager pm = getServer().getPluginManager();
		
		blockListener = new BukkitBlockListener(this, eventManager);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);
		
		if (pm.isPluginEnabled("Spout")) {
			pm.registerEvent(Type.CUSTOM_EVENT, new SpoutChestAccessListener(this, eventManager), Priority.Monitor, this);
			log.info(logPrefix+ "Using Spout API to log chest access");
		}
		
		playerListener = new BukkitPlayerListener(this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, playerListener, Priority.Monitor, this);
		
		Player[] players = getServer().getOnlinePlayers();
		Debug.getInstance().debug("Running all ",players.length," online players through PlayerTracker login");
		for(int i=0; i < players.length; i++)
			playerStateManager.getPlayerTracker().playerLogin(players[i].getName());
		
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is enabled");
		Debug.getInstance().debug("onEnable() finished");
}
	
	@Override
	public void onDisable() {
		Debug.getInstance().debug("onDisable() starting");
		getServer().getScheduler().cancelTasks(this);

		try {
			playerStateManager.save();
		}
		catch(Exception e) {
			log.severe(logPrefix+"error saving playerStateManager: "+e.getMessage());
		}

		eventManager.unregisterAllPluginEnrichers(this);
		eventManager.unregisterAllPluginHandlers(this);
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is disabled");
		Debug.getInstance().debug("onDisable() finished");
	}

	public void loadConfig() {
		File file = new File(getDataFolder(), "config.yml");
		if( !file.exists() ) {
			jarUtil.copyConfigFromJar("config.yml", file);
		}

		if( !configLoaded ) {
			super.getConfig();
			configLoaded = true;
		}
		else
			super.reloadConfig();
		
		Debug.getInstance().init(log, logPrefix, "plugins/Heimdall/debug.log", false);
		Debug.getInstance().setDebug(getConfig().getBoolean("devDebug", false), Level.FINEST);
		Debug.getInstance().setDebug(getConfig().getBoolean("debug", false));
	}

	public NotifyEngine getNotifyEngine() { return notifyEngine; }
	public LastGriefTrackingEngine getLastGriefTrackingEngine() { return lastGriefTrackingEngine; }
	public BlockHistoryManager getBlockHistoryManager() { return blockHistoryManager; }
	public PlayerStateManager getPlayerStateManager() { return playerStateManager; }
	
	@Override
	public PermissionSystem getPermissionSystem() { return perm; }

	@Override
	public Logger getLogger() { return log; }

	@Override
	public String getLogPrefix() { return logPrefix; }

	@Override
	public JarUtils getJarUtils() { return jarUtil; }

	@Override
	public File getFile() { return super.getFile(); }
	
	@Override
	public ClassLoader getClassLoader() { return super.getClassLoader(); }
}
