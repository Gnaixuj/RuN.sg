package com.example.cz2006trial;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MapsTrackFragment extends Fragment {

    private static final String TAG = "MapsTrackFragment";
    private Button createButton;
    private Button backButton;
    private Button startButton;
    private Button endButton;
    private Chronometer mChronometer;
    private long timeElapsed = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_track, container, false);
        createButton = view.findViewById(R.id.buttonCreate);
        backButton = view.findViewById(R.id.buttonBack);
        startButton = view.findViewById(R.id.buttonStart);
        endButton = view.findViewById(R.id.buttonEnd);
        mChronometer = view.findViewById(R.id.chronometer);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Click Create", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity)getActivity()).setLayoutWeight(10);
                ((MapsActivity)getActivity()).setViewPager(0);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startButton.getText().equals("Start") || startButton.getText().equals("Resume")) {
                    mChronometer.setBase(SystemClock.elapsedRealtime() + timeElapsed);
                    startButton.setText("Pause");
                    endButton.setVisibility(View.VISIBLE);
                    mChronometer.start();
                    ((MapsActivity)getActivity()).beginTracking();
                }
                else {
                    startButton.setText("Resume");
                    mChronometer.stop();
                    timeElapsed = mChronometer.getBase() - SystemClock.elapsedRealtime();
                    ((MapsActivity)getActivity()).endTracking();
                }
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endButton.setVisibility(View.INVISIBLE);
                startButton.setText("Start");
                mChronometer.stop();
                timeElapsed = mChronometer.getBase() - SystemClock.elapsedRealtime();
                ((MapsActivity)getActivity()).endTracking();
                UserLocationSessionEntity.clearAllUserLocation();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        UserLocationSessionEntity.clearAllUserLocation();
    }
}
