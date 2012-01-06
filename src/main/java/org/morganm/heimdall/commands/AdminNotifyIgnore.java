/**
 * 
 */
package org.morganm.heimdall.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.morganm.heimdall.command.BaseCommand;

/**
 * @author morganm
 *
 */
public class AdminNotifyIgnore extends BaseCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if( args.length < 1 ) {
			sender.sendMessage(command.getUsage());
			return true;
		}
		if( sender instanceof ConsoleCommandSender ) {
			sender.sendMessage("This command cannot be run as Console");
			return true;
		}
		
		plugin.getNotifyEngine().addNotifyIgnore(sender.getName(), args[0]);
		sender.sendMessage("Heimdall alerts related to player "+args[0]+" are now ignored.");
		return true;
	}

}
