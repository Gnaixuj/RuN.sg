package com.example.cz2006trial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendargoals, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        dateView = view.findViewById(R.id.dateView);
        dailyGoalView = view.findViewById(R.id.dailyGoalView);
        editGoalButton = view.findViewById(R.id.EditGoalButton);
        progressView = view.findViewById(R.id.progressView);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //display how calendar looks like initially
        instantiateCalendar();

        //highlight dates for incomplete goals and completed goals on calendar view
        decorateGoalDates();

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
                displayGoalFromDatabase(date);

                //go to EditGoalActivity page when pressed
                editGoalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapsActivity activity = (MapsActivity) getActivity();
                        activity.setDate(date);
                        activity.getNavController().navigate(R.id.nav_editgoals);

                    }
                });

            }
        });
    }

    public void decorateGoalDates() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoalDates = FirebaseDatabase.getInstance().getReference("goals").child(UID);
        databaseGoalDates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<CalendarDay> completeGoalList = new ArrayList<CalendarDay>();
                final ArrayList<CalendarDay> incompleteGoalList = new ArrayList<CalendarDay>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d != null) {
                        GoalEntity goal = d.getValue(GoalEntity.class);
                        String dateString = d.getKey();
                        if (goal.getDistance() >= goal.getTarget()) {
                            completeGoalList.add(CalendarDay.from(GoalController.convertStringToDate(dateString)));
                        } else {
                            incompleteGoalList.add(CalendarDay.from(GoalController.convertStringToDate(dateString)));
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
        });
    }

    public void displayGoalFromDatabase(final String date) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference("goals").child(UID).child(date);
        databaseGoals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateView.setText("Date: " + date);
                GoalEntity goal = dataSnapshot.getValue(GoalEntity.class);
                if (goal != null) {
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
        });
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
