package com.example.cz2006trial.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class UserRoute {

    private Date date;
    private String startPointName;
    private String endPointName;

    //private LatLng startPoint;
    private double startLatitude;
    private double startLongitude;
    //private LatLng endPoint;
    private double endLatitude;
    private double endLongitude;
    private String distance;
    private String timeTaken;

    public UserRoute() {

    }

    public UserRoute(Date date, String startPointName, String endPointName, LatLng startPoint, LatLng endPoint, String distance, String timeTaken) {
        this.date = date;
        this.startPointName = startPointName;
        this.endPointName = endPointName;
        this.startLatitude = startPoint.latitude;
        this.startLongitude = startPoint.longitude;
        this.endLatitude = endPoint.latitude;
        this.endLongitude = endPoint.longitude;
        this.distance = distance;
        this.timeTaken = timeTaken;
    }

    public String output() {
        return startPointName + " " + endPointName;
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
        return new LatLng(startLatitude, startLongitude);
    }

    public void setStartPoint(LatLng startPoint) {
        startLatitude = startPoint.latitude;
        startLongitude = startPoint.longitude;
    }

    public LatLng getEndPoint() {
        return new LatLng(endLatitude, endLongitude);
    }

    public void setEndPoint(LatLng endPoint) {
        endLatitude = endPoint.latitude;
        endLongitude = endPoint.longitude;
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

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }
}
