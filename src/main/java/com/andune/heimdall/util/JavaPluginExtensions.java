/**
 *
 */
package com.andune.heimdall.util;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author andune
 */
public interface JavaPluginExtensions extends Plugin {
    // version: 1
    public PermissionSystem getPermissionSystem();

    public Logger getLogger();

    public String getLogPrefix();

    public JarUtils getJarUtils();

    public File getFile();

    public ClassLoader getClassLoaderPublic();
}
