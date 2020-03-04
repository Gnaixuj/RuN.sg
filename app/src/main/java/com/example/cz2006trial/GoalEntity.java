package com.example.cz2006trial;

public class GoalEntity {

    private String goalDate;
    private double dailyGoalDistance;
    private double dailyGoalTarget;

    public GoalEntity(String goalDate) {
        this.goalDate = goalDate;
        dailyGoalDistance = 0;
        dailyGoalTarget = -1;
    }

    public GoalEntity(String goalDate, double dailyGoalDistance, double dailyGoalTarget) {
        this.goalDate = goalDate;
        this.dailyGoalDistance = dailyGoalDistance;
        this.dailyGoalTarget = dailyGoalTarget;
    }

    public void setGoalDate(String goalDate) {
        this.goalDate = goalDate;
    }

    public String getGoalDate() {
        return goalDate;
    }

    public void setDailyGoalDistance(double dailyGoalDistance) {
        this.dailyGoalDistance = dailyGoalDistance;
    }

    public void setDailyGoalTarget(double dailyGoalTarget) {
        this.dailyGoalTarget = dailyGoalTarget;
    }

    public double getDailyGoalDistance() {
        return dailyGoalDistance;
    }

    public double getDailyGoalTarget() {
        return dailyGoalTarget;
    }

}
