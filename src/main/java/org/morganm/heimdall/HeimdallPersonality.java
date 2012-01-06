/**
 * 
 */
package org.morganm.heimdall;

/** Things related to Heimdall's personality are here in one easy spot, to allow
 * for turning him off if desired.
 * 
 * @author morganm
 *
 */
public class HeimdallPersonality {
	private Heimdall plugin;

	public HeimdallPersonality(final Heimdall plugin) {
		this.plugin = plugin;
	}
	
	public boolean isEnabled() {
		return plugin.getConfig().getBoolean("core.personality", true);
	}
}
