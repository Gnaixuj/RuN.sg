package com.example.cz2006trial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.Map;

public class MapsMainFragment extends Fragment {

/*    private static final String TAG = "MapsMainFragment";
    private Button createButton;
    private Button trackButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_main, container, false);
        createButton = view.findViewById(R.id.buttonCreate);
        trackButton = view.findViewById(R.id.buttonTrack);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        final MapFragment fragm = (MapFragment) fm.findFragmentById(R.id.map);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragm.setLayoutWeight(1);
                fragm.setViewPager(2);
            }
        });

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragm.setLayoutWeight(1);
                fragm.setViewPager(1);
            }
        });

        return view;
    }*/
}
