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
import com.andune.heimdall.util.General;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * @author andune
 */
public class GriefTeleport extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        Player commandPlayer = null;
        if (sender instanceof Player)
            commandPlayer = (Player) sender;

        // console doesn't make sense for this command
        if (commandPlayer == null)
            return false;

        String playerName = plugin.getLastGriefTrackingEngine().getLastGriefPlayerName();
        if (args.length > 0)
            playerName = args[0];

        if (playerName != null) {
            Location l = plugin.getLastGriefTrackingEngine().getLastGriefLocation(playerName);
            if (l != null) {
                sender.sendMessage("Teleporting to location " + General.getInstance().shortLocationString(l)
                        + " for last grief action by player " + playerName);
                General.getInstance().safeTeleport(commandPlayer, l, TeleportCause.COMMAND);
            }
            else
                sender.sendMessage("No griefLocation found for player " + playerName);
        }
        else {
            sender.sendMessage("No player found");
        }

        return true;
    }

}
