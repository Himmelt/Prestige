package org.soraworld.prestige.core;

import org.bukkit.entity.Player;

public class PrestigeData {

    private int score = 0;
    private int level = 0;

    public PrestigeData(Player player) {

    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int lvl() {
        return level;
    }

    public int updateLevel() {
        return 0;
    }

}
