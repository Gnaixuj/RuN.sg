package com.example.cz2006trial;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileController {

    public static void setEditedUserProfileOnDatabase(String username, String email, String DOB, double height, double weight, double BMI) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfileEntity userProfile = new UserProfileEntity(username, email, DOB, height, weight, BMI);
        databaseUserProfile.setValue(userProfile);
    }

    public static void setUserProfileOnDatabase(String username, String email) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfileEntity userProfile = new UserProfileEntity(username, email);
        databaseUserProfile.setValue(userProfile);
    }

}
