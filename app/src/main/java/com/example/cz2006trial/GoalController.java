package com.example.cz2006trial;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GoalController {

    public static void getMarkedGoalDates(final FirebaseCalendarCallback firebaseCallback) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoalDates = FirebaseDatabase.getInstance().getReference("goals").child(UID);
        databaseGoalDates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<CalendarDay> completeGoalList = new ArrayList<CalendarDay>();
                final List<CalendarDay> incompleteGoalList = new ArrayList<CalendarDay>();
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
                System.out.println(goalData[0]);
                System.out.println(goalData[1]);
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
        void onCallback(List<CalendarDay> completeGoalDates, List<CalendarDay> incompleteGoalDates);
    }

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


    /*public static double getDailyGoalDistance(String date) {
        for (int i = 0; i < goal.getAllGoals().size();i++) {
            if (goal.getAllGoals().get(i).getDate().equals(date)) {
                return goal.getAllGoals().get(i).getDistance();
            }
        }
        return 0;
    }

    public static double getDailyGoalTarget(String date) {
        for (int i = 0; i < goal.getAllGoals().size();i++) {
            if (goal.getAllGoals().get(i).getDate().equals(date)) {
                return goal.getAllGoals().get(i).getTarget();
            }
        }
        return -1;
    }*/

    public static String validateGoalFields(String dailyTarget,double distance) {
        if (dailyTarget.equals("")) {
            return "Please enter a positive value";
        }
        double dailyTargetDouble = Double.parseDouble(dailyTarget);
        if (dailyTargetDouble == 0) {
            return "Please enter a positive value";
        } else if (dailyTargetDouble < distance) {
            return "Please enter a positive value higher than the current distance travelled";
        }
        else if (dailyTargetDouble >= 100) {
            return "Please enter a positive value lower than 100";
        }
        return "success";
    }

    public static boolean updateDataOnDatabase(String date, double distance, double target) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference("goals").child(UID).child(date);
        GoalEntity goal = new GoalEntity(date, distance, target);
        databaseGoals.setValue(goal);
        return true;
    }

    /*public static void updateDailyTarget(String date, double distance) {
        for (int i = 0; i < goal.getAllGoals().size();i++) {
            if (goal.getAllGoals().get(i).getDate().equals(date)) {
                goal.getAllGoals().get(i).setTarget(distance);
                break;
            }
        }
    }

    public static void updateDailyProgress(double distance) {

    }

    public static void updateWeeklyProgress(double distance) {

    }

    public static void appendNewGoal(GoalEntity newGoal) {
        goal.getAllGoals().add(newGoal);
    }*/
}
