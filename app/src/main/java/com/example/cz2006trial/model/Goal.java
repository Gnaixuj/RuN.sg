package com.example.cz2006trial.model;

public class Goal {

    // the date user made a goal
    private String date;
    // distance travelled by user on a particular date
    private double distance;
    // target distance set by user on a particular date
    private double target;

    // a constructor used dynamically when an instance of Goal is created from data retrieved from firebase database
    public Goal() {

    }

    // a constructor mainly used to create an instance of Goal to update data on firebase database
    public Goal(String date, double distance, double target) {
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
