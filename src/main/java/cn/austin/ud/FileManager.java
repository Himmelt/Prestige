package cn.austin.ud;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class FileManager {

    static String difficultKillFormula;
    static String simpleKillFormula;
    static String easyKillFormula;
    static String difficultDieFormula;
    static String simpleDieFormula;
    static String easyDieFormula;
    private static List openWorldList = new ArrayList();
    private static List gradeList = new ArrayList();
    private static File configFile;
    private static FileConfiguration config;


    public static List getGrades() {
        return gradeList;
    }

    public static List getOpenWorlds() {
        return openWorldList;
    }

    public static long relaodConfig() {
        Prestige.INSTANCE.getDataFolder().mkdirs();
        (new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas")).mkdirs();
        if (!(configFile = getConfigFile()).exists()) {
            createConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        return loadConfig();
    }

    private static File getConfigFile() {
        return new File(Prestige.INSTANCE.getDataFolder(), "config.yml");
    }

    private static void createConfig() {
        Prestige.INSTANCE.saveResource("config.yml", false);
        Prestige.INSTANCE.getLogger().info("生成配置文件完毕");
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    private static void loadPlayers() {
        Iterator var1 = Prestige.INSTANCE.getServer().getWorlds().iterator();

        while (var1.hasNext()) {
            World w = (World) var1.next();
            Iterator var3 = w.getEntitiesByClass(Player.class).iterator();

            while (var3.hasNext()) {
                Player p = (Player) var3.next();
                Listeners.playerDatas.put(p.getName().toLowerCase(), new PlayerData(p));
            }
        }

    }

    private static long loadConfig() {
        Prestige.INSTANCE.getLogger().info("读取配置文件中...");
        long currentTime = System.currentTimeMillis();
        int size = (size = config.getConfigurationSection("GradeList").getKeys(false).size()) == 0 ? 1 : size;
        int total = 0;
        Grade[] grades = new Grade[size];
        grades[total] = new Grade("Default");
        Iterator world = config.getConfigurationSection("GradeList").getKeys(false).iterator();

        while (world.hasNext()) {
            String index = (String) world.next();
            if (!index.equalsIgnoreCase("Default")) {
                ++total;
                grades[total] = new Grade(index);
            }
        }

        Arrays.sort(grades, new Comparator<Grade>() {
            public int compare(Grade g1, Grade g2) {
                return Integer.compare(g1.getScore(), g2.getScore());
            }
        });

        int var8;
        for (var8 = 0; var8 < grades.length - 1; ++var8) {
            if (grades[var8].getScore() != -1) {
                if (var8 - 1 >= 0) {
                    grades[var8].setLastGrade(grades[var8 - 1]);
                }

                grades[var8].setNextGrade(grades[var8 + 1]);
                gradeList.add(grades[var8]);
            }
        }

        if (grades.length >= 2) {
            grades[grades.length - 1].setLastGrade(grades[grades.length - 2]);
        }

        grades[grades.length - 1].setNextGrade(null);
        gradeList.add(grades[grades.length - 1]);
        difficultKillFormula = config.getString("DifficultKillFormula", "0");
        simpleKillFormula = config.getString("SimpleKillFormula", "0");
        easyKillFormula = config.getString("EasyKillFormula", "0");
        difficultDieFormula = config.getString("difficultDieFormula", "0");
        simpleDieFormula = config.getString("SimpleDieFormula", "0");
        easyDieFormula = config.getString("EasyDieFormula", "0");
        openWorldList = config.getStringList("OpenWorlds");
        if (openWorldList == null) {
            openWorldList = Arrays.asList("");
        }

        var8 = 0;

        for (Iterator var7 = openWorldList.iterator(); var7.hasNext(); ++var8) {
            String var9 = (String) var7.next();
            openWorldList.set(var8, var9.toLowerCase());
        }

        Listeners.playerDatas = new HashMap();
        loadPlayers();
        Toper.sortRank();
        Prestige.INSTANCE.getLogger().info("读取配置文件完毕...耗时" + (System.currentTimeMillis() - currentTime) + "ms");
        return System.currentTimeMillis() - currentTime;
    }
}
