package cn.austin.ud;

import org.bukkit.ChatColor;

import java.util.Iterator;

public class Grade {

    private int Score;
    private Grade nextGrade;
    private Grade lastGrade;
    private String keyName;
    private String name;
    private String prefix;
    private String suffix;
    private int score;


    public Grade(String keyName) {
        this.keyName = keyName;
        if (keyName.equalsIgnoreCase("Default")) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', FileManager.getConfig().getString("GradeList..Default..Prefix", "none"));
            this.suffix = ChatColor.translateAlternateColorCodes('&', FileManager.getConfig().getString("GradeList..Default..Suffix", "none"));
            this.name = ChatColor.translateAlternateColorCodes('&', FileManager.getConfig().getString("GradeList..Default..Name", "奇怪的段位"));
            this.score = 0;
        } else {
            this.prefix = ChatColor.translateAlternateColorCodes('&', FileManager.getConfig().getString("GradeList.." + keyName + "..Prefix", "none"));
            this.suffix = ChatColor.translateAlternateColorCodes('&', FileManager.getConfig().getString("GradeList.." + keyName + "..Suffix", "none"));
            this.name = ChatColor.translateAlternateColorCodes('&', FileManager.getConfig().getString("GradeList.." + keyName + "..Name", "奇怪的段位"));
            this.score = FileManager.getConfig().getInt("GradeList.." + keyName + "..Score", -1);
            if (this.score == -1) {
                Prestige.INSTANCE.getLogger().warning("读取段巍峨 \"" + keyName + "\" 分数时出现错误...");
            }
        }

    }

    public int hashCode() {
        return this.score;
    }

    public boolean equals(Object obj) {
        return ((Grade) obj).keyName.equalsIgnoreCase(this.keyName);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public int getScore() {
        return this.score;
    }

    public void setLastGrade(Grade grade) {
        this.lastGrade = grade;
    }

    public void setNextGrade(Grade grade) {
        this.nextGrade = grade;
    }

    public Grade getLastGrade() {
        return this.lastGrade;
    }

    public Grade getNextGrade() {
        return this.nextGrade;
    }

    public String getName() {
        return this.name;
    }

    public static Grade getGrade(int score) {
        Grade currentGrade = null;
        Iterator var3 = FileManager.getGrades().iterator();

        while (var3.hasNext()) {
            Grade g = (Grade) var3.next();
            if (g.getScore() != -1 && score >= g.getScore()) {
                currentGrade = g;
            }
        }

        return currentGrade;
    }
}
