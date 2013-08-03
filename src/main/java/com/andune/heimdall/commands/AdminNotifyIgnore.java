/**
 * 
 */
package com.andune.heimdall.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.andune.heimdall.command.BaseCommand;

/**
 * @author andune
 *
 */
public class AdminNotifyIgnore extends BaseCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if( args.length < 1 ) {
			Set<String> ignores = plugin.getNotifyEngine().getNotifyIgnoreList(sender.getName());
			if( ignores != null ) {
				StringBuilder sb = new StringBuilder(80);
				for(String s : ignores) {
					if( sb.length() > 0 )
						sb.append(", ");
					sb.append(s);
				}
				sender.sendMessage("Current ignore list: "+sb.toString());
				
				return true;
			}
			
			// otherwise, just send usage and exit
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
