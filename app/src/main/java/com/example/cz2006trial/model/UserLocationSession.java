package com.example.cz2006trial.model;

import java.util.ArrayList;
import java.util.Date;

public class UserLocationSession {

    private ArrayList<UserLocation> session = new ArrayList<>();
    private Date timestamp;
    private double distance;
    private String timeTaken;

    public UserLocationSession() {

    }

    public UserLocationSession(Date timestamp) {
        this.timestamp = timestamp;
        distance = 0;
    }

    public UserLocationSession(Date timestamp, double distance, String timeTaken) {
        this.timestamp = timestamp;
        this.distance = distance;
        this.timeTaken = timeTaken;
    }

    public void addUserLocation(UserLocation userLocation) {
        session.add(userLocation);
        if (!session.isEmpty()) {
            UserLocation prevUserLocation = session.get(session.size() - 1);
            double addDistance = Math.sqrt(Math.pow(prevUserLocation.getLatitude() - userLocation.getLatitude(), 2)
                    + Math.pow(prevUserLocation.getLongitude() - userLocation.getLongitude(), 2));
            distance += Math.round(addDistance * 10) / 10.0;
        }
    }

    public void clearAllUserLocation() {
        distance = 0;
        session.clear();
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public ArrayList<UserLocation> getSession() {
        return session;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void addDistance(double distance) {
        this.distance += distance;
    }

}
