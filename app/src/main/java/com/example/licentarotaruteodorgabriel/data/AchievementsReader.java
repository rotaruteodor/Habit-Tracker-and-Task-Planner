package com.example.licentarotaruteodorgabriel.data;

import android.content.Context;

import com.example.licentarotaruteodorgabriel.classes.achievement_classes.GeneralAchievement;
import com.example.licentarotaruteodorgabriel.classes.achievement_classes.StreakAchievement;
import com.example.licentarotaruteodorgabriel.classes.achievement_classes.StrengthAchievement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class AchievementsReader {
    public static ArrayList<StreakAchievement> getStreakAchievements(Context context) {
        ArrayList<StreakAchievement> achievements = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("streak_achievements.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            JSONArray achievementsJSONArray = new JSONArray(jsonString);
            for (int i = 0; i < achievementsJSONArray.length(); i++) {
                JSONObject achievement = achievementsJSONArray.getJSONObject(i);
                String name = achievement.getString("name");
                int minStreak = achievement.getInt("minimumStreak");
                achievements.add(new StreakAchievement(name, minStreak));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return achievements;
    }


    public static ArrayList<StrengthAchievement> getStrengthAchievements(Context context) {
        ArrayList<StrengthAchievement> achievements = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("strength_achievements.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONArray achievementsJSONArray = new JSONArray(jsonString);
            for (int i = 0; i < achievementsJSONArray.length(); i++) {
                JSONObject achievement = achievementsJSONArray.getJSONObject(i);
                String name = achievement.getString("name");
                double minStrength = achievement.getDouble("minimumStrength");
                achievements.add(new StrengthAchievement(name, minStrength));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return achievements;
    }


    public static ArrayList<GeneralAchievement> getGeneralAchievements(Context context) {
        ArrayList<GeneralAchievement> achievements = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("general_achievements.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONArray achievementsJSONArray = new JSONArray(jsonString);
            for (int i = 0; i < achievementsJSONArray.length(); i++) {
                JSONObject achievement = achievementsJSONArray.getJSONObject(i);
                String name = achievement.getString("name");
                String description = achievement.getString("description");
                achievements.add(new GeneralAchievement(name, description));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return achievements;
    }
}
