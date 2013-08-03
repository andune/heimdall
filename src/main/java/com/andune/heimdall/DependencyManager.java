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
package com.andune.heimdall;

import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.diddiz.LogBlock.LogBlock;
import me.botsko.prism.Prism;
import net.milkbowl.vault.Vault;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 * This class manages all of our plugin dependencies. It exists so that if a plugin is
 * reloaded, our reference to that plugin is also updated.
 *
 * @author andune
 */
public class DependencyManager implements Listener {
    private final Heimdall heimdall;
    private LWCBridge lwcBridge;

    public DependencyManager(final Heimdall plugin) {
        this.heimdall = plugin;
        this.lwcBridge = new LWCBridge(heimdall);
    }

    public LWCBridge getLWCBridge() {
        return lwcBridge;
    }

    @EventHandler
    public void onPluginEnable(final PluginEnableEvent event) {
        final Plugin plugin = event.getPlugin();

        if (plugin.getDescription().getName().equals("LWC") && plugin instanceof LWCPlugin) {
            heimdall.verbose("detected LWC plugin load, updating plugin reference");
            lwcBridge.updateLWC((LWCPlugin) plugin);
            return;
        }

        if (plugin.getDescription().getName().equals("LogBlock") && plugin instanceof LogBlock) {
            heimdall.verbose("detected LogBlock plugin load, updating plugin reference");
            heimdall.getBlockHistoryManager().pluginLoaded((LogBlock) plugin);
            return;
        }

        if (plugin.getDescription().getName().equals("Prism") && plugin instanceof Prism) {
            heimdall.verbose("detected Prism plugin load, updating plugin reference");
            heimdall.getBlockHistoryManager().pluginLoaded((Prism) plugin);
            return;
        }

        // detect if the permission system we use just got reloaded
        switch (heimdall.getPermissionSystem().getSystemInUse()) {
            case VAULT:
                if (plugin.getDescription().getName().equals("Vault") && plugin instanceof Vault) {
                    heimdall.verbose("detected Vault plugin load, updating plugin reference");
                    heimdall.getPermissionSystem().setupPermissions(heimdall.isVerboseEnabled());
                }
                break;

            case WEPIF:
                if (plugin.getDescription().getName().equals("WorldEdit") && plugin instanceof WorldEditPlugin) {
                    heimdall.verbose("detected WorldEdit plugin load, updating plugin reference");
                    heimdall.getPermissionSystem().setupPermissions(heimdall.isVerboseEnabled());
                }
                break;
        }
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        final Plugin plugin = event.getPlugin();

        if (plugin.getDescription().getName().equals("LWC") && plugin instanceof LWCPlugin) {
            heimdall.verbose("detected LWC plugin unload, removing plugin reference");
            lwcBridge.updateLWC(null);
            return;
        }

        if (plugin.getDescription().getName().equals("LogBlock") && plugin instanceof LogBlock) {
            heimdall.verbose("detected LogBlock plugin unload, removing plugin reference");
            heimdall.getBlockHistoryManager().pluginUnloaded((LogBlock) plugin);
            return;
        }
    }
}
