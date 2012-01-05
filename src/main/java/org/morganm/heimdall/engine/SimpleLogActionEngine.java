/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;
import java.io.IOException;

import org.morganm.util.Debug;
import org.morganm.util.JavaPluginExtensions;

/** Simple engine to log griefer actions.
 * 
 * @author morganm
 *
 */
public class SimpleLogActionEngine implements ActionEngine {
//	private static long TIME_BETWEEN_FLUSH = 5000;	// 5 seconds
	
	private final JavaPluginExtensions plugin;
	private EngineLog log;
	private boolean flushScheduled = false;
	private final LogFlusher logFlusher = new LogFlusher();
	
	public SimpleLogActionEngine(final JavaPluginExtensions plugin) {
		this.plugin = plugin;

		// TODO: drive file location from config file
		this.log = new EngineLog(new File("plugins/Heimdall/logs/simpleLogActionEngine.log"));
		try {
			log.init();
		}
		catch(IOException e) {
			log = null;
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.morganm.heimdall.engine.ActionEngine#processGriefValue(org.bukkit.entity.Player, float)
	 */
	@Override
	public boolean processGriefValue(String playerName, float griefValue) {
		Debug.getInstance().debug("processGriefValue(): playerName=",playerName,", griefvalue=",griefValue);
		if( log != null ) {
			try {
				log.log(playerName+" now has griefValue "+griefValue);
				
				if( !flushScheduled ) {
					flushScheduled = true;
					plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, logFlusher, 100);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	private class LogFlusher implements Runnable {
		public void run() {
			try { 
				log.flush();
			} catch(IOException e) {}
			flushScheduled = false;
		}
	}
	
}
