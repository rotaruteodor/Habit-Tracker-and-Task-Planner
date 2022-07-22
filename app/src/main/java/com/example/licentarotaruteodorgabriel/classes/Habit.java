package com.example.licentarotaruteodorgabriel.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Habit implements Parcelable {
    private String name;
    private String description;
    private Long startDate;
    private HabitEvaluationType evaluationType;
    private Double numericalGoal;
    private HabitNumericalComparisonType numericalComparisonType;
    private String numericalUnit;
    private HabitFrequency frequency;
    private ArrayList<Integer> daysOfWeek;
    private ArrayList<Integer> daysOfMonth;
    private Integer numberOfDaysForRepeat;
    private HashMap<String, Double> realizations;
    private String category;
    private String firebaseId;
    private Boolean isArchived;
    private int color;
    private int daysToFormHabit;

    public Habit() {
    }

    public Habit(String name, String description, Long startDate,
                 HabitEvaluationType evaluationType,
                 Double numericalGoal, HabitNumericalComparisonType numericalComparisonType,
                 String numericalUnit, HabitFrequency frequency,
                 ArrayList<Integer> daysOfWeek, ArrayList<Integer> daysOfMonth, Integer numberOfDaysForRepeat, String category,
                 String firebaseId, Boolean isArchived, int color, HashMap<String, Double> realizations, int daysToFormHabit) {

        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.evaluationType = evaluationType;
        this.numericalGoal = numericalGoal;
        this.numericalComparisonType = numericalComparisonType;
        this.numericalUnit = numericalUnit;
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.daysOfMonth = daysOfMonth;
        this.numberOfDaysForRepeat = numberOfDaysForRepeat;
        this.category = category;
        this.firebaseId = firebaseId;
        this.isArchived = isArchived;
        this.color = color;
        this.realizations = realizations;
        this.daysToFormHabit = daysToFormHabit;
    }

    public Habit(String name, String description, Long startDate,
                 HabitEvaluationType evaluationType,
                 Double numericalGoal, HabitNumericalComparisonType numericalComparisonType,
                 String numericalUnit, HabitFrequency frequency,
                 ArrayList<Integer> daysOfWeek, ArrayList<Integer> daysOfMonth,
                 Integer numberOfDaysForRepeat,
                 String category,
                 String firebaseId, Boolean isArchived, int color, int daysToFormHabit) {

        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.evaluationType = evaluationType;
        this.numericalGoal = numericalGoal;
        this.numericalComparisonType = numericalComparisonType;
        this.numericalUnit = numericalUnit;
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.daysOfMonth = daysOfMonth;
        this.numberOfDaysForRepeat = numberOfDaysForRepeat;
        this.category = category;
        this.firebaseId = firebaseId;
        this.isArchived = isArchived;
        this.color = color;
        this.realizations = new HashMap<>();
        this.daysToFormHabit = daysToFormHabit;
    }

    public int getDaysToFormHabit() {
        return daysToFormHabit;
    }

    public void setDaysToFormHabit(int daysToFormHabit) {
        this.daysToFormHabit = daysToFormHabit;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public HabitEvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(HabitEvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public Double getNumericalGoal() {
        return numericalGoal;
    }

    public void setNumericalGoal(Double numericalGoal) {
        this.numericalGoal = numericalGoal;
    }

    public HabitNumericalComparisonType getNumericalComparisonType() {
        return numericalComparisonType;
    }

    public void setNumericalComparisonType(HabitNumericalComparisonType numericalComparisonType) {
        this.numericalComparisonType = numericalComparisonType;
    }

    public String getNumericalUnit() {
        return numericalUnit;
    }

    public void setNumericalUnit(String numericalUnit) {
        this.numericalUnit = numericalUnit;
    }

    public HabitFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(HabitFrequency frequency) {
        this.frequency = frequency;
    }

    public ArrayList<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(ArrayList<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public ArrayList<Integer> getDaysOfMonth() {
        return daysOfMonth;
    }

    public void setDaysOfMonth(ArrayList<Integer> daysOfMonth) {
        this.daysOfMonth = daysOfMonth;
    }

    public Integer getNumberOfDaysForRepeat() {
        return numberOfDaysForRepeat;
    }

    public void setNumberOfDaysForRepeat(Integer numberOfDaysForRepeat) {
        this.numberOfDaysForRepeat = numberOfDaysForRepeat;
    }

    public HashMap<String, Double> getRealizations() {
        return realizations;
    }

    public void setRealizations(HashMap<String, Double> realizations) {
        this.realizations = realizations;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }


    @Override
    public String toString() {
        return "Habit{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", evaluationType=" + evaluationType +
                ", numericalGoal=" + numericalGoal +
                ", numericalComparisonType=" + numericalComparisonType +
                ", numericalUnit='" + numericalUnit + '\'' +
                ", frequency=" + frequency +
                ", daysOfWeek=" + daysOfWeek +
                ", daysOfMonth=" + daysOfMonth +
                ", numberOfDaysForRepeat=" + numberOfDaysForRepeat +
                ", realizations=" + realizations +
                ", category='" + category + '\'' +
                ", firebaseId='" + firebaseId + '\'' +
                ", isArchived=" + isArchived +
                ", color=" + color +
                ", daysToFormHabit=" + daysToFormHabit +
                '}';
    }


    public void updateRealizationValue(String key, Double newValue) {
        realizations.put(key, newValue);
    }

    public void removeRealization(String realizationDate) {
        realizations.remove(realizationDate);
    }

    private Habit(Parcel source) {
        name = source.readString();
        description = source.readString();
        startDate = source.readLong();
        evaluationType = HabitEvaluationType.valueOf(source.readString());
        numericalGoal = (Double) source.readSerializable();
        String auxNumericalComparisonType = source.readString();
        if (auxNumericalComparisonType == null) {
            numericalComparisonType = null;
        } else {
            numericalComparisonType = HabitNumericalComparisonType.valueOf(auxNumericalComparisonType);
        }
        numericalUnit = source.readString();
        frequency = HabitFrequency.valueOf(source.readString());
        daysOfWeek = (ArrayList<Integer>) source.readSerializable();
        daysOfMonth = (ArrayList<Integer>) source.readSerializable();
        numberOfDaysForRepeat = (Integer) source.readSerializable();
        category = source.readString();
        firebaseId = source.readString();
        isArchived = Boolean.valueOf(source.readString());
        color = source.readInt();
        daysToFormHabit = source.readInt();
        int realizationsSize = source.readInt();
        realizations = new HashMap<>();
        for (int i = 0; i < realizationsSize; i++) {
            String key = source.readString();
            Double value = (Double) source.readValue(Double.class.getClassLoader());
            realizations.put(key, value);
        }

    }

    public static Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel source) {
            return new Habit(source);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(startDate);
        dest.writeString(evaluationType.name());
        dest.writeSerializable(numericalGoal);
        if (numericalComparisonType == null) {
            dest.writeString(null);
        } else {
            dest.writeString(numericalComparisonType.name());
        }
        dest.writeString(numericalUnit);
        dest.writeString(frequency.name());
        dest.writeSerializable(daysOfWeek);
        dest.writeSerializable(daysOfMonth);
        dest.writeSerializable(numberOfDaysForRepeat);
        dest.writeString(category);
        dest.writeString(firebaseId);
        dest.writeString(isArchived.toString());
        dest.writeInt(color);
        dest.writeInt(daysToFormHabit);
        dest.writeInt(realizations.size());
        for (Map.Entry<String, Double> entry : realizations.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }
}
