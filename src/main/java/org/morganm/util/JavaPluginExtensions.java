/**
 * 
 */
package org.morganm.util;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

/**
 * @author morganm
 *
 */
public interface JavaPluginExtensions extends Plugin {
	// version: 1
	public PermissionSystem getPermissionSystem();
	public Logger getLogger();
	public String getLogPrefix();
	public JarUtils getJarUtils();
	public File getFile();
	public ClassLoader getClassLoader();
}
