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
package com.andune.heimdall.engine;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.Event;
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.player.FriendTracker;
import com.andune.heimdall.player.PlayerStateManager;
import com.andune.heimdall.util.Debug;
import com.andune.heimdall.util.General;
import com.andune.heimdall.util.PermissionSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author andune
 */
public class NotifyEngine extends AbstractEngine {
    private static final String DEFAULT_CONFIG_FILE = "engine/notify.yml";
    private static final int SECONDS_BETWEEN_NOTIFY = 10;

    private final Heimdall plugin;
    private final PlayerStateManager playerStateManager;
    private final PermissionSystem perms;
    private final Map<String, Float> lastNotifyValues = new HashMap<String, Float>(20);
    private final Map<String, Set<String>> notifyIgnores = new HashMap<String, Set<String>>();
    private final Map<String, Map<String, NotifyAntiFlood>> lastNotify = new HashMap<String, Map<String, NotifyAntiFlood>>(10);
    private final FileConfiguration config;
    private final EngineLog engineLog;
    private final FriendTracker friendTracker;

    public NotifyEngine(final Heimdall plugin, final String configFile) {
        if (configFile == null)
            throw new NullPointerException("configFile is null");

        this.plugin = plugin;
        this.playerStateManager = this.plugin.getPlayerStateManager();
        this.perms = this.plugin.getPermissionSystem();
        this.friendTracker = this.plugin.getFriendTracker();

        this.config = loadConfig(plugin, configFile, DEFAULT_CONFIG_FILE);
        this.engineLog = new EngineLog(plugin, new File("plugins/Heimdall/logs/notify.log"));
    }

    @Override
    public Event.Type[] getRegisteredEventTypes() {
        return new Event.Type[]{Event.Type.BLOCK_CHANGE, Event.Type.INVENTORY_CHANGE,
                Event.Type.HEIMDALL_FRIEND_EVENT, Event.Type.HEIMDALL_FRIEND_INVITE_SENT};
    }

    @Override
    public void processBlockChange(BlockChangeEvent event) {
        processEvent(event, event.griefValue, event.blockOwner, " (owner=", event.blockOwner, ", Material=", event.type, ")");
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("[Items: ");
        int baseLength = sb.length();
        for (int i = 0; i < event.diff.length; i++) {
            if (sb.length() > baseLength)
                sb.append(",");
            sb.append(event.diff[i].getType());
            sb.append(":");
            sb.append(event.diff[i].getAmount());
        }
        sb.append("]");

        processEvent(event, event.griefValue, event.blockOwner, " (owner=", event.blockOwner, ") ", sb.toString());
    }

    @Override
    public void processHeimdallFriendEvent(FriendEvent event) {
        final List<Player> notifyTargets = getOnlineNotifyTargets();
        if (notifyTargets != null && notifyTargets.size() > 0) {
            for (Player p : notifyTargets) {
                p.sendMessage(ChatColor.RED + "[Heimdall]" + ChatColor.WHITE
                        + " Player " + event.getPlayerName() + " has friended player " + event.getFriend());
            }
        }
    }

    @Override
    public void processHeimdallFriendInvite(FriendInviteEvent event) {
        final List<Player> notifyTargets = getOnlineNotifyTargets();
        if (notifyTargets != null && notifyTargets.size() > 0) {
            for (Player p : notifyTargets) {
                p.sendMessage(ChatColor.RED + "[Heimdall]" + ChatColor.WHITE
                        + " Sent automated friend invite to player " + event.getPlayerName() + " for player " + event.getInvitedFriend());
            }
        }
    }

    private void processEvent(final Event event, final float griefPoints, final String blockOwner, final Object... arg) {
        Float lastNotifyValue = lastNotifyValues.get(event.getPlayerName());
        if (lastNotifyValue == null)
            lastNotifyValue = Float.valueOf(0);
        lastNotifyValue = (float) Math.ceil(lastNotifyValue);
        float newValue = (float) Math.ceil(playerStateManager.getPlayerState(event.getPlayerName()).getGriefPoints());

        // if the number has gone up by at least a whole number, time to notify
        if (newValue > lastNotifyValue) {
            Debug.getInstance().debug("NotifyEngine:processEvent() newValue=", newValue, ", lastNotifyValue=", lastNotifyValue);

            if (blockOwner != null) {
                if (!friendTracker.isPosssibleFriend(blockOwner, event.getPlayerName()))
                    doNotify(event, griefPoints, arg);
                else
                    Debug.getInstance().debug("NotifyEngine: did not notify since ", event.getPlayerName(), " is possible friend of block owner ", blockOwner);
            }
            else
                Debug.getInstance().debug("NotifyEngine:processEvent() detected griefPoint change when owner is null");
        }

        // we update no matter what, because if the value went down, we want to capture
        // the new value so that any future griefing will be notified on as well
        lastNotifyValues.put(event.getPlayerName(), newValue);
    }

    /**
     * Called when an admin wants to ignore notifications from a given player.
     *
     * @param playerAdmin
     * @param playerIgnored
     */
    public void addNotifyIgnore(final String playerAdmin, final String playerIgnored) {
        Set<String> ignores = notifyIgnores.get(playerAdmin);
        if (ignores == null) {
            ignores = new HashSet<String>();
            notifyIgnores.put(playerAdmin, ignores);
        }
        ignores.add(playerIgnored);
    }

