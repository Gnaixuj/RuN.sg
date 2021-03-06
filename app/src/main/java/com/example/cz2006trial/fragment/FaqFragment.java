package com.example.cz2006trial.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cz2006trial.adapter.CustomExpandableListAdapter;
import com.example.cz2006trial.adapter.ExpandableListData;
import com.example.cz2006trial.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This fragment is used to display FAQ.
 */
public class FaqFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_faq, container, false);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListDetail = ExpandableListData.getData("FAQ");
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        return view;
    }
}
