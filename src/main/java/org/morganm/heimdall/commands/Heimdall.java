/**
 * 
 */
package org.morganm.heimdall.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.morganm.heimdall.command.BaseCommand;
import org.morganm.heimdall.util.Debug;

/**
 * @author morganm
 *
 */
public class Heimdall extends BaseCommand {

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if( args.length < 0 ) {
			return false;
		}
		
		if( "debug".equals(args[0]) && args.length > 1 ) {
			if( "on".equals(args[1]) ) {
				Debug.getInstance().setDebug(true);
				sender.sendMessage("Debugging enabled");
				return true;
			}
			else if( "off".equals(args[1]) ) {
				Debug.getInstance().setDebug(false);
				Debug.getInstance().resetConsoleToINFO();
				sender.sendMessage("Debugging disabled");
				return true;
			}
		}
		// TODO Auto-generated method stub
		return false;
	}

}
