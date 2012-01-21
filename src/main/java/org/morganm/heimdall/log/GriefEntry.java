/**
 * 
 */
package org.morganm.heimdall.log;

import org.bukkit.Location;

/**
 * @author morganm
 *
 */
public class GriefEntry implements Comparable<GriefEntry> {
	public static enum Type {
		BLOCK_BREAK_NOT_OWNER,
		CHEST_ACCESS_NOT_OWNER,
		NEW_PLAYER,
		BANNED_PLAYER,
		UNBANNED_PLAYER,
		PLAYER_KICKED
	}
	
	private final Type activity;			// the activity type
	private final long time;				// the time the activity took place
	private final String playerName;		// the player performing the activity
	private final float griefPoints;		// the number of grief points assessed for this activity
	private final float totalGriefPoints;	// the total number of grief points the players now has as a result
	private final Location location;		// the location of the event (if any, can be null)
	private final String blockOwner;		// the owner of the location of the event (if any, can be null)
	private final String additionalData;	// any additional data
	
	/**
	 * 
	 * @param activity the activity type
	 * @param time the time the activity took place
	 * @param playerName the player performing the activity
	 * @param griefPoints the number of grief points assessed for this activity
	 * @param totalGriefPoints the total number of grief points the players now has as a result
	 * @param location the location of the event (if any, can be null)
	 * @param blockOwner the owner of the location of the event (if any, can be null)
	 */
	public GriefEntry(final Type activity, final long time, final String playerName, final float griefPoints,
			final float totalGriefPoints, final Location location, final String blockOwner,
			final String additionalData) {
		this.activity = activity;
		this.time = time;
		this.playerName = playerName;
		this.griefPoints = griefPoints;
		this.totalGriefPoints = totalGriefPoints;
		this.location = location;
		this.blockOwner = blockOwner;
		this.additionalData = additionalData;
	}
	
	public Type getActivity() {
		return activity;
	}
	
	public long getTime() {
		return time;
	}

	public String getPlayerName() {
		return playerName;
	}

	public float getGriefPoints() {
		return griefPoints;
	}

	public float getTotalGriefPoints() {
		return totalGriefPoints;
	}

	public Location getLocation() {
		return location;
	}

	public String getBlockOwner() {
		return blockOwner;
	}
	
	public String getAdditionalData() {
		return additionalData;
	}

	@Override
	public int compareTo(GriefEntry o) {
		if( time == o.time )
			return 0;
		else if( time < o.time )
			return -1;
		else
			return 1;
	}
}
