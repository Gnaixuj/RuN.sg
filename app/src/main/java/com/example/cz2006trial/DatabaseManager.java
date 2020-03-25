package com.example.cz2006trial;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cz2006trial.controller.GoalController;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;

public class DatabaseManager {


    public static void getData(final DatabaseCallback databaseCallback, String path, final String arg) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(UID).child(path);
        final ArrayList<String> stringArgs = new ArrayList<>();
        final double[] doubleArgs = new double[10];
        final String[] errorMsg = new String[2];
        switch (path) {
            case "userProfile":
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
                            databaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, null);
                        } else {
                            errorMsg[1] = "Something went wrong. PLease re-login and try again";
                            databaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        errorMsg[0] = "The read failed: " + databaseError.getCode();
                        databaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, null);
                    }
                });
                break;

            case "goals":
                /*if (arg != null) {
                    databaseRef.child(arg).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Goal goal = dataSnapshot.getValue(Goal.class);
                            if (goal != null) {
                                doubleArgs[0] = Math.round(goal.getDistance() * 10) / 10.0;
                                doubleArgs[1] = goal.getTarget();
                            }
                            else {
                                doubleArgs[0] = 0;
                                errorMsg[1] = "noTarget";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            errorMsg[0] = "The read failed: " + databaseError.getCode();
                            databaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, null);
                        }
                    });
                }*/
                //else {
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Goal> goals = new ArrayList<Goal>();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            if (d != null) {
                                Goal goal = d.getValue(Goal.class);
                                String dateString = d.getKey();
                                goals.add(goal);
                                stringArgs.add(dateString);
                                if (arg != null && dateString.equals(arg)) {
                                    doubleArgs[0] = Math.round(goal.getDistance() * 10) / 10.0;
                                    doubleArgs[1] = goal.getTarget();
                                }
                            } else {
                                doubleArgs[0] = 0;
                                errorMsg[1] = "noTarget";
                            }
                        }
                        databaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, goals);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        errorMsg[0] = "The read failed: " + databaseError.getCode();
                        databaseCallback.onCallback(stringArgs, doubleArgs, errorMsg, null);
                    }
                });
                //}
                break;

            default:
                break;
        }
    }

    public interface DatabaseCallback {
        void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals);
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

}
