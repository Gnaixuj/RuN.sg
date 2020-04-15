package com.example.cz2006trial.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cz2006trial.database.DatabaseManager;
import com.example.cz2006trial.database.ImageDatabaseManager;
import com.example.cz2006trial.R;
import com.example.cz2006trial.model.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This fragment is used to display user profile information retrieved from Firebase.
 */
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

    // display user profile information based on data retrieved from Firebase
    public void displayProfile() {
        DatabaseManager.getProfileData(new DatabaseManager.ProfileDatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg) {
                // Database read failed
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                    // No data available for retrieval
                else if (errorMsg[1] != null)
                    Toast.makeText(getContext(), errorMsg[1], Toast.LENGTH_LONG).show();
                    // Data available for retrieval
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
                    // retrieve profile photo from Firebase Storage
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message, byte[] bytes) {
                            Log.d("inside", "profilephoto");
                            if (bytes != null) {
                                profilePhoto.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                profilePhoto.setVisibility(View.VISIBLE);
                            }
                        }
                    }, "retrieve", profilePhoto);
                    // get goal data from Firebase to display total distance travelled by user and the daily target
                    DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
                        @Override
                        public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                            double totalDistance = 0.0;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                            TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
                            sdf.setTimeZone(tz);
                            java.util.Date curDate = new java.util.Date();
                            String date = sdf.format(curDate);
                            for (Goal goal : goals) {
                                totalDistance += goal.getDistance();
                                if (goal.getDate().equals(date)) {
                                    // if daily target is set, display daily target. Otherwise, display '-'
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

    // make TextView and Image View visible only after the data is retrieved from Firebase
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

    // display updated user profile information if user updates user profile in EditProfileFragment
    @Override
    public void onStart() {
        super.onStart();
        displayProfile();
    }
}