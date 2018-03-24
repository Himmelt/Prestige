package org.soraworld.prestige.config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.core.Level;
import org.soraworld.prestige.core.Rank;
import org.soraworld.prestige.util.ServerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Config {

    private String lang = "en_us";
    private final File file;
    private final LangKeys langKeys;
    private final YamlConfiguration config = new YamlConfiguration();
    private final Plugin plugin;
    private final File score;
    private final YamlConfiguration scfg = new YamlConfiguration();
    private final Rank rank = new Rank();

    public String difficultKillFormula = "2*($DeadScore$-$KillerScore$)";
    public String simpleKillFormula = "$DeadScore$/$KillerGradeScore$+1";
    public String easyKillFormula = "$KillerScore$/($KillerScore$-$DeadScore$)";
    public String difficultDieFormula = "($KillerScore$-$DeadScore$)/($DeadScore$/10)";
    public String simpleDieFormula = "$KillerGradeScore$/100";
    public String easyDieFormula = "($DeadScore$-$KillerScore$)*1.5";

    private int maxLvl = 0;
    private boolean allWorld = false;
    private final HashSet<World> worlds = new HashSet<>();
    private final HashMap<Integer, Level> levels = new HashMap<>();
    private final HashMap<OfflinePlayer, Integer> scores = new HashMap<>();

    public Config(File path, Plugin plugin) {
        this.file = new File(path, "config.yml");
        this.score = new File(path, "score.yml");
        this.langKeys = new LangKeys(new File(path, "lang"));
        this.plugin = plugin;
    }

    public void load() {
        if (!file.exists()) {
            setLang(lang);
            save();
            return;
        }
        try {
            config.load(file);
            setLang(config.getString("lang"));
            readWorlds(config.getStringList("worlds"));
            readLevels(config.getConfigurationSection("levels"));

            difficultKillFormula = config.getString("difficultKillFormula");
            difficultDieFormula = config.getString("difficultDieFormula");
            simpleKillFormula = config.getString("simpleKillFormula");
            simpleDieFormula = config.getString("simpleDieFormula");
            easyKillFormula = config.getString("easyKillFormula");
            easyDieFormula = config.getString("easyDieFormula");

        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("config file load exception !!!");
        }
        loadScore();
    }

    public void save() {
        try {
            config.set("lang", lang);

            config.set("difficultKillFormula", difficultKillFormula);
            config.set("difficultDieFormula", difficultDieFormula);
            config.set("simpleKillFormula", simpleKillFormula);
            config.set("simpleDieFormula", simpleDieFormula);
            config.set("easyKillFormula", easyKillFormula);
            config.set("easyDieFormula", easyDieFormula);

            writeLevels(config.createSection("levels"));
            config.set("worlds", writeWorlds());
            config.save(file);
        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("config file save exception !!!");
        }
        saveScore();
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

    private void saveScore() {
        try {
            for (OfflinePlayer player : scores.keySet()) {
                Integer score = scores.get(player);
                if (score != null) {
                    scfg.set(player.getName(), score);
                }
            }
            scfg.save(score);
        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("score file save exception !!!");
        }
    }

    private void loadScore() {
        scores.clear();
        if (!score.exists()) {
            saveScore();
            return;
        }
        try {
            scfg.load(score);
            for (String key : scfg.getKeys(false)) {
                Integer score = scfg.getInt(key);
                if (!key.isEmpty()) {
                    scores.put(Bukkit.getOfflinePlayer(key), score);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("score file load exception !!!");
        }
    }

    public void setLang(String lang) {
        if (lang != null && !lang.isEmpty()) {
            this.lang = lang;
        } else {
            this.lang = "en_us";
        }
        langKeys.setLang(this.lang);
    }

    public String getLang() {
        return this.lang;
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

    public int getScore(OfflinePlayer player) {
        if (scores.get(player) == null) {
            scores.put(player, 0);
        }
        return scores.get(player);
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

}
