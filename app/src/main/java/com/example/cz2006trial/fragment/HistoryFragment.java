package com.example.cz2006trial.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cz2006trial.R;
import com.example.cz2006trial.adapter.HistoryRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * This fragment is used to display a list of saved routes to users.
 * Saved routes can be either created routes or tracked routes.
 */
public class HistoryFragment extends Fragment implements HistoryRecyclerViewAdapter.OnItemListener {

    private static final String TAG = "HistoryFragment";

    private ArrayList < Pair < String, Date > > mDatasetHistoryRoutes = new ArrayList < Pair < String, Date > > ();
    private ArrayList < Pair < String, Date > > mDatasetSavedRoutes = new ArrayList < Pair < String, Date > > ();

    private enum DataType {
        HISTORY_ROUTES,
        SAVED_ROUTES
    }

    private DataType currentDataType;

    private HistoryRecyclerViewAdapter historyRoutesAdapter;
    private HistoryRecyclerViewAdapter savedRoutesAdapter;
    private HistoryRecyclerViewAdapter adapter;
    private RecyclerView historyRoutesView;
    private RecyclerView savedRoutesView;
    private LinearLayout layout;
    private CheckBox checkAll;
    private Button deleteButton;
    private TextView noDataTextView;

    //implement 2 recycler views that alternate visibility based on the radio button

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initDataset();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentDataType = DataType.HISTORY_ROUTES;

        RadioButton historyRoutesRadioButton = view.findViewById(R.id.history_routes_rb);
        RadioButton savedRoutesRadioButton = view.findViewById(R.id.saved_routes_rb);

        noDataTextView = view.findViewById(R.id.no_data_text_view);

        //initialize both recycler views
        historyRoutesView = view.findViewById(R.id.history_routes_view);
        savedRoutesView = view.findViewById(R.id.saved_routes_view);

        historyRoutesView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedRoutesView.setLayoutManager(new LinearLayoutManager(getContext()));

        historyRoutesAdapter = new HistoryRecyclerViewAdapter(getActivity().getApplicationContext(), mDatasetHistoryRoutes, this);
        savedRoutesAdapter = new HistoryRecyclerViewAdapter(getActivity().getApplicationContext(), mDatasetSavedRoutes, this);

        historyRoutesView.setAdapter(historyRoutesAdapter);
        savedRoutesView.setAdapter(savedRoutesAdapter);

        adapter = historyRoutesAdapter;

        deleteButton = view.findViewById(R.id.delete_hist);
        layout = view.findViewById(R.id.delete);

        Button deleteSelected = view.findViewById(R.id.delete_selected);
        checkAll = view.findViewById(R.id.check_all_hist);

        toggleVisibility();

        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo delete selected
                if (adapter != null) {
                    ArrayList<Pair<String, Date>> checkedItems = adapter.removeAllChecked();

                    if (currentDataType == DataType.HISTORY_ROUTES) {
                        mDatasetHistoryRoutes.removeAll(checkedItems);
                    } else {
                        mDatasetHistoryRoutes.removeAll(checkedItems);
                    }

                    for (Pair<String, Date> item : checkedItems) {
                        debugOutput("item is " + item.first);
                        deleteDataFromFirebase(item.first);
                    }
                }

