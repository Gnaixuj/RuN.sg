package com.example.cz2006trial.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.ImageDatabaseManager;
import com.example.cz2006trial.R;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UserProfileFragment extends Fragment {

    private ImageView profilePhoto;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView DOBTextView;
    private TextView heightTextView;
    private TextView weightTextView;
    private TextView BMITextView;
    private ImageView editProfileButton;
    private TextView totalDistanceTextView;
    private TextView todayTargetTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePhoto = view.findViewById(R.id.profile);
        usernameTextView = view.findViewById(R.id.username);
        emailTextView = view.findViewById(R.id.email);
        DOBTextView = view.findViewById(R.id.DOB);
        heightTextView = view.findViewById(R.id.height);
        weightTextView = view.findViewById(R.id.weight);
        BMITextView = view.findViewById(R.id.BMI);
        editProfileButton = view.findViewById(R.id.edit);
        totalDistanceTextView = view.findViewById(R.id.totalDistance);
        todayTargetTextView = view.findViewById(R.id.todayTarget);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayProfile();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.nav_editprofile);
            }
        });
    }

    public void displayProfile() {
        DatabaseManager.getProfileData(new DatabaseManager.ProfileDatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg) {
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else if (errorMsg[1] != null)
                    Toast.makeText(getContext(), errorMsg[1], Toast.LENGTH_LONG).show();
                else {
                    heightTextView.setText("-");
                    weightTextView.setText("-");
                    BMITextView.setText("-");
                    usernameTextView.setText(stringArgs.get(0));
                    emailTextView.setText(stringArgs.get(1));
                    DOBTextView.setText(stringArgs.get(2));
                    if (doubleArgs[0] != 0)
                        heightTextView.setText("" + doubleArgs[0]);
                    if (doubleArgs[1] != 0)
                        weightTextView.setText("" + doubleArgs[1]);
                    if (doubleArgs[2] != 0)
                        BMITextView.setText("" + doubleArgs[2]);
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message) {
                        }
                    }, "retrieve", profilePhoto);
                    DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
                        @Override
                        public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                            double totalDistance = 0.0;
                            double todayTarget;
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String date = dateFormat.format(Calendar.getInstance().getTime());
                            for (Goal goal : goals) {
                                totalDistance += goal.getDistance();
                                if (goal.getDate().equals(date)) {
                                    if (goal.getTarget() > 0)
                                        todayTargetTextView.setText(Math.round(goal.getDistance() * 10) / 10.0 + "/" + goal.getTarget() + " km");
                                }

                            }
                            totalDistanceTextView.setText(Math.round(totalDistance * 10) / 10.0 + " km");
                        }
                    }, null);
                    loadingComplete();
                }
            }
        });
    }

    private void loadingComplete() {
        profilePhoto.setVisibility(View.VISIBLE);
        usernameTextView.setVisibility(View.VISIBLE);
        emailTextView.setVisibility(View.VISIBLE);
        DOBTextView.setVisibility(View.VISIBLE);
        heightTextView.setVisibility(View.VISIBLE);
        weightTextView.setVisibility(View.VISIBLE);
        BMITextView.setVisibility(View.VISIBLE);
        editProfileButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
            @Override
            public void onCallback(String[] message) {
            }
        }, "retrieve", profilePhoto);
    }
}
