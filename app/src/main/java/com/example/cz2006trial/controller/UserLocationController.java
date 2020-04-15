package com.example.cz2006trial.controller;

import com.example.cz2006trial.database.DatabaseManager;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserLocation;
import com.example.cz2006trial.model.UserLocationSession;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserLocationController {

    // add coordinates of user current location as long as the user is moving when tracking routes
    public static void addUserLocation(UserLocationSession userLocationSession, LatLng geopoints, Date timestamp) {
        GoogleMapController controller = GoogleMapController.getController();
        double latitude = geopoints.latitude;
        double longitude = geopoints.longitude;
        UserLocation userLocation = new UserLocation(latitude, longitude, timestamp);
        ArrayList<UserLocation> userSession = userLocationSession.getSession();

        // check user session is not empty to calculate distance between current and previous user location
        // user session refers to a user tracking session from the time user begins tracking until the time the user ends tracking
        if (!userSession.isEmpty()) {
            final double distance = calculateDistance(userSession.get(userSession.size() - 1).getLatitude(),
                    userSession.get(userSession.size() - 1).getLongitude(),
                    latitude, longitude);

            // get current date in Singapore
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
            sdf.setTimeZone(tz);
            java.util.Date curDate = new java.util.Date();
            final String dateString = sdf.format(curDate);

            // retrieve goal data from firebase database via Database Manager
            DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
                @Override
                public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                    // create new goal data in firebase database via Database Manager
                    // if no goal data is retrieved to store distance travelled
                    if (errorMsg[1] != null)
                        DatabaseManager.updateGoalData(dateString, 0, -1);
                        // update goal data with the distance travelled by user during tracking
                    else {
                        DatabaseManager.updateGoalData(dateString, doubleArgs[0] + distance, doubleArgs[1]);

                    }
                }
            }, dateString);
            userLocationSession.addDistance(distance);
        }
        userLocationSession.addUserLocation(userLocation);
        controller.displayTrackingDistance(userLocationSession);
    }

    // a function to calculate distance using latitude and longitude of two locations
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

    // a function to convert time in long format to a more organized string format
    public static void setTimeTaken(UserLocationSession userLocationSession, long diff) {
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        String timeTaken = diffHours + "hr " + diffMinutes + "min " + diffSeconds + "s";
        userLocationSession.setTimeTaken(timeTaken);
    }

}
