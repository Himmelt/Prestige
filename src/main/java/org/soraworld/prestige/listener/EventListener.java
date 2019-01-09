package org.soraworld.prestige.listener;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.soraworld.prestige.manager.FameManager;
import org.soraworld.prestige.core.Level;
import org.soraworld.prestige.core.PlayerScore;
import org.soraworld.prestige.util.MathUtil;

import java.util.Arrays;
import java.util.List;

public class EventListener implements Listener {

    private final FameManager config;

    public EventListener(FameManager config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            if (event.getFormat().contains("%1$s")) {
                PlayerScore ps = config.getScore(event.getPlayer().getName());
                Level level = ps.getLevel();
                StringBuilder build = new StringBuilder(event.getFormat());
                build.insert(build.indexOf("%1$s"), level.getPrefix().replace('&', ChatColor.COLOR_CHAR));
                build.insert(build.indexOf("%1$s") + 4, level.getSuffix().replace('&', ChatColor.COLOR_CHAR) + ChatColor.WHITE);
                event.setFormat(build.toString());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player deader = event.getEntity();
        final Player killer = deader.getKiller();
        if (killer != null && config.isWorldOpen(killer.getWorld())) {
            // TODO calculate score
            PlayerScore psKill = config.getScore(killer.getName());
            PlayerScore psDead = config.getScore(deader.getName());
            Level killLvl = psKill.getLevel();
            Level deadLvl = psDead.getLevel();

            List<String> variables = Arrays.asList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
            List<Integer> values = Arrays.asList(psKill.getScore(), psDead.getScore(), killLvl.getScore(), deadLvl.getScore());

            MathUtil pool;
            int killPoint;
            int deadPoint;
            if (killLvl.getScore() == deadLvl.getScore()) {
                pool = new MathUtil();
                killPoint = (int) pool.calculate(replace(config.simpleKill, variables, values));
                deadPoint = (int) pool.calculate(replace(config.simpleDie, variables, values));
            } else if (killLvl.getScore() > deadLvl.getScore()) {
                pool = new MathUtil();
                killPoint = (int) pool.calculate(replace(config.easyKill, variables, values));
                deadPoint = (int) pool.calculate(replace(config.difficultDie, variables, values));
            } else {
                pool = new MathUtil();
                killPoint = (int) pool.calculate(replace(config.difficultKill, variables, values));
                deadPoint = (int) pool.calculate(replace(config.easyDie, variables, values));
            }

            psKill.addScore(killPoint);
            psDead.addScore(deadPoint * -1);

            config.send(killer, "killChange", deadLvl.fullName(deader), killPoint, psKill.getScore());
            config.send(deader, "deadChange", killLvl.fullName(killer), deadPoint, psDead.getScore());

            checkLevel(killer, psKill.getLevel(), killLvl);
            checkLevel(deader, psDead.getLevel(), deadLvl);

            config.saveScore();
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

    private void checkLevel(Player player, Level now, Level old) {
        if (now.getScore() > old.getScore()) {
            config.send(player, "levelUp", now.getName());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, player.getLocation().getPitch());
        } else if (now.getScore() < old.getScore()) {
            config.send(player, "levelDown", now.getName());
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 10.0F, player.getLocation().getPitch());
        }
    }

}