                toggleVisibility();
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Selected items deleted", Toast.LENGTH_SHORT).show();
            }
        });


        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (adapter != null) adapter.setChecked(b);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteButton.getText().equals("Delete History")) {
                    deleteButton.setText("Done");
                    if (adapter != null) adapter.showCheckBox(true);
                    layout.setVisibility(View.VISIBLE);
                } else {
                    deleteButton.setText("Delete History");
                    checkAll.setChecked(false);
                    if (adapter != null) adapter.showCheckBox(false);
                    layout.setVisibility(View.GONE);
                }
            }
        });

        historyRoutesRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    currentDataType = DataType.HISTORY_ROUTES;
                    adapter = historyRoutesAdapter;
                    toggleVisibility();
                } else {
                    currentDataType = DataType.SAVED_ROUTES;
                    adapter = savedRoutesAdapter;
                    toggleVisibility();
                }
            }
        });
    }

    private void toggleVisibility() {
        if (historyRoutesView != null && savedRoutesView != null) {
            if (currentDataType == DataType.HISTORY_ROUTES) {
                historyRoutesView.setVisibility(View.VISIBLE);
                savedRoutesView.setVisibility(View.GONE);

                Log.d(TAG, "toggleVisibility: size = "+ mDatasetHistoryRoutes.size());
                if (mDatasetHistoryRoutes.isEmpty()) {
                    noDataTextView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "toggleVisibility: visible");
                } else
                    noDataTextView.setVisibility(View.INVISIBLE);
            } else {
                historyRoutesView.setVisibility(View.GONE);
                savedRoutesView.setVisibility(View.VISIBLE);

                if (mDatasetSavedRoutes.isEmpty())
                    noDataTextView.setVisibility(View.VISIBLE);
                else
                    noDataTextView.setVisibility(View.INVISIBLE);
            }
        }
        layout.setVisibility(View.GONE);
        checkAll.setChecked(false);
        deleteButton.setText("Delete History");
        if (adapter != null) adapter.showCheckBox(false);
    }

    private void deleteDataFromFirebase(String item) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference;
        if (currentDataType == DataType.HISTORY_ROUTES)
            databaseReference = FirebaseDatabase.getInstance().getReference().child(UID).child("userLocationSessions").child(item);
        else
            databaseReference = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes").child(item);
        debugOutput(item);
        databaseReference.removeValue();
    }

    private static class SortByDate implements Comparator<Pair < String, Date >>  {
        public int compare(Pair < String, Date > a, Pair < String, Date > b) {
            //                System.out.println("Date 1 occurs after Date 2");
            //                System.out.println("Date 1 occurs before Date 2");
            //                System.out.println("Both dates are equal");
            return Integer.compare(a.second.compareTo(b.second), 0);
        }
    }

    private void initDataset() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        debugOutput("initDataset: UID = " + UID);

        final DatabaseReference databaseLocationSessions= FirebaseDatabase.getInstance().getReference().child(UID).child("userLocationSessions");
        databaseLocationSessions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    Date value = postSnapshot.child("timestamp").getValue(Date.class);

                    Log.d(TAG, "onDataChange: HistoryRoute: key = " + key);
                    Log.d(TAG, "onDataChange: HistoryRoute: timestamp = " + value);

                    mDatasetHistoryRoutes.add(new Pair < String, Date > (key, value));
                }

                Log.d(TAG, "onDataChange: HistoryRoute: size = " + mDatasetHistoryRoutes.size());

                if (currentDataType.equals(DataType.HISTORY_ROUTES)) {
                    if (mDatasetHistoryRoutes.isEmpty())
                        noDataTextView.setVisibility(View.VISIBLE);
                    else {
                        Log.d(TAG, "onDataChange: HistoryRoute: invisible");
                        noDataTextView.setVisibility(View.INVISIBLE);
                    }
                }
                Collections.sort(mDatasetHistoryRoutes, new SortByDate());
                historyRoutesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDataset: HistoryRoute: fail = " + databaseError.getCode());
            }
        });

        final DatabaseReference databaseSavedRoutes = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes");
        databaseSavedRoutes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    Date value = postSnapshot.child("date").getValue(Date.class);

                    Log.d(TAG, "onDataChange: SavedRoute: key = " + key);
                    Log.d(TAG, "onDataChange: SavedRoute: timestamp = " + value);

                    mDatasetSavedRoutes.add(new Pair < String, Date > (key, value));
                }

                if (mDatasetSavedRoutes.isEmpty() && currentDataType.equals(DataType.SAVED_ROUTES)) {
                    noDataTextView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onDataChange: SavedRoute: visible");
                } else if (!mDatasetSavedRoutes.isEmpty() && currentDataType.equals(DataType.SAVED_ROUTES)) {
                    noDataTextView.setVisibility(View.INVISIBLE);
                }
                savedRoutesAdapter.notifyDataSetChanged();
                Collections.sort(mDatasetSavedRoutes, new SortByDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDataset: SavedRoute: fail = " + databaseError.getCode());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        debugOutput("onNoteClick: Clicked position = " + position);
        Bundle bundle = new Bundle();
        bundle.putString("route_type", (currentDataType == HistoryFragment.DataType.HISTORY_ROUTES ? "userLocationSessions" : "userSavedRoutes"));
        bundle.putString("selected_route", (currentDataType == HistoryFragment.DataType.HISTORY_ROUTES ? mDatasetHistoryRoutes.get(position).first:
                mDatasetSavedRoutes.get(position).first));
        Navigation.findNavController(getView()).navigate(R.id.nav_route, bundle);
    }

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
    }
}