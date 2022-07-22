package com.example.licentarotaruteodorgabriel.classes.achievement_classes;

public class StrengthAchievement extends Achievement {
    private double minimumStrength;

    public StrengthAchievement(String name, double minimumStrength) {
        super(name);
        this.minimumStrength = minimumStrength;
    }

    public double getMinimumStrength() {
        return minimumStrength;
    }
}
