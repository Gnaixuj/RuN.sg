package com.example.cz2006trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    TextView usernameTextView;
    TextView emailTextView;
    TextView genderTextView;
    TextView DOBTextView;
    TextView heightTextView;
    TextView weightTextView;
    TextView BMITextView;
    ImageView editProfileButton;
    ProgressBar userProfileLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        genderTextView = findViewById(R.id.gender);
        DOBTextView = findViewById(R.id.DOB);
        heightTextView = findViewById(R.id.height);
        weightTextView = findViewById(R.id.weight);
        BMITextView = findViewById(R.id.BMI);
        editProfileButton = findViewById(R.id.edit);
        userProfileLoading = findViewById(R.id.userProfileLoading);

        displayProfileFromDatabase();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
            }
        });
    }

    public void displayProfileFromDatabase() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference(UID).child("userProfile");
        databaseUserProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfileEntity userProfile = dataSnapshot.getValue(UserProfileEntity.class);
                if (userProfile != null) {
                    usernameTextView.setText(userProfile.getUsername());
                    emailTextView.setText(userProfile.getEmail());
                    genderTextView.setText(userProfile.getGender());
                    if (userProfile.getDOB() != null)
                        DOBTextView.setText(userProfile.getDOB().toString());
                    if (userProfile.getHeight() != 0)
                        heightTextView.setText("" + userProfile.getHeight());
                    if (userProfile.getWeight() != 0)
                        weightTextView.setText("" + userProfile.getWeight());
                    if (userProfile.getBMI() != 0)
                        BMITextView.setText("" + userProfile.getBMI());
                    userProfileLoading.setVisibility(View.GONE);
                    usernameTextView.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.VISIBLE);
                    genderTextView.setVisibility(View.VISIBLE);
                    DOBTextView.setVisibility(View.VISIBLE);
                    heightTextView.setVisibility(View.VISIBLE);
                    weightTextView.setVisibility(View.VISIBLE);
                    BMITextView.setVisibility(View.VISIBLE);
                    editProfileButton.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong. PLease re-login and try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
