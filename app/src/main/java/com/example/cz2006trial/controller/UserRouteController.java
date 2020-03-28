package com.example.cz2006trial.controller;

import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class UserRouteController {

    public static void setStartMarkerInfo(UserRoute userRoute, Marker marker) {
        userRoute.setStartPoint(marker.getPosition());
        userRoute.setStartPointName(marker.getTitle());
    }

    public static void setEndMarkerInfo(UserRoute userRoute, Marker marker) {
        userRoute.setEndPoint(marker.getPosition());
        userRoute.setEndPointName(marker.getTitle());
    }

    public static String getStartPointName(UserRoute userRoute) {
        return userRoute.getStartPointName();
    }

    public static String getEndPointName(UserRoute userRoute) {
        return userRoute.getEndPointName();
    }

    public static LatLng getStartPointPos(UserRoute userRoute) {
        return userRoute.getStartPoint();
    }

    public static LatLng getEndPointPos(UserRoute userRoute) {
        return userRoute.getEndPoint();
    }

    public static void updateUserRouteDatabase(UserRoute userRoute) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date = Calendar.getInstance().getTime();
        String dateString = date.toString();
        DatabaseReference databaseUserSavedRoutes = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes").child(dateString);
        UserRoute userSavedRoute = new UserRoute(
                date,
                userRoute.getStartPointName(),
                userRoute.getEndPointName(),
                userRoute.getStartPoint(),
                userRoute.getEndPoint(),
                userRoute.getDistance(),
                userRoute.getTimeTaken());
        databaseUserSavedRoutes.setValue(userSavedRoute);
    }

    public static void setDistanceTimeTaken(UserRoute userRoute, String displayDistance, String displayTravelTime) {
        userRoute.setDistance(displayDistance);
        userRoute.setTimeTaken(displayTravelTime);
    }
}
