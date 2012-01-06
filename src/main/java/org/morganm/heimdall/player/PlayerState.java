/**
 * 
 */
package org.morganm.heimdall.player;

/**
 * @author morganm
 *
 */
public interface PlayerState {
	
	/** Return the name of the player represented by this objec.t
	 * 
	 * @return
	 */
	public String getName();
	
	/** Increment the grief count, should return the new value.
	 * 
	 * @param f the mount to increment by (positive or negative)
	 * @return new grief point total
	 */
	public float incrementGriefPoints(float f);
	/** Return the total grief points for this player.
	 * 
	 * @return
	 */
	public float getGriefPoints();
	/** Return true if this player is exempt from grief checks.
	 * 
	 * @return
	 */
	public boolean isExemptFromChecks();
	/** Return true if this player is a friend of the other player.
	 * 
	 * @param p
	 * @return
	 */
	public boolean isFriend(PlayerState p);
	/** Return the number of points accumulated "against" a given player, such as
	 * by destroying their blocks or stealing from their chests. Does not take into
	 * account the "friend" status of the players.
	 * 
	 * @param p
	 * @return
	 */
	public float getPointsByOwner(PlayerState p);
}
