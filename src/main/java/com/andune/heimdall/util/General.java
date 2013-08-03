/**
 * 
 */
package com.andune.heimdall.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

/**
 * @author andune
 *
 */
public final class General {
	// class version: 1
	
	private static BlockFace[] directions = new BlockFace[] {
		BlockFace.UP,
		BlockFace.NORTH,
		BlockFace.WEST,
		BlockFace.SOUTH,
		BlockFace.EAST,
		BlockFace.DOWN
	};
	private static General instance;
	
	private General() {}
	
	public static General getInstance() {
		if( instance == null )
			instance = new General();
		return instance;
	}
	
	/** Recursively look for 2 vertical safe air spots nearest the given location.
	 * 
	 *  TODO: ensure safety by also checking for lava underneath
	 * 
	 * @param base
	 */
	private Location findSafeLocation(final Set<Location> alreadyTraversed, final int level, final Location location) {
		Block base = location.getBlock();
		Block up = base.getRelative(BlockFace.UP);
		
		if( base.getTypeId() == 0 && up.getTypeId() == 0 )
			return location;
		else {
			// first try all the closest blocks before recursing further
			for(int i=0; i < directions.length; i++) {
				Block tryBlock = base.getRelative(directions[i]);
				Location tryLocation = tryBlock.getLocation();
				if( alreadyTraversed.contains(tryLocation) ) {
					continue;
				}
				alreadyTraversed.add(tryLocation);
				up = tryBlock.getRelative(BlockFace.UP);
				
				if( tryBlock.getTypeId() == 0 && up.getTypeId() == 0 )
					return location;
			}
			
			// we only recurse so far before we give up
			if( level > 10 )
				return null;
			
			// if we're here, none of them were safe, now recurse
			for(int i=0; i < directions.length; i++) {
				Location recurseLocation = base.getRelative(directions[i]).getLocation();
				if( alreadyTraversed.contains(recurseLocation) )
					continue;
				Location result = findSafeLocation(alreadyTraversed, level+1, recurseLocation);
				if( result != null )
					return result;
			}
		}
		
		return null;
	}
	
	/** Safely teleport a player to a location. Should avoid them being stuck in blocks,
	 * teleported over lava, etc.  (not fully implemented)
	 * 
	 * @param p
	 * @param l
	 */
	public void safeTeleport(final Player p, final Location l, final TeleportCause cause) {
		Location target = findSafeLocation(new HashSet<Location>(10), 0, l);

		// if we didn't find a safe location, then just teleport them to the original location
		if( target == null )
			target = l;
		
		p.teleport(target, cause);
	}
	
	public String shortLocationString(final Location l) {
		if( l == null )
			return "null";
		else {
			World w = l.getWorld();
			String worldName = null;
			if( w != null )
				worldName = w.getName();
			else
				worldName = "(world deleted)";
			return worldName+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		}
	}
	
	/** Read a string that was written by "shortLocationString" and turn it into
	 * a location object (if possible). Can return null.
	 * 
	 * @param locatinString
	 * @return
	 */
	public Location readShortLocationString(final String locationString) {
		Location location = null;
		if( locationString != null ) {
			String[] pieces = locationString.split(",");
			
			// make sure all the elements are there and it's not a deleted world 
			if( pieces.length == 4 && !pieces[0].equals("(world deleted)") ) {
				World w = Bukkit.getWorld(pieces[0]);
				int x = 0; int y = 0; int z = 0;
				try {
					x = Integer.parseInt(pieces[1]);
					y = Integer.parseInt(pieces[2]);
					z = Integer.parseInt(pieces[3]);
				} catch(NumberFormatException e) {}
				
				location = new Location(w, x, y, z);
			}
		}
		
		return location;
	}
	
	/** Return whether or not Player p is a new player (first time logged in).
	 * 
	 * @param p
	 * @return
	 */
    public boolean isNewPlayer(Player p) {
//    	return !p.hasPlayedBefore();	// this is broken at the moment

    	String playerDat = p.getName() + ".dat";
    	
    	// start with the easy, most likely check
    	File file = new File("world/players/"+playerDat);
    	if( file.exists() )
    		return false;
    	
    	// if we didn't find any record of this player on any world, they must be new
    	return true;
    }

	/** Code borrowed from @Diddiz's LogBlock
	 * 
	 * @param items1
	 * @param items2
	 * @return
	 */
	public ItemStack[] compareInventories(ItemStack[] items1, ItemStack[] items2) {
		final ItemStackComparator comperator = new ItemStackComparator();
		final ArrayList<ItemStack> diff = new ArrayList<ItemStack>();
		final int l1 = items1.length, l2 = items2.length;
		int c1 = 0, c2 = 0;
		while (c1 < l1 || c2 < l2) {
			if (c1 >= l1) {
				diff.add(items2[c2]);
				c2++;
				continue;
			}
			if (c2 >= l2) {
				items1[c1].setAmount(items1[c1].getAmount() * -1);
				diff.add(items1[c1]);
				c1++;
				continue;
			}
			final int comp = comperator.compare(items1[c1], items2[c2]);
			if (comp < 0) {
				items1[c1].setAmount(items1[c1].getAmount() * -1);
				diff.add(items1[c1]);
				c1++;
			} else if (comp > 0) {
				diff.add(items2[c2]);
				c2++;
			} else {
				final int amount = items2[c2].getAmount() - items1[c1].getAmount();
				if (amount != 0) {
					items1[c1].setAmount(amount);
					diff.add(items1[c1]);
				}
				c1++;
				c2++;
			}
		}
		return diff.toArray(new ItemStack[diff.size()]);
	}

	/** Code borrowed from @Diddiz's LogBlock
	 * 
	 * @param items
	 * @return
	 */
	public ItemStack[] compressInventory(ItemStack[] items) {
		final ArrayList<ItemStack> compressed = new ArrayList<ItemStack>();
		for (final ItemStack item : items)
			if (item != null) {
				final int type = item.getTypeId();
				final byte data = rawData(item);
				boolean found = false;
				for (final ItemStack item2 : compressed)
					if (type == item2.getTypeId() && data == rawData(item2)) {
						item2.setAmount(item2.getAmount() + item.getAmount());
						found = true;
						break;
					}
				if (!found)
					compressed.add(new ItemStack(type, item.getAmount(), (short)0, data));
			}
		Collections.sort(compressed, new ItemStackComparator());
		return compressed.toArray(new ItemStack[compressed.size()]);
	}

	/** Code borrowed from @Diddiz's LogBlock 
	 * 
	 * @param item
	 * @return
	 */
	public static byte rawData(ItemStack item) {
		return item.getType() != null ? item.getData() != null ? item.getData().getData() : 0 : 0;
	}
	
	/** Code borrowed from @Diddiz's LogBlock 
	 * 
	 * @param item
	 * @return
	 */
	public static class ItemStackComparator implements Comparator<ItemStack>
	{
		@Override
		public int compare(ItemStack a, ItemStack b) {
			final int aType = a.getTypeId(), bType = b.getTypeId();
			if (aType < bType)
				return -1;
			if (aType > bType)
				return 1;
			final byte aData = rawData(a), bData = rawData(b);
			if (aData < bData)
				return -1;
			if (aData > bData)
				return 1;
			return 0;
		}
	}
}
