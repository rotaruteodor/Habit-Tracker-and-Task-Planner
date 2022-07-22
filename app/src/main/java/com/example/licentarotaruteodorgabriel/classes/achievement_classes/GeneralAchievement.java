package com.example.licentarotaruteodorgabriel.classes.achievement_classes;

public class GeneralAchievement extends Achievement {
    private String description;

    public GeneralAchievement(String name, String description) {
        super(name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
