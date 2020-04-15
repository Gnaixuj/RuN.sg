package com.example.cz2006trial.model;

import java.util.Date;

public class UserLocation {

    // latitude of user location on map
    private double latitude;
    // longitude of user location on map
    private double longitude;
    // the date
    private Date timestamp;

    // a constructor used dynamically when an instance of UserLocation is created from data retrieved from firebase database
    public UserLocation() {

    }

    // a constructor mainly used to create an instance of UserLocation to update data on firebase database
    public UserLocation(double latitude, double longitude, Date timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
