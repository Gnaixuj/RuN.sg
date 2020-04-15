package com.example.cz2006trial.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cz2006trial.database.DatabaseManager;
import com.example.cz2006trial.database.ImageDatabaseManager;
import com.example.cz2006trial.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView userImage;
    private TextView userName;
    private TextView userEmail;


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        displayProfile();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        View headView = navigationView.getHeaderView(0);

        userImage = headView.findViewById(R.id.user_image);
        userName = headView.findViewById(R.id.user_name);
        userEmail = headView.findViewById(R.id.user_email);

        userEmail.setVisibility(View.INVISIBLE);
        userName.setVisibility(View.INVISIBLE);
        userImage.setVisibility(View.INVISIBLE);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map, R.id.nav_goals, R.id.nav_faq, R.id.nav_profile, R.id.nav_history)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }

    // retrieve profile data from firebase database via Database Manager and display it
    public void displayProfile() {
        DatabaseManager.getProfileData(new DatabaseManager.ProfileDatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg) {
                if (errorMsg[0] != null)
                    Toast.makeText(getApplicationContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else if (errorMsg[1] != null)
                    Toast.makeText(getApplicationContext(), errorMsg[1], Toast.LENGTH_LONG).show();
                else {

                    userName.setText(stringArgs.get(0));
                    userEmail.setText(stringArgs.get(1));
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message, byte[] bytes) {
                            if (bytes != null ) userImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        }
                    }, "retrieve",userImage);

                    loadingComplete();
                }
            }
        });
    }

    // display profile information which is called only when system has retrieved profile data from firebase database
    private void loadingComplete() {
        userEmail.setVisibility(View.VISIBLE);
        userName.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);
        userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
        userImage.setLayoutParams(lp);
    }

    // logged out user when log out button is pressed
    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
        finish();
    }
}
