/**
 *
 */
package com.andune.heimdall.blockhistory;

import org.bukkit.Location;

/**
 * @author andune
 */
public class BlockHistory {
    private final String owner;        // the owner of this block (if any)
    private final int typeId;        // the typeId of the block when it was created by its owner
    private final Location location;

    public BlockHistory(final String owner, final int typeId, final Location location) {
        this.owner = owner;
        this.typeId = typeId;
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public int getTypeId() {
        return typeId;
    }

    public Location getLocation() {
        return location;
    }
}
