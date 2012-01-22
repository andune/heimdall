/**
 * 
 */
package org.morganm.heimdall.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.morganm.heimdall.Heimdall;
import org.morganm.heimdall.command.YesNoCommand;
import org.morganm.heimdall.event.FriendEvent;
import org.morganm.heimdall.event.FriendInviteEvent;
import org.morganm.heimdall.util.Debug;

/** Class which keeps track of the state of player friendships.
 * 
 * @author morganm
 *
 */
public class FriendTracker {
	private final static String newLine = System.getProperty("line.separator");
	
	@SuppressWarnings("unused")
	private final Heimdall plugin;
	private final Debug debug;
//	private final HashMap<FriendRelationship, Float> friendPoints = new HashMap<FriendRelationship, Float>(100);
	
	/* Hash that keeps track of all relationships a person is in.
	 */
	private final HashMap<String, Set<FriendRelationship>> allRelationships = new HashMap<String, Set<FriendRelationship>>(100);
	private final HashMap<String, HashMap<FriendRelationship, Integer>> invitePoints = new HashMap<String, HashMap<FriendRelationship, Integer>>(10);
	
	public FriendTracker(final Heimdall plugin) {
		this.plugin = plugin;
		this.debug = Debug.getInstance();
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
		if( fr.points[0] > 30 && fr.points[1] > 30 ) {
			return true;
		}
		else
			return false;
	}
	
	/** Return true if we know that player1 has declared player2 as a friend.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public boolean isFriend(String player1, String player2) {
		FriendRelationship fr = getRelationship(player1, player2);
		if( fr.players[0].equals(player1) )
			return fr.isConfirmedFriend[0];
		else
			return fr.isConfirmedFriend[1];
	}
	
	/** Return all possible friends a player has, this includes both confirmed friends
	 * as well as "possible" friends (that Heimdall auto-detected).
	 * 
	 * @param player
	 * @return guaranteed not to be null
	 */
	public Set<String> getAllPossibleFriends(final String player) {
		Set<String> friends = new HashSet<String>(5);
		
		Set<FriendRelationship> relationships = getRelationships(player);
		if( relationships != null ) {
			for(FriendRelationship fr : relationships) {
				// add the player that isn't us
				if( fr.players[0].equalsIgnoreCase(player) )
					friends.add(fr.players[1]);
				else
					friends.add(fr.players[0]);
			}
		}
		
		return friends;
	}
	
	/** Return all friend points that player1 has accumulated with player2.
	 * 
	 * @param player1
	 * @param player2
	 * @return
	 */
	public float getFriendPoints(final String player1, final String player2) {
		FriendRelationship fr = getRelationship(player1, player2);
		
		if( fr.players[0].equals(player1) )
			return fr.points[0];
		else
			return fr.points[1];
	}

	/** Return known friends of a given player: only friends that have been confirmed
	 * are returned (ie. this excludes just "possible" friends).
	 * 
	 * @param player
	 * @return guaranteed not to be null
	 */
	public List<String> getFriends(final String player) {
		List<String> friends = new ArrayList<String>(5);
		
		Set<FriendRelationship> relationships = getRelationships(player);
		if( relationships != null ) {
			for(FriendRelationship fr : relationships) {
				// add the player that isn't us
				if( fr.players[0].equalsIgnoreCase(player) ) {
					if( fr.isConfirmedFriend[0] )
						friends.add(fr.players[1]);
				}
				else {
					if( fr.isConfirmedFriend[1] )
						friends.add(fr.players[0]);
				}
			}
		}
		
		return friends;
	}
	
	/** Return known NOT friends of a given player. A player can explicitly tell us that
	 * another player is NOT their friend, this returns that list.
	 * 
	 * @param player
	 * @return guaranteed not to be null
	 */
	public List<String> getNotFriends(final String player) {
		List<String> notFriends = new ArrayList<String>(5);
		
		Set<FriendRelationship> relationships = getRelationships(player);
		if( relationships != null ) {
			for(FriendRelationship fr : relationships) {
				// add the player that isn't us
				if( fr.players[0].equalsIgnoreCase(player) ) {
					if( fr.isConfirmedNotFriend[0] )
						notFriends.add(fr.players[1]);
				}
				else {
					if( fr.isConfirmedNotFriend[1] )
						notFriends.add(fr.players[0]);
				}
			}
		}
		
		return notFriends;
	}
	
