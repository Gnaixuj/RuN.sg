package com.example.cz2006trial;

import androidx.annotation.NonNull;

import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserLocation;
import com.example.cz2006trial.model.UserLocationSession;
import com.example.cz2006trial.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseManager {


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

    public interface GoalDatabaseCallback {
        void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals);
    }

    public interface ProfileDatabaseCallback {
        void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg);
    }











    public static void updateProfileData(String username, String email) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfile userProfile = new UserProfile(username, email);
        databaseUserProfile.setValue(userProfile);
    }

    public static void updateProfileData(String username, String email, String DOB, double height, double weight, double BMI) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfile userProfile = new UserProfile(username, email, DOB, height, weight, BMI);
        databaseUserProfile.setValue(userProfile);
    }

    public static void updateGoalData(String date, double distance, double target) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoal = FirebaseDatabase.getInstance().getReference().child(UID).child("goals").child(date);
        Goal goal = new Goal(date, distance, target);
        databaseGoal.setValue(goal);
    }

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
}
