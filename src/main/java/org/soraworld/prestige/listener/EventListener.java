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
import org.soraworld.prestige.core.PlayerScore;
import org.soraworld.prestige.util.MathUtil;
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
            PlayerScore psKill = config.getScore(killer);
            PlayerScore psDead = config.getScore(deader);
            Level killLvl = psKill.getLevel();
            Level deadLvl = psDead.getLevel();

            List<String> variables = ListUtil.arrayList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
            List<Integer> values = ListUtil.arrayList(psKill.getScore(), psDead.getScore(), killLvl.getScore(), deadLvl.getScore());

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

            config.iiChat.send(killer, config.iiLang.format("killChange", deadLvl.fullName(deader), killPoint));
            config.iiChat.send(deader, config.iiLang.format("deadChange", killLvl.fullName(killer), deadPoint));

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
            config.iiChat.send(player, config.iiLang.format("levelUp", now.getName()));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, player.getLocation().getPitch());
        } else if (now.getScore() < old.getScore()) {
            config.iiChat.send(player, config.iiLang.format("levelDown", now.getName()));
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 10.0F, player.getLocation().getPitch());
        }
    }

}
