package com.example.cz2006trial;

import androidx.annotation.MainThread;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import me.ithebk.barchart.BarChart;
import me.ithebk.barchart.BarChartModel;

public class ViewCalendarGoals extends AppCompatActivity {

    CalendarView calendarView;
    TextView dateView;
    TextView dailyGoalView;
    Button editGoalButton;
    TextView progressView;
    String date;

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
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.add(Calendar.MONTH, -1);
        long result = c.getTimeInMillis();
        calendarView.setMinDate(result);
        c.add(Calendar.MONTH,2);
        result = c.getTimeInMillis();
        calendarView.setMaxDate(result);

        /*BarChart barChart = findViewById(R.id.goalProgressBar);
        barChart.setBarMaxValue(100);

        BarChartModel barChartModel = new BarChartModel();
        barChartModel.setBarValue(101);
        barChartModel.setBarColor(Color.parseColor("#9C27B0"));
        barChartModel.setBarTag(null); //You can set your own tag to bar model
        barChartModel.setBarText("50");

        barChart.addBar(barChartModel);*/

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = getZeroPadding(dayOfMonth) + "/" + getZeroPadding(month+1) + "/" + year;
                //if date is in the past, do not show button to
                LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));

                if (year < currentDate.getYear() || month+1 < currentDate.getMonthValue() ||
                        (month+1 == currentDate.getMonthValue() && dayOfMonth < currentDate.getDayOfMonth())) {
                    editGoalButton.setVisibility(View.GONE);
                }
                else {
                    editGoalButton.setVisibility(View.VISIBLE);
                }
                displayDailyGoal(date);

                editGoalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GoalController.getDailyGoalTarget(date) == -1) {
                            DailyGoal newGoal = new DailyGoal(date);
                            GoalController.appendNewGoal(newGoal);
                        }
                        Intent editGoalIntent = new Intent(getApplicationContext(), EditGoals.class);
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

    private void displayDailyGoal(String date) {
        final double dailyGoalTarget = GoalController.getDailyGoalTarget(date);
        final double dailyGoalDistance = GoalController.getDailyGoalDistance(date);
        dateView.setText("Date: " + date);
        if (dailyGoalTarget == -1) {
            dailyGoalView.setText("No daily target set");
            progressView.setVisibility(View.GONE);
        }
        else {
            dailyGoalView.setText("Target: "+dailyGoalDistance+" / "+dailyGoalTarget+ " km");
            progressView.setVisibility(View.VISIBLE);
            if (dailyGoalDistance/dailyGoalTarget >= 1) {
                progressView.setText("100%");
            }
            else {
                progressView.setText(Math.round((dailyGoalDistance/dailyGoalTarget)*100) + "%");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displayDailyGoal(date);
    }

    /*private Date convertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }*/

}