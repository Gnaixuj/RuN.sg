package com.example.cz2006trial.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.cz2006trial.database.DatabaseManager;
import com.example.cz2006trial.controller.GoalController;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This fragment is used to display goals using a material calendar and
 * update goal information to Firebase.
 */
public class GoalsFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView dateView;
    private TextView distanceView;
    private TextView targetView;
    private Button editGoalButton;
    private TextView progressView;
    private ProgressBar loadingBar;
    private EditText newTargetView;
    private LinearLayout newTargetLayout;
    private double distance;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        dateView = view.findViewById(R.id.dateView);
        distanceView = view.findViewById(R.id.distanceView);
        targetView = view.findViewById(R.id.targetView);
        editGoalButton = view.findViewById(R.id.EditGoalButton);
        progressView = view.findViewById(R.id.progressView);
        loadingBar = view.findViewById(R.id.goalsFragmentLoading);
        newTargetView = view.findViewById(R.id.newTargetView);
        newTargetLayout = view.findViewById(R.id.newtarget_layout);
        newTargetView.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
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

        //force user to input new goal target up to 4 digits in 1 decimal


        //what is displayed when a date is selected on the calendar view
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay calendarDate, boolean selected) {

                //convert date to string to store in database
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                final String date = dateFormat.format(calendarDate.getDate());

                //display distance travelled, goal target and progress for selected date
                displayGoal(date);

                //edit goal button to appear only on future dates including current date
                if (calendarDate.isBefore(CalendarDay.today())) {
                    editGoalButton.setVisibility(View.GONE);
                } else {
                    editGoalButton.setVisibility(View.VISIBLE);
                }

                if (editGoalButton.getText().equals("EDIT"))
                    newTargetLayout.setVisibility(View.GONE);
                else
                    newTargetLayout.setVisibility(View.VISIBLE);

                editGoalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editGoalButton.getText().equals("EDIT")) {
                            newTargetLayout.setVisibility(View.VISIBLE);
                            editGoalButton.setText("DONE");
                        } else {
                            String newGoalTargetText = String.valueOf(newTargetView.getText());
                            String message = GoalController.validateGoalFields(newGoalTargetText);
                            if (message.equals("noEdit")) {
                                editGoalButton.setText("EDIT");
                                newTargetView.setText("");
                                newTargetLayout.setVisibility(View.GONE);
                            } else if (message.equals("Goal Target Updated")) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                DatabaseManager.updateGoalData(date, distance, Double.parseDouble(newGoalTargetText));
                                editGoalButton.setText("EDIT");
                                newTargetView.setText("");
                                newTargetLayout.setVisibility(View.GONE);
                                decorateGoalDates();
                                displayGoal(date);
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });
    }

    // instantiate calendar accordingly before displaying it on user interface
    private void instantiateCalendar() {
        //Set max and min date that can be shown on the calendar
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
        sdf.setTimeZone(tz);
        java.util.Date curDate = new java.util.Date();
        String date = sdf.format(curDate);
        Date todayDate = DatabaseManager.convertStringToDate(date);
        Calendar cMin = Calendar.getInstance(Locale.ENGLISH);
        cMin.setTimeInMillis(todayDate.getTime());
        Log.d("calendar", cMin.toString());
        cMin.add(Calendar.MONTH, -1);
        Log.d("calendar", cMin.toString());
        Calendar cMax = Calendar.getInstance(Locale.ENGLISH);
        Log.d("calendar", cMax.toString());
        cMax.setTimeInMillis(todayDate.getTime());
        Log.d("calendar", cMax.toString());
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
        currentDate.add(CalendarDay.from(todayDate));
        calendarView.addDecorators(new GoalDecorator(Color.WHITE, true, currentDate));
    }

    // decorate incomplete goals and completed goals by displaying a red dot and green dot respectively on affected dates
    public void decorateGoalDates() {
        DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else {
                    final ArrayList<CalendarDay> completeGoalList = new ArrayList<CalendarDay>();
                    final ArrayList<CalendarDay> incompleteGoalList = new ArrayList<CalendarDay>();
                    for (int i = 0; i < goals.size(); i++) {
                        if (goals.get(i).getTarget() != -1 && goals.get(i).getTarget() != 0) {
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
        }, null);
    }

    // retrieve goal data from firebase database via Database Manager and display it
    public void displayGoal(final String date) {
        DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                dateView.setText(date);
                // database read failed
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                    // no data available for retrieval
                else if (errorMsg[1] != null) {
                    distanceView.setText(R.string.zeroDistance);
                    targetView.setText(R.string.noTargetSet);
                }
                // data avilable for retrieval
                else {
                    // when daily target is not -1 or 0, display target distance value
                    if (doubleArgs[1] != -1 && doubleArgs[1] != 0) {
                        distance = doubleArgs[0];
                        distanceView.setText("" + Math.round(doubleArgs[0] * 10) / 10.0);
                        targetView.setText("" + doubleArgs[1]);
                        double progress = Math.max(0, Math.min(100 * doubleArgs[0] / doubleArgs[1], 100));
                        progressView.setText(Math.round(progress) + "%");
                    }
                    // otherwise, display 'no daily target set'
                    else {
                        distance = doubleArgs[0];
                        distanceView.setText("" + Math.round(doubleArgs[0] * 10) / 10.0);
                        targetView.setText(R.string.noTargetSet);
                    }
                }
            }
        }, date);
    }

    // to decorate selected day/days on the calendar
    private static class GoalDecorator implements DayViewDecorator {

        private final int color;
        private final boolean bold;
        private final HashSet<CalendarDay> markedDates;

        GoalDecorator(int color, boolean bold, ArrayList<CalendarDay> dates) {
            this.color = color;
            this.bold = bold;
            this.markedDates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return markedDates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            if (bold) {
                view.addSpan(new StyleSpan(Typeface.BOLD));
                view.addSpan(new RelativeSizeSpan(1.5f));
            } else {
                view.addSpan(new DotSpan(10, color));
            }
        }
    }
}
