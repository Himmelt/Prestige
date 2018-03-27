package org.soraworld.prestige.core;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Level implements Comparable<Level> {

    private int score;
    private String name;
    private String prefix;
    private String suffix;

    public Level(String name, int score, String prefix, String suffix) {
        this.name = name;
        this.score = score < 0 ? 0 : score;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name == null ? name = "" : name;
    }

    public String getPrefix() {
        return prefix == null ? "" : prefix;
    }

    public String getSuffix() {
        return suffix == null ? "" : suffix;
    }

    public String fullName(Player player) {
        return getPrefix() + player.getName() + getSuffix();
    }

    @Override
    public int compareTo(@Nonnull Level level) {
        return this.score - level.score;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Level && this.score == ((Level) obj).score;
    }

}
