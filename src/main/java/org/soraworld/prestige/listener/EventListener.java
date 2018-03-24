package org.soraworld.prestige.listener;

import cn.austin.API.NMSSender;
import cn.austin.ud.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
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
        playerData.put(event.getPlayer().getName().toLowerCase(), new PlayerData(event.getPlayer()));
        if (playerMessagePool.get(event.getPlayer().getName().toLowerCase()) != null && !event.getPlayer().isDead()) {
            this.handlePlayerMessage(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        //playerData.put(event.getPlayer().getName().toLowerCase(), null);
        playerData.remove(event.getPlayer().getName().toLowerCase());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            if (event.getFormat().contains("%1$s")) {
                Grade grade = playerData.get(event.getPlayer().getName().toLowerCase()).getGrade();
                StringBuilder build = new StringBuilder(event.getFormat());
                if (!grade.getPrefix().equalsIgnoreCase("none")) {
                    build.insert(build.indexOf("%1$s"), grade.getPrefix());
                }
                if (!grade.getSuffix().equalsIgnoreCase("none")) {
                    build.insert(build.indexOf("%1$s") + 4, grade.getSuffix() + "§f");
                }
                event.setFormat(build.toString());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (playerData.get(event.getPlayer().getName().toLowerCase()) == null) {
            playerData.put(event.getPlayer().getName().toLowerCase(), new PlayerData(event.getPlayer()));
        }
        if (playerMessagePool.get(event.getPlayer().getName().toLowerCase()) != null) {
            this.handlePlayerMessage(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        if (killer != null) {
            if (FileManager.getOpenWorlds().contains(killer.getLocation().getWorld().getName()) || FileManager.getOpenWorlds().contains("*")) {
                System.out.println(FileManager.getOpenWorlds().contains(killer.getLocation().getWorld().getName().toLowerCase()) + " " + FileManager.getOpenWorlds().contains("*"));
                Player dead = event.getEntity();
                final PlayerData killerD = playerData.get(killer.getName().toLowerCase());
                PlayerData deadD = playerData.get(dead.getName().toLowerCase());
                Grade killerG = killerD.getGrade();
                Grade deadG = deadD.getGrade();
                List translateList = Arrays.asList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
                List valueList = Arrays.asList(killerD.getScore(), deadD.getScore(), killerG.getScore(), deadG.getScore());
                //boolean killerGet = false;
                //boolean deadGet = false;
                Calculator pool;
                int killerGet1;
                int deadGet1;
                if (killerG.equals(deadG)) {
                    pool = new Calculator();
                    killerGet1 = (int) pool.calculate(this.translateFormula(config.simpleKillFormula, translateList, valueList));
                    deadGet1 = (int) pool.calculate(this.translateFormula(config.simpleDieFormula, translateList, valueList));
                } else if (killerG.getScore() > deadG.getScore()) {
                    pool = new Calculator();
                    killerGet1 = (int) pool.calculate(this.translateFormula(config.easyKillFormula, translateList, valueList));
                    deadGet1 = (int) pool.calculate(this.translateFormula(config.difficultDieFormula, translateList, valueList));
                } else {
                    if (killerG.getScore() >= deadG.getScore()) {
                        Prestige.INSTANCE.getLogger().warning("计算分数时出现错误，或因为有重复分数段位");
                        return;
                    }

                    pool = new Calculator();
                    killerGet1 = (int) pool.calculate(this.translateFormula(config.difficultKillFormula, translateList, valueList));
                    deadGet1 = (int) pool.calculate(this.translateFormula(config.easyDieFormula, translateList, valueList));
                }

                killerD.addScore(killerGet1);
                deadD.delScore(deadGet1);
                NMSSender.sendActionBar(killer, "§e你斩杀了 " + (deadG.getPrefix().equalsIgnoreCase("none") ? "" : deadG.getPrefix()) + dead.getName() + (deadG.getSuffix().equalsIgnoreCase("none") ? "" : deadG.getSuffix()) + " §e" + (killerGet1 < 0 ? "扣了" : "获得") + "了 §a" + killerGet1 + " §e点声望值");
                ArrayList<String> pool1 = playerMessagePool.get(dead.getName().toLowerCase());
                if (pool1 == null) {
                    pool1 = new ArrayList<>();
                }

                pool1.add("§e你被 " + (killerG.getPrefix().equalsIgnoreCase("none") ? "" : killerG.getPrefix()) + killer.getName() + (killerG.getSuffix().equalsIgnoreCase("none") ? "" : killerG.getSuffix()) + " §e斩杀" + "§e" + (deadGet1 >= 0 ? "扣了" : "获得") + "§a " + deadGet1 + " §e点声望值");
                final int killerUpdate = killerD.updateGrade();
                if (killerUpdate != 0) {
                    Prestige.INSTANCE.getServer().getScheduler().runTaskLater(Prestige.INSTANCE, new Runnable() {
                        public void run() {
                            Grade newGrade = killerD.getGrade();
                            NMSSender.sendActionBar(killer, killerUpdate == 1 ? "§e恭喜你，你晋升到了 " + newGrade.getName() : "§c很遗憾，你掉段到了 " + newGrade.getName());
                            killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 10.0F, killer.getLocation().getPitch());
                        }
                    }, messagePoolSendDelay);
                }

                int deadUpdate = deadD.updateGrade();
                if (deadUpdate != 0) {
                    Grade newGrade = deadD.getGrade();
                    pool1.add(deadUpdate == 1 ? "§e恭喜你，你晋升了到了 " + newGrade.getName() : "§c很遗憾，你掉段了到了 " + newGrade.getName());
                }

                playerMessagePool.put(dead.getName().toLowerCase(), pool1);
                Toper.updatePlayer(killerD);
                Toper.updatePlayer(deadD);
            }
        }
    }


    @EventHandler
    public void onSave(WorldSaveEvent event) {
        config.save();
    }

    private void handlePlayerMessage(final Player player) {
        int i = 1;
        ArrayList<String> list = playerMessagePool.get(player.getName().toLowerCase());

        for (final String msg : list) {
            Prestige.INSTANCE.getServer().getScheduler().runTaskLater(Prestige.INSTANCE, new Runnable() {
                public void run() {
                    if (!player.isDead() && player.isOnline()) {
                        NMSSender.sendActionBar(player, msg);
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
