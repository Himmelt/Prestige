package org.soraworld.prestige.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.core.PrestigeData;
import org.soraworld.prestige.util.ServerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    private String lang = "en_us";
    private final File file;
    private final LangKeys langKeys;
    private final YamlConfiguration config = new YamlConfiguration();
    private final Plugin plugin;


    public String difficultKillFormula;
    public String simpleKillFormula;
    public String easyKillFormula;
    public String difficultDieFormula;
    public String simpleDieFormula;
    public String easyDieFormula;
    private ArrayList<String> openWorldList = new ArrayList<>();


    private final HashMap<Player, PrestigeData> databases = new HashMap<>();

    public Config(File path, Plugin plugin) {
        this.file = new File(path, "config.yml");
        this.langKeys = new LangKeys(new File(path, "lang"));
        this.plugin = plugin;
    }

    public void load() {
        if (!file.exists()) {
            if (lang == null || lang.isEmpty()) {
                lang = "en_us";
            }
            langKeys.setLang(lang);
            return;
        }
        try {
            config.load(file);
            lang = config.getString("lang");
            if (lang == null || lang.isEmpty()) {
                lang = "en_us";
            }
            langKeys.setLang(lang);
        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("config file load exception !!!");
        }
    }

    public void save() {
        try {
            config.set("lang", lang);
            config.save(file);
        } catch (Throwable e) {
            e.printStackTrace();
            ServerUtils.console("config file save exception !!!");
        }
    }

    public void lang(String lang) {
        if (lang != null && !lang.isEmpty()) {
            this.lang = lang;
            langKeys.setLang(lang);
        }
    }

    public String lang() {
        return this.lang;
    }

    public boolean isOpenWorld(String world) {
        return openWorldList.contains("*") || openWorldList.contains(world);
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

}
