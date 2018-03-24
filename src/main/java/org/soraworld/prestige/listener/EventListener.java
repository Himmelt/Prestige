package org.soraworld.prestige.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.core.Level;
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
            int killerData = config.getScore(killer);
            int deaderData = config.getScore(deader);
            Level killerLevel = config.getLevel(killerData);
            Level deaderLevel = config.getLevel(deaderData);

            List<String> variables = ListUtils.arrayList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
            List<Integer> values = ListUtils.arrayList(killerData, deaderData, killerLevel.getScore(), deaderLevel.getScore());

            MathUtils pool;
            int killerGet1;
            int deadGet1;
            if (killerLevel.equals(deaderLevel)) {
                pool = new MathUtils();
                killerGet1 = (int) pool.calculate(replaceFormula(config.simpleKillFormula, variables, values));
                deadGet1 = (int) pool.calculate(replaceFormula(config.simpleDieFormula, variables, values));
            } else if (killerLevel.getScore() > deaderLevel.getScore()) {
                pool = new MathUtils();
                killerGet1 = (int) pool.calculate(this.replaceFormula(config.easyKillFormula, variables, values));
                deadGet1 = (int) pool.calculate(this.replaceFormula(config.difficultDieFormula, variables, values));
            } else {
                if (killerLevel.getScore() >= deaderLevel.getScore()) {
                    ServerUtils.console("Calculate errors");
                    // TODO
                    //return;
                }

                pool = new MathUtils();
                killerGet1 = (int) pool.calculate(this.replaceFormula(config.difficultKillFormula, variables, values));
                deadGet1 = (int) pool.calculate(this.replaceFormula(config.easyDieFormula, variables, values));
            }

            killerData += killerGet1;
            deaderData -= deadGet1;

            ServerUtils.send(killer, "§e你斩杀了 " + (deaderLevel.getPrefix().equalsIgnoreCase("none") ? "" : deaderLevel.getPrefix()) + deader.getName() + (deaderLevel.getSuffix().equalsIgnoreCase("none") ? "" : deaderLevel.getSuffix()) + " §e" + (killerGet1 < 0 ? "扣了" : "获得") + "了 §a" + killerGet1 + " §e点声望值");
            ServerUtils.send(deader, "§e你被 " + (killerLevel.getPrefix().equalsIgnoreCase("none") ? "" : killerLevel.getPrefix()) + killer.getName() + (killerLevel.getSuffix().equalsIgnoreCase("none") ? "" : killerLevel.getSuffix()) + " §e斩杀" + "§e" + (deadGet1 >= 0 ? "扣了" : "获得") + "§a " + deadGet1 + " §e点声望值");

            Level killerNewLevel = config.getLevel(killerData);
            if (killerNewLevel.lvl() > killerLevel.lvl()) {
                ServerUtils.send(killer, "§e恭喜你，你晋升到了 " + killerNewLevel.getName());
                killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 10.0F, killer.getLocation().getPitch());
            } else if (killerNewLevel.lvl() < killerLevel.lvl()) {
                ServerUtils.send(killer, "§c很遗憾，你掉段到了 " + killerNewLevel.getName());
                killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 10.0F, killer.getLocation().getPitch());
            }

            Level deaderNewLevel = config.getLevel(deaderData);
            if (deaderNewLevel.lvl() > deaderNewLevel.lvl()) {
                ServerUtils.send(deader, "§e恭喜你，你晋升到了 " + deaderNewLevel.getName());
                deader.playSound(deader.getLocation(), Sound.ANVIL_USE, 10.0F, deader.getLocation().getPitch());
            } else if (deaderNewLevel.lvl() < deaderNewLevel.lvl()) {
                ServerUtils.send(deader, "§c很遗憾，你掉段到了 " + deaderNewLevel.getName());
                deader.playSound(deader.getLocation(), Sound.ANVIL_USE, 10.0F, deader.getLocation().getPitch());
            }

            config.setScore(killer, killerData);
            config.setScore(deader, deaderData);
            config.saveScore();
            config.updateRank();
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
