package org.soraworld.prestige.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.core.Level;
import org.soraworld.prestige.core.PlayerScore;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

public class Config extends IIConfig {

    private final File score_file;
    private final YamlConfiguration score_yaml = new YamlConfiguration();

    public String difficultKill = "2*($DeadScore$-$KillerScore$)";
    public String simpleKill = "$DeadScore$/$KillerGradeScore$+1";
    public String easyKill = "$KillerScore$/($KillerScore$-$DeadScore$)";
    public String difficultDie = "($KillerScore$-$DeadScore$)/($DeadScore$/10)";
    public String simpleDie = "$KillerGradeScore$/100";
    public String easyDie = "($DeadScore$-$KillerScore$)*1.5";

    private final HashSet<World> worlds = new HashSet<>();
    private final TreeSet<Level> levels = new TreeSet<>();
    private final TreeSet<PlayerScore> rank = new TreeSet<>();
    private final HashMap<OfflinePlayer, PlayerScore> scores = new HashMap<>();

    public Config(File path, Plugin plugin) {
        super(path, plugin);
        this.score_file = new File(path, "score.yml");
    }

    private void readWorlds(List<String> list) {
        worlds.clear();
        if (list != null && !list.isEmpty()) {
            for (String name : list) {
                World world = Bukkit.getWorld(name);
                if (world != null) {
                    worlds.add(world);
                }
            }
        }
    }

    private List<String> writeWorlds() {
        ArrayList<String> list = new ArrayList<>();
        for (World world : worlds) {
            list.add(world.getName());
        }
        return list;
    }

    private void readLevels(List<?> list) {
        levels.clear();
        if (list != null) {
            for (Object obj : list) {
                if (obj instanceof ConfigurationSection) {
                    ConfigurationSection section = (ConfigurationSection) obj;
                    levels.add(new Level(
                            section.getString("name", "Level"),
                            section.getInt("score", 0),
                            section.getString("prefix", ""),
                            section.getString("suffix", "")
                    ));
                }
            }
        }
    }

    private List<MemoryConfiguration> writeLevels() {
        List<MemoryConfiguration> list = new ArrayList<>();
        for (Level level : levels) {
            MemoryConfiguration sec = new MemoryConfiguration();
            sec.set("name", level.getName());
            sec.set("score", level.getScore());
            sec.set("prefix", level.getPrefix());
            sec.set("suffix", level.getSuffix());
            list.add(sec);
        }
        return list;
    }

    public void saveScore() {
        try {
            for (PlayerScore player : rank) {
                score_yaml.set(player.getName(), player.getScore());
            }
            score_yaml.save(score_file);
        } catch (Throwable e) {
            if (debug) e.printStackTrace();
            iiChat.console("&cScore file save exception !!!");
        }
    }

    private void loadScore() {
        rank.clear();
        scores.clear();
        if (!score_file.exists()) {
            saveScore();
            return;
        }
        try {
            score_yaml.load(score_file);
            for (String key : score_yaml.getKeys(false)) {
                setScore(Bukkit.getOfflinePlayer(key), score_yaml.getInt(key));
            }
        } catch (Throwable e) {
            if (debug) e.printStackTrace();
            iiChat.console("&cScore file load exception !!!");
        }
    }

    public boolean isWorldOpen(World world) {
        return worlds.contains(world);
    }

    public void openWorld(World world) {
        if (world != null) worlds.add(world);
        save();
    }

    public void closeWorld(World world) {
        worlds.remove(world);
        save();
    }

    public PlayerScore getScore(OfflinePlayer player) {
        PlayerScore ps = scores.get(player);
        if (ps == null) {
            ps = new PlayerScore(player, 0);
            rank.add(ps);
            scores.put(player, ps);
        }
        return ps;
    }

    public void setScore(OfflinePlayer player, int score) {
        PlayerScore ps = scores.get(player);
        if (ps == null) {
            ps = new PlayerScore(player, score);
            rank.add(ps);
            scores.put(player, ps);
        }
        ps.setScore(score);
    }

    public Level getLevel(OfflinePlayer player) {
        PlayerScore ps = scores.get(player);
        if (ps == null) {
            ps = new PlayerScore(player, 0);
            rank.add(ps);
            scores.put(player, ps);
        }
        return ps.getLevel();
    }

    protected void loadOptions() {
        easyDie = config_yaml.getString("easyDieFormula");
        easyKill = config_yaml.getString("easyKillFormula");
        simpleDie = config_yaml.getString("simpleDieFormula");
        simpleKill = config_yaml.getString("simpleKillFormula");
        difficultDie = config_yaml.getString("difficultDieFormula");
        difficultKill = config_yaml.getString("difficultKillFormula");
        readWorlds(config_yaml.getStringList("worlds"));
        readLevels(config_yaml.getList("levels"));
        loadScore();
    }

    protected void saveOptions() {
        config_yaml.set("easyDieFormula", easyDie);
        config_yaml.set("easyKillFormula", easyKill);
        config_yaml.set("simpleDieFormula", simpleDie);
        config_yaml.set("simpleKillFormula", simpleKill);
        config_yaml.set("difficultDieFormula", difficultDie);
        config_yaml.set("difficultKillFormula", difficultKill);
        config_yaml.set("worlds", writeWorlds());
        config_yaml.set("levels", writeLevels());
        saveScore();
    }

    @Nonnull
    protected ChatColor defaultChatColor() {
        return ChatColor.GOLD;
    }

    @Nonnull
    protected String defaultChatHead() {
        return "[" + Constant.PLUGIN_NAME + "] ";
    }

}
