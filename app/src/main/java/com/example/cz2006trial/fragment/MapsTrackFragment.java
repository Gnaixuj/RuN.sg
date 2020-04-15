package com.example.cz2006trial.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cz2006trial.database.DatabaseManager;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.controller.GoogleMapController;
import com.example.cz2006trial.R;
import com.example.cz2006trial.controller.UserLocationController;
import com.example.cz2006trial.model.UserLocationSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This fragment is used to track route based on user's location
 */
public class MapsTrackFragment extends Fragment {

    private static final String TAG = "MapsTrackFragment";
    private Button startButton;
    private Button endButton;
    private Chronometer mChronometer;
    private TextView distanceTravelledView;
    private TextView goalProgressView;
    private long timeElapsed = 0;
    private UserLocationSession userLocationSession = new UserLocationSession();

    private GoogleMapController controller = GoogleMapController.getController();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_track, container, false);
        startButton = view.findViewById(R.id.buttonStart);
        endButton = view.findViewById(R.id.buttonEnd);
        mChronometer = view.findViewById(R.id.chronometer);
        distanceTravelledView = view.findViewById(R.id.distanceTravelled);
        goalProgressView = view.findViewById(R.id.goalProgress);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when button is 'Start', clear tracked route on map if any and start tracking
                if (startButton.getText().equals("Start")) {
                    controller.clearTrack();
                    distanceTravelledView.setText("Distance travelled: 0.0 km");
                    endButton.setText("End");
                    mChronometer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
                    startButton.setText("Pause");
                    endButton.setVisibility(View.VISIBLE);
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    mChronometer.start();
                    // get current date in Singapore
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                    TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
                    sdf.setTimeZone(tz);
                    java.util.Date curdate = new java.util.Date();
                    String dateStr = sdf.format(curdate);
                    Date date = DatabaseManager.convertStringToDate(dateStr);
                    // create an instance of UserLocationSession to mark the start of tracking
                    userLocationSession = new UserLocationSession(date);
                    controller.beginTracking(userLocationSession);
                    // display tracking details which include distance travelled and daily target
                    displaySession(userLocationSession);
                }
                // when button is 'Resume', resume tracking
                else if (startButton.getText().equals("Resume")) {
                    mChronometer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
                    startButton.setText("Pause");
                    mChronometer.start();
                    controller.resumeTracking(userLocationSession, SystemClock.elapsedRealtime() - mChronometer.getBase());
                }
                // when button is 'Pause', stop tracking
                else {
                    startButton.setText("Resume");
                    mChronometer.stop();
                    timeElapsed = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    controller.pauseTracking(userLocationSession, timeElapsed);
                }
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when button is 'End', end tracking
                if (endButton.getText().equals("End")) {
                    endButton.setText("Save");
                    startButton.setText("Start");
                    mChronometer.stop();
                    timeElapsed = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    controller.endTracking(userLocationSession, timeElapsed);
                }
                // when button is 'Save', update user tracking route information to firebase database via Database Manager
                else {
                    UserLocationController.setTimeTaken(userLocationSession, timeElapsed);
                    DatabaseManager.updateUserLocationSession(userLocationSession);
                    Toast.makeText(getContext(), "Route saved successfully", Toast.LENGTH_SHORT).show();
                    endButton.setText("End");
                    endButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("inside", "onStart");
        this.userLocationSession = controller.getUserLocationSession();
        Log.d("paused", "" + controller.isStartTrack() + controller.isTrackPaused());
        // if user moves to a new page/fragment while the user is still tracking and come back to tracking page,
        // reload previous tracking state and paused the tracking
        if (controller.isStartTrack() || controller.isTrackPaused()) {
            startButton.setText("Resume");
            mChronometer.setBase(SystemClock.elapsedRealtime() - controller.getTimeElapsed());
            distanceTravelledView.setText("Distance travelled: " + Math.round(userLocationSession.getDistance() * 10) / 10.0 + " km");
            distanceTravelledView.setVisibility(View.VISIBLE);
            timeElapsed = controller.getTimeElapsed();
            endButton.setVisibility(View.VISIBLE);
            displaySession(userLocationSession);
        }
        // to make sure that the tracking route that has ended is still on the map when user comes back from different fragment
        else if (controller.isTrackEnded()) {
            endButton.setText("Save");
            endButton.setVisibility(View.VISIBLE);
            startButton.setText("Start");
            timeElapsed = controller.getTimeElapsed();
            distanceTravelledView.setText("Distance travelled: " + Math.round(userLocationSession.getDistance() * 10) / 10.0 + " km");
            distanceTravelledView.setVisibility(View.VISIBLE);
        }

    }

    // display distance travelled by user during tracking
    // display daily target retrieved from firebase database via Database Manager
    public void displaySession(final UserLocationSession userLocationSession) {
        controller.setDisplayTrackingDistanceListener(new GoogleMapController.DisplayTrackingDistanceListener() {
            @Override
            public void onChange() {
                controller.setTimeElapsed(SystemClock.elapsedRealtime() - mChronometer.getBase());
                distanceTravelledView.setText("Distance travelled: " + Math.round(userLocationSession.getDistance() * 10) / 10.0 + " km");
                distanceTravelledView.setVisibility(View.VISIBLE);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");
                sdf.setTimeZone(tz);
                java.util.Date curDate = new java.util.Date();
                String dateString = sdf.format(curDate);

                Log.d("date", dateString);
                DatabaseManager.getGoalData(new DatabaseManager.GoalDatabaseCallback() {
                    @Override
                    public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                        // Database read failed
                        if (errorMsg[0] != null)
                            Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                            // No data available for retrieval
                        else if (errorMsg[1] != null) {
                            goalProgressView.setText(R.string.noTargetSet);
                        }
                        // data available for retrieval
                        else {
                            // when daily target is not -1 or 0, display target distance value
                            if (doubleArgs[1] != -1 && doubleArgs[1] != 0) {
                                goalProgressView.setText("Target: " + (Math.round(doubleArgs[0] * 10) / 10.0) + " / " + doubleArgs[1] + " km");
                            }
                            // otherwise, display 'no daily target set'
                            else {
                                goalProgressView.setText(R.string.noTargetSet);

                            }
                        }
                    }
                }, dateString);
            }
        });
    }
}
