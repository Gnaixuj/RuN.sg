package com.example.cz2006trial.historyPage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.cz2006trial.R;
import com.example.cz2006trial.controller.GoogleMapController;
import com.example.cz2006trial.model.UserLocation;
import com.example.cz2006trial.model.UserLocationSession;
import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RoutePageActivity extends AppCompatActivity {

    private static final String TAG = "RoutePageActivity";
    private GoogleMap mMap;
    private String keyDB, routeType;
    private UserLocationSession mUserLocationSession;
    private UserRoute mUserRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_page);
        
        Log.d(TAG, "onCreate: Started");
        
        if (getIntent().hasExtra("selected_route")) {
            keyDB = getIntent().getStringExtra("selected_route");
            Log.d(TAG, "onCreate: keyDB = " + keyDB);
        } else {
            Log.d(TAG, "onCreate: error - intent didn't pass thru");
        }

        if (getIntent().hasExtra("route_type")) {
            routeType = getIntent().getStringExtra("route_type");
            Log.d(TAG, "onCreate: routeType = " + routeType);
        } else {
            Log.d(TAG, "onCreate: error - intent didn't pass thru");
        }

        initData();
    }

    private void initData() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(UID).child(routeType).child(keyDB);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (routeType.equals("userLocationSessions")) {
                    mUserLocationSession = dataSnapshot.getValue(UserLocationSession.class);
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        if (postSnapshot.getKey().equals("distance"))
                            break;
                        Log.d(TAG, "onDataChange: user location = " + postSnapshot.getKey());
                        mUserLocationSession.getSession().add(postSnapshot.getValue(UserLocation.class));
                    }
                }
                else {
                    Log.d(TAG, "trying to fetch userRoute");

                    mUserRoute = new UserRoute();
                    mUserRoute.setDate(dataSnapshot.child("date").getValue(Date.class));
                    mUserRoute.setDistance(dataSnapshot.child("distance").getValue(String.class));

                    mUserRoute.setEndPoint(new LatLng(dataSnapshot.child("endPoint").child("latitude").getValue(Double.class),
                                                      dataSnapshot.child("endPoint").child("longitude").getValue(Double.class)));

                    mUserRoute.setEndPointName(dataSnapshot.child("endPointName").getValue(String.class));

                    mUserRoute.setStartPoint(new LatLng(dataSnapshot.child("startPoint").child("latitude").getValue(Double.class),
                                                        dataSnapshot.child("startPoint").child("longitude").getValue(Double.class)));

                    mUserRoute.setStartPointName(dataSnapshot.child("startPointName").getValue(String.class));
                    mUserRoute.setTimeTaken(dataSnapshot.child("timeTaken").getValue(String.class));

                    Log.d(TAG, "onDataChange: userRoute = " + mUserRoute.output());
//                    mUserRoute = dataSnapshot.getValue(UserRoute.class);
                }
                Log.d(TAG, "onDataChange: data fetched");
                initMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: fail = " + databaseError.getCode());
            }
        });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                Log.d(TAG, "onMapReady: map is created and routeType = " + routeType);

//                Example: add a marker in Sydney, Australia, and move the camera.
//                LatLng sydney = new LatLng(-34, 151);
//                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                if (routeType.equals("userLocationSessions"))
                    showHistoryRoute();
                else
                    showSavedRoute();
            }
        });
    }

    private void showHistoryRoute() {
        ArrayList < LatLng > locations = new ArrayList <> ();

        Log.d(TAG, "showHistoryRoute: started");
        Log.d(TAG, "showHistoryRoute: userLocationSession = " + mUserLocationSession.getTimestamp());
        Log.d(TAG, "showHistoryRoute: size = " + mUserLocationSession.getSession().size());

        for (UserLocation i : mUserLocationSession.getSession()) {
            LatLng cur = new LatLng(i.getLatitude(), i.getLongitude());
            locations.add(cur);
            Log.d(TAG, "showHistoryRoute: " + cur);
        }

        mMap.addPolyline(new PolylineOptions().addAll(locations).width(10.0f).color(Color.BLUE));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198), 10));

//        Example
//        // Add polylines and polygons to the map. This section shows just
//        // a single polyline. Read the rest of the tutorial to learn more.
//        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-35.016, 143.321),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309)));
//
//        // Position the map's camera near Alice Springs in the center of Australia,
//        // and set the zoom factor so most of Australia shows on the screen.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
    }

    private GoogleMapController controller = GoogleMapController.getController();

    private void showSavedRoute() {
        Log.d(TAG, "showSavedRoute: started");
        controller.setCreateListener(new GoogleMapController.CreateListener() {
            @Override
            public void onChange() {
                controller.getDirections(mUserRoute.getStartPoint(), mUserRoute.getEndPoint());
                routeDone();
            }
        });
    }

    public void routeDone() {
        Log.d(TAG, "routeDone: started");
        controller.setRouteListener(new GoogleMapController.RouteListener() {
            @Override
            public void onChange() {
                ArrayList<LatLng> route = controller.getRoute();
                Log.d(TAG, "routeDone: onChange: finished and printing");
                mMap.addPolyline(new PolylineOptions().addAll(route).width(10.0f).color(Color.GREEN));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198), 10));
            }
        });
    }
}
