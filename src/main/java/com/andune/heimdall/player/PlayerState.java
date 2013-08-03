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
package com.andune.heimdall.player;

import com.andune.heimdall.log.GriefLog;

/**
 * @author andune
 */
public interface PlayerState {

    /**
     * Return the name of the player represented by this objec.t
     *
     * @return
     */
    public String getName();

    /**
     * Increment the grief count, should return the new value.
     *
     * @param f the mount to increment by (positive or negative)
     * @return new grief point total
     */
    public float incrementGriefPoints(float f, String owner);

    /**
     * Return the total grief points for this player.
     *
     * @return
     */
    public float getGriefPoints();

    /**
     * Return the total antiGrief points for this player.
     *
     * @return
     */
    public float getAntiGriefPoints();

    /**
     * Return true if this player is exempt from grief checks.
     *
     * @return
     */
    public boolean isExemptFromChecks();

    /**
     * Return true if this player is a friend of the other player.
     *
     * @param p
     * @return
     */
    public boolean isFriend(PlayerState p);

    /**
     * Return the number of points accumulated "against" a given player, such as
     * by destroying their blocks or stealing from their chests. Does not take into
     * account the "friend" status of the players.
     *
     * @param p
     * @return
     */
    public float getPointsByOwner(PlayerState p);

    /**
     * When one player friends another, this method can be used to accumulate any
     * griefPoints that were accumulated against that player prior to the friendship.
     * The expected behavior is that this should clear the griefPointsByOwner
     * against that player.
     *
     * @param p
     */
    public void clearPointsByOwner(PlayerState p);

    /**
     * Get the GriefLog object for this player.
     *
     * @return the GriefLog object, or null if not implemented
     */
    public GriefLog getGriefLog();

    /**
     * Save and close out this PlayerState object (close all open file handles).
     */
    public void close() throws Exception;

    /**
     * Save this PlayerState to backing store (up to implementation).
     */
    public void save() throws Exception;

    /**
     * Restore this PlayerState from backing store (up to implementation).  Assumption
     * is that the implementation is given the name (as returned by getName()) and
     * must load the rest of the elements.
     */
    public void load() throws Exception;
}
