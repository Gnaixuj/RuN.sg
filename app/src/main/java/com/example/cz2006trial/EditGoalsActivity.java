package com.example.cz2006trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditGoalsActivity extends AppCompatActivity {

    TextView dateView;
    TextView goalDistanceView;
    TextView initialGoalTargetView;
    TextView newGoalTargetPlainText;
    EditText newGoalTargetEditText;
    TextView errorMessageView;
    Button editGoalDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editgoals);

        dateView = findViewById(R.id.editGoalDateView);
        goalDistanceView = findViewById(R.id.goalDistance);
        initialGoalTargetView = findViewById(R.id.initialGoalTarget);
        newGoalTargetPlainText = findViewById(R.id.newGoalTargetText);
        newGoalTargetEditText = findViewById(R.id.newGoalTarget);
        errorMessageView = findViewById(R.id.errorMessage);
        editGoalDoneButton = findViewById(R.id.DoneEditGoalButton);

        //allow users to input 3 digit number that allow only one decimal place
        newGoalTargetEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,1)});

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the data…
        final String date = bundle.getString("date");

        //display initial goals for editing
        displayGoalFromDatabase(date);

    }

    public void displayGoalFromDatabase(final String date) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference("goals").child(UID).child(date);
        databaseGoals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateView.setText("Date: " + date);
                final GoalEntity goal = dataSnapshot.getValue(GoalEntity.class);
                final double goalDistance;
                if (goal != null) {
                    goalDistance = (Math.round(goal.getDistance() * 10) / 10.0);
                    goalDistanceView.setText("Distance travelled: " + goalDistance + " km");
                    initialGoalTargetView.setText("Initial Goal Target: " + goal.getTarget() + " km");
                } else {
                    goalDistance = 0;
                    goalDistanceView.setText(R.string.zeroDistance);
                    initialGoalTargetView.setVisibility(View.GONE);
                }
                editGoalDoneButton.setVisibility(View.VISIBLE);
                editGoalDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newGoalTargetText = String.valueOf(newGoalTargetEditText.getText());
                        String message = GoalController.validateGoalFields(newGoalTargetText, goalDistance);
                        if (message.equals("success")) {
                            errorMessageView.setVisibility(View.GONE);
                            Toast toast = Toast.makeText(getApplicationContext(), "Goal Target Updated", Toast.LENGTH_SHORT);
                            toast.show();
                            if (GoalController.updateDataOnDatabase(date, goalDistance, Double.parseDouble(newGoalTargetText))) {
                                finish();
                            } else {
                                errorMessageView.setText("Error. Something went wrong. Please retry.");
                            }
                        } else {
                            errorMessageView.setText(message);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
