package com.example.cz2006trial;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class UserLocationController {

    public static void addUserLocation(UserLocationSessionEntity userLocationSession, LatLng geopoints, Date timestamp) {
        double latitude = geopoints.latitude;
        double longitude = geopoints.longitude;
        UserLocationEntity userLocation = new UserLocationEntity(latitude, longitude, timestamp);

        ArrayList<UserLocationEntity> userSession = userLocationSession.getSession();
        if (!userSession.isEmpty()) {
            double distance = calculateDistance(userSession.get(userSession.size() - 1).getLatitude(),
                    userSession.get(userSession.size() - 1).getLongitude(),
                    latitude, longitude);
            GoalController.getDistanceDatabase(timestamp, distance);
            userLocationSession.addDistance(distance);
        }
        userLocationSession.addUserLocation(userLocation);
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            //dist in kilometres
            dist = dist * 60 * 1.1515 * 1.609344;
            return dist;
        }
    }

    public static boolean updateUserLocation(UserLocationSessionEntity userLocationSession) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserSession = FirebaseDatabase.getInstance().getReference("userLocationSessions")
                .child(UID).child(userLocationSession.getTimestamp().toString());
        databaseUserSession.setValue(new UserLocationSessionEntity(userLocationSession.getTimestamp(), userLocationSession.getDistance(), userLocationSession.getTimeTaken()));
        for (int i = 0; i < userLocationSession.getSession().size(); i++) {
            DatabaseReference databaseUserLocation = databaseUserSession.child(userLocationSession.getSession().get(i).getTimestamp().toString());
            databaseUserLocation.setValue(new UserLocationEntity(userLocationSession.getSession().get(i).getLatitude(),
                    userLocationSession.getSession().get(i).getLongitude(),
                    userLocationSession.getSession().get(i).getTimestamp()));
        }
        return true;
    }

    public static void calculateNSetTimeTaken(UserLocationSessionEntity userLocationSession, long diff) {
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        String timeTaken = diffHours + "hr " + diffMinutes + "min " + diffSeconds + "s";
        userLocationSession.setTimeTaken(timeTaken);
    }

}
