package com.example.cz2006trial;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocationEntity {

    private double[] geo_point;
    private @ServerTimestamp
    Date timestamp;

    public UserLocationEntity() {}

    public UserLocationEntity(double[] geo_point) {
        this.geo_point = geo_point;
    }

    public double[] getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(double[] geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserLocationEntity{" +
                "geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                '}';
    }
}
