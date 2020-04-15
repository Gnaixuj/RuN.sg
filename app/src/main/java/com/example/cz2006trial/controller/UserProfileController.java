package com.example.cz2006trial.controller;

public class UserProfileController {

    // calculate BMI of user based on the user's height and weight
    public static double calculateBMI(double height, double weight) {
        height = height / 100.0;
        double bmi = weight / (height * height);
        return Math.round(bmi * 10) / 10.0;
    }

}
