package com.example.cz2006trial;

import com.google.android.gms.maps.model.LatLng;

public class UserLocationController {

    public static void addUserLocation(LatLng geopoints) {
        double[] geopoint = new double[2];
        geopoint[0] = geopoints.latitude;
        geopoint[1] = geopoints.longitude;
        System.out.println(geopoints.latitude);
        System.out.println(geopoints.longitude);
        UserLocationEntity userLocation = new UserLocationEntity(geopoint);
        UserLocationSessionEntity.addUserLocation(userLocation);
    }

}
