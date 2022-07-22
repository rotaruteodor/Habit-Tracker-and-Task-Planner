package com.example.licentarotaruteodorgabriel.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class TasksStats implements Parcelable {
    private int nrOfAllTasks;
    private int nrOfAllTasksCompleted;
    private int nrOfAllTasksCompletedBeforeDue;
    private int nrOfAllTasksCompletedAfterDue;
    private double totalDeviation;

    public TasksStats() {
        nrOfAllTasks = 0;
        nrOfAllTasksCompleted = 0;
        nrOfAllTasksCompletedBeforeDue = 0;
        nrOfAllTasksCompletedAfterDue = 0;
        totalDeviation = 0;
    }

    public double getTotalDeviation() {
        return totalDeviation;
    }

    public void setTotalDeviation(double totalDeviation) {
        this.totalDeviation = totalDeviation;
    }

    public int getNrOfAllTasks() {
        return nrOfAllTasks;
    }

    public void setNrOfAllTasks(int nrOfAllTasks) {
        this.nrOfAllTasks = nrOfAllTasks;
    }

    public int getNrOfAllTasksCompleted() {
        return nrOfAllTasksCompleted;
    }

    public void setNrOfAllTasksCompleted(int nrOfAllTasksCompleted) {
        this.nrOfAllTasksCompleted = nrOfAllTasksCompleted;
    }

    public int getNrOfAllTasksCompletedBeforeDue() {
        return nrOfAllTasksCompletedBeforeDue;
    }

    public void setNrOfAllTasksCompletedBeforeDue(int nrOfAllTasksCompletedBeforeDue) {
        this.nrOfAllTasksCompletedBeforeDue = nrOfAllTasksCompletedBeforeDue;
    }

    public int getNrOfAllTasksCompletedAfterDue() {
        return nrOfAllTasksCompletedAfterDue;
    }

    public void setNrOfAllTasksCompletedAfterDue(int nrOfAllTasksCompletedAfterDue) {
        this.nrOfAllTasksCompletedAfterDue = nrOfAllTasksCompletedAfterDue;
    }


    public void addToNrOfAllTasks(int number){
        nrOfAllTasks+=number;
    }
    public void subtractFromNrOfAllTasks(int number){
        nrOfAllTasks-=number;
    }

    public void addToNrOfAllTasksCompleted(int number){
        nrOfAllTasksCompleted+=number;
    }
    public void subtractFromNrOfAllTasksCompleted(int number){
        nrOfAllTasksCompleted-=number;
    }

    public void addToNrOfAllTasksCompletedBeforeDue(int number){
        nrOfAllTasksCompletedBeforeDue+=number;
    }
    public void subtractFromNrOfAllTasksCompletedBeforeDue(int number){
        nrOfAllTasksCompletedBeforeDue-=number;
    }

    public void addToNrOfAllTasksCompletedAfterDue(int number){
        nrOfAllTasksCompletedAfterDue+=number;
    }
    public void subtractFromNrOfAllTasksCompletedAfterDue(int number){
        nrOfAllTasksCompletedAfterDue-=number;
    }

    public void addToTotalDeviation(double number){
        totalDeviation+=number;
    }
    public void subtractFromTotalDeviation(double number){
        nrOfAllTasks-=number;
    }

    private TasksStats(Parcel source) {
        nrOfAllTasks = source.readInt();
        nrOfAllTasksCompleted = source.readInt();
        nrOfAllTasksCompletedBeforeDue = source.readInt();
        nrOfAllTasksCompletedAfterDue = source.readInt();
        totalDeviation = source.readDouble();
    }

    public static Creator<TasksStats> CREATOR = new Creator<TasksStats>() {
        @Override
        public TasksStats createFromParcel(Parcel source) {
            return new TasksStats(source);
        }

        @Override
        public TasksStats[] newArray(int size) {
            return new TasksStats[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(nrOfAllTasks);
        dest.writeInt(nrOfAllTasksCompleted);
        dest.writeInt(nrOfAllTasksCompletedBeforeDue);
        dest.writeInt(nrOfAllTasksCompletedAfterDue);
        dest.writeDouble(totalDeviation);
    }
}