	/** Send a friend invite from one player to the other. This asks "friend" if they want to
	 * be friends with "actor". Friendship is not necessarily mutual.
	 * 
	 * @param actor
	 * @param friend
	 */
	public void sendFriendInvite(final String actor, final String friend) {
		Player friendPlayer = Bukkit.getPlayer(friend);
		if( friendPlayer != null ) {
			friendPlayer.sendMessage(actor+" and you appear to be working together. Please type /yes in the next 15 seconds to confirm you are working together (this is an anti-grief measure).");
			YesNoCommand.getInstance().registerCallback(friend,
					new CommandExecutor() {
						@Override
						public boolean onCommand(CommandSender arg0, Command arg1, String arg2,	String[] arg3) {
							if( arg2.startsWith("/yes") ) {
								FriendRelationship fr = getRelationship(actor, friend);
								if( fr.players[0].equals(friend) ) {
									fr.isConfirmedFriend[0] = true;
									fr.isConfirmedNotFriend[0] = false;
								}
								else {
									fr.isConfirmedFriend[1] = true;
									fr.isConfirmedNotFriend[1] = false;
								}
								arg0.sendMessage("Thank you, "+actor+" has been confirmed as your friend.");
								FriendEvent event = new FriendEvent(friend, actor);
								plugin.getEventManager().pushEvent(event);
								debug.debug("FriendTracker: actor ",actor," confirmed as friend of ",friend);
							}
							else if( arg2.startsWith("/no") ) {
								FriendRelationship fr = getRelationship(actor, friend);
								if( fr.players[0].equals(friend) ) {
									fr.isConfirmedNotFriend[0] = true;
									fr.isConfirmedFriend[0] = false;
								}
								else {
									fr.isConfirmedNotFriend[1] = true;
									fr.isConfirmedFriend[1] = false;
								}
								arg0.sendMessage("OK, "+actor+" is confirmed as NOT being your friend. Type \"/friend "+actor+"\" if you change your mind.");
								debug.debug("FriendTracker: actor ",actor," DENIED as friend of ",friend);
							}
							// TODO Auto-generated method stub
							return true;
						}
					}, 15);
			
			FriendInviteEvent event = new FriendInviteEvent(friend, actor);
			plugin.getEventManager().pushEvent(event);
		}
	}
	
	/** Add "friend points" between two players.
	 * 
	 * @param actor the player the points are added/subtracted from
	 * @param friend the "receiving" player of the points. That is, the actor is assumed to have acted against this person
	 * in either a positive or negative manner.
	 * @param points the total points to be added/subtracted
	 * @return the total friends points the actor has now accumulated with friend
	 */
	public float addFriendPoints(String actor, String friend, float points) {
		FriendRelationship fr = getRelationship(actor, friend);
		boolean explicitNoFriend = false;

		float newPoints = 0;
		
		debug.debug("FriendTracker::addFriendPoints: actor=",actor,", friend=",friend,", points=",points);
		
		// add points to whichever slot (0 or 1) the "actor" is in
		if( actor.equals(fr.players[0]) ) {
			fr.points[0] += points;
			newPoints = fr.points[0];
			explicitNoFriend = fr.isConfirmedNotFriend[1];	// other player explicitly said no
		}
		else {
			fr.points[1] += points;
			newPoints = fr.points[1];
			explicitNoFriend = fr.isConfirmedNotFriend[0];	// other player explicitly said no
		}

		if( !explicitNoFriend ) {
			int lastInvite = 0;
			HashMap<FriendRelationship, Integer> map = invitePoints.get(friend);
			if( map == null ) {
				map = new HashMap<FriendRelationship, Integer>();
				invitePoints.put(friend, map);
			}
			Integer i = map.get(fr);
			if( i != null )
				lastInvite = i.intValue();
			
			debug.debug("FriendTracker::addFriendPoints: lastInvite=",lastInvite);
			if( newPoints > 50 && lastInvite < 50 ) {
				debug.debug("FriendTracker::addFriendPoints: sending first invite");
				sendFriendInvite(actor, friend);
				map.put(fr, 50);
			}
			else if( newPoints > 100 && lastInvite < 100 ) {
				debug.debug("FriendTracker::addFriendPoints: sending second invite");
				sendFriendInvite(actor, friend);
				map.put(fr, 100);
			}
			else if( newPoints > 200 && lastInvite < 200 ) {
				debug.debug("FriendTracker::addFriendPoints: sending third invite");
				sendFriendInvite(actor, friend);
				map.put(fr, 200);
			}
			// we try up to 3 times to "link up" friends, after that, we give up
		}
		else
			debug.debug("FriendTracker::addFriendPoints: explicitNoFriend is set, no action taken");
		
		return newPoints;
	}
	
