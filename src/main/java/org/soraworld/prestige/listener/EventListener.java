package org.soraworld.prestige.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;

import java.util.HashMap;

public class EventListener implements Listener {

    private final Config config;
    private final Plugin plugin;
    private final HashMap<Player, Long> clicks = new HashMap<>();

    public EventListener(Config config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onSave(WorldSaveEvent event) {
        config.save();
    }

}
