package com.example.cz2006trial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EditGoalsFragment extends Fragment {


    TextView dateView;
    TextView goalDistanceView;
    TextView initialGoalTargetView;
    TextView newGoalTargetPlainText;
    EditText newGoalTargetEditText;
    TextView errorMessageView;
    Button editGoalDoneButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_editgoals, container, false);

        dateView = view.findViewById(R.id.editGoalDateView);
        goalDistanceView = view.findViewById(R.id.goalDistance);
        initialGoalTargetView = view.findViewById(R.id.initialGoalTarget);
        newGoalTargetPlainText = view.findViewById(R.id.newGoalTargetText);
        newGoalTargetEditText = view.findViewById(R.id.newGoalTarget);
        errorMessageView = view.findViewById(R.id.errorMessage);
        editGoalDoneButton = view.findViewById(R.id.DoneEditGoalButton);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        MapsActivity activity = (MapsActivity) getActivity();
        final String date = activity.getDate();
        Log.i("edit goals", "date is shown");
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
                    if (goal.getTarget() == -1) {
                        initialGoalTargetView.setVisibility(View.GONE);
                    }
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
                            Toast.makeText(getContext(), "Goal Target Updated", Toast.LENGTH_SHORT).show();
                            if (GoalController.updateDataOnDatabase(date, goalDistance, Double.parseDouble(newGoalTargetText))) {
                                getActivity().onBackPressed();
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
