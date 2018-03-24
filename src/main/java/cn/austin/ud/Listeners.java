package cn.austin.ud;

import cn.austin.API.NMSSender;
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

import java.util.*;

public class Listeners implements Listener {

    private static final int messagePoolSendDelay = 80;
    static Map playerDatas = new HashMap();
    private static Map playerMessagePool = new HashMap();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerDatas.put(e.getPlayer().getName().toLowerCase(), new PlayerData(e.getPlayer()));
        if (playerMessagePool.get(e.getPlayer().getName().toLowerCase()) != null && !e.getPlayer().isDead()) {
            this.handlePlayerMessage(e.getPlayer());
        }

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        playerDatas.put(e.getPlayer().getName().toLowerCase(), null);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!e.isCancelled()) {
            if (e.getFormat().contains("%1$s")) {
                Grade grade = ((PlayerData) playerDatas.get(e.getPlayer().getName().toLowerCase())).getGrade();
                StringBuilder build = new StringBuilder(e.getFormat());
                if (!grade.getPrefix().equalsIgnoreCase("none")) {
                    build.insert(build.indexOf("%1$s"), grade.getPrefix());
                }

                if (!grade.getSuffix().equalsIgnoreCase("none")) {
                    build.insert(build.indexOf("%1$s") + 4, grade.getSuffix() + "§f");
                }

                e.setFormat(build.toString());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (playerDatas.get(e.getPlayer().getName().toLowerCase()) == null) {
            playerDatas.put(e.getPlayer().getName().toLowerCase(), new PlayerData(e.getPlayer()));
        }

        if (playerMessagePool.get(e.getPlayer().getName().toLowerCase()) != null) {
            this.handlePlayerMessage(e.getPlayer());
        }

    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerDeath(PlayerDeathEvent e) {
        final Player killer = e.getEntity().getKiller();
        if (killer != null) {
            if (FileManager.getOpenWorlds().contains(killer.getLocation().getWorld().getName()) || FileManager.getOpenWorlds().contains("*")) {
                System.out.println(FileManager.getOpenWorlds().contains(killer.getLocation().getWorld().getName().toLowerCase()) + " " + FileManager.getOpenWorlds().contains("*"));
                Player dead = e.getEntity();
                final PlayerData killerD = (PlayerData) playerDatas.get(killer.getName().toLowerCase());
                PlayerData deadD = (PlayerData) playerDatas.get(dead.getName().toLowerCase());
                Grade killerG = killerD.getGrade();
                Grade deadG = deadD.getGrade();
                List translateList = Arrays.asList("$KillerScore$", "$DeadScore$", "$KillerGradeScore$", "$DeadGradeScore$");
                List valueList = Arrays.asList(killerD.getScore(), deadD.getScore(), killerG.getScore(), deadG.getScore());
                boolean killerGet = false;
                boolean deadGet = false;
                Calculator pool;
                int killerGet1;
                int deadGet1;
                if (killerG.equals(deadG)) {
                    pool = new Calculator();
                    killerGet1 = (int) pool.calculate(this.translateFormula(FileManager.simpleKillFormula, translateList, valueList));
                    deadGet1 = (int) pool.calculate(this.translateFormula(FileManager.simpleDieFormula, translateList, valueList));
                } else if (killerG.getScore() > deadG.getScore()) {
                    pool = new Calculator();
                    killerGet1 = (int) pool.calculate(this.translateFormula(FileManager.easyKillFormula, translateList, valueList));
                    deadGet1 = (int) pool.calculate(this.translateFormula(FileManager.difficultDieFormula, translateList, valueList));
                } else {
                    if (killerG.getScore() >= deadG.getScore()) {
                        Prestige.INSTANCE.getLogger().warning("计算分数时出现错误，或因为有重复分数段位");
                        return;
                    }

                    pool = new Calculator();
                    killerGet1 = (int) pool.calculate(this.translateFormula(FileManager.difficultKillFormula, translateList, valueList));
                    deadGet1 = (int) pool.calculate(this.translateFormula(FileManager.easyDieFormula, translateList, valueList));
                }

                killerD.addScore(killerGet1);
                deadD.delScore(deadGet1);
                NMSSender.sendActionBar(killer, "§e你斩杀了 " + (deadG.getPrefix().equalsIgnoreCase("none") ? "" : deadG.getPrefix()) + dead.getName() + (deadG.getSuffix().equalsIgnoreCase("none") ? "" : deadG.getSuffix()) + " §e" + (killerGet1 < 0 ? "扣了" : "获得") + "了 §a" + killerGet1 + " §e点声望值");
                Object pool1 = playerMessagePool.get(dead.getName().toLowerCase());
                if (pool1 == null) {
                    pool1 = new ArrayList();
                }

                ((List) pool1).add("§e你被 " + (killerG.getPrefix().equalsIgnoreCase("none") ? "" : killerG.getPrefix()) + killer.getName() + (killerG.getSuffix().equalsIgnoreCase("none") ? "" : killerG.getSuffix()) + " §e斩杀" + "§e" + (deadGet1 >= 0 ? "扣了" : "获得") + "§a " + deadGet1 + " §e点声望值");
                final int killerUpdate = killerD.updateGrade();
                if (killerUpdate != 0) {
                    Prestige.INSTANCE.getServer().getScheduler().runTaskLater(Prestige.INSTANCE, new Runnable() {
                        public void run() {
                            Grade newGrade = killerD.getGrade();
                            NMSSender.sendActionBar(killer, killerUpdate == 1 ? "§e恭喜你，你晋升到了 " + newGrade.getName() : "§c很遗憾，你掉段到了 " + newGrade.getName());
                            killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 10.0F, killer.getLocation().getPitch());
                        }
                    }, 80L);
                }

                int deadUpdate = deadD.updateGrade();
                if (deadUpdate != 0) {
                    Grade newGrade = deadD.getGrade();
                    ((List) pool1).add(deadUpdate == 1 ? "§e恭喜你，你晋升了到了 " + newGrade.getName() : "§c很遗憾，你掉段了到了 " + newGrade.getName());
                }

                playerMessagePool.put(dead.getName().toLowerCase(), pool1);
                Toper.updatePlayer(killerD);
                Toper.updatePlayer(deadD);
            }
        }
    }

    public void handlePlayerMessage(final Player p) {
        int i = 1;

        for (Iterator var4 = ((List) playerMessagePool.get(p.getName().toLowerCase())).iterator(); var4.hasNext(); ++i) {
            final String msg = (String) var4.next();
            Prestige.INSTANCE.getServer().getScheduler().runTaskLater(Prestige.INSTANCE, new Runnable() {
                public void run() {
                    if (!p.isDead() && p.isOnline()) {
                        NMSSender.sendActionBar(p, msg);
                    }
                }
            }, (long) (i == 0 ? 10 : 80 * i));
        }

        playerMessagePool.put(p.getName().toLowerCase(), null);
    }

    public String translateFormula(String formula, List keys, List values) {
        for (int i = 0; i < keys.size(); ++i) {
            formula = formula.replace((CharSequence) keys.get(i), String.valueOf(values.get(i)));
        }

        return formula;
    }
}
