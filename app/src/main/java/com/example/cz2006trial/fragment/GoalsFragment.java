package com.example.cz2006trial.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.controller.GoalController;
import com.example.cz2006trial.GoalDecorator;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.activity.MapsActivity;
import com.example.cz2006trial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class GoalsFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView dateView;
    private TextView dailyGoalView;
    private Button editGoalButton;
    private TextView progressView;
    private ProgressBar loadingBar;
    private ArrayList<Goal> storedGoals = new ArrayList<>();




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        dateView = view.findViewById(R.id.dateView);
        dailyGoalView = view.findViewById(R.id.dailyGoalView);
        editGoalButton = view.findViewById(R.id.EditGoalButton);
        progressView = view.findViewById(R.id.progressView);
        loadingBar = view.findViewById(R.id.goalsFragmentLoading);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //display how calendar looks like initially
        instantiateCalendar();

        //highlight dates for incomplete goals and completed goals on calendar view
        decorateGoalDates();
        loadingBar.setVisibility(View.GONE);

        //what is displayed when a date is selected on the calendar view
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay calendarDate, boolean selected) {

                //convert date to string to store in database
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                final String date = dateFormat.format(calendarDate.getDate());

                //edit goal button to appear only on future dates including current date
                if (calendarDate.isBefore(CalendarDay.today())) {
                    editGoalButton.setVisibility(View.GONE);
                } else {
                    editGoalButton.setVisibility(View.VISIBLE);
                }

                //display distance travelled, goal target and progress for selected date
                displayGoal(date);

                //go to EditGoalActivity page when pressed
                editGoalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapsActivity activity = (MapsActivity) getActivity();
                        activity.setDate(date);
                        Navigation.findNavController(getView()).navigate(R.id.nav_editgoals);

                    }
                });

            }
        });
    }

    /*public void displayGoals(String date) {
        dateView.setText("Date: " + date);
        for (Goal goal : storedGoals) {
            if (goal.getDate().equals(date)) {
                if (goal.getTarget() != -1) {
                    dailyGoalView.setText("Target: " + (Math.round(goal.getDistance() * 10) / 10.0) + " / " + goal.getTarget() + " km");
                    double progress = Math.max(0, Math.min(100 * goal.getDistance() / goal.getTarget(), 100));
                    progressView.setText("Progress: " + Math.round(progress) + "%");
                    return;
                } else {
                    progressView.setText("Distance travelled: " + (Math.round(goal.getDistance() * 10) / 10.0));
                    dailyGoalView.setText(R.string.noTargetSet);
                    return;
                }
            }
        }
        progressView.setText(R.string.zeroDistance);
        dailyGoalView.setText(R.string.noTargetSet);
    }*/

    public void decorateGoalDates() {

        DatabaseManager.getData(new DatabaseManager.DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                storedGoals = goals;
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else {
                    final ArrayList<CalendarDay> completeGoalList = new ArrayList<CalendarDay>();
                    final ArrayList<CalendarDay> incompleteGoalList = new ArrayList<CalendarDay>();
                    for (int i = 0; i < goals.size(); i++) {
                        System.out.println("" + goals.get(i).getDistance() + " " + goals.get(i).getTarget());
                        if (goals.get(i).getTarget() != -1) {
                            if (goals.get(i).getDistance() >= goals.get(i).getTarget()) {
                                completeGoalList.add(CalendarDay.from(GoalController.convertStringToDate(stringArgs.get(i))));
                            } else {
                                incompleteGoalList.add(CalendarDay.from(GoalController.convertStringToDate(stringArgs.get(i))));
                            }
                        }
                    }
                    calendarView.addDecorators(new GoalDecorator(Color.GREEN, false, completeGoalList));
                    calendarView.addDecorators(new GoalDecorator(Color.RED, false, incompleteGoalList));
                    calendarView.setVisibility(View.VISIBLE);
                }
            }
        }, "goals", null);


        /*String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoalDates = FirebaseDatabase.getInstance().getReference("goals").child(UID);
        databaseGoalDates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<CalendarDay> completeGoalList = new ArrayList<CalendarDay>();
                final ArrayList<CalendarDay> incompleteGoalList = new ArrayList<CalendarDay>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d != null) {
                        Goal goal = d.getValue(Goal.class);
                        String dateString = d.getKey();
                        if (goal.getTarget() != -1) {
                            if (goal.getDistance() >= goal.getTarget()) {
                                completeGoalList.add(CalendarDay.from(GoalController.convertStringToDate(dateString)));
                            } else {
                                incompleteGoalList.add(CalendarDay.from(GoalController.convertStringToDate(dateString)));
                            }
                        }
                    }
                }
                calendarView.addDecorators(new GoalDecorator(Color.GREEN, false, completeGoalList));
                calendarView.addDecorators(new GoalDecorator(Color.RED, false, incompleteGoalList));
                calendarView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/
    }

    public void displayGoal(final String date) {
        DatabaseManager.getData(new DatabaseManager.DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                dateView.setText("Date: " + date);
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else if (errorMsg[1] != null) {
                    progressView.setText(R.string.zeroDistance);
                    dailyGoalView.setText(R.string.noTargetSet);
                } else {
                    if (doubleArgs[1] != -1 && doubleArgs[1] != 0) {
                        dailyGoalView.setText("Target: " + (Math.round(doubleArgs[0] * 10) / 10.0) + " / " + doubleArgs[1] + " km");
                        double progress = Math.max(0, Math.min(100 * doubleArgs[0] / doubleArgs[1], 100));
                        progressView.setText("Progress: " + Math.round(progress) + "%");
                    } else {
                        progressView.setText("Distance travelled: " + (Math.round(doubleArgs[0] * 10) / 10.0));
                        dailyGoalView.setText(R.string.noTargetSet);
                    }
                }
            }
        }, "goals", date);


        /*String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference("goals").child(UID).child(date);
        databaseGoals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateView.setText("Date: " + date);
                Goal goal = dataSnapshot.getValue(Goal.class);
                if (goal != null) {
                    System.out.println("Target " + goal.getTarget());
                    if (goal.getTarget() != -1) {
                        dailyGoalView.setText("Target: " + (Math.round(goal.getDistance() * 10) / 10.0) + " / " + goal.getTarget() + " km");
                        double progress = Math.max(0, Math.min(100 * goal.getDistance() / goal.getTarget(), 100));
                        progressView.setText("Progress: " + Math.round(progress) + "%");
                    } else {
                        progressView.setText("Distance travelled: " + (Math.round(goal.getDistance() * 10) / 10.0));
                        dailyGoalView.setText(R.string.noTargetSet);
                    }
                } else {
                    progressView.setText(R.string.zeroDistance);
                    dailyGoalView.setText(R.string.noTargetSet);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/
    }

    private void instantiateCalendar() {
        //Set max and min date that can be shown on the calendar
        Calendar cMin = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cMin.add(Calendar.MONTH, -1);
        Calendar cMax = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cMax.add(Calendar.MONTH, 1);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(cMin))
                .setMaximumDate(CalendarDay.from(cMax))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        ;
        //decorate current date
        ArrayList<CalendarDay> currentDate = new ArrayList<CalendarDay>();
        //code to decorate current date
        currentDate.add(CalendarDay.today());
        calendarView.addDecorators(new GoalDecorator(Color.WHITE, true, currentDate));
    }

}
