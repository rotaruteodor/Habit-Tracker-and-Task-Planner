package com.example.licentarotaruteodorgabriel.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private String name;
    private String description;
    private Long dueDate;
    private Integer priority;
    private String category;
    private Boolean isDone;
    private String firebaseId;
    private int color;
    private Long completionDate;
    private int notificationId;

    public Task() {
    }

    public Task(String name, String description, Long dueDate,
                Integer priority, String category, String firebaseId,
                int color, int notificationId) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.firebaseId = firebaseId;
        isDone = false;
        this.color = color;
        completionDate = null;
        this.notificationId = notificationId;
    }

    public Long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Long completionDate) {
        this.completionDate = completionDate;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
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

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean done) {
        isDone = done;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", category='" + category + '\'' +
                ", isDone=" + isDone +
                ", firebaseId='" + firebaseId + '\'' +
                ", color=" + color +
                ", completionDate=" + completionDate +
                '}';
    }

    private Task(Parcel source) {
        name = source.readString();
        description = source.readString();
        dueDate = source.readLong();
        priority = source.readInt();
        category = source.readString();
        isDone = Boolean.valueOf(source.readString());
        color = source.readInt();
        firebaseId = source.readString();
        completionDate = (Long) source.readValue(Long.class.getClassLoader());
        notificationId = source.readInt();
    }

    public static Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
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
        dest.writeLong(dueDate);
        dest.writeInt(priority);
        dest.writeString(category);
        dest.writeString(isDone.toString());
        dest.writeInt(color);
        dest.writeString(firebaseId);
        dest.writeValue(completionDate);
        dest.writeInt(notificationId);
    }

}
