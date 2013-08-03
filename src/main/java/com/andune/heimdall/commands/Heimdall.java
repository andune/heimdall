/**
 *
 */
package com.andune.heimdall.commands;

import com.andune.heimdall.command.BaseCommand;
import com.andune.heimdall.command.YesNoCommand;
import com.andune.heimdall.util.Debug;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author andune
 */
public class Heimdall extends BaseCommand {

    /* (non-Javadoc)
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (args.length < 0) {
            return false;
        }

        if ("friend".equals(args[0])) {
            if (args.length < 3) {
                sender.sendMessage("Usage: /heimdall friend <player1> <player2>");
                return true;
            }

            // friend both sides of the relationship
            plugin.getFriendTracker().addFriend(args[1], args[2]);
            plugin.getFriendTracker().addFriend(args[2], args[1]);
            sender.sendMessage(args[1] + " and " + args[2] + " are now friends.");
            return true;
        }
        else if ("debug".equals(args[0]) && args.length > 1) {
            if ("on".equals(args[1])) {
                Debug.getInstance().resetConsoleToINFO();
                Debug.getInstance().setDebug(true);
                sender.sendMessage("Debugging enabled");
                return true;
            }
            else if ("off".equals(args[1])) {
                Debug.getInstance().setDebug(false);
                Debug.getInstance().resetConsoleToINFO();
                sender.sendMessage("Debugging disabled");
                return true;
            }
        }
        else if (args[0].startsWith("track") && args.length > 1) {
            if ("reset".equals(args[1])) {
                plugin.getPlayerStateManager().getPlayerTracker().reset();
                sender.sendMessage("Player tracker reset");
                return true;
            }
        }
        else if ("saveall".equals(args[0])) {
            try {
                plugin.getPlayerStateManager().save();
                sender.sendMessage("Forced save-all");
            } catch (Exception e) {
                sender.sendMessage("Error with save-all: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        else if ("df".equals(args[0])) {        // dump friends
            sender.sendMessage("Dumping friend map");
            sender.sendMessage(plugin.getFriendTracker().dumpFriendsMap());
            return true;
        }
        else if ("tyn".equals(args[0])) {        // testing
            YesNoCommand.getInstance().registerCallback(sender.getName(),
                    new CommandExecutor() {
                        @Override
                        public boolean onCommand(CommandSender sender, Command arg1, String arg2,
                                                 String[] arg3) {
                            if (arg2.startsWith("/yes")) {
                                sender.sendMessage("Yes command received");
                            }
                            else if (arg2.startsWith("/no")) {
                                sender.sendMessage("No command received");
                            }
                            else {
                                sender.sendMessage("ERROR, not supposed to happen: " + arg2);
                            }
                            return true;
                        }
                    }, 15);
            sender.sendMessage("Yes command test started, send /yes within 15 seconds");
            return true;
        }

        return true;
    }

}
