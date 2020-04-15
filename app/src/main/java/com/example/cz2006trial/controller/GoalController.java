package com.example.cz2006trial.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoalController {

    // convert date in string format to date format
    public static Date convertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    // validate goal fields to ensure error checking. Based on the error, return corresponding error message
    public static String validateGoalFields(String target) {

        if (target.equals("")) {
            return "noEdit";
        }

        double targetDouble = Double.parseDouble(target);
        if (targetDouble == 0) {
            return "Please enter a positive value";
        } else if (targetDouble >= 100) {
            return "Please enter a positive value lower than 100";
        }

        return "Goal Target Updated";
    }
}
