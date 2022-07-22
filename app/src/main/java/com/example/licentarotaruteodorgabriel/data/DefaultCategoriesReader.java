package com.example.licentarotaruteodorgabriel.data;

import java.util.ArrayList;

public class DefaultCategoriesReader {
    public static ArrayList<String> get(){
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Sport");
        categories.add("Work");
        categories.add("Health");
        categories.add("Nutrition");
        categories.add("Finance");
        categories.add("Mental health");
        categories.add("Other");
        return categories;
    }
}
