package com.example.cz2006trial.controller;

import androidx.annotation.NonNull;

import com.example.cz2006trial.model.Goal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoalController {

    public static Date convertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static String validateGoalFields(String target, double distance) {

        if (target.equals("")) {
            return "Please enter a positive value";
        }

        double targetDouble = Double.parseDouble(target);
        if (targetDouble == 0) {
            return "Please enter a positive value";
        } else if (targetDouble < distance) {
            return "Please enter a positive value higher than the current distance travelled";
        } else if (targetDouble >= 100) {
            return "Please enter a positive value lower than 100";
        }

        return "success";
    }

    public static boolean updateDataOnDatabase(String date, double distance, double target) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference().child(UID).child("goals").child(date);
        Goal goal = new Goal(date, distance, target);
        databaseGoals.setValue(goal);
        return true;
    }

    public static void getDistanceDatabase(final Date date, final double distance) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String dateString = dateFormat.format(date);
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference().child(UID).child("goals").child(dateString);
        databaseGoals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Goal goal = dataSnapshot.getValue(Goal.class);
                if (goal != null) {
                    updateDataOnDatabase(dateString, goal.getDistance() + distance, goal.getTarget());
                } else {
                    updateDataOnDatabase(dateString, 0, -1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }



    /*public static void getMarkedGoalDates(final FirebaseCalendarCallback firebaseCallback) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoalDates = FirebaseDatabase.getInstance().getReference("goals").child(UID);
        databaseGoalDates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<CalendarDay> completeGoalList = new ArrayList<CalendarDay>();
                final ArrayList<CalendarDay> incompleteGoalList = new ArrayList<CalendarDay>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d != null) {
                        GoalEntity goal = d.getValue(GoalEntity.class);
                        String dateString = d.getKey();
                        if (goal.getDistance() >= goal.getTarget()) {
                            completeGoalList.add(CalendarDay.from(convertStringToDate(dateString)));
                        } else {
                            incompleteGoalList.add(CalendarDay.from(convertStringToDate(dateString)));
                        }
                    }
                }
                firebaseCallback.onCallback(completeGoalList, incompleteGoalList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void getGoalFromDatabase(final FirebaseGoalCallback firebaseCallback, String date) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference("goals").child(UID).child(date);
        databaseGoals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GoalEntity goal = dataSnapshot.getValue(GoalEntity.class);
                double[] goalData = new double[2];
                if (goal != null) {
                    goalData[0] = goal.getDistance();
                    goalData[1] = goal.getTarget();
                } else {
                    goalData[0] = 0;
                    goalData[1] = -1;
                }
                firebaseCallback.onCallback(goalData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public interface FirebaseGoalCallback {
        void onCallback(double[] goalData);
    }

    public interface FirebaseCalendarCallback {
        void onCallback(ArrayList<CalendarDay> completeGoalDates, ArrayList<CalendarDay> incompleteGoalDates);
    }*/
}
