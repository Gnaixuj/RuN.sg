package com.example.cz2006trial.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class UserRoute {

    // the date
    private Date date;
    // the name of the starting location marker
    private String startPointName;
    // the name of the ending location marker
    private String endPointName;
    // the latitude of the starting location marker
    private double startLatitude;
    // the longitude of the starting location marker
    private double startLongitude;
    // the latitude of the ending location marker
    private double endLatitude;
    // the longitude of the ending location marker
    private double endLongitude;
    // the total distance of created route
    private String distance;
    // the estimated time taken to travel the route
    private String timeTaken;

    // a constructor mainly used to create an instance of UserRoute to update data on firebase database
    public UserRoute() {

    }

    // a constructor mainly used to create an instance of UserRoute to update data on firebase database
    public UserRoute(Date date, String startPointName, String endPointName, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String distance, String timeTaken) {
        this.date = date;
        this.startPointName = startPointName;
        this.endPointName = endPointName;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
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

    public LatLng startPointRetrieve() {
        return new LatLng(startLatitude, startLongitude);
    }

    // update both latitude and longitude of startPoint
    public void startPointUpdate(LatLng startPoint) {
        startLatitude = startPoint.latitude;
        startLongitude = startPoint.longitude;
    }

    public LatLng endPointRetrieve() {
        return new LatLng(endLatitude, endLongitude);
    }

    // update both latitude and longitude of endPoint
    public void endPointUpdate(LatLng endPoint) {
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
