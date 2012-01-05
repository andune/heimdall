/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.File;
import java.io.IOException;

import org.morganm.util.JavaPluginExtensions;

/** Simple engine to log griefer actions.
 * 
 * @author morganm
 *
 */
public class SimpleLogActionEngine implements ActionEngine {
	@SuppressWarnings("unused")
	private final JavaPluginExtensions plugin;
	private EngineLog log;
	
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
		if( log != null ) {
			try {
				log.log(playerName+" now has griefValue "+griefValue);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}

}
