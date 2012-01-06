/**
 * 
 */
package org.morganm.heimdall.player;

/**
 * @author morganm
 *
 */
public class PlayerStateImpl implements PlayerState {
	private String name;
	private float griefCount;
	
	public PlayerStateImpl(final String name, final float griefCount) {
		this.name = name;
		this.griefCount = griefCount;
	}
	
	@Override
	public String getName() { return name; }

	@Override
	public float incrementGriefPoints(float f) {
		griefCount += f;
		return griefCount;
	}

	@Override
	public float getGriefPoints() {
		return griefCount;
	}

	@Override
	public boolean isExemptFromChecks() {
		// TODO: something intelligent later
		return false;
	}

	@Override
	public boolean isFriend(PlayerState p) {
		// TODO: something intelligent later
		return false;
	}

	@Override
	public float getPointsByOwner(PlayerState p) {
		// TODO: something intelligent later
		return 0;
	}

}
