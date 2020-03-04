package com.example.cz2006trial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditGoalsActivity extends AppCompatActivity {

    TextView editGoalDateView;
    TextView goalDistanceView;
    TextView initialGoalTargetView;
    TextView newGoalTargetPlainText;
    EditText newGoalTargetEditText;
    TextView errorMessageView;
    Button doneEditGoalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editgoals);

        editGoalDateView = findViewById(R.id.editGoalDateView);
        goalDistanceView = findViewById(R.id.goalDistance);
        initialGoalTargetView = findViewById(R.id.initialGoalTarget);
        newGoalTargetPlainText = findViewById(R.id.newGoalTargetText);
        newGoalTargetEditText = findViewById(R.id.newGoalTarget);
        errorMessageView = findViewById(R.id.errorMessage);
        doneEditGoalButton = findViewById(R.id.DoneEditGoalButton);

        newGoalTargetEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,1)});

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the data…
        final String goalDate = bundle.getString("date");
        final double goalDistance = GoalController.getDailyGoalDistance(goalDate);
        double initialGoalTarget = GoalController.getDailyGoalTarget(goalDate);
        editGoalDateView.setText("Date: " + goalDate);
        goalDistanceView.setText("Distance travelled: " + goalDistance + " km");
        if (initialGoalTarget == -1) {
            initialGoalTargetView.setVisibility(View.GONE);
        }
        else {
            initialGoalTargetView.setText("Initial Goal Target: " + initialGoalTarget + " km");
        }

        doneEditGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newGoalTargetText = String.valueOf(newGoalTargetEditText.getText());
                String message = GoalController.validateGoalFields(newGoalTargetText, goalDistance);
                if (message.equals("success")) {
                    errorMessageView.setVisibility(View.GONE);
                    Toast toast=Toast. makeText(getApplicationContext(),"Goal Target Updated",Toast. LENGTH_SHORT);
                    toast. show();
                    GoalController.updateDailyTarget(goalDate, Double.parseDouble(newGoalTargetText));
                    finish();
                }
                else {
                    errorMessageView.setText(message);
                }
            }
        });

    }
}