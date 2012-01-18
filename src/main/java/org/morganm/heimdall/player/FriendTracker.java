/**
 * 
 */
package org.morganm.heimdall.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.HeimdallException;

/** Class which keeps track of the state of player friendships.
 * 
 * @author morganm
 *
 */
public class FriendTracker {
	private final static String newLine = System.getProperty("line.separator");
	
	private final Heimdall plugin;
//	private final HashMap<FriendRelationship, Float> friendPoints = new HashMap<FriendRelationship, Float>(100);
	
	/* Hash that keeps track of all relationships a person is in.
	 */
	private final HashMap<String, Set<FriendRelationship>> allRelationships = new HashMap<String, Set<FriendRelationship>>(100);
	
	public FriendTracker(final Heimdall plugin) {
		this.plugin = plugin;
	}
	
	/** Lookup the status of whether or not player1 and player2 are thought to be
	 * friends based on "fuzzy data" of their interactions on the server.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public boolean isPosssibleFriend(String player1, String player2) {
		FriendRelationship fr = getRelationship(player1, player2);

		// TODO: tweak this until it makes sense
		if( fr.points[0] > 50 && fr.points[1] > 50 ) {
			return true;
		}
		else
			return false;
	}
	
	/** Return true if we know for sure (or beyond a reasonable doubt) that player1
	 * and player2 are friends.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public boolean isFriend(String player1, String player2) {
		return false;
	}
	
	/** Add "friend points" between two players.
	 * 
	 * @param actor the player the points are added/subtracted from
	 * @param friend the "recieving" player of the points. That is, the actor is assumed to have acted against this person
	 * in either a positive or negative manner.
	 * @param points the total points to be added/subtracted
	 */
	public void addFriendPoints(String actor, String friend, float points) {
		FriendRelationship fr = getRelationship(actor, friend);

		if( fr == null ) {
			try {
				fr = newRelationship(actor, friend);
			}
			// exception should be impossible since we just called getRelationship() immediately prior,
			// thus confirming there is no existing relationship.
			catch(HeimdallException e) { e.printStackTrace(); }
		}
		
		// add points to whichever slot (0 or 1) the "actor" is in
		if( actor.equals(fr.players[0]) )
			fr.points[0] += points;
		else
			fr.points[1] += points;
	}
	
	/** Return all relationships that a person is part of (if any).
	 * 
	 * @param player
	 * @return
	 */
	private Set<FriendRelationship> getRelationships(final String player) {
		return allRelationships.get(player);
	}
	
	/** Return any existing relationship between two players (no new relationship will
	 * be created if one doesn't already exist).
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	private FriendRelationship getRelationship(final String player1, final String player2) {
		Set<FriendRelationship> relationships = getRelationships(player1);
		if( relationships == null )
			return null;

		for(FriendRelationship fr : relationships) {
			for(int i=0; i < fr.players.length; i++) {
				if( player2.equals(fr.players[i]) ) {
					return fr;
				}
			}
		}
		
		return null;
	}
	
	private void addRelationship(final FriendRelationship relationship) {
		for(int i=0; i < relationship.players.length; i++) {
			Set<FriendRelationship> playerRelationships = allRelationships.get(relationship.players[i]);
			if( playerRelationships == null ) {
				playerRelationships = new HashSet<FriendRelationship>();
				allRelationships.put(relationship.players[i], playerRelationships);
			}
			
			playerRelationships.add(relationship);
		}
	}
	
	public String dumpFriendsMap() {
		StringBuilder sb = new StringBuilder(100);
		for(Map.Entry<String, Set<FriendRelationship>> entry : allRelationships.entrySet()) {
			sb.append("Player ");
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(newLine);
			for(FriendRelationship fr : entry.getValue()) {
				sb.append("  Friend: ");
				if( fr.players[0].equals(entry.getKey()) ) {
					sb.append(fr.players[1]);
					sb.append(", friendPoints=");
					sb.append(fr.points[0]);	// how many points I have with/against that player
				}
				else {
					sb.append(fr.players[0]);
					sb.append(", friendPoints=");
					sb.append(fr.points[1]);	// how many points I have with/against that player
				}
				sb.append(newLine);
			}
		}
		
		return sb.toString();
	}
	
	/** Create a new relationship between two players.
	 * 
	 * @param player1
	 * @param player2
	 */
	private FriendRelationship newRelationship(final String player1, final String player2) throws HeimdallException {
		if( getRelationship(player1, player2) != null )
			throw new HeimdallException("newRelationship called when relationship already exists");
		
		FriendRelationship fr = new FriendRelationship();
		fr.players[0] = player1;
		fr.players[1] = player2;
		fr.points[0] = fr.points[1] = 0;
		addRelationship(fr);
		
		return fr;
	}
	
	private class FriendRelationship {
		public String[] players = new String[2];
		public float[] points = new float[2];
	}
}
