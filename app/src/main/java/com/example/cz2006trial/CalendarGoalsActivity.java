package com.example.cz2006trial;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class CalendarGoalsActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView dateView;
    private TextView dailyGoalView;
    private Button editGoalButton;
    private TextView progressView;
    private String date;
    private Collection<CalendarDay> calendarDays = new Collection<CalendarDay>() {
        @Override
        public boolean add(CalendarDay object) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends CalendarDay> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean contains(Object object) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NonNull
        @Override
        public Iterator<CalendarDay> iterator() {
            return null;
        }

        @Override
        public boolean remove(Object object) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(T[] array) {
            return null;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendargoals);

        calendarView = findViewById(R.id.calendarView);
        dateView = findViewById(R.id.dateView);
        dailyGoalView = findViewById(R.id.dailyGoalView);
        editGoalButton = findViewById(R.id.EditGoalButton);
        progressView = findViewById(R.id.progressView);

        //Set max and min date that can be shown on the calendar
        setMinMaxDate();
        highlightGoalDates();
        calendarView.addDecorator(new CurrentDateDecorator(this));

        /*BarChart barChart = findViewById(R.id.goalProgressBar);
        barChart.setBarMaxValue(100);

        BarChartModel barChartModel = new BarChartModel();
        barChartModel.setBarValue(101);
        barChartModel.setBarColor(Color.parseColor("#9C27B0"));
        barChartModel.setBarTag(null); //You can set your own tag to bar model
        barChartModel.setBarText("50");

        barChart.addBar(barChartModel);*/


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay calendarDate, boolean selected) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                date = dateFormat.format(calendarDate.getDate());
                System.out.println(date);
                if (calendarDate.isBefore(CalendarDay.today())) {
                    editGoalButton.setVisibility(View.GONE);
                } else {
                    editGoalButton.setVisibility(View.VISIBLE);
                }
                displayDailyGoal(date);

                editGoalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editGoalIntent = new Intent(getApplicationContext(), EditGoalsActivity.class);
                        //Create the bundle
                        Bundle bundle = new Bundle();
                        //Add your data to bundle
                        bundle.putString("date", date);
                        //Add the bundle to the intent
                        editGoalIntent.putExtras(bundle);
                        //Fire that second activity
                        startActivityForResult(editGoalIntent, 1);
                    }
                });
            }
        });
    }

    private String getZeroPadding(int dayMonth) {
        if (dayMonth < 10) {
            return "0"+dayMonth;
        }
        return String.valueOf(dayMonth);
    }

    private void displayDailyGoal(final String date) {
        //final double dailyGoalTarget = GoalController.getDailyGoalTarget(date);
        //final double dailyGoalDistance = GoalController.getDailyGoalDistance(date);
        GoalController.getGoalFromDatabase(new GoalController.FirebaseGoalCallback() {
            @Override
            public void onCallback(double[] goalData) {
                dateView.setText("Date: " + date);
                if (goalData[1] == -1) {
                    dailyGoalView.setText("No daily target set");
                    progressView.setVisibility(View.GONE);
                } else {
                    dailyGoalView.setText("Target: " + goalData[0] + " / " + goalData[1] + " km");
                    progressView.setVisibility(View.VISIBLE);
                    if (goalData[1] == 0) {
                        progressView.setText("Error");
                    } else if (goalData[0] / goalData[1] >= 1) {
                        progressView.setText("100%");
                    } else {
                        progressView.setText(Math.round((goalData[0] / goalData[1]) * 100) + "%");
                    }
                }
            }
        }, date);
    }

    //Set max and min date that can be shown on the calendar
    private void setMinMaxDate() {
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
    }

    private void highlightGoalDates() {
        GoalController.getMarkedGoalDates(new GoalController.FirebaseCalendarCallback() {
            @Override
            public void onCallback(List<CalendarDay> completeGoalDates, List<CalendarDay> incompleteGoalDates) {
                //adding list of CalendarDays for completed goals
                calendarDays = completeGoalDates;
                //code to decorate selected dates
                calendarView.addDecorators(new GoalDecorator(Color.parseColor("#00ff00"), calendarDays));
                //clear calendarDays
                calendarDays.clear();
                //adding list of CalendarDays for completed goals
                calendarDays = incompleteGoalDates;
                //code to decorate selected dates
                calendarView.addDecorators(new GoalDecorator(Color.parseColor("#ff0000"), calendarDays));
                calendarView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displayDailyGoal(date);
    }

}
