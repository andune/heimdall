/**
 * 
 */
package com.andune.heimdall.command;

import org.bukkit.command.CommandExecutor;

/**
 * @author andune
 *
 */
public interface Command extends CommandExecutor {
	public void setPlugin(com.andune.heimdall.Heimdall plugin);
}
