/**
 *
 */
package com.andune.heimdall.command;


/**
 * @author andune
 */
public abstract class BaseCommand implements Command {
    protected com.andune.heimdall.Heimdall plugin;

    public void setPlugin(com.andune.heimdall.Heimdall plugin) {
        this.plugin = plugin;
    }
}
