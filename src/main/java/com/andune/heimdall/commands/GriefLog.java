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
package com.andune.heimdall.commands;

import com.andune.heimdall.command.BaseCommand;
import com.andune.heimdall.log.GriefEntry;
import com.andune.heimdall.player.PlayerState;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.General;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author andune
 */
public class GriefLog extends BaseCommand {
    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        PlayerStateManager playerStateManager = plugin.getPlayerStateManager();
        PlayerState ps = playerStateManager.getPlayerState(args[0]);
        com.andune.heimdall.log.GriefLog griefLog = ps.getGriefLog();

        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number: " + args[1]);
                return true;
            }
        }

        GriefEntry[] entries = null;
        try {
            entries = griefLog.getLastNEntries(page * 5);
        } catch (IOException e) {
            sender.sendMessage("Error retrieving grief entries for player " + args[0] + ", check system log");
            e.printStackTrace();
        }

        if (entries != null && entries.length > 0) {
            if (page < 2)
                sender.sendMessage("Last 5 grief log entries for player " + args[0] + ":");
            else {
                int bottom = (page - 1) * 5;
                int top = page * 5;
                if (entries.length < (page * 5)) {
                    page = (entries.length / 5) + 1;
                    top = entries.length;
                    bottom = top - 5;
                }
                sender.sendMessage("Last " + bottom + " to " + top + " (page " + page + ") grief log entries for player " + args[0] + ":");
            }

            for (int i = 0; i < 5; i++) {
                StringBuilder sb = new StringBuilder(80);
                sb.append("[");
                sb.append(dateFormat.format(new Date(entries[i].getTime())));
                sb.append("] ");
                sb.append(entries[i].getActivity().toString());
                sb.append(": l=");
                sb.append(General.getInstance().shortLocationString(entries[i].getLocation()));
                sb.append(", gp=");
                sb.append(entries[i].getGriefPoints());
                sb.append(", tgp=");
                sb.append(entries[i].getTotalGriefPoints());
                sb.append(", owner=");
                sb.append(entries[i].getBlockOwner());
                sb.append(", ");
                sb.append(entries[i].getAdditionalData());

                sender.sendMessage(sb.toString());
            }
            sender.sendMessage("Total grief points: " + ps.getGriefPoints());
        }
        else
            sender.sendMessage("No grief log found for player " + args[0]);

        return true;
    }

}
