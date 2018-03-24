package org.soraworld.prestige.core;

import org.bukkit.entity.Player;

public final class PrestigeData {

    private int score = 0;
    private Level level = new Level();

    public PrestigeData(Player player) {

    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public Level getLevel() {
        return level;
    }


    public int updateLevel() {
        return 0;
    }

}
