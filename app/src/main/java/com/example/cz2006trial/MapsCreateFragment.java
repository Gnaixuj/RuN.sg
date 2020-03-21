package com.example.cz2006trial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.common.collect.Maps;

public class MapsCreateFragment extends Fragment {

    private static final String TAG = "MapsCreateFragment";
    private Button trackButton;
    private Button backButton;
    private Button createButton;
    private Button saveButton;
    private Button setStartButton;
    private Button setEndButton;
    private TextView startPoint;
    private TextView endPoint;
    private UserRouteEntity userRoute = new UserRouteEntity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_create, container, false);
        trackButton = view.findViewById(R.id.buttonTrack);
        backButton = view.findViewById(R.id.buttonBack);
        createButton = view.findViewById(R.id.buttonCreate);
        saveButton = view.findViewById(R.id.buttonSave);
        startPoint = view.findViewById(R.id.startPoint);
        endPoint = view.findViewById(R.id.endPoint);
        setStartButton = view.findViewById(R.id.buttonSetStart);
        setEndButton = view.findViewById(R.id.buttonSetEnd);

        displayStartEndText();

        setStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setStartButton.getText().equals("SET")) {
                    setStartButton.setText("DONE");
                    createButton.setVisibility(View.GONE);
                    setEndButton.setVisibility(View.GONE);
                    ((MapsActivity) getActivity()).setStartPoint(userRoute);
                } else {
                    setStartButton.setText("SET");
                    createButton.setVisibility(View.VISIBLE);
                    setEndButton.setVisibility(View.VISIBLE);
                    displayStartEndText();
                }
            }
        });

        setEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setEndButton.getText().equals("SET")) {
                    setEndButton.setText("DONE");
                    createButton.setVisibility(View.GONE);
                    setStartButton.setVisibility(View.GONE);
                    ((MapsActivity) getActivity()).setEndPoint(userRoute);
                } else {
                    setEndButton.setText("SET");
                    setStartButton.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    displayStartEndText();
                }
            }
        });

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity) getActivity()).setLayoutWeight(1);
                ((MapsActivity) getActivity()).setViewPager(1);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity) getActivity()).setLayoutWeight(10);
                ((MapsActivity) getActivity()).setViewPager(0);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createButton.getText().equals("Create")) {
                    String message = ((MapsActivity) getActivity()).createRoute(userRoute);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    if (message == "Route created") {
                        setStartButton.setVisibility(View.GONE);
                        setEndButton.setVisibility(View.GONE);
                        createButton.setText("Create New");
                        saveButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    ((MapsActivity) getActivity()).clearRouteDetails();
                    userRoute = new UserRouteEntity();
                    createButton.setText("Create");
                    displayStartEndText();
                    setStartButton.setVisibility(View.VISIBLE);
                    setEndButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.GONE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setVisibility(View.GONE);
                UserRouteController.updateUserRouteDatabase(userRoute);
            }
        });

        return view;
    }

    public void displayStartEndText() {
        String startPointText = UserRouteController.getStartPointName(userRoute);
        String endPointText = UserRouteController.getEndPointName(userRoute);
        if (startPointText != null)
            startPoint.setText("Start Point: " + startPointText);
        else
            startPoint.setText("Start Point: Select an access point marker");
        if (endPointText != null)
            endPoint.setText("End Point: " + endPointText);
        else
            endPoint.setText("End Point: Select an access point marker");
    }
}