	/** Add player2 as a friend of player1; will fire a FRIEND event.
	 * 
	 * @param player1
	 * @param player2
	 */
	public void addFriend(final String player1, final String player2) {
		setFriend(player1, player2);
		FriendEvent event = new FriendEvent(player1, player2);
		plugin.getEventManager().pushEvent(event);
	}
	
	/** Add player2 as an explicit friend of player1.
	 * Package visibility.
	 * 
	 * @param player1
	 * @param player2
	 */
	void setFriend(final String player1, final String player2) {
		FriendRelationship fr = getRelationship(player1, player2);
		if( fr.players[0].equals(player1) ) {
			fr.isConfirmedFriend[0] = true;
			fr.isConfirmedNotFriend[0] = false;
		}
		else {
			fr.isConfirmedFriend[1] = true;
			fr.isConfirmedNotFriend[1] = false;
		}
	}
	
	/** Add player2 as an explicit notFriend of player1.
	 * Package visibility.
	 * 
	 * @param player1
	 * @param player2
	 */
	void setNotFriend(final String player1, final String player2) {
		FriendRelationship fr = getRelationship(player1, player2);
		if( fr.players[0].equals(player1) ) {
			fr.isConfirmedNotFriend[0] = true;
			fr.isConfirmedFriend[0] = false;
		}
		else {
			fr.isConfirmedNotFriend[1] = true;
			fr.isConfirmedFriend[1] = false;
		}
	}

	/** Used to set friend points on load. No action is taken on the data.
	 * Package visibility.
	 * 
	 * @param actor
	 * @param friend
	 * @param points
	 */
	void setFriendPoints(final String actor, final String friend, final float points) {
		FriendRelationship fr = getRelationship(actor, friend);
		if( fr.players[0].equals(actor) )
			fr.points[0] = points;
		else 
			fr.points[1] = points;
	}

	/** Return all relationships that a person is part of (if any).
	 * 
	 * @param player
	 * @return all relationships for the given player, could be null
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
			return newRelationship(player1, player2);

		for(FriendRelationship fr : relationships) {
			for(int i=0; i < fr.players.length; i++) {
				if( player2.equals(fr.players[i]) ) {
					return fr;
				}
			}
		}
		
		// if we get here, no relationship exists yet
		return newRelationship(player1, player2);
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
	
	/** Create a new relationship between two players. Should only be called from inside of
	 * getRelationship(), any other use will have undefined results.
	 * 
	 * @param player1
	 * @param player2
	 */
	private FriendRelationship newRelationship(final String player1, final String player2) {
//		if( getRelationship(player1, player2) != null )
//			throw new HeimdallException("newRelationship called when relationship already exists");
		
		FriendRelationship fr = new FriendRelationship(player1, player2);
		fr.points[0] = fr.points[1] = 0;
		addRelationship(fr);
		
		return fr;
	}
	
	private class FriendRelationship {
		public String[] players = new String[2];
		public float[] points = new float[2];
		/* True to match the side that confirmed. So if element 0 is true, then
		 * players[0] has confirmed they are a friend of players[1].
		 */
		public boolean[] isConfirmedFriend = new boolean[] { false, false };
		public boolean[] isConfirmedNotFriend = new boolean[] { false, false };
		
		public FriendRelationship(String player1, String player2) {
			players[0] = player1;
			players[1] = player2;
		}
	}
}
