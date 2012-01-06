/**
 * 
 */
package org.morganm.heimdall.event.handlers;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.heimdall.engine.Engine;
import org.morganm.heimdall.event.BlockChangeEvent;
import org.morganm.heimdall.event.EventHandler;
import org.morganm.heimdall.event.InventoryChangeEvent;

/**
 * @author morganm
 *
 */
public class HeimdallEventHandler extends EventHandler {
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final Engine processEngine;
	private final List<Engine> actionEngines;
	
	public HeimdallEventHandler(final JavaPlugin plugin, final Engine engine,
			final List<Engine> actionEngines) {
		this.plugin = plugin;
		this.processEngine = engine;
		this.actionEngines = actionEngines;
	}
	
	@Override
	public void processEvent(final BlockChangeEvent event) {
		processEngine.processBlockChange(event);
		
		if( event.griefValue != 0 && actionEngines != null )
			for(Engine e : actionEngines)
				e.processBlockChange(event);
	}
	
	@Override
	public void processEvent(final InventoryChangeEvent event) {
		processEngine.processInventoryChange(event);
		
		if( event.griefValue != 0 && actionEngines != null )
			for(Engine e : actionEngines)
				e.processInventoryChange(event);
	}
}
