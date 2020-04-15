package com.example.cz2006trial.model;

import java.util.ArrayList;
import java.util.Date;

public class UserLocationSession {

    // an array list to store instances of userLocation, also defines the tracking route
    private ArrayList<UserLocation> session = new ArrayList<>();
    // the date
    private Date timestamp;
    // the total tracking distance
    private double distance;
    // the total time taken during tracking
    private String timeTaken;

    // a constructor used dynamically when an instance of UserLocationSession is created from data retrieved from firebase database
    public UserLocationSession() {

    }

    // a constructor to initialize userLocationSession with current date
    public UserLocationSession(Date timestamp) {
        this.timestamp = timestamp;
        distance = 0;
    }

    // a constructor mainly used to create an instance of UserLocationSession to update data on firebase database
    public UserLocationSession(Date timestamp, double distance, String timeTaken) {
        this.timestamp = timestamp;
        this.distance = distance;
        this.timeTaken = timeTaken;
    }

    // add instance of userLocation to session list and add distance travelled by user during tracking to attribute distance
    public void addUserLocation(UserLocation userLocation) {
        session.add(userLocation);
        if (!session.isEmpty()) {
            UserLocation prevUserLocation = session.get(session.size() - 1);
            double addDistance = Math.sqrt(Math.pow(prevUserLocation.getLatitude() - userLocation.getLatitude(), 2)
                    + Math.pow(prevUserLocation.getLongitude() - userLocation.getLongitude(), 2));
            distance += Math.round(addDistance * 10) / 10.0;
        }
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
