/**
 *
 */
package com.andune.heimdall.engine;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.event.BlockChangeEvent;
import com.andune.heimdall.event.FriendEvent;
import com.andune.heimdall.event.FriendInviteEvent;
import com.andune.heimdall.event.InventoryChangeEvent;
import com.andune.heimdall.event.PlayerEvent;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Abstract class to implement do-nothing methods so that Engine implementations
 * can only listen to events they are interested in and just ignore the rest.
 *
 * @author andune
 */
public abstract class AbstractEngine implements Engine {
    protected YamlConfiguration loadConfig(final Heimdall plugin, final String configFile, final String defaultConfigFile) {
        File file = new File(configFile);
        // copy default into place if file doesn't exist
        if (!file.exists())
            plugin.getJarUtils().copyConfigFromJar(defaultConfigFile, file);

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // shouldn't ever happen according to Bukkit contract, but better to be paranoid
        if (config == null)
            throw new NullPointerException("Yaml config is null: " + configFile);

        return config;
    }

    @Override
    public void processBlockChange(BlockChangeEvent event) {
    }

    @Override
    public void processInventoryChange(InventoryChangeEvent event) {
    }

    @Override
    public void processChatMessage(String message) {
    }

    @Override
    public void processPlayerEvent(PlayerEvent event) {
    }

    @Override
    public void processHeimdallFriendEvent(FriendEvent event) {
    }

    @Override
    public void processHeimdallFriendInvite(FriendInviteEvent event) {
    }
}
