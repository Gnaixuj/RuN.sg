package com.example.cz2006trial.historyPage;

import android.os.Bundle;
import android.util.Log;

import com.example.cz2006trial.R;

import androidx.appcompat.app.AppCompatActivity;

public class RoutePageActivity extends AppCompatActivity {

    private static final String TAG = "RoutePageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_page);
        
        Log.d(TAG, "onCreate: Started");

        String keyDB, routeType;
        
        if (getIntent().hasExtra("selected_route")) {
            keyDB = getIntent().getStringExtra("selected_route");
            Log.d(TAG, "onCreate: keyDB = " + keyDB);
        } else {
            Log.d(TAG, "onCreate: error - intent didn't pass thru");
        }

        if (getIntent().hasExtra("route_type")) {
            routeType = getIntent().getStringExtra("route_type");
            Log.d(TAG, "onCreate: routeType = " + routeType);
        } else {
            Log.d(TAG, "onCreate: error - intent didn't pass thru");
        }
    }
}
