package org.soraworld.prestige.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import org.soraworld.prestige.core.Level;
import org.soraworld.prestige.core.PrestigeData;
import org.soraworld.prestige.core.Rank;
import org.soraworld.prestige.util.ListUtils;
import org.soraworld.prestige.util.MathUtils;
import org.soraworld.prestige.util.ServerUtils;

import java.util.List;

public class EventListener implements Listener {

    private final Config config;
    private final Plugin plugin;

    public EventListener(Config config, Plugin plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        config.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        config.savePlayerData(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            // TODO chat prefix format
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player deader = event.getEntity();
        final Player killer = deader.getKiller();
        if (killer != null && config.isWorldOpen(killer.getWorld())) {
            // TODO calculate score
            final PrestigeData killerData = config.getPlayerData(killer);
            final PrestigeData deaderData = config.getPlayerData(deader);
            Level killerLevel = killerData.getLevel();
            Level deadLevel = deaderData.getLevel();

            List<String> variables = ListUtils.arrayList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
            List<Integer> values = ListUtils.arrayList(killerData.getScore(), deaderData.getScore(), killerLevel.getScore(), deadLevel.getScore());
            //boolean killerGet = false;
            //boolean deadGet = false;
            MathUtils pool;
            int killerGet1;
            int deadGet1;
            if (killerLevel.equals(deadLevel)) {
                pool = new MathUtils();
                killerGet1 = (int) pool.calculate(replaceFormula(config.simpleKillFormula, variables, values));
                deadGet1 = (int) pool.calculate(replaceFormula(config.simpleDieFormula, variables, values));
            } else if (killerLevel.getScore() > deadLevel.getScore()) {
                pool = new MathUtils();
                killerGet1 = (int) pool.calculate(this.replaceFormula(config.easyKillFormula, variables, values));
                deadGet1 = (int) pool.calculate(this.replaceFormula(config.difficultDieFormula, variables, values));
            } else {
                if (killerLevel.getScore() >= deadLevel.getScore()) {
                    ServerUtils.console("Calculate errors");
                    // TODO
                    //return;
                }

                pool = new MathUtils();
                killerGet1 = (int) pool.calculate(this.replaceFormula(config.difficultKillFormula, variables, values));
                deadGet1 = (int) pool.calculate(this.replaceFormula(config.easyDieFormula, variables, values));
            }

            killerData.addScore(killerGet1);
            deaderData.addScore(-1 * deadGet1);

            ServerUtils.send(killer, "§e你斩杀了 " + (deadLevel.getPrefix().equalsIgnoreCase("none") ? "" : deadLevel.getPrefix()) + deader.getName() + (deadLevel.getSuffix().equalsIgnoreCase("none") ? "" : deadLevel.getSuffix()) + " §e" + (killerGet1 < 0 ? "扣了" : "获得") + "了 §a" + killerGet1 + " §e点声望值");
            ServerUtils.send(deader, "§e你被 " + (killerLevel.getPrefix().equalsIgnoreCase("none") ? "" : killerLevel.getPrefix()) + killer.getName() + (killerLevel.getSuffix().equalsIgnoreCase("none") ? "" : killerLevel.getSuffix()) + " §e斩杀" + "§e" + (deadGet1 >= 0 ? "扣了" : "获得") + "§a " + deadGet1 + " §e点声望值");

            final int killerUpdate = killerData.updateLevel();
            if (killerUpdate != 0) {
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    public void run() {
                        Level level = killerData.getLevel();
                        ServerUtils.send(killer, killerUpdate == 1 ? "§e恭喜你，你晋升到了 " + level.getName() : "§c很遗憾，你掉段到了 " + level.getName());
                        killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 10.0F, killer.getLocation().getPitch());
                    }
                }, 80);
            }

            int deadUpdate = deaderData.updateLevel();
            if (deadUpdate != 0) {
                Level newGrade = deaderData.getLevel();
                ServerUtils.send(deader, deadUpdate == 1 ? "§e恭喜你，你晋升了到了 " + newGrade.getName() : "§c很遗憾，你掉段到了 " + newGrade.getName());
            }

            Rank.update(killer, killerData);
            Rank.update(deader, deaderData);
        }
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        config.save();
    }

    private String replaceFormula(String formula, List<String> keys, List<Integer> values) {
        for (int i = 0; i < keys.size(); ++i) {
            formula = formula.replace(keys.get(i), String.valueOf(values.get(i)));
        }
        return formula;
    }

}
