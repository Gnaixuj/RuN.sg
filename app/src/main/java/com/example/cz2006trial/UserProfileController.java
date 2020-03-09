package com.example.cz2006trial;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileController {

    private static String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static void getUserProfileFromDatabase(final FirebaseCallback firebaseCallback) {
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference("users").child(UID);
        databaseUserProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfileEntity userProfile = dataSnapshot.getValue(UserProfileEntity.class);
                String[] userProfileData = new String[2];
                if (userProfile != null) {
                    userProfileData[0] = userProfile.getEmail();
                    userProfileData[1] = userProfile.getUsername();
                } else {
                    userProfileData[0] = null;
                    userProfileData[1] = null;
                }
                System.out.println(userProfileData[0]);
                System.out.println(userProfileData[1]);
                firebaseCallback.onCallback(userProfileData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static boolean setUserProfileOnDatabase(String email, String username) {
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference("users").child(UID);
        UserProfileEntity userProfile = new UserProfileEntity(email, username);
        databaseUserProfile.setValue(userProfile);
        return true;
    }

    public interface FirebaseCallback {
        void onCallback(String[] userProfileData);
    }

}
