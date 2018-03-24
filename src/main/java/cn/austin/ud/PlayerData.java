package cn.austin.ud;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerData {

    private Player p;
    private Grade grade;
    private int score;
    private File playerDataFile;
    private FileConfiguration data;
    private String name;


    public PlayerData(Player p) {
        this.p = p;
        this.getPlayerData();
        String fileName = this.playerDataFile.getName();
        this.name = fileName.substring(0, fileName.lastIndexOf("."));
    }

    public PlayerData(File file) {
        (new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas")).mkdirs();
        String fileName = file.getName();
        this.name = fileName.substring(0, fileName.lastIndexOf("."));
        this.playerDataFile = file;
        this.data = YamlConfiguration.loadConfiguration(file);
        if (this.data.get("Score") == null) {
            this.data.set("Score", 0);

            try {
                this.data.save(file);
            } catch (IOException var4) {
                Prestige.INSTANCE.getLogger().warning("读取 " + file.getName() + " 文件时出现错误");
            }
        }

        this.score = this.data.getInt("Score");
        this.grade = Grade.getGrade(this.score);
    }

    public String getName() {
        return this.name;
    }

    public File getPlayerFile() {
        return this.playerDataFile;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public int getScore() {
        return this.score;
    }

    public void delScore(int i) {
        this.score -= i;
        this.score = this.score < 0 ? 0 : this.score;
        this.saveData();
    }

    public void addScore(int i) {
        this.score += i;
        this.score = this.score < 0 ? 0 : this.score;
        this.saveData();
    }

    public void setScore(int i) {
        this.score = i;
        this.score = this.score < 0 ? 0 : this.score;
        this.saveData();
    }

    private void saveData() {
        this.data.set("Score", this.score);

        try {
            this.data.save(this.playerDataFile);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private void getPlayerData() {
        boolean createNew = false;
        (new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas")).mkdirs();

        try {
            this.playerDataFile = new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas", this.p.getName().toLowerCase() + ".yml");
            if (!this.playerDataFile.exists()) {
                this.playerDataFile.createNewFile();
                Prestige.INSTANCE.getLogger().info("创建玩家 " + this.p.getName().toLowerCase() + " 数据文件");
                createNew = true;
            }

            this.data = YamlConfiguration.loadConfiguration(this.playerDataFile);
            if (this.data.get("Score") == null) {
                this.data.set("Score", 0);
                this.data.save(this.playerDataFile);
            }

            this.score = this.data.getInt("Score");
            this.grade = Grade.getGrade(this.score);
            if (createNew) {
                Toper.addNewRank(this);
            }
        } catch (IOException var3) {
            Prestige.INSTANCE.getLogger().warning("生成玩家 " + this.p.getName() + " 的数据文件时出现错误");
        }

    }

    public int updateGrade() {
        byte result;
        for (result = 0; this.grade.getNextGrade() != null && this.score >= this.grade.getNextGrade().getScore(); result = 1) {
            this.grade = this.grade.getNextGrade();
        }

        while (this.grade.getLastGrade() != null && this.score < this.grade.getScore()) {
            this.grade = this.grade.getLastGrade();
            result = -1;
        }

        return result;
    }
}
