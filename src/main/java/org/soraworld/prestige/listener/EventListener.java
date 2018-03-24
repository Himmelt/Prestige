package org.soraworld.prestige.listener;

import cn.austin.ud.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.util.ServerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventListener implements Listener {

    private final Config config;
    private final Plugin plugin;

    private static final int messagePoolSendDelay = 80;
    private static HashMap<String, ArrayList<String>> playerMessagePool = new HashMap<>();
    private static final HashMap<String, PlayerData> playerData = new HashMap<>();

    public EventListener(Config config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        playerData.put(event.getPlayer().getName().toLowerCase(), new PlayerData(event.getPlayer()));
//        if (playerMessagePool.get(event.getPlayer().getName().toLowerCase()) != null && !event.getPlayer().isDead()) {
//            this.handlePlayerMessage(event.getPlayer());
//        }
        config.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        //playerData.put(event.getPlayer().getName().toLowerCase(), null);
        //playerData.remove(event.getPlayer().getName().toLowerCase());
        config.savePlayerData(event.getPlayer(), true);
    }

/*    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (playerData.get(event.getPlayer().getName().toLowerCase()) == null) {
            playerData.put(event.getPlayer().getName().toLowerCase(), new PlayerData(event.getPlayer()));
        }
        if (playerMessagePool.get(event.getPlayer().getName().toLowerCase()) != null) {
            this.handlePlayerMessage(event.getPlayer());
        }
    }*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            // TODO chat prefix format
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Player killer = dead.getKiller();
        if (killer != null && config.isOpenWorld(killer.getWorld().getName())) {
            // TODO calculate score
        }
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        config.save();
    }

    private void handlePlayerMessage(final Player player) {
        int i = 1;
        ArrayList<String> list = playerMessagePool.get(player.getName().toLowerCase());

        for (final String msg : list) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    if (!player.isDead() && player.isOnline()) {
                        ServerUtils.send(player, msg);
                    }
                }
            }, (long) (i == 0 ? 10 : messagePoolSendDelay * i));
            i++;
        }

        playerMessagePool.put(player.getName().toLowerCase(), null);
    }

    private String translateFormula(String formula, List keys, List values) {
        for (int i = 0; i < keys.size(); ++i) {
            formula = formula.replace((CharSequence) keys.get(i), String.valueOf(values.get(i)));
        }
        return formula;
    }

}
