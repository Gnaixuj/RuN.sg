package com.example.cz2006trial;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class UserProfileController {

    public static boolean setEditedUserProfileOnDatabase(String username, String email, String gender, Date DOB, double height, double weight, double BMI) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfileEntity userProfile = new UserProfileEntity(username, email, gender, DOB, height, weight, BMI);
        databaseUserProfile.setValue(userProfile);
        return true;
    }

    public static boolean setUserProfileOnDatabase(String username, String email) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseUserProfile = FirebaseDatabase.getInstance().getReference().child(UID).child("userProfile");
        UserProfileEntity userProfile = new UserProfileEntity(username, email);
        databaseUserProfile.setValue(userProfile);
        return true;
    }

}
