package com.example.cz2006trial.controller;

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserLocation;
import com.example.cz2006trial.model.UserLocationSession;
import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserLocationController {

    public static void addUserLocation(UserLocationSession userLocationSession, LatLng geopoints, Date timestamp) {
        GoogleMapController controller = GoogleMapController.getController();
        double latitude = geopoints.latitude;
        double longitude = geopoints.longitude;
        UserLocation userLocation = new UserLocation(latitude, longitude, timestamp);

        ArrayList<UserLocation> userSession = userLocationSession.getSession();
        if (!userSession.isEmpty()) {
            final double distance = calculateDistance(userSession.get(userSession.size() - 1).getLatitude(),
                    userSession.get(userSession.size() - 1).getLongitude(),
                    latitude, longitude);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            final String dateString = dateFormat.format(timestamp);
            DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
                @Override
                public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                    if (errorMsg[1] != null)
                        DatabaseManager.updateGoalData(dateString, 0, -1);
                    else {
                        DatabaseManager.updateGoalData(dateString, doubleArgs[0] + distance, doubleArgs[1]);

                    }
                }
            }, dateString);

            //GoalController.updateTrackingDistance(timestamp, distance);
            userLocationSession.addDistance(distance);
        }
        userLocationSession.addUserLocation(userLocation);
        controller.displayTrackingDistance(userLocationSession);
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

    public static void setTimeTaken(UserLocationSession userLocationSession, long diff) {
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        String timeTaken = diffHours + "hr " + diffMinutes + "min " + diffSeconds + "s";
        userLocationSession.setTimeTaken(timeTaken);
    }

}
