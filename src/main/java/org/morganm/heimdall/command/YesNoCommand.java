/**
 * 
 */
package org.morganm.heimdall.command;

import java.util.HashMap;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/** So as to not fight with other plugins that might use a /yes and /no response,
 * we implement our yes/no responses as pre-command hook. We keep track of when
 * we ask a question and look for a /yes or /no response within the time window of
 * asking. We don't otherwise snarf /yes commands, which lets them pass through
 * to other plugins that might be doing the same.
 * 
 * @author morganm
 *
 */
public class YesNoCommand implements Listener {
	private static final String[] emptyStringArray = new String[] {};
	private static YesNoCommand instance;
	private final HashMap<String, CallbackEntity> yesCallBacks = new HashMap<String, CallbackEntity>(10);
	
	private YesNoCommand() {}
	static public YesNoCommand getInstance() {
		if( instance == null ) {
			synchronized(YesNoCommand.class) {
				if( instance == null )
					instance = new YesNoCommand();
			}
		}
		
		return instance;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		CallbackEntity callbackEntity = yesCallBacks.get(event.getPlayer().getName());
		if( callbackEntity != null ) {
			if( event.getMessage().startsWith("/yes") || event.getMessage().startsWith("/no") ) {
				if( System.currentTimeMillis() <= callbackEntity.invalidTime ) {
					// if true, that means we processed the /yes command, so stop any further processing 
					if( callbackEntity.executor.onCommand(event.getPlayer(), null, event.getMessage(), emptyStringArray) ) {
						event.setCancelled(true);
					}
				}
				yesCallBacks.remove(event.getPlayer().getName());
				callbackEntity = null;
			}

			// cleanup if time has expired
			if( callbackEntity != null && System.currentTimeMillis() > callbackEntity.invalidTime ) {
				yesCallBacks.remove(event.getPlayer().getName());
			}
		}
	}

	/* new-style, when it's ready for primetime
	@EventHandler(priority=EventPriority.HIGHEST)
	public void playerPreCommand(final PlayerCommandPreprocessEvent event) {
	}
	*/
	
	public void registerCallback(final String playerName, final CommandExecutor executor, final int timeout_seconds) {
		yesCallBacks.put(playerName, new CallbackEntity(executor, System.currentTimeMillis()+(timeout_seconds*1000)));
	}
	
	private class CallbackEntity {
		final public CommandExecutor executor;
		final public long invalidTime;
		
		public CallbackEntity(final CommandExecutor executor, final long invalidTime) {
			this.executor = executor;
			this.invalidTime = invalidTime;
		}
	}
}
