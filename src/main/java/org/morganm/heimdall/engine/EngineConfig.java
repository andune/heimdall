/**
 * 
 */
package org.morganm.heimdall.engine;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.event.handlers.EngineWrapper;
import org.morganm.heimdall.util.Debug;

/** Class to read in config file engine definitions and register the Engines
 * with Heimdall.
 * 
 * @author morganm
 *
 */
public class EngineConfig {
	private final Logger log;
	private final String logPrefix;
	private final Heimdall plugin;
	private final Debug debug;
	
	public EngineConfig(final Heimdall plugin) {
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.logPrefix = plugin.getLogPrefix();
		this.debug = Debug.getInstance();
	}
	
	/** Read plugin config to determine registered engines and register them all.
	 * 
	 */
	public void registerEngines() {
		FileConfiguration config = plugin.getConfig();

		final String[] sections = new String[] { "enrichers", "handlers" };
		for(int i=0; i < sections.length; i++) {
			ConfigurationSection section = config.getConfigurationSection(sections[i]);
			Set<String> keys = section.getKeys(false);
			for(String handler : keys) {
				String className = config.getString(sections[i]+"."+handler+".class");
				String configFile = config.getString(sections[i]+"."+handler+".configFile");
				debug.debug("Instantiating engine ",handler,", class=",className,", configFile=",configFile);
				Engine engine = instantiateEngine(handler, className, configFile);
				if( engine != null ) {
					EngineWrapper wrapper = new EngineWrapper(engine);
					plugin.getEventManager().registerEnricher(plugin, wrapper);
					debug.debug("Engine ",handler," registered successfully");
				}
			}
		}
	}
	
	private Engine instantiateEngine(final String handlerName, final String className, final String configFile) {
		Engine engine = null;

		try {
			Class<?> clazz = Class.forName(className);
			Class<? extends Engine> engineClass = clazz.asSubclass(Engine.class);
			Constructor<? extends Engine> constructor = null;

			// first try a constructor accepting a configFile argument
			try {
				constructor = engineClass.getConstructor(Heimdall.class, String.class);
				engine = constructor.newInstance(plugin, configFile);
			}
			catch(NoSuchMethodException nsme) {
				// only Exception we catch is if the constructor doesn't exist; we'll
				// continue on to find another one. Any other exception is an error
				// condition and will be passed on to the surrounding try/catch for
				// error processing.
			}

			// if no other constructor was found, find the default engine constructor
			if( constructor == null ) {
				constructor = engineClass.getConstructor(Heimdall.class);
				engine = constructor.newInstance(plugin);
			}
		}
		catch(ClassNotFoundException cnfe) {
			log.log(Level.WARNING, logPrefix+"Class not found for engine key "+handlerName+": "+className, cnfe);
		}
		catch(Exception e) {
			log.log(Level.WARNING, logPrefix+"Exception instantiating engine "+handlerName+": "+e.getMessage(), e);
		}

		return engine;
	}
}
