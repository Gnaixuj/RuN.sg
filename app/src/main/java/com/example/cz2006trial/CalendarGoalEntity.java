package com.example.cz2006trial;

import java.util.ArrayList;

public class CalendarGoalEntity {

    private String username;
    private ArrayList<GoalEntity> allGoals = new ArrayList<GoalEntity>();

    public CalendarGoalEntity() {
        GoalEntity B = new GoalEntity("21-03-2020", 11, 10);
        GoalEntity C = new GoalEntity("22-03-2020", 4.5, 5.5);
        allGoals.add(B);
        allGoals.add(C);
        username = "fazli";
    }

    public ArrayList<GoalEntity> getAllGoals() {
        return allGoals;
    }


}
