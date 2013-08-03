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

import com.andune.heimdall.util.Debug;
import com.andune.heimdall.util.PermissionSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

/**
 * Things related to Heimdall's personality are here in one easy spot, to allow
 * for turning him off if desired.
 *
 * @author andune
 */
public class HeimdallPersonality {
    private static final String[] griefBellows = new String[]{
            ChatColor.RED + "Heimdall bellows: What have we here? I'm going to keep my eye on this one.",
            ChatColor.RED + "Heimdall bellows to someone far away: STOP RUINING MY WORLD!",
            ChatColor.RED + "Heimdall says: I smell a rat."
    };
    private final Random random = new Random(System.currentTimeMillis());
    private final Heimdall plugin;
    private final PermissionSystem perm;

    public HeimdallPersonality(final Heimdall plugin) {
        this.plugin = plugin;
        this.perm = this.plugin.getPermissionSystem();
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("core.personality.enabled", true);
    }

    public void announcePossibleGriefer(final String playerName) {
        if (!isEnabled())
            return;

        List<String> silentPerms = plugin.getConfig().getStringList("core.personality.silentPerms");

        int r = random.nextInt(griefBellows.length);
        final String bellow = griefBellows[r];
        Debug.getInstance().debug("Heimdall bellow string chosen: ", bellow);

        Player[] players = Bukkit.getOnlinePlayers();
        for (int i = 0; i < players.length; i++) {
            // don't send the message to the griefer
            if (players[i].getName().equalsIgnoreCase(playerName))
                continue;

            // look to see if player is has a "silentPerm", in which case Heimdall is silent to them
            boolean silentPerm = false;
            if (silentPerms != null) {
                for (String permission : silentPerms) {
                    if (perm.has(players[i], permission)) {
                        silentPerm = true;
                        break;
                    }
                }
            }

            if (!silentPerm) {
                players[i].sendMessage(bellow);
//				Debug.getInstance().debug("Heimdall \"possible grief\" bellow sent to player ",players[i]);
            }
        }
    }
}
