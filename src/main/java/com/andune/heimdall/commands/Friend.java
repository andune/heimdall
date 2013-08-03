/**
 *
 */
package com.andune.heimdall.commands;

import com.andune.heimdall.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author andune
 */
public class Friend extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        if (plugin.getFriendTracker().isFriend(sender.getName(), args[0])) {
            sender.sendMessage(args[0] + " is already your friend.");
        }
        else {
            Player p = Bukkit.getPlayer(args[0]);
            if (p != null) {
                plugin.getFriendTracker().addFriend(sender.getName(), p.getName());
                sender.sendMessage(p.getName() + " added as your friend.");
            }
            else
                sender.sendMessage("Player " + args[0] + " not found.");
        }

        return true;
    }

}
