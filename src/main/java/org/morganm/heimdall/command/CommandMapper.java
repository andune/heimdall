/**
 * 
 */
package org.morganm.heimdall.command;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.Heimdall;

/**
 * @author morganm
 *
 */
public class CommandMapper {
	private final Logger log;
	private final String logPrefix;
	private final Heimdall plugin;
	private final Map<String, CommandExecutor> commandExecutorMap = new HashMap<String, CommandExecutor>();
	
	public CommandMapper(final Heimdall plugin) {
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.logPrefix = plugin.getLogPrefix();
	}
	
	public void mapCommands() {
		parseCommands();
		
		for(Entry<String, CommandExecutor> entry : commandExecutorMap.entrySet()) {
			final PluginCommand pc = ((JavaPlugin) plugin).getCommand(entry.getKey());
			pc.setExecutor(entry.getValue());
		}
		
		/*
		List<Command> commands = PluginCommandYamlParser.parse(plugin);
		for(Command cmd : commands) {
			// we make this assumption b/c it's true, and if it's never not true, then
			// we want to blow up spectacularly with an exception
			final PluginCommand pc = (PluginCommand) cmd;
			
			final String cmdName = cmd.getName();
		}
		*/
	}
	
	private void parseCommands() {
        Object object = plugin.getDescription().getCommands();
        if (object == null) {
            return;
        }

        @SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;

        if (map != null) {
            for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
            	String commandName = entry.getKey();
                Object className = entry.getValue().get("executor");
                
                if( className != null && className instanceof String ) {
                	try {
	                	Class<?> clazz = Class.forName((String) className, true, plugin.getClassLoaderPublic());
	                    Class<? extends Command> commandClass = clazz.asSubclass(Command.class);
	                    Constructor<? extends Command> constructor = commandClass.getConstructor();
	
	                    Command command = constructor.newInstance();
	                    commandExecutorMap.put(commandName, command);
	                    command.setPlugin(plugin);
                	}
                	catch(Throwable ex) {
                		log.warning(logPrefix+"Error loading command "+commandName+": "+ex.getMessage());
                		ex.printStackTrace();
                	}
                }
            }
        }
	}
}
