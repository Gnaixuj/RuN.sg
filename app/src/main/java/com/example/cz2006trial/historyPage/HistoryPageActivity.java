package com.example.cz2006trial.historyPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.example.cz2006trial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryPageActivity extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener {

    private static final String TAG = "HistoryPageActivity";
    private ArrayList < String > mDatasetHistoryRoutes = new ArrayList <> ();
    private ArrayList < String > mDatasetSavedRoutes = new ArrayList <> ();
    private ArrayList < String > mDataset;

    private enum DataType {
        HISTORY_ROUTES,
        SAVED_ROUTES
    }

    private DataType currentDataType;

    private RadioButton historyRoutesRadioButton;
    private RadioButton savedRoutesRadioButton;

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
//        System.out.println(TAG + " === " + msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        debugOutput("onCreate: Started");

        historyRoutesRadioButton = findViewById(R.id.history_routes_rb);
        historyRoutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = DataType.HISTORY_ROUTES;
                mDataset = mDatasetHistoryRoutes;
                initRecyclerView();
            }
        });

        savedRoutesRadioButton = findViewById(R.id.saved_routes_rb);
        savedRoutesRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDataType = DataType.SAVED_ROUTES;
                mDataset = mDatasetSavedRoutes;
                initRecyclerView();
            }
        });

        initDataset();
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

                currentDataType = DataType.HISTORY_ROUTES;
                mDataset = mDatasetHistoryRoutes;
                initRecyclerView();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDatasetSavedRoute: Fail = " + databaseError.getCode());
            }
        });
    }

    private void initRecyclerView() {
        debugOutput("initRecyclerView: Started");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mDataset, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onNoteClick(int position) {
        debugOutput("onNoteClick: Clicked position = " + position);

        Intent intent = new Intent(this, RoutePageActivity.class);

        intent.putExtra("route_type", (currentDataType == DataType.HISTORY_ROUTES ? "userLocationSessions" : "userSavedRoutes"));
        intent.putExtra("selected_route", mDataset.get(position));
        startActivity(intent);
    }
}
