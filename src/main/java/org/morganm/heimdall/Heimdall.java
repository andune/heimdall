/**
 * 
 */
package org.morganm.heimdall;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.util.JarUtils;
import org.morganm.heimdall.util.PermissionSystem;

/**
 * @author morganm
 *
 */
public class Heimdall extends JavaPlugin {
	public static final Logger log = Logger.getLogger(Heimdall.class.toString());
	public static final String logPrefix = "[Heimdall] ";

	private String version;
	private int buildNumber = -1;
	private boolean configLoaded = false;
	
	private PermissionSystem perm;
	private JarUtils jarUtil;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		jarUtil = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtil.getBuildNumber();

		perm = new PermissionSystem(this, log, logPrefix);
		perm.setupPermissions();
		
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is enabled");
	}
	
	@Override
	public void onDisable() {
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is disabled");
	}


}
