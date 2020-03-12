package com.example.cz2006trial;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class UserLocationSessionEntity {

    private static ArrayList<UserLocationEntity> session = new ArrayList<>();
    private static @ServerTimestamp
    Date timestamp;

    public static void addUserLocation(UserLocationEntity userLocation) {
        session.add(userLocation);
    }

    public static void clearAllUserLocation() {
        session.clear();
    }

}
