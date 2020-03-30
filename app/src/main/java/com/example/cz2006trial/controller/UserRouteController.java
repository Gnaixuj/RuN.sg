package com.example.cz2006trial.controller;

import android.util.Log;

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserRouteController {

    public static void setStartMarkerInfo(UserRoute userRoute, Marker marker) {
        userRoute.startPointUpdate(marker.getPosition());
        userRoute.setStartPointName(marker.getTitle());
    }

    public static void setEndMarkerInfo(UserRoute userRoute, Marker marker) {
        userRoute.endPointUpdate(marker.getPosition());
        userRoute.setEndPointName(marker.getTitle());
    }

    public static String getStartPointName(UserRoute userRoute) {
        return userRoute.getStartPointName();
    }

    public static String getEndPointName(UserRoute userRoute) {
        return userRoute.getEndPointName();
    }

    public static LatLng getStartPointPos(UserRoute userRoute) {
        return userRoute.startPointRetrieve();
    }

    public static LatLng getEndPointPos(UserRoute userRoute) {
        return userRoute.endPointRetrieve();
    }

    public static void updateUserRouteDatabase(UserRoute userRoute) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
        sdf.setTimeZone(tz);
        java.util.Date curdate = new java.util.Date();
        String dateStr = sdf.format(curdate);
        Date date = DatabaseManager.convertStringToDate(dateStr);
        Log.d("date", date.toString());
        String dateString = date.toString();
        DatabaseReference databaseUserSavedRoutes = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes").child(dateString);
        UserRoute userSavedRoute = new UserRoute(
                date,
                userRoute.getStartPointName(),
                userRoute.getEndPointName(),
                userRoute.getStartLatitude(),
                userRoute.getStartLongitude(),
                userRoute.getEndLatitude(),
                userRoute.getEndLongitude(),
                userRoute.getDistance(),
                userRoute.getTimeTaken());
        databaseUserSavedRoutes.setValue(userSavedRoute);
    }

    public static void setDistanceTimeTaken(UserRoute userRoute, String displayDistance, String displayTravelTime) {
        userRoute.setDistance(displayDistance);
        userRoute.setTimeTaken(displayTravelTime);
    }
}
