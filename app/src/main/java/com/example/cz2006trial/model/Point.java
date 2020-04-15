package com.example.cz2006trial.model;

import com.google.android.gms.maps.model.LatLng;

public class Point {
    // name of a point marker on a map
    private String name;
    // coordinates of a point marker on a map
    private LatLng location;
    // description of a point marker on a map
    private String description;
    // type of a point marker on a map
    private String type;

    // constructor to initialize Point
    public Point(String name, LatLng location, String description, String type) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
