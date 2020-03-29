package com.example.cz2006trial.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cz2006trial.R;
import com.example.cz2006trial.historyPage.HistoryPageActivity;
import com.example.cz2006trial.historyPage.RecyclerViewAdapter;
import com.example.cz2006trial.historyPage.RoutePageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements RecyclerViewAdapter.OnNoteListener {

    private static final String TAG = "HistoryFragment";
    private ArrayList< String > mDatasetHistoryRoutes = new ArrayList <> ();
    private ArrayList < String > mDatasetSavedRoutes = new ArrayList <> ();
    private ArrayList < String > mDataset;
    private enum DataType {
        HISTORY_ROUTES,
        SAVED_ROUTES
    }
    private DataType currentDataType;

    private RadioButton historyRoutesRadioButton;
    private RadioButton savedRoutesRadioButton;

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history_page, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataset();

        mDataset = new ArrayList<>();
        this.root = view;
        historyRoutesRadioButton = view.findViewById(R.id.history_routes_rb);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), mDataset, this);
        //recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        historyRoutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = HistoryFragment.DataType.HISTORY_ROUTES;
                //mDataset = (ArrayList<String>) mDatasetHistoryRoutes.clone();
                //debugOutput(String.valueOf(mDataset.size()));
                //adapter.notifyDataSetChanged();
                initRecyclerView(root, mDatasetHistoryRoutes);
            }
        });

        savedRoutesRadioButton = view.findViewById(R.id.saved_routes_rb);
        savedRoutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = HistoryFragment.DataType.SAVED_ROUTES;
                //mDataset = (ArrayList<String>) mDatasetSavedRoutes.clone();
                //adapter.notifyDataSetChanged();
                initRecyclerView(root, mDatasetSavedRoutes);
            }
        });

        //initDataset();
        //debugOutput(String.valueOf(mDatasetHistoryRoutes.size()));
    }

    private void initDataset() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        debugOutput("initDataset: UID = " + UID);

        final DatabaseReference databaseLocationSessions= FirebaseDatabase.getInstance().getReference().child(UID).child("userLocationSessions");
        final DatabaseReference databaseSavedRoutes = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes");

        databaseLocationSessions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: after fetching key (History Route) = " + postSnapshot.getKey());
                    mDatasetHistoryRoutes.add(postSnapshot.getKey());
                }

                currentDataType = HistoryFragment.DataType.HISTORY_ROUTES;
                //mDataset = mDatasetHistoryRoutes;
                //adapter.notifyDataSetChanged();
                initRecyclerView(root, mDatasetHistoryRoutes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDatasetHistoryRoute: Fail = " + databaseError.getCode());
            }
        });

        databaseSavedRoutes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: after fetching key (Saved Route) = " + postSnapshot.getKey());
                    mDatasetSavedRoutes.add(postSnapshot.getKey());
                }
                currentDataType = DataType.SAVED_ROUTES;
                //mDataset = mDatasetHistoryRoutes;
                //adapter.notifyDataSetChanged();
                initRecyclerView(root, mDatasetSavedRoutes);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDatasetSavedRoute: Fail = " + databaseError.getCode());
            }
        });
    }

    private void initRecyclerView(View view, ArrayList<String> dataset) {
        debugOutput("initRecyclerView: Started");

        adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), dataset, this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onNoteClick(int position) {
        debugOutput("onNoteClick: Clicked position = " + position);
        Bundle bundle = new Bundle();
        bundle.putString("route_type", (currentDataType == HistoryFragment.DataType.HISTORY_ROUTES ? "userLocationSessions" : "userSavedRoutes"));
        bundle.putString("selected_route", (currentDataType == HistoryFragment.DataType.HISTORY_ROUTES ? mDatasetHistoryRoutes.get(position):
                mDatasetSavedRoutes.get(position)));

        Navigation.findNavController(getView()).navigate(R.id.nav_route, bundle);

    }

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
//        System.out.println(TAG + " === " + msg);
    }
}
