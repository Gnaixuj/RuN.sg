package com.example.cz2006trial;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.data.Layer;

import java.util.Calendar;
import java.util.Date;

public class UserRouteController {

    public static void removeAccessPoints(Layer accesslayer) {
        accesslayer.removeLayerFromMap();
    }

    public static void setStartMarkerInfo(UserRouteEntity userRoute, Marker marker) {
        userRoute.setStartPoint(marker.getPosition());
        userRoute.setStartPointName(marker.getTitle());
    }

    public static void setEndMarkerInfo(UserRouteEntity userRoute, Marker marker) {
        userRoute.setEndPoint(marker.getPosition());
        userRoute.setEndPointName(marker.getTitle() + " " + marker.getId());
    }

    public static String getStartPointName(UserRouteEntity userRoute) {
        return userRoute.getStartPointName();
    }

    public static String getEndPointName(UserRouteEntity userRoute) {
        return userRoute.getEndPointName();
    }

    public static LatLng getStartPointPos(UserRouteEntity userRoute) {
        return userRoute.getStartPoint();
    }

    public static LatLng getEndPointPos(UserRouteEntity userRoute) {
        return userRoute.getEndPoint();
    }

    public static boolean existStartPoint(UserRouteEntity userRoute) {
        if (userRoute.getStartPointName() != null)
            return true;
        return false;
    }

    public static boolean existEndPoint(UserRouteEntity userRoute) {
        if (userRoute.getEndPointName() != null)
            return true;
        return false;
    }

    public static void setDistanceTimeTaken(UserRouteEntity userRoute, String distance, String timeTaken) {
        userRoute.setDistance(distance);
        userRoute.setTimeTaken(timeTaken);
    }

    public static void updateUserRouteDatabase(UserRouteEntity userRoute) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date = Calendar.getInstance().getTime();
        String dateString = date.toString();
        DatabaseReference databaseUserSavedRoutes = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes").child(dateString);
        UserRouteEntity userSavedRoute = new UserRouteEntity(
                date,
                userRoute.getStartPointName(),
                userRoute.getEndPointName(),
                userRoute.getStartPoint(),
                userRoute.getEndPoint(),
                userRoute.getDistance(),
                userRoute.getTimeTaken());
        databaseUserSavedRoutes.setValue(userSavedRoute);
    }
}
