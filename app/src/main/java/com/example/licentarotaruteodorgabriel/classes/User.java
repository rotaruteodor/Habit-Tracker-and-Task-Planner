package com.example.licentarotaruteodorgabriel.classes;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.data.DefaultCategoriesReader;
import com.example.licentarotaruteodorgabriel.enums.Gender;

import java.time.LocalDate;
import java.util.ArrayList;

public class User implements Parcelable {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Long dateOfBirth;
    private Gender gender;
    private Boolean rememberLoginCredentials;
    private ArrayList<String> categories;
    private String firebaseId;
    private int wakeupHour;
    private int wakeupMinute;
    private int sleepHour;
    private int sleepMinute;

    public User() {
    }

    public User(String firstname, String lastname, String email,
                String password, Long dateOfBirth, Gender gender,
                Boolean rememberLoginCredentials, String firebaseId,
                int wakeupHour, int wakeupMinute, int sleepHour, int sleepMinute) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.rememberLoginCredentials = rememberLoginCredentials;
        this.categories = DefaultCategoriesReader.get();
        this.firebaseId = firebaseId;
        this.wakeupHour = wakeupHour;
        this.wakeupMinute = wakeupMinute;
        this.sleepHour = sleepHour;
        this.sleepMinute = sleepMinute;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getRememberLoginCredentials() {
        return rememberLoginCredentials;
    }

    public void setRememberLoginCredentials(Boolean rememberLoginCredentials) {
        this.rememberLoginCredentials = rememberLoginCredentials;
    }

    public int getWakeupHour() {
        return wakeupHour;
    }

    public void setWakeupHour(int wakeupHour) {
        this.wakeupHour = wakeupHour;
    }

    public int getWakeupMinute() {
        return wakeupMinute;
    }

    public void setWakeupMinute(int wakeupMinute) {
        this.wakeupMinute = wakeupMinute;
    }

    public int getSleepHour() {
        return sleepHour;
    }

    public void setSleepHour(int sleepHour) {
        this.sleepHour = sleepHour;
    }

    public int getSleepMinute() {
        return sleepMinute;
    }

    public void setSleepMinute(int sleepMinute) {
        this.sleepMinute = sleepMinute;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int obtainAgeInYears() {
        return (int) ((LocalDate.now().toEpochDay() - dateOfBirth) / 365);
    }


    @Override
    public String toString() {
        return "User{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", rememberLoginCredentials=" + rememberLoginCredentials +
                ", categories=" + categories +
                ", firebaseId='" + firebaseId + '\'' +
                ", wakeupHour=" + wakeupHour +
                ", wakeupMinute=" + wakeupMinute +
                ", sleepHour=" + sleepHour +
                ", sleepMinute=" + sleepMinute +
                '}';
    }


    private User(Parcel source) {
        firstname = source.readString();
        lastname = source.readString();
        email = source.readString();
        password = source.readString();
        dateOfBirth = source.readLong();
        gender = Gender.valueOf(source.readString());
        rememberLoginCredentials = Boolean.valueOf(source.readString());
        categories = (ArrayList<String>) source.readSerializable();
        firebaseId = source.readString();
        wakeupHour = source.readInt();
        wakeupMinute = source.readInt();
        sleepHour = source.readInt();
        sleepMinute = source.readInt();
    }

    public static Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeLong(dateOfBirth);
        dest.writeString(gender.name());
        dest.writeString(rememberLoginCredentials.toString());
        dest.writeSerializable(categories);
        dest.writeString(firebaseId);
        dest.writeInt(wakeupHour);
        dest.writeInt(wakeupMinute);
        dest.writeInt(sleepHour);
        dest.writeInt(sleepMinute);
    }
}
