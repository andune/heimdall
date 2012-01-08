/**
 * 
 */
package org.morganm.heimdall.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.morganm.heimdall.command.BaseCommand;
import org.morganm.heimdall.util.General;

/**
 * @author morganm
 *
 */
public class GriefTeleport extends BaseCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player commandPlayer = null;
		if( sender instanceof Player )
			commandPlayer = (Player) sender;

		// console doesn't make sense for this command
		if( commandPlayer == null )
			return false;
		
		String playerName = plugin.getLastGriefTrackingEngine().getLastGriefPlayerName();
		if( args.length > 0 )
			playerName = args[0];
		
		if( playerName != null ) {
			Location l = plugin.getLastGriefTrackingEngine().getLastGriefLocation(playerName);
			if( l != null ) {
				sender.sendMessage("Teleporting to location "+General.getInstance().shortLocationString(l)
						+" for last grief action by player "+playerName);
				General.getInstance().safeTeleport(commandPlayer, l, TeleportCause.COMMAND);
			}
			else
				sender.sendMessage("No griefLocation found for player "+playerName);
		}
		else {
			sender.sendMessage("No player found");
		}
		
		return true;
	}

}
