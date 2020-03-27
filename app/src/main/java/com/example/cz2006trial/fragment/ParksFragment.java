package com.example.cz2006trial.fragment;

import android.app.Dialog;
import android.media.Rating;
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

import java.text.BreakIterator;
import java.util.HashMap;

public class ParksFragment extends DialogFragment {

    static ParksFragment newInstance(HashMap<String, Object> info) {
        ParksFragment fragment = new ParksFragment();
        Bundle args = new Bundle();
        args.putSerializable("info", info);
        fragment.setArguments(args);
        return fragment;
    }

/*    public ParksFragment (HashMap<String, Object> info) {
        this.info = info;
    }*/

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

        bar.setStepSize(0.01f);

        HashMap<String, Object> info = new HashMap<>();

        info = (HashMap<String, Object>) getArguments().getSerializable("info");
        title.setText(info.get("name").toString());
        address.setText(info.get("address").toString());
        if ((Boolean) info.get("open")) {
            open.setText("Open Now");
        }
        else {
            open.setText("Closed");
        }
        bar.setRating(Float.parseFloat(info.get("rating").toString()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
