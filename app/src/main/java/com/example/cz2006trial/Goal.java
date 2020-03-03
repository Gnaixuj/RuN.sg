package com.example.cz2006trial;

import java.util.ArrayList;

public class Goal {

    private String username;
    private ArrayList<DailyGoal> allGoals = new ArrayList<DailyGoal>();

    public Goal() {
        DailyGoal A = new DailyGoal("20/03/2020",9,10);
        DailyGoal B = new DailyGoal("21/03/2020",11,10);
        DailyGoal C = new DailyGoal("22/03/2020", 4.5, 5.5);
        allGoals.add(A);
        allGoals.add(B);
        allGoals.add(C);
        username = "fazli";
    }

    public ArrayList<DailyGoal> getAllGoals() {
        return allGoals;
    }


}
