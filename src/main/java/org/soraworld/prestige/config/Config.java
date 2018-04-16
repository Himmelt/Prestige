package org.soraworld.prestige.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.core.Level;
import org.soraworld.prestige.core.PlayerScore;
import org.soraworld.violet.config.IIConfig;
import org.soraworld.violet.yaml.IYamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

public class Config extends IIConfig {

    private final File score_file;
    private final IYamlConfiguration score_yaml = new IYamlConfiguration();

    public String difficultKill = "2*($DeadScore$-$KillerScore$)";
    public String simpleKill = "$DeadScore$/$KillerGradeScore$+1";
    public String easyKill = "$KillerScore$/($KillerScore$-$DeadScore$)";
    public String difficultDie = "($KillerScore$-$DeadScore$)/($DeadScore$/10)";
    public String simpleDie = "$KillerGradeScore$/100";
    public String easyDie = "($DeadScore$-$KillerScore$)*1.5";

    private final HashSet<World> worlds = new HashSet<>();
    private final TreeSet<Level> levels = new TreeSet<>();
    private final TreeSet<PlayerScore> rank = new TreeSet<>();
    private final HashMap<String, PlayerScore> scores = new HashMap<>();

    public Config(File path) {
        super(path);
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
                if (obj instanceof Map) {
                    Map map = (Map) obj;
                    Object name = map.get("name");
                    Object score = map.get("score");
                    Object prefix = map.get("prefix");
                    Object suffix = map.get("suffix");
                    Object commands = map.get("commands");
                    if (name instanceof String && score instanceof Integer) {
                        if (!(prefix instanceof String)) prefix = "";
                        if (!(suffix instanceof String)) suffix = "";
                        if (!(commands instanceof List)) commands = new ArrayList<String>();
                        Level level = new Level((String) name, (Integer) score, (String) prefix, (String) suffix);
                        for (Object o : (List) commands) {
                            if (o instanceof String) {
                                level.addCommand((String) o);
                            }
                        }
                        levels.add(level);
                    }
                }
            }
        }
        defaultLevel();
    }

    private void defaultLevel() {
        if (levels.isEmpty()) {
            Level level = new Level("Level", 10, "", "");
            level.addCommand("tell {player} prestige command test 1.");
            level.addCommand("tell {player} prestige command test 2.");
            levels.add(level);
        }
    }

    private List<?> writeLevels() {
        defaultLevel();
        List<Map> list = new ArrayList<>();
        for (Level level : levels) {
            Map<String, Object> sec = new LinkedHashMap<>();
            sec.put("name", level.getName());
            sec.put("score", level.getScore());
            sec.put("prefix", level.getPrefix());
            sec.put("suffix", level.getSuffix());
            sec.put("commands", level.getCommands());
            list.add(sec);
        }
        return list;
    }

    public void saveScore() {
        try {
            LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
            for (PlayerScore ps : rank) {
                scores.put(ps.getName(), ps.getScore());
            }
            score_yaml.set("scores", scores);
            score_yaml.save(score_file);
        } catch (Throwable e) {
            if (getDebug()) e.printStackTrace();
            console("&cScore file save exception !!!");
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
            Object obj = score_yaml.get("scores");
            if (obj instanceof MemorySection) {
                MemorySection sec = (MemorySection) obj;
                for (String player : sec.getKeys(false)) {
                    PlayerScore ps = getScore(player);
                    ps.setScore(sec.getInt(player));
                }
            }
        } catch (Throwable e) {
            if (getDebug()) e.printStackTrace();
            console("&cScore file load exception !!!");
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

    public PlayerScore getScore(String player) {
        PlayerScore ps = scores.get(player);
        if (ps == null) {
            ps = new PlayerScore(player, this, 0);
            rank.remove(ps);
            rank.add(ps);
            scores.put(player, ps);
        }
        return ps;
    }

    public void showRank(CommandSender sender, int page) {
        if (page < 1) page = 1;
        send(sender, "rankHead");
        Iterator<PlayerScore> it = rank.iterator();
        for (int i = 1; i <= page * 10 && it.hasNext(); i++) {
            PlayerScore ps = it.next();
            if (i >= page * 10 - 9) {
                send(sender, "rankLine", i, ps.getName(), ps.getScore(), ps.getLevel().getName());
            }
        }
        send(sender, "rankFoot", page, rank.size() / 10 + 1);
    }

    public Level computeLevel(int score) {
        for (Level level : levels) {
            if (score < level.getScore()) return level;
        }
        return levels.last();
    }

    public TreeSet<PlayerScore> getRank() {
        return rank;
    }

    public void execCommands(String name) {
        if (name == null || name.isEmpty()) {
            for (PlayerScore ps : rank) {
                Player player = Bukkit.getPlayer(ps.getName());
                if (player != null) {
                    Level level = ps.getLevel();
                    for (String cmd : level.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
                    }
                }
            }
        } else {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                Level level = getScore(name).getLevel();
                for (String cmd : level.getCommands()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
                }
            }
        }
    }

    protected void loadOptions() {
        easyDie = getCfgYaml().getString("easyDieFormula");
        easyKill = getCfgYaml().getString("easyKillFormula");
        simpleDie = getCfgYaml().getString("simpleDieFormula");
        simpleKill = getCfgYaml().getString("simpleKillFormula");
        difficultDie = getCfgYaml().getString("difficultDieFormula");
        difficultKill = getCfgYaml().getString("difficultKillFormula");
        readWorlds(getCfgYaml().getStringList("worlds"));
        readLevels(getCfgYaml().getList("levels"));
        loadScore();
    }

    protected void saveOptions() {
        getCfgYaml().set("easyDieFormula", easyDie);
        getCfgYaml().set("easyKillFormula", easyKill);
        getCfgYaml().set("simpleDieFormula", simpleDie);
        getCfgYaml().set("simpleKillFormula", simpleKill);
        getCfgYaml().set("difficultDieFormula", difficultDie);
        getCfgYaml().set("difficultKillFormula", difficultKill);
        getCfgYaml().set("worlds", writeWorlds());
        getCfgYaml().set("levels", writeLevels());
        saveScore();
    }

    public void afterLoad() {

    }

    @Nonnull
    public String getAdminPerm() {
        return Constant.PERM_ADMIN;
    }

    @Nonnull
    public ChatColor getHeadColor() {
        return ChatColor.GOLD;
    }

    @Nonnull
    public String getPlainHead() {
        return "[" + Constant.PLUGIN_NAME + "] ";
    }

    public void setPlainHead(String s) {
    }
}
