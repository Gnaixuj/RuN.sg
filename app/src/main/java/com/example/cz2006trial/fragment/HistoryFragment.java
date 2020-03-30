package com.example.cz2006trial.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cz2006trial.R;
import com.example.cz2006trial.historyPage.HistoryRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements HistoryRecyclerViewAdapter.OnItemListener {

    private static final String TAG = "HistoryFragment";
    private ArrayList< String > mDatasetHistoryRoutes = new ArrayList <> ();
    private ArrayList < String > mDatasetSavedRoutes = new ArrayList <> ();
    //private ArrayList < String > mDataset;
    private enum DataType {
        HISTORY_ROUTES,
        SAVED_ROUTES
    }
    private DataType currentDataType;

    private RadioButton historyRoutesRadioButton;
    private RadioButton savedRoutesRadioButton;

    HistoryRecyclerViewAdapter historyRoutesAdapter;
    HistoryRecyclerViewAdapter savedRoutesAdapter;
    HistoryRecyclerViewAdapter adapter;
    RecyclerView historyRoutesView;
    RecyclerView savedRoutesView;
    View root;
    LinearLayout layout;
    CheckBox checkAll;
    Button deleteButton;

    //implement 2 recycler views that alternate visibility based on the radio button

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initDataset();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize dataset
        //initDataset();


        currentDataType = DataType.HISTORY_ROUTES;

        //mDataset = new ArrayList<>();
        this.root = view;
        historyRoutesRadioButton = view.findViewById(R.id.history_routes_rb);
        savedRoutesRadioButton = view.findViewById(R.id.saved_routes_rb);

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
        //layout.setVisibility(View.GONE);

        Button deleteSelected = view.findViewById(R.id.delete_selected);
        checkAll = view.findViewById(R.id.check_all_hist);

        toggleVisibility();

        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo delete selected
                if (adapter != null) {
                    ArrayList<String> checkedItems = adapter.removeAllChecked();
                    for (String item: checkedItems) {
                        debugOutput("item is " + item);
                        deleteData(item);
/*                        if (currentDataType == DataType.HISTORY_ROUTES) {
                            historyRoutesAdapter.removeItems();
                            historyRoutesAdapter.notifyDataSetChanged();
                            adapter = historyRoutesAdapter;
                        }
                            //mDatasetHistoryRoutes.remove(item);
                        else {
                            savedRoutesAdapter.removeItems();
                            savedRoutesAdapter.notifyDataSetChanged();
                            adapter = savedRoutesAdapter;*/
                        }
                         /*   mDatasetSavedRoutes.remove(item);
                        adapter.removeItem(i);
                        i++;*/
                    }
                toggleVisibility();
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Selected items deleted", Toast.LENGTH_SHORT).show();
/*                    if (currentDataType == DataType.HISTORY_ROUTES) initRecyclerView(root, mDatasetHistoryRoutes);
                    else initRecyclerView(root, mDatasetSavedRoutes);*/
//                }
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
                }
                else {
                    deleteButton.setText("Delete History");
                    checkAll.setChecked(false);
                    if (adapter != null) adapter.showCheckBox(false);
                    layout.setVisibility(View.GONE);

                }

            }
        });
        //adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), mDataset, this);
        //recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        historyRoutesRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    currentDataType = DataType.HISTORY_ROUTES;
                    adapter = historyRoutesAdapter;
                    toggleVisibility();
                    //currentDataType = HistoryFragment.DataType.HISTORY_ROUTES;
                    //mDataset = (ArrayList<String>) mDatasetHistoryRoutes.clone();
                    //debugOutput(String.valueOf(mDataset.size()));
                    //adapter.notifyDataSetChanged();
                    //initRecyclerView(root, mDatasetHistoryRoutes);
                }
                else {
                    currentDataType = DataType.SAVED_ROUTES;
                    adapter = savedRoutesAdapter;
                    toggleVisibility();
                    //mDataset = (ArrayList<String>) mDatasetSavedRoutes.clone();
                    //adapter.notifyDataSetChanged();
                    //initRecyclerView(root, mDatasetSavedRoutes);
                }
            }
        });

/*        historyRoutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        savedRoutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        //initDataset();
        //debugOutput(String.valueOf(mDatasetHistoryRoutes.size()));
    }

    private void toggleVisibility() {
        if (historyRoutesView != null && savedRoutesView != null) {
            if (currentDataType == DataType.HISTORY_ROUTES) {
                historyRoutesView.setVisibility(View.VISIBLE);
                savedRoutesView.setVisibility(View.GONE);
            }
            else {
                historyRoutesView.setVisibility(View.GONE);
                savedRoutesView.setVisibility(View.VISIBLE);
            }
        }
        layout.setVisibility(View.GONE);
        checkAll.setChecked(false);
        deleteButton.setText("Delete History");
        if (adapter != null) adapter.showCheckBox(false);

    }

    private void deleteData(String item) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference;
        if (currentDataType == DataType.HISTORY_ROUTES)
            databaseReference = FirebaseDatabase.getInstance().getReference().child(UID).child("userLocationSessions").child(item);
        else
            databaseReference = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes").child(item);
        debugOutput(item);
        databaseReference.removeValue();
    }

    private void initDataset() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        debugOutput("initDataset: UID = " + UID);

        final DatabaseReference databaseLocationSessions= FirebaseDatabase.getInstance().getReference().child(UID).child("userLocationSessions");
        final DatabaseReference databaseSavedRoutes = FirebaseDatabase.getInstance().getReference().child(UID).child("userSavedRoutes");

        databaseLocationSessions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
/*                if (mDatasetHistoryRoutes != null)
                    mDatasetHistoryRoutes.clear();*/
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: after fetching key (History Route) = " + postSnapshot.getKey());

                    mDatasetHistoryRoutes.add(postSnapshot.getKey());
                }

                historyRoutesAdapter.notifyDataSetChanged();

                //currentDataType = HistoryFragment.DataType.HISTORY_ROUTES;
                //mDataset = mDatasetHistoryRoutes;
                //adapter.notifyDataSetChanged();
                //initRecyclerView(root, mDatasetHistoryRoutes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDatasetHistoryRoute: Fail = " + databaseError.getCode());
            }
        });

        databaseSavedRoutes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
/*                if (mDatasetSavedRoutes != null)
                    mDatasetSavedRoutes.clear();*/
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: after fetching key (Saved Route) = " + postSnapshot.getKey());

                    mDatasetSavedRoutes.add(postSnapshot.getKey());
                }
                //currentDataType = DataType.SAVED_ROUTES;
                //mDataset = mDatasetHistoryRoutes;
                //adapter.notifyDataSetChanged();
                //initRecyclerView(root, mDatasetSavedRoutes);

                savedRoutesAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDatasetSavedRoute: Fail = " + databaseError.getCode());
            }
        });
    }

/*    private void initRecyclerView(View view, ArrayList<String> dataset) {
        debugOutput("initRecyclerView: Started");

        adapter = new HistoryRecyclerViewAdapter(getActivity().getApplicationContext(), dataset, this);
        recyclerView.setAdapter(adapter);

    }*/

    @Override
    public void onItemClick(int position) {
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
