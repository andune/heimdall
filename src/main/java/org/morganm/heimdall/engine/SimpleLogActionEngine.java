/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;
import java.io.IOException;

import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.Event;
import org.morganm.heimdall.event.InventoryChangeEvent;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.util.Debug;
import org.morganm.util.JavaPluginExtensions;

/** Simple engine to log griefer actions.
 * 
 * @author morganm
 *
 */
public class SimpleLogActionEngine implements Engine {
//	private static long TIME_BETWEEN_FLUSH = 5000;	// 5 seconds
	
	private final JavaPluginExtensions plugin;
	private EngineLog log;
	private boolean flushScheduled = false;
	private final LogFlusher logFlusher = new LogFlusher();
	private final PlayerStateManager playerStateManager;
	
	public SimpleLogActionEngine(final JavaPluginExtensions plugin, final PlayerStateManager playerStateManager) {
		this.plugin = plugin;
		this.playerStateManager = playerStateManager;

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

	@Override
	public void processBlockChange(BlockChangeEvent event) {
		logEvent(event, event.griefValue);
	}

	@Override
	public void processInventoryChange(InventoryChangeEvent event) {
		logEvent(event, event.griefValue);
	}

	@Override
	public void processChatMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
	private void logEvent(final Event event, final float griefValue) {
		Debug.getInstance().debug("processGriefValue(): playerName=",event.getPlayerName(),", griefvalue=",griefValue);
		if( log != null ) {
			try {
				PlayerState ps = playerStateManager.getPlayerState(event.getPlayerName());
				log.log(event.getPlayerName()+" accumulated grief points "+griefValue+", total grief now is "+ps.getGriefPoints());
				
				if( !flushScheduled ) {
					flushScheduled = true;
					plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, logFlusher, 100);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
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
