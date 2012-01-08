/**
 * 
 */
package org.morganm.heimdall.commands;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.morganm.heimdall.command.BaseCommand;
import org.morganm.heimdall.log.GriefEntry;
import org.morganm.heimdall.player.PlayerState;
import org.morganm.heimdall.player.PlayerStateManager;
import org.morganm.heimdall.util.General;

/**
 * @author morganm
 *
 */
public class GriefLog extends BaseCommand {
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if( args.length < 1 ) {
			sender.sendMessage(command.getUsage());
			return true;
		}
		
		PlayerStateManager playerStateManager = plugin.getPlayerStateManager();
		PlayerState ps = playerStateManager.getPlayerState(args[0]);
		org.morganm.heimdall.log.GriefLog griefLog = ps.getGriefLog();
		
		GriefEntry[] entries = null;
		try {
			entries = griefLog.getLastNEntries(5);
		}
		catch(IOException e) {
			sender.sendMessage("Error retrieving grief entries for player "+args[0]+", check sytem log");
			e.printStackTrace();
		}
		
		if( entries != null && entries.length > 0 ) {
			sender.sendMessage("Last 5 grief log entries for player "+args[0]+":");
			for(int i=0; i < entries.length; i++) {
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
			sender.sendMessage("Total grief points: "+ps.getGriefPoints());
		}
		else
			sender.sendMessage("No grief log found for player "+args[0]);
		
		return true;
	}

}
