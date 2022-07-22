package com.example.licentarotaruteodorgabriel.classes.achievement_classes;

public abstract class Achievement {
    private String name;

    public Achievement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
