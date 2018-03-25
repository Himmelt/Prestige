package org.soraworld.prestige.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.core.Level;
import org.soraworld.prestige.core.Rank;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Config extends IIConfig {

    private final File score;
    private final YamlConfiguration score_yaml = new YamlConfiguration();
    private final Rank rank = new Rank();

    public String difficultKill = "2*($DeadScore$-$KillerScore$)";
    public String simpleKill = "$DeadScore$/$KillerGradeScore$+1";
    public String easyKill = "$KillerScore$/($KillerScore$-$DeadScore$)";
    public String difficultDie = "($KillerScore$-$DeadScore$)/($DeadScore$/10)";
    public String simpleDie = "$KillerGradeScore$/100";
    public String easyDie = "($DeadScore$-$KillerScore$)*1.5";

    private int maxLvl = 0;
    private boolean allWorld = false;
    private final HashSet<World> worlds = new HashSet<>();
    private final HashMap<Integer, Level> levels = new HashMap<>();
    private final HashMap<OfflinePlayer, Integer> scores = new HashMap<>();

    public Config(File path, Plugin plugin) {
        super(path, plugin);
        this.score = new File(path, "score.yml");
    }

    private void readWorlds(List<String> list) {
        worlds.clear();
        if (list != null && !list.isEmpty()) {
            if (list.contains("*")) {
                allWorld = true;
            } else {
                for (String name : list) {
                    World world = Bukkit.getWorld(name);
                    if (world != null) {
                        worlds.add(world);
                    }
                }
            }
        }
    }

    private List<String> writeWorlds() {
        ArrayList<String> list = new ArrayList<>();
        if (allWorld) {
            list.add("*");
        } else {
            for (World world : worlds) {
                list.add(world.getName());
            }
        }
        return list;
    }

    private void readLevels(ConfigurationSection section) {
        levels.clear();
        maxLvl = 0;
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    int lvl = Integer.valueOf(key);
                    ConfigurationSection sec = section.getConfigurationSection(key);
                    if (lvl >= 0 && sec != null) {
                        if (lvl > maxLvl) maxLvl = lvl;
                        levels.put(lvl, new Level(lvl, sec.getString("name", "Level " + lvl), sec.getInt("score", lvl), sec.getString("prefix", "prefix " + lvl), sec.getString("suffix", "suffix " + lvl)));
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        if (levels.get(0) == null) {
            levels.put(0, new Level(0, "Level 0", 0, "prefix 0", "suffix 0"));
        }
    }

    private void writeLevels(ConfigurationSection section) {
        for (int lvl : levels.keySet()) {
            Level level = levels.get(lvl);
            ConfigurationSection sec = section.createSection(String.valueOf(lvl));
            if (sec != null) {
                sec.set("name", level.getName());
                sec.set("score", level.getScore());
                sec.set("prefix", level.getPrefix());
                sec.set("suffix", level.getSuffix());
            }
        }
    }

    public void saveScore() {
        try {
            for (OfflinePlayer player : scores.keySet()) {
                Integer score = scores.get(player);
                if (score != null) {
                    score_yaml.set(player.getName(), score);
                }
            }
            score_yaml.save(score);
        } catch (Throwable e) {
            if (debug) e.printStackTrace();
            iiChat.console("&cScore file save exception !!!");
        }
    }

    private void loadScore() {
        scores.clear();
        if (!score.exists()) {
            saveScore();
            return;
        }
        try {
            score_yaml.load(score);
            for (String key : score_yaml.getKeys(false)) {
                Integer score = score_yaml.getInt(key);
                if (!key.isEmpty()) {
                    scores.put(Bukkit.getOfflinePlayer(key), score);
                }
            }
        } catch (Throwable e) {
            if (debug) e.printStackTrace();
            iiChat.console("&cScore file load exception !!!");
        }
    }

    public boolean isWorldOpen(World world) {
        return allWorld || worlds.contains(world);
    }

    public void openWorld(World world) {
        if (world != null) {
            worlds.add(world);
        }
        save();
    }

    public void closeWorld(World world) {
        worlds.remove(world);
        save();
    }

    public int getScore(Player player) {
        if (scores.get(player) == null) {
            scores.put(player, 0);
        }
        return scores.get(player);
    }

    public void setScore(Player player, int score) {
        scores.put(player, score < 0 ? 0 : score);
    }

    public Level getLevel(int score) {
        for (int i = maxLvl; i >= 0; i--) {
            Level level = levels.get(i);
            if (level != null && score > level.getScore()) {
                return level;
            }
        }
        return levels.get(0);
    }

    public void updateRank() {
        rank.update();
    }

    protected void loadOptions() {
        easyDie = config_yaml.getString("easyDieFormula");
        easyKill = config_yaml.getString("easyKillFormula");
        simpleDie = config_yaml.getString("simpleDieFormula");
        simpleKill = config_yaml.getString("simpleKillFormula");
        difficultDie = config_yaml.getString("difficultDieFormula");
        difficultKill = config_yaml.getString("difficultKillFormula");
        readWorlds(config_yaml.getStringList("worlds"));
        readLevels(config_yaml.getConfigurationSection("levels"));
    }

    protected void saveOptions() {
        config_yaml.set("easyDieFormula", easyDie);
        config_yaml.set("easyKillFormula", easyKill);
        config_yaml.set("simpleDieFormula", simpleDie);
        config_yaml.set("simpleKillFormula", simpleKill);
        config_yaml.set("difficultDieFormula", difficultDie);
        config_yaml.set("difficultKillFormula", difficultKill);
        config_yaml.set("worlds", writeWorlds());
        writeLevels(config_yaml.createSection("levels"));
    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.GOLD;
    }

    @Nonnull
    protected String defaultChatHead() {
        return Constant.PLUGIN_NAME;
    }

}
