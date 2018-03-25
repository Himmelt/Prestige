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
import org.soraworld.prestige.util.MathUtils;
import org.soraworld.violet.util.ListUtil;

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
            int killScore = config.getScore(killer);
            int deadScore = config.getScore(deader);
            if (killScore < 0) killScore = 0;
            if (deadScore < 0) deadScore = 0;
            Level killLvl = config.getLevel(killScore);
            Level deadLvl = config.getLevel(deadScore);

            List<String> variables = ListUtil.arrayList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
            List<Integer> values = ListUtil.arrayList(killScore, deadScore, killLvl.getScore(), deadLvl.getScore());

            MathUtils pool;
            int killPoint;
            int deadPoint;
            if (killLvl == deadLvl) {
                pool = new MathUtils();
                killPoint = (int) pool.calculate(replace(config.simpleKill, variables, values));
                deadPoint = (int) pool.calculate(replace(config.simpleDie, variables, values));
            } else if (killLvl.lvl() > deadLvl.lvl()) {
                pool = new MathUtils();
                killPoint = (int) pool.calculate(replace(config.easyKill, variables, values));
                deadPoint = (int) pool.calculate(replace(config.difficultDie, variables, values));
            } else {
                pool = new MathUtils();
                killPoint = (int) pool.calculate(replace(config.difficultKill, variables, values));
                deadPoint = (int) pool.calculate(replace(config.easyDie, variables, values));
            }

            killScore += killPoint;
            deadScore -= deadPoint;

            config.iiChat.send(killer, config.iiLang.format("killChange", killLvl.fullName(killer), killPoint));
            config.iiChat.send(deader, config.iiLang.format("deadChange", deadLvl.fullName(deader), deadPoint));

            checkLevel(killer, killLvl, config.getLevel(killScore));
            checkLevel(deader, deadLvl, config.getLevel(deadScore));

            config.setScore(killer, killScore);
            config.setScore(deader, deadScore);
            config.saveScore();
            config.updateRank();
        }
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        config.save();
    }

    private String replace(String formula, List<String> keys, List<Integer> values) {
        for (int i = 0; i < keys.size(); ++i) {
            Integer val = values.get(i);
            formula = formula.replace(keys.get(i), val < 0 ? "(" + val + ")" : val.toString());
        }
        return formula;
    }

    private void checkLevel(Player player, Level old, Level now) {
        if (now.lvl() > old.lvl()) {
            config.iiChat.send(player, config.iiLang.format("levelUp", now.getName()));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, player.getLocation().getPitch());
        } else if (now.lvl() < old.lvl()) {
            config.iiChat.send(player, config.iiLang.format("levelDown", now.getName()));
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 10.0F, player.getLocation().getPitch());
        }
    }

}
