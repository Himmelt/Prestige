package org.soraworld.prestige.core;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class Level implements Comparable<Level> {

    private final int score;
    private final String name;
    private final String prefix;
    private final String suffix;
    private final ArrayList<String> commands = new ArrayList<>();

    public Level(String name, int score, String prefix, String suffix) {
        this.name = name;
        this.score = score < 1 ? 1 : score;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name == null ? "" : name;
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
        // Ascending
        return this.score - level.score;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Level && this.score == ((Level) obj).score;
    }

    public void addCommand(String command) {
        this.commands.add(command);
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        return "{Level:" + name + ",score:" + score + "}";
    }

}
