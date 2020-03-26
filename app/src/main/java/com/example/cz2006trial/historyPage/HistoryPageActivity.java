package com.example.cz2006trial.historyPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
    private ArrayList < String > mDataset = new ArrayList <> ();

    private void debugOutput(String msg) {
        Log.d(TAG, msg);
//        System.out.println(TAG + " === " + msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        debugOutput("onCreate: Started");

        initDataset();
    }

    private void initDataset() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseLocationSessions = FirebaseDatabase.getInstance().getReference().child(UID).child("userLocationSessions");

        debugOutput("initDataset: UID = " + UID);

        databaseLocationSessions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: after fetching key = " + postSnapshot.getKey());
//                    UserLocationSessionEntity cur = postSnapshot.getValue(UserLocationSessionEntity.class);
                    mDataset.add(postSnapshot.getKey());
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                debugOutput("initDataset: Fail = " + databaseError.getCode());
            }
        });
    }

    private void initRecyclerView(){
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
        intent.putExtra("selected_route", mDataset.get(position));
        startActivity(intent);
    }
}
