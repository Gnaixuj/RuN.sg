package com.example.cz2006trial.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cz2006trial.R;
import com.example.cz2006trial.history.recyclerview.MainActivity;
import com.example.cz2006trial.historyPage.HistoryPageActivity;
import com.example.cz2006trial.model.Goal;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MapsActivity extends AppCompatActivity {



    private String date;
    //private Goal goal;
    private AppBarConfiguration mAppBarConfiguration;


    public void setDate(String date) {
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    /*public Goal getGoal() {
        return goal;
    }
    public void setGoal(Goal goal) {
        this.goal = goal;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("MapsActivity", "oncreate");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map, R.id.nav_goals, R.id.nav_faq, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

/*            case R.id.action_restart: {
                mMap.clear();
                userMarker.remove();
                destination = null;
                locations.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM));
                return true;
            }

            case R.id.action_track: {

                return true;
            }

            case R.id.action_direction: {
                mMap.clear();
                button.setVisibility(View.VISIBLE);
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                for (Marker marker : accessPoint) {
                    mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).visible(true));
                }

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        destination = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        return false;
                    }
                });

                return true;

            }
*/

            case R.id.history: {
                startActivity(new Intent(MapsActivity.this, HistoryPageActivity.class));
                return true;
            }



        }
        return super.onOptionsItemSelected(item);
    }

    public void logOut(View view) {
        Log.i("log out", "i am clicked");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
        finish();
    }
}
