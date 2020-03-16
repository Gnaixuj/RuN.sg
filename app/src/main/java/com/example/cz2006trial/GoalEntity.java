package com.example.cz2006trial;

public class GoalEntity {

    private String date;
    private double distance;
    private double target;

    public GoalEntity(String date, double distance, double target) {
        this.date = date;
        this.distance = distance;
        this.target = target;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }
}
