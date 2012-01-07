/**
 * 
 */
package org.morganm.heimdall.command;



/**
 * @author morganm
 *
 */
public abstract class BaseCommand implements Command {
	protected org.morganm.heimdall.Heimdall plugin;

	public void setPlugin(org.morganm.heimdall.Heimdall plugin) {
		this.plugin = plugin;
	}
}
