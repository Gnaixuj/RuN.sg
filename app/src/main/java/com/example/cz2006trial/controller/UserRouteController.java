package com.example.cz2006trial.controller;

import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.model.Marker;

public class UserRouteController {

    // set the coordinates and name of the starting location to userRoute entity
    public static void setStartMarkerInfo(UserRoute userRoute, Marker marker) {
        userRoute.startPointUpdate(marker.getPosition());
        userRoute.setStartPointName(marker.getTitle());
    }

    // set the coordinates and name of the ending location to userRoute entity
    public static void setEndMarkerInfo(UserRoute userRoute, Marker marker) {
        userRoute.endPointUpdate(marker.getPosition());
        userRoute.setEndPointName(marker.getTitle());
    }

    // get the name of the starting location to userRoute entity
    public static String getStartPointName(UserRoute userRoute) {
        return userRoute.getStartPointName();
    }

    // get the name of the ending location to userRoute entity
    public static String getEndPointName(UserRoute userRoute) {
        return userRoute.getEndPointName();
    }

    // set created route's distance and estimated time taken to travel that route to userRoute entity
    public static void setDistanceTimeTaken(UserRoute userRoute, String displayDistance, String displayTravelTime) {
        userRoute.setDistance(displayDistance);
        userRoute.setTimeTaken(displayTravelTime);
    }
}
