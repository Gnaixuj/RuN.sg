package com.example.cz2006trial.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RouteFragment extends Fragment {

    private static final String TAG = "RoutePageActivity";
    private GoogleMap mMap;
    private String keyDB, routeType;
    private UserLocationSession mUserLocationSession;
    private UserRoute mUserRoute;

    private TextView dateView;
    private TextView distanceView;
    private TextView durationView;
    private TextView durationHeaderView;

    private GoogleMapController controller = GoogleMapController.getController();


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.display_map);
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
        String pattern = "EEE, MMM dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dateView.setText(simpleDateFormat.format(mUserLocationSession.getTimestamp()));
        distanceView.setText("" + mUserLocationSession.getDistance());
        durationView.setText(mUserLocationSession.getTimeTaken());

        ArrayList< LatLng > locations = new ArrayList <> ();

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

    private void showSavedRoute() {
        String pattern = "EEE, MMM dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dateView.setText(simpleDateFormat.format(mUserRoute.getDate()));
        distanceView.setText("" + mUserRoute.getDistance());
        durationHeaderView.setText("Approx. duration");
        durationView.setText(mUserRoute.getTimeTaken());

        controller.setCreateHistoryRoute(true);
        controller.getDirections(mUserRoute.startPointRetrieve(), mUserRoute.endPointRetrieve());
        routeDone();
        controller.setCreateHistoryRoute(false);
        mMap.addMarker(new MarkerOptions().position(mUserRoute.startPointRetrieve())
                .title(mUserRoute.getStartPointName())
                .snippet("Your Starting Location"));
        mMap.addMarker(new MarkerOptions().position(mUserRoute.endPointRetrieve())
                .title(mUserRoute.getEndPointName())
                .snippet("Your Ending Location"));
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateView = view.findViewById(R.id.date);
        distanceView = view.findViewById(R.id.distance);
        durationView = view.findViewById(R.id.duration);
        durationHeaderView = view.findViewById(R.id.duration_header);

        Bundle bundle = getArguments();
        if (bundle != null) {
            keyDB = bundle.getString("selected_route");
            routeType = bundle.getString("route_type");
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

                    mUserRoute = dataSnapshot.getValue(UserRoute.class);

                    Log.d(TAG, "onDataChange: userRoute = " + mUserRoute.output());
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

    private void routeDone() {
        controller.setRouteListener(new GoogleMapController.RouteListener() {
            @Override
            public void onChange() {
                ArrayList<LatLng> historyRoute = controller.getHistoryRoute();
                mMap.addPolyline(new PolylineOptions().addAll(historyRoute).width(10.0f).color(Color.BLACK));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198), 10));
            }
        });
    }
}
