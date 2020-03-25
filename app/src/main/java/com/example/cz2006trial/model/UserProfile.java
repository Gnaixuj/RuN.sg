package com.example.cz2006trial.model;

public class UserProfile {

    private String username;
    private String email;
    private String DOB;
    private double height;
    private double weight;
    private double BMI;

    public UserProfile() {

    }

    public UserProfile(String username, String email, String DOB, double height, double weight, double BMI) {
        this.username = username;
        this.email = email;
        this.DOB = DOB;
        this.height = height;
        this.weight = weight;
        this.BMI = BMI;
    }

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
