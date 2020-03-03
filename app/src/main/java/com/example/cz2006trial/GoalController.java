package com.example.cz2006trial;

import java.util.ArrayList;

public class GoalController {
    private static Goal goal = new Goal();

    public static double getDailyGoalDistance(String date) {
        for (int i = 0; i < goal.getAllGoals().size();i++) {
            if (goal.getAllGoals().get(i).getGoalDate().equals(date)) {
                return goal.getAllGoals().get(i).getDailyGoalDistance();
            }
        }
        return 0;
    }

    public static double getDailyGoalTarget(String date) {
        for (int i = 0; i < goal.getAllGoals().size();i++) {
            if (goal.getAllGoals().get(i).getGoalDate().equals(date)) {
                return goal.getAllGoals().get(i).getDailyGoalTarget();
            }
        }
        return -1;
    }

    public static String validateGoalFields(String dailyTarget,double distance) {
        if (dailyTarget.equals("")) {
            return "Please enter a positive value";
        }
        double dailyTargetDouble = Double.parseDouble(dailyTarget);
        if (dailyTargetDouble < distance) {
            return "Please enter a positive value higher than the current distance travelled";
        }
        else if (dailyTargetDouble >= 100) {
            return "Please enter a positive value lower than 100";
        }
        return "success";
    }

    /*public static boolean validateGoalFields(double weeklyTarget, double DailyTarget) {

        return false;
    }*/

    public static void updateDailyTarget(String date, double distance) {
        for (int i = 0; i < goal.getAllGoals().size();i++) {
            if (goal.getAllGoals().get(i).getGoalDate().equals(date)) {
                goal.getAllGoals().get(i).setDailyGoalTarget(distance);
                break;
            }
        }
    }

    public static void updateDailyProgress(double distance) {

    }

    public static void updateWeeklyProgress(double distance) {

    }

    public static void appendNewGoal(DailyGoal newGoal) {
        goal.getAllGoals().add(newGoal);
    }
}
