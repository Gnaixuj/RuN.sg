package com.example.cz2006trial.fragment;

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

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.controller.GoalController;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.activity.MapsActivity;
import com.example.cz2006trial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class EditGoalsFragment extends Fragment {


    private TextView dateView;
    private TextView goalDistanceView;
    private TextView initialGoalTargetView;
    private EditText newGoalTargetEditText;
    private TextView errorMessageView;
    private Button editGoalDoneButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_goals, container, false);

        dateView = view.findViewById(R.id.editGoalDateView);
        goalDistanceView = view.findViewById(R.id.goalDistance);
        initialGoalTargetView = view.findViewById(R.id.initialGoalTarget);
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
        displayGoal(date);
    }

    public void displayGoal(final String date) {

        dateView.setText("Date: " + date);
        DatabaseManager.getData(new DatabaseManager.DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, final double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else if (errorMsg[1] != null) {
                    goalDistanceView.setText(R.string.zeroDistance);
                    initialGoalTargetView.setVisibility(View.GONE);
                } else {
                    goalDistanceView.setText("Distance travelled: " + doubleArgs[0] + " km");
                    initialGoalTargetView.setText("Initial Goal Target: " + doubleArgs[1] + " km");
                    if (doubleArgs[1] == -1 || doubleArgs[1] == 0) {
                        initialGoalTargetView.setVisibility(View.GONE);
                    }
                }
                editGoalDoneButton.setVisibility(View.VISIBLE);
                editGoalDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newGoalTargetText = String.valueOf(newGoalTargetEditText.getText());
                        String message = GoalController.validateGoalFields(newGoalTargetText, doubleArgs[0]);
                        if (message.equals("success")) {
                            errorMessageView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Goal Target Updated", Toast.LENGTH_SHORT).show();
                            DatabaseManager.updateGoalData(date, doubleArgs[0], Double.parseDouble(newGoalTargetText));
                            getActivity().onBackPressed();
                            /*if (GoalController.updateDataOnDatabase(date, doubleArgs[0], Double.parseDouble(newGoalTargetText))) {
                                getActivity().onBackPressed();
                            } else {
                                errorMessageView.setText("Error. Something went wrong. Please retry.");
                            }*/
                        } else {
                            errorMessageView.setText(message);
                        }
                    }
                });
            }
        }, "goals", date);


        /*String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseGoals = FirebaseDatabase.getInstance().getReference("goals").child(UID).child(date);
        databaseGoals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateView.setText("Date: " + date);
                final Goal goal = dataSnapshot.getValue(Goal.class);
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
        });*/
    }
}
