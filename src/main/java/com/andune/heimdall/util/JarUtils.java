/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 *
 */
package com.andune.heimdall.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * @author andune
 */
public class JarUtils {
    // version: 10
    private final Logger log;
    private final String logPrefix;
    private JavaPluginExtensions plugin;
    private File jarFile;

    public JarUtils(JavaPluginExtensions plugin, File jarFile, Logger log, String logPrefix) {
        this.plugin = plugin;
        this.jarFile = this.plugin.getFile();
        this.log = this.plugin.getLogger();
        this.logPrefix = this.plugin.getLogPrefix();
    }
//	public JarUtils(JavaPlugin plugin, File jarFile) {
//		this(plugin,jarFile, null, null);
//	}

    /**
     * Code adapted from Puckerpluck's MultiInv plugin. Used to copy a config file
     * from the plugin JAR to the plugin's data directory.
     *
     * @param fileName the name of the file. example: "config.yml"
     * @param outfile  the File to copy to. example: "plugins/MyPlugin/config.yml"
     */
    public void copyConfigFromJar(String fileName, File outfile) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!outfile.canRead()) {
            try {
                JarFile jar = new JarFile(jarFile);

                file.getParentFile().mkdirs();
                JarEntry entry = jar.getJarEntry(fileName);
                InputStream is = jar.getInputStream(entry);
                FileOutputStream os = new FileOutputStream(outfile);
                byte[] buf = new byte[(int) entry.getSize()];
                is.read(buf, 0, (int) entry.getSize());
                os.write(buf);
                os.close();
            } catch (Exception e) {
                log.warning(logPrefix + " Could not copy config file " + fileName + " to default location");
            }
        }
    }

    /**
     * Return the build string from the jar manifest.
     *
     * @return
     */
    public String getBuild() {
        String build = "unknown";

        try {
            JarFile jar = new JarFile(jarFile);
            Manifest manifest = jar.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            build = attributes.getValue("Implementation-Build");
        } catch (Exception e) {
            log.warning(logPrefix + " Could not load build string from JAR");
        }

        return build;
    }
}
