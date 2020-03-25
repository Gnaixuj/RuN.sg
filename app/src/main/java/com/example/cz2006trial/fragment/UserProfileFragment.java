package com.example.cz2006trial.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cz2006trial.DatabaseManager;
import com.example.cz2006trial.ImageDatabaseManager;
import com.example.cz2006trial.R;
import com.example.cz2006trial.model.Goal;
import com.example.cz2006trial.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    private ImageView profilePhoto;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView DOBTextView;
    private TextView heightTextView;
    private TextView weightTextView;
    private TextView BMITextView;
    private ImageView editProfileButton;
    private ProgressBar userProfileLoading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_profile, container, false);

        profilePhoto = view.findViewById(R.id.profile);
        usernameTextView = view.findViewById(R.id.username);
        emailTextView = view.findViewById(R.id.email);
        DOBTextView = view.findViewById(R.id.DOB);
        heightTextView = view.findViewById(R.id.height);
        weightTextView = view.findViewById(R.id.weight);
        BMITextView = view.findViewById(R.id.BMI);
        editProfileButton = view.findViewById(R.id.edit);
        userProfileLoading = view.findViewById(R.id.userProfileLoading);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayProfile();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.nav_editprofile);
            }
        });
    }

/*public class UserProfileActivity extends AppCompatActivity {

    ImageView profilePhoto;
    TextView usernameTextView;
    TextView emailTextView;
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

        profilePhoto = findViewById(R.id.profile);
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
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

 */

    public void displayProfile() {
        DatabaseManager.getData(new DatabaseManager.DatabaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stringArgs, double[] doubleArgs, String[] errorMsg, ArrayList<Goal> goals) {
                if (errorMsg[0] != null)
                    Toast.makeText(getContext(), errorMsg[0], Toast.LENGTH_LONG).show();
                else if (errorMsg[1] != null)
                    Toast.makeText(getContext(), errorMsg[1], Toast.LENGTH_LONG).show();
                else {
                    heightTextView.setText("-");
                    weightTextView.setText("-");
                    BMITextView.setText("-");
                    usernameTextView.setText(stringArgs.get(0));
                    emailTextView.setText(stringArgs.get(1));
                    DOBTextView.setText(stringArgs.get(2));
                    if (doubleArgs[0] != 0)
                        heightTextView.setText("" + doubleArgs[0]);
                    if (doubleArgs[1] != 0)
                        weightTextView.setText("" + doubleArgs[1]);
                    if (doubleArgs[2] != 0)
                        BMITextView.setText("" + doubleArgs[2]);
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message) {
                        }
                    }, "retrieve", profilePhoto);
                    loadingComplete();
                }
            }
        }, "userProfile", null);

        /*String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference(UID).child("userProfile");
        databaseUserProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                if (userProfile != null) {
                    heightTextView.setText("-");
                    weightTextView.setText("-");
                    BMITextView.setText("-");
                    usernameTextView.setText(userProfile.getUsername());
                    emailTextView.setText(userProfile.getEmail());
                    DOBTextView.setText(userProfile.getDOB());
                    if (userProfile.getHeight() != 0)
                        heightTextView.setText("" + userProfile.getHeight());
                    if (userProfile.getWeight() != 0)
                        weightTextView.setText("" + userProfile.getWeight());
                    if (userProfile.getBMI() != 0)
                        BMITextView.setText("" + userProfile.getBMI());
                    ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
                        @Override
                        public void onCallback(String[] message) {
                        }
                    }, "retrieve", profilePhoto);
                    loadingComplete();
                } else {
                    Toast.makeText(getContext(), "Something went wrong. PLease re-login and try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/
    }

    private void loadingComplete() {
        userProfileLoading.setVisibility(View.GONE);
        profilePhoto.setVisibility(View.VISIBLE);
        usernameTextView.setVisibility(View.VISIBLE);
        emailTextView.setVisibility(View.VISIBLE);
        DOBTextView.setVisibility(View.VISIBLE);
        heightTextView.setVisibility(View.VISIBLE);
        weightTextView.setVisibility(View.VISIBLE);
        BMITextView.setVisibility(View.VISIBLE);
        editProfileButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageDatabaseManager.imageDatabase(new ImageDatabaseManager.ImageCallback() {
            @Override
            public void onCallback(String[] message) {
            }
        }, "retrieve", profilePhoto);
    }
}