    /**
     * Called to clear a previously set admin ignore.
     *
     * @param playerAdmin
     * @param playerIgnored
     */
    public void removeNotifyIgnore(final String playerAdmin, final String playerIgnored) {
        Set<String> ignores = notifyIgnores.get(playerAdmin);
        if (ignores != null)
            ignores.remove(playerIgnored);
    }

    public Set<String> getNotifyIgnoreList(final String adminPlayer) {
        return notifyIgnores.get(adminPlayer);
    }

    private void doNotify(final Event event, final float griefPoints, final Object... arg) {
        final float totalGriefPoints = playerStateManager.getPlayerState(event.getPlayerName()).getGriefPoints();
        final float totalAntiGriefPoints = playerStateManager.getPlayerState(event.getPlayerName()).getAntiGriefPoints();
        final List<Player> notifyTargets = getOnlineNotifyTargets();

        if (notifyTargets != null && notifyTargets.size() > 0) {
            final StringBuilder sb = new StringBuilder(60);
            for (int i = 0; i < arg.length; i++) {
                sb.append(arg[i]);
            }
            final String additionalData = sb.toString();

            final String lowerCaseName = event.getPlayerName().toLowerCase();
            final StringBuilder targetsString = new StringBuilder(60);
            for (Player p : notifyTargets) {
                Set<String> ignores = notifyIgnores.get(p.getName());

                // notify if no ignores are set or if the player is not in the ignore list
                if (ignores == null || !ignores.contains(lowerCaseName)) {
                    Map<String, NotifyAntiFlood> map = lastNotify.get(p.getName());
                    if (map == null) {
                        map = new HashMap<String, NotifyAntiFlood>(5);
                        lastNotify.put(p.getName(), map);
                    }

                    NotifyAntiFlood naf = map.get(event.getPlayerName());
                    if (naf == null) {
                        naf = new NotifyAntiFlood();
                        map.put(event.getPlayerName(), naf);
                    }
                    // notification suppression; suppress notify events that are close in time, although
                    // if a single large grief event comes through, always send that one.
                    else if (griefPoints < 20 && System.currentTimeMillis() < naf.lastNotify + (SECONDS_BETWEEN_NOTIFY * 1000)) {
                        naf.griefPointsAccrued += griefPoints;    // tally grief points accrued while suppressed
                        naf.suppressedEventCount++;
                        Debug.getInstance().debug("suppressed notification to ", p, " about player ", event.getPlayerName());
                        break;    // skip this notify
                    }
                    // we're past SECONDS_BETWEEN_NOTIFY, lift suppression
                    else if (naf.suppressedEventCount > 0) {
                        long seconds = (System.currentTimeMillis() - naf.lastNotify) / 1000;

                        p.sendMessage(ChatColor.RED + "[Heimdall]" + ChatColor.WHITE
                                + " Player " + event.getPlayerName() + " has accumulated "
                                + naf.griefPointsAccrued + " since last notification " + seconds + " seconds ago."
                                + " (Heimdall suppressed " + naf.suppressedEventCount + " notification events as"
                                + " part of it's anti notify-flood system and send you this summary instead)");
                        Debug.getInstance().debug("notification suppression message sent to ", p, ", total suppressed events = ", naf.suppressedEventCount);
                    }

                    naf.lastNotify = System.currentTimeMillis();
                    naf.griefPointsAccrued = 0;
                    naf.suppressedEventCount = 0;

                    p.sendMessage(ChatColor.RED + "[Heimdall]" + ChatColor.WHITE
                            + " Player " + event.getPlayerName() + " has accumulated "
                            + totalGriefPoints + "/" + totalAntiGriefPoints
                            + " total grief/antigrief points. Latest action " + event.getEventTypeString()
                            + " at location {" + General.getInstance().shortLocationString(event.getLocation()) + "}"
                            + additionalData);

                    if (targetsString.length() > 0)
                        targetsString.append(",");
                    targetsString.append(p.getName());
                }
            }

            try {
                engineLog.log("[" + new Date() + "] Notified {" + targetsString.toString()
                        + "} of possible grief event by " + event.getPlayerName()
                        + ": " + event.getEventTypeString()
                        + ", loc={" + General.getInstance().shortLocationString(event.getLocation())
                        + "}, data=" + additionalData);
            } catch (Exception e) {
            }
        }
    }

    private List<Player> getOnlineNotifyTargets() {
        List<Player> notifyTargets = new ArrayList<Player>(5);

        String permission = config.getString("notifyPermission");
        if (permission != null) {
            Player[] players = Bukkit.getOnlinePlayers();
            for (int i = 0; i < players.length; i++) {
                if (perms.has(players[i].getName(), permission))
                    notifyTargets.add(players[i]);
            }
        }

        return notifyTargets;
    }

    /**
     * Class for keeping state data when supression notification floods.
     *
     * @author andune
     */
    private class NotifyAntiFlood {
        public long lastNotify;
        public float griefPointsAccrued = 0;
        public int suppressedEventCount = 0;
    }
}
