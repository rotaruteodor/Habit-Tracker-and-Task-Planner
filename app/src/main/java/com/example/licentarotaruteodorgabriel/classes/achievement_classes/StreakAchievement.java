package com.example.licentarotaruteodorgabriel.classes.achievement_classes;

public class StreakAchievement extends Achievement {
    private int minimumStreak;

    public StreakAchievement(String name, int minimumStreak) {
        super(name);
        this.minimumStreak = minimumStreak;
    }

    public int getMinimumStreak() {
        return minimumStreak;
    }
}
