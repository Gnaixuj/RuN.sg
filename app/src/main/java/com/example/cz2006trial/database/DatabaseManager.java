package com.example.cz2006trial.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserLocation;
import com.example.cz2006trial.model.UserLocationSession;
import com.example.cz2006trial.model.UserProfile;
import com.example.cz2006trial.model.UserRoute;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseManager {

    // retrieve goal data from firebase database and dynamically update the interface when the database reference updates
    public static void getGoalData(final GoalDatabaseCallback goalDatabaseCallback, final String date) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(UID).child("goals");
        final ArrayList<String> stringArgs = new ArrayList<>();
        final double[] doubleArgs = new double[5];
        final String[] errorMsg = new String[2];
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Goal> goals = new ArrayList<Goal>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d != null) {
                        Goal goal = d.getValue(Goal.class);
                        String dateString = d.getKey();
                        goals.add(goal);
                        stringArgs.add(dateString);
                        if (date != null && dateString.equals(date)) {
                            doubleArgs[0] = goal.getDistance();
                            doubleArgs[1] = goal.getTarget();
                        }
                    } else {
                        doubleArgs[0] = 0;
                        errorMsg[1] = "noTarget";
                    }
                }
                goalDatabaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, goals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorMsg[0] = "The read failed: " + databaseError.getCode();
                goalDatabaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, null);
            }
        });
    }

    // retrieve profile data from firebase database and dynamically update the interface when the database reference updates
    public static void getProfileData(final ProfileDatabaseCallback profileDatabaseCallback) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(UID).child("userProfile");
        final ArrayList<String> stringArgs = new ArrayList<>();
        final double[] doubleArgs = new double[5];
        final String[] errorMsg = new String[2];
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                if (userProfile != null) {
                    stringArgs.add(userProfile.getUsername());
                    stringArgs.add(userProfile.getEmail());
                    stringArgs.add(userProfile.getDOB());
                    doubleArgs[0] = userProfile.getHeight();
                    doubleArgs[1] = userProfile.getWeight();
                    doubleArgs[2] = userProfile.getBMI();
                    profileDatabaseCallback.onCallback(stringArgs, doubleArgs, errorMsg);
                } else {
                    errorMsg[1] = "Something went wrong. PLease re-login and try again";
                    profileDatabaseCallback.onCallback(stringArgs, doubleArgs, errorMsg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorMsg[0] = "The read failed: " + databaseError.getCode();
                profileDatabaseCallback.onCallback(stringArgs, doubleArgs, errorMsg);
            }
        });
    }

    // nested interface so as to allow relevant fragments to retrieve goal data
    // only when the data has been retrieved fully from firebase database
    public interface GoalDatabaseCallback {
        void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals);
    }

    // nested interface so as to allow relevant fragments to retrieve profile data
    // only when the data has been retrieved fully from firebase database
    public interface ProfileDatabaseCallback {
        void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg);
    }

    // update user profile information in firebase database upon registration
    public static void updateProfileData(String username, String email) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfile userProfile = new UserProfile(username, email);
        databaseUserProfile.setValue(userProfile);
    }

    // update user profile information in firebase database
    public static void updateProfileData(String username, String email, String DOB, double height, double weight, double BMI) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfile userProfile = new UserProfile(username, email, DOB, height, weight, BMI);
        databaseUserProfile.setValue(userProfile);
    }

    // update goal information in firebase database
    public static void updateGoalData(String date, double distance, double target) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoal = FirebaseDatabase.getInstance().getReference().child(UID).child("goals").child(date);
        Goal goal = new Goal(date, distance, target);
        databaseGoal.setValue(goal);
    }

    // update user's tracking session information in firebase database
    public static void updateUserLocationSession(UserLocationSession userLocationSession) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserSession = FirebaseDatabase.getInstance().getReference()
                .child(UID).child("userLocationSessions").child(userLocationSession.getTimestamp().toString());
        databaseUserSession.setValue(new UserLocationSession(userLocationSession.getTimestamp(), userLocationSession.getDistance(), userLocationSession.getTimeTaken()));
        for (int i = 0; i < userLocationSession.getSession().size(); i++) {
            DatabaseReference databaseUserLocation = databaseUserSession.child(userLocationSession.getSession().get(i).getTimestamp().toString());
            databaseUserLocation.setValue(new UserLocation(userLocationSession.getSession().get(i).getLatitude(),
                    userLocationSession.getSession().get(i).getLongitude(),
                    userLocationSession.getSession().get(i).getTimestamp()));
        }
    }

    // update user's created route information in firebase database
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

    // convert date in string format to appropriate date format
    public static Date convertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }
}
