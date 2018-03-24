package org.soraworld.prestige.core;

public class Level {

    private int lvl;
    private int score;
    private String name;
    private String prefix;
    private String suffix;

    public Level(int lvl, String name, int score, String prefix, String suffix) {
        this.name = name;
        this.lvl = lvl < 0 ? 0 : lvl;
        this.score = score < 0 ? 0 : score;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public int getScore() {
        return score;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getName() {
        return name;
    }

    public int lvl() {
        return lvl;
    }
}
