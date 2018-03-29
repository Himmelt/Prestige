package org.soraworld.prestige.core;

import org.soraworld.prestige.config.Config;

import javax.annotation.Nonnull;

public class PlayerScore implements Comparable<PlayerScore> {

    private int score;
    private Level level;
    private final Config config;
    private final String player;

    public PlayerScore(String player, Config config, int score) {
        this.player = player;
        this.config = config;
        setScore(score);
    }

    public String getName() {
        return player;
    }

    public void setScore(int score) {
        if (score < 0) score = 0;
        if (this.score != score) {
            this.score = score;
            update();
        }
    }

    public void addScore(int score) {
        setScore(this.score + score);
    }

    public int getScore() {
        return score;
    }

    private void update() {
        config.updateRank(this);
        this.level = config.computeLevel(this.score);
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
