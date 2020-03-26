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
        
        if (getIntent().hasExtra("selected_route")) {
            String data = getIntent().getStringExtra("selected_route");
            Log.d(TAG, "onCreate: " + data);
        } else {
            Log.d(TAG, "onCreate: error - intent didn't pass thru");
        }
    }
}
