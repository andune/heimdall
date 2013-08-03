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

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

/**
 * Class that provides a simplified wrapper to LWC.
 *
 * @author andune
 */
public class LWCBridge {
    private final Heimdall plugin;
    private LWC lwc;

    public LWCBridge(final Heimdall plugin) {
        this.plugin = plugin;

        Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LWC");
        if (p != null)
            updateLWC((LWCPlugin) p);
    }

    public void updateLWC(final LWCPlugin lwcPlugin) {
        if (lwcPlugin != null) {
            this.lwc = lwcPlugin.getLWC();
            this.plugin.getLogger().info(this.plugin.getLogPrefix() + "LWC version " + lwcPlugin.getDescription().getVersion() + " found and will be used to identify public chests");
        }
        else
            this.lwc = null;
    }

    public boolean isEnabled() {
        // TODO: should include config option as well
        return (lwc != null);
    }

    public boolean isPublic(Block b) {
        if (!isEnabled())
            return false;

        Protection protection = lwc.findProtection(b);
        if (protection != null && protection.getType() == Protection.Type.PUBLIC)
            return true;
        else
            return false;
    }
}
