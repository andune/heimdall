/**
 * 
 */
package org.morganm.heimdall.command;

import org.bukkit.command.CommandExecutor;

/**
 * @author morganm
 *
 */
public interface Command extends CommandExecutor {
	public void setPlugin(org.morganm.heimdall.Heimdall plugin);
}
