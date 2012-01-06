/**
 * 
 */
package org.morganm.heimdall;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.blockhistory.BlockHistoryFactory;
import org.morganm.heimdall.blockhistory.BlockHistoryManager;
import org.morganm.heimdall.command.CommandMapper;
import org.morganm.heimdall.engine.Engine;
import org.morganm.heimdall.engine.MainProcessEngine;
import org.morganm.heimdall.engine.NotifyEngine;
import org.morganm.heimdall.engine.SimpleLogActionEngine;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.EventManager;
import org.morganm.heimdall.event.enrichers.BlockHistoryEnricher;
import org.morganm.heimdall.event.handlers.HeimdallEventHandler;
import org.morganm.heimdall.listener.BukkitBlockListener;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.util.Debug;
import org.morganm.util.JarUtils;
import org.morganm.util.JavaPluginExtensions;
import org.morganm.util.PermissionSystem;

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
	private PlayerStateManager playerStateManager;
	private Engine griefEngine;
	private NotifyEngine notifyEngine;
	private BlockHistoryManager blockHistoryManager;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		jarUtil = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtil.getBuildNumber();

		loadConfig();
		
		perm = new PermissionSystem(this, log, logPrefix);
		perm.setupPermissions();

		new CommandMapper(this).mapCommands();		// map our command objects
		
		// initialize various objects needed to get things going
		blockHistoryManager = BlockHistoryFactory.getBlockHistoryManager(this);
		playerStateManager = new PlayerStateManager(this);
		playerStateManager.load();
		griefEngine = new MainProcessEngine(this, playerStateManager);
		eventManager = new EventManager(this);
		
		// register enricher to add block history information to events
		if( blockHistoryManager != null )
			eventManager.registerEnricher(this, Event.Type.BLOCK_CHANGE,
					new BlockHistoryEnricher(this, blockHistoryManager));
		
		final ArrayList<Engine> actionEngines = new ArrayList<Engine>();
		actionEngines.add(new SimpleLogActionEngine(this, playerStateManager));
		notifyEngine = new NotifyEngine(this, playerStateManager);
		actionEngines.add(notifyEngine);
		
		// main handler for passing events to anti-grief engine
		eventManager.registerHandler(this, Event.Type.BLOCK_CHANGE,
				new HeimdallEventHandler(this,  griefEngine, actionEngines));
		
		PluginManager pm = getServer().getPluginManager();
		
		blockListener = new BukkitBlockListener(this, eventManager);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);
		
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is enabled");
	}
	
	@Override
	public void onDisable() {
		eventManager.unregisterAllPluginEnrichers(this);
		eventManager.unregisterAllPluginHandlers(this);
		getServer().getScheduler().cancelTasks(this);
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is disabled");
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
		
		Debug.getInstance().init(log, logPrefix, false);
		Debug.getInstance().setDebug(getConfig().getBoolean("devDebug", false), Level.FINEST);
		Debug.getInstance().setDebug(getConfig().getBoolean("debug", false));
	}

	public NotifyEngine getNotifyEngine() { return notifyEngine; }
	public BlockHistoryManager getBlockHistoryManager() { return blockHistoryManager; }
	
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
