package org.soraworld.prestige.config;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.core.PrestigeData;
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

    public String difficultKillFormula = "2*($DeadScore$-$KillerScore$)";
    public String simpleKillFormula = "$DeadScore$/$KillerGradeScore$+1";
    public String easyKillFormula = "$KillerScore$/($KillerScore$-$DeadScore$)";
    public String difficultDieFormula = "($KillerScore$-$DeadScore$)/($DeadScore$/10)";
    public String simpleDieFormula = "$KillerGradeScore$/100";
    public String easyDieFormula = "($DeadScore$-$KillerScore$)*1.5";

    private boolean allWorld = false;
    private final HashSet<World> worlds = new HashSet<>();
    private final HashMap<Player, PrestigeData> databases = new HashMap<>();

    public Config(File path, Plugin plugin) {
        this.file = new File(path, "config.yml");
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

    public void save() {
        try {
            config.set("lang", lang);

            config.set("difficultKillFormula", difficultKillFormula);
            config.set("difficultDieFormula", difficultDieFormula);
            config.set("simpleKillFormula", simpleKillFormula);
            config.set("simpleDieFormula", simpleDieFormula);
            config.set("easyKillFormula", easyKillFormula);
            config.set("easyDieFormula", easyDieFormula);

            config.set("worlds", writeWorlds());
            config.save(file);
        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("config file save exception !!!");
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

    public void loadPlayerData(Player player) {
        if (player != null) {
            if (databases.get(player) == null) {
                databases.put(player, new PrestigeData(player));
            }
        }
    }

    public void savePlayerData(Player player, boolean quit) {
        if (player != null) {
            PrestigeData data = databases.get(player);
            if (data != null) {
                // TODO save data to file
            }
            if (quit) databases.remove(player);
        }
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

    public PrestigeData getPlayerData(Player player) {
        if (player != null) {
            PrestigeData data = databases.get(player);
            if (data == null) {
                loadPlayerData(player);
                data = databases.get(player);
                if (data == null) {
                    data = new PrestigeData(player);
                    databases.put(player, data);
                }
            }
            return data;
        }
        return null;
    }

}
