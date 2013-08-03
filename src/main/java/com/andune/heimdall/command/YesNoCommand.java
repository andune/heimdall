/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 *
 */
package com.andune.heimdall.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;

/**
 * So as to not fight with other plugins that might use a /yes and /no response,
 * we implement our yes/no responses as pre-command hook. We keep track of when
 * we ask a question and look for a /yes or /no response within the time window of
 * asking. We don't otherwise snarf /yes commands, which lets them pass through
 * to other plugins that might be doing the same.
 *
 * @author andune
 */
public class YesNoCommand implements Listener {
    private static final String[] emptyStringArray = new String[]{};
    private static YesNoCommand instance;
    private final HashMap<String, CallbackEntity> yesCallBacks = new HashMap<String, CallbackEntity>(10);

    private YesNoCommand() {
    }

    static public YesNoCommand getInstance() {
        if (instance == null) {
            synchronized (YesNoCommand.class) {
                if (instance == null)
                    instance = new YesNoCommand();
            }
        }

        return instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CallbackEntity callbackEntity = yesCallBacks.get(event.getPlayer().getName());
        if (callbackEntity != null) {
            if (event.getMessage().startsWith("/yes") || event.getMessage().startsWith("/no")) {
                if (System.currentTimeMillis() <= callbackEntity.invalidTime) {
                    // if true, that means we processed the /yes command, so stop any further processing
                    if (callbackEntity.executor.onCommand(event.getPlayer(), null, event.getMessage(), emptyStringArray)) {
                        event.setCancelled(true);
                    }
                }
                yesCallBacks.remove(event.getPlayer().getName());
                callbackEntity = null;
            }

            // cleanup if time has expired
            if (callbackEntity != null && System.currentTimeMillis() > callbackEntity.invalidTime) {
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
        yesCallBacks.put(playerName, new CallbackEntity(executor, System.currentTimeMillis() + (timeout_seconds * 1000)));
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
