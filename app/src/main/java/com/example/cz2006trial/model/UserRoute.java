package com.example.cz2006trial.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class UserRoute {

    private Date date;
    private String startPointName;
    private String endPointName;

    private LatLng startPoint;
    private LatLng endPoint;
    private String distance;
    private String timeTaken;

    public UserRoute() {

    }

    public UserRoute(Date date, String startPointName, String endPointName, LatLng startPoint, LatLng endPoint, String distance, String timeTaken) {
        this.date = date;
        this.startPointName = startPointName;
        this.endPointName = endPointName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distance = distance;
        this.timeTaken = timeTaken;
    }

    public String getStartPointName() {
        return startPointName;
    }

    public void setStartPointName(String startPointName) {
        this.startPointName = startPointName;
    }

    public String getEndPointName() {
        return endPointName;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }

    public LatLng getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
