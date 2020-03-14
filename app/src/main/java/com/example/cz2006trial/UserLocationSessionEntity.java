package com.example.cz2006trial;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class UserLocationSessionEntity {

    private ArrayList<UserLocationEntity> session = new ArrayList<>();
    private Date timestamp;
    private double distance;
    private String timeTaken;

    public UserLocationSessionEntity() {

    }

    public UserLocationSessionEntity(Date timestamp) {
        this.timestamp = timestamp;
        distance = 0;
    }

    public UserLocationSessionEntity(ArrayList<UserLocationEntity> session, Date timestamp, double distance) {
        this.session = session;
        this.timestamp = timestamp;
        this.distance = distance;
    }

    public UserLocationSessionEntity(Date timestamp, double distance, String timeTaken) {
        this.timestamp = timestamp;
        this.distance = distance;
        this.timeTaken = timeTaken;
    }

    public void addUserLocation(UserLocationEntity userLocation) {
        session.add(userLocation);
        if (!session.isEmpty()) {
            UserLocationEntity prevUserLocation = session.get(session.size() - 1);
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

    public ArrayList<UserLocationEntity> getSession() {
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
