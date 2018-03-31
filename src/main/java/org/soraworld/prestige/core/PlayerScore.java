package org.soraworld.prestige.core;

import org.soraworld.prestige.config.Config;

import javax.annotation.Nonnull;

public class PlayerScore implements Comparable<PlayerScore> {

    private int score;
    private Level level;
    private final String player;
    private final Config config;

    public PlayerScore(String player, Config config, int score) {
        this.player = player;
        this.config = config;
        this.score = score < 0 ? 0 : score;
        this.level = config.computeLevel(this.score);
    }

    public String getName() {
        return player;
    }

    public void setScore(int score) {
        if (score < 0) score = 0;
        if (this.score != score) {
            // remove
            config.getRank().remove(this);
            this.score = score;
            this.level = config.computeLevel(score);
            // add
            config.getRank().add(this);
        }
    }

    public void addScore(int score) {
        setScore(this.score + score);
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(@Nonnull PlayerScore other) {
        // Descending
        // return other.score - this.score;
        if (this.player.equals(other.player)) return 0;
        return this.score > other.score ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof PlayerScore && this.player.equals(((PlayerScore) obj).player);
    }

    @Override
    public String toString() {
        return "{" + player + "," + score + "}";
    }

    public Level getLevel() {
        return level;
    }

}
