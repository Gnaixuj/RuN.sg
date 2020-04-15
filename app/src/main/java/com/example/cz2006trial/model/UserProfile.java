package com.example.cz2006trial.model;

public class UserProfile {

    // the user's username
    private String username;
    // the user's email
    private String email;
    // the user's date of birth
    private String DOB;
    // the user's height
    private double height;
    // the user's weight
    private double weight;
    // the user's BMI
    private double BMI;

    // a constructor used dynamically when an instance of UserProfile is created from data retrieved from firebase database
    public UserProfile() {

    }

    // a constructor mainly used to create an instance of UserProfile to update data on firebase database
    public UserProfile(String username, String email, String DOB, double height, double weight, double BMI) {
        this.username = username;
        this.email = email;
        this.DOB = DOB;
        this.height = height;
        this.weight = weight;
        this.BMI = BMI;
    }

    // a constructor mainly used to create an instance of UserProfile to update data on firebase database
    public UserProfile(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getBMI() {
        return BMI;
    }

    public void setBMI(double BMI) {
        this.BMI = BMI;
    }
}
