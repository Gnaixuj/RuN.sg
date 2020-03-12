package com.example.cz2006trial;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocationEntity {

    private GeoPoint geo_point;
    private @ServerTimestamp
    Date timestamp;

    public UserLocationEntity() {}

    public UserLocationEntity(GeoPoint geo_point, Date timestamp) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
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
