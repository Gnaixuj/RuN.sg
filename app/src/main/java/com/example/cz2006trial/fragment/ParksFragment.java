package com.example.cz2006trial.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cz2006trial.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Locale;

/**
 * This fragment is used to display park information using a dialog fragment and
 * allow user to be directed to GoogleMaps application when dialog is clicked
 */
public class ParksFragment extends DialogFragment {

    static ParksFragment newInstance(HashMap<String, Object> info) {
        ParksFragment fragment = new ParksFragment();
        Bundle args = new Bundle();
        args.putSerializable("info", info);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_park_popup, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = view.findViewById(R.id.park_title);
        TextView address = view.findViewById(R.id.park_address);
        TextView open = view.findViewById(R.id.park_open);
        RatingBar bar = view.findViewById(R.id.ratingBar);
        ImageButton button = view.findViewById(R.id.imageButton);
        TextView openMap = view.findViewById(R.id.go_to_map);
        TextView rating = view.findViewById(R.id.rating_text);

        bar.setStepSize(0.01f);

        HashMap<String, Object> info;

        info = (HashMap<String, Object>) getArguments().getSerializable("info");
        final String name = info.get("name").toString();
        title.setText(name);
        address.setText(info.get("address").toString());
        final LatLng location = (LatLng) info.get("location");

        if ((Boolean) info.get("open")) {
            open.setText("Open Now");
            open.setTextColor(getActivity().getResources().getColor(R.color.color5, getActivity().getTheme()));
        } else {
            open.setText("Closed");
            open.setTextColor(Color.RED);
        }
        bar.setRating(Float.parseFloat(info.get("rating").toString()));
        if (Float.parseFloat(info.get("rating").toString()) == 0.0f) {
            rating.setText("No rating");
        } else {
            rating.setText("Rating: " + info.get("rating").toString());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", location.latitude, location.longitude, name);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });

    }
}
