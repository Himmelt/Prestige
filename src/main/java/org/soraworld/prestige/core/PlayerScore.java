package org.soraworld.prestige.core;

import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;

public class PlayerScore implements Comparable<PlayerScore> {

    private int score;
    private Level level;
    private final OfflinePlayer player;

    public PlayerScore(OfflinePlayer player) {
        this.player = player;
    }

    public PlayerScore(OfflinePlayer player, int score) {
        this.player = player;
        setScore(score);
    }

    public String getName() {
        return player.getName();
    }

    public void setScore(int score) {
        if (score < 0) score = 0;
        this.score = score;
        updateLevel();
    }

    public void addScore(int score) {
        setScore(this.score + score);
    }

    public int getScore() {
        return score;
    }

    private void updateLevel() {

    }

    @Override
    public int compareTo(@Nonnull PlayerScore other) {
        return this.score - other.score;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof PlayerScore && this.player == ((PlayerScore) obj).player;
    }

    public Level getLevel() {
        // TODO level
        return level;
    }


    public OfflinePlayer getPlayer() {
        return player;
    }
}
