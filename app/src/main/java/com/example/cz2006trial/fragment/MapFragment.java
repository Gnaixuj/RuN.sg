package com.example.cz2006trial.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cz2006trial.controller.GoogleMapController;
import com.example.cz2006trial.R;
import com.example.cz2006trial.controller.UserLocationController;
import com.example.cz2006trial.model.UserLocation;
import com.example.cz2006trial.model.UserLocationSession;
import com.example.cz2006trial.controller.UserRouteController;
import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private ImageView arrowImg;

    private GoogleMap mMap;
    private Marker pointChosen;

    private boolean startTrack = false;

    private UserLocationSession userLocationSession = new UserLocationSession();

    private boolean createRoute = false;
    private boolean setStartPoint = false;
    private boolean setEndPoint = false;
    private UserRoute userRoute = new UserRoute();
    private Marker startPoint;
    private Marker endPoint;
    private ArrayList<Polyline> routeLine = new ArrayList<>();

    private KmlLayer parklayer;
    private GeoJsonLayer pcnLayer;
    private KmlLayer accesslayer;
    private GeoJsonLayer accessLayer;

    private BottomSheetBehavior bottomSheetBehavior;


    private ArrayList<LatLng> locations = new ArrayList<>();
    private ArrayList<Marker> accessPoint = new ArrayList<>();

    private GoogleMapController controller = GoogleMapController.getController();


    private final long MINTIME = 1000 * 2;
    private final float MINDIST = 0;
    private final int ZOOM = 12;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private LatLng userLocation;
    private LatLng lastLocation;


    private LocationManager locationManager;
    private LocationListener locationListener;

    private Marker userMarker;
    private GeoJsonLayer parksLayer;
    private ArrayList<Marker> parkPoint = new ArrayList<>();;
    private ArrayList<Polyline> pcn = new ArrayList<>();

    //method to check whether permission for location access has been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //how often location is updated
                    //startTrackerService();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDIST, locationListener);
                }
            }
        }
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState)  {

        Log.i("Mapfragment", "create");

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        Switch switchPark = root.findViewById(R.id.switch_park);
        Switch switchPcn = root.findViewById(R.id.switch_pcn);

        switchPark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) showPark();
                else removePark();
            }
        });

        switchPcn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) showPcn();
                else removePcn();
            }
        });



        arrowImg = root.findViewById(R.id.arrow_bottom_sheet);
        arrowImg.setImageResource(R.drawable.ic_arrow_up);
        final View bottomSheet = root.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //clicking on arrow will expand/collapse bottom sheet
        arrowImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        //to set image of arrow
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        arrowImg.setImageResource(R.drawable.ic_arrow_up);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        arrowImg.setImageResource(R.drawable.ic_arrow_down);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        BottomNavigationView navigationView = root.findViewById(R.id.bottom_nav);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_create, R.id.nav_track, R.id.nav_search)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.bottom_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);

/*        //change text of bottom sheet
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.i("navcontroller", String.valueOf(destination.getId()));
                if (peekText.getText().equals("Track")) peekText.setText("Create");
                else peekText.setText("Track");
            }
        });*/
        //navigationView.setOnNavigationItemSelectedListener(this);

        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void showPark() {
        if (mMap != null && parkPoint != null) {
            for (Marker marker: parkPoint) {
                marker.setVisible(true);
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if(!marker.getTitle().equals("Your Location")) {
                        String title = marker.getTitle();
                        controller.getInfo(title);
                        controller.setPlaceListener(new GoogleMapController.PlaceListener() {
                            @Override
                            public void onChange() {
                                HashMap <String, Object> info = controller.getParkInfo();
                                for (HashMap.Entry<String, Object> entry : info.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();
                                    Log.i(key, value.toString());
                                }
                                DialogFragment parksFragment = ParksFragment.newInstance(info);
                                parksFragment.show(getChildFragmentManager(), "Dialog shown");
                            }
                        });
                    }
                    return false;
                }
            });
        }
    }

    private void removePark() {
        if (mMap != null && parkPoint != null) {
            for (Marker marker: parkPoint) {
                marker.setVisible(false);
            }
        }
        mMap.setOnMarkerClickListener(null);
    }

    private void showPcn() {
        if (mMap != null && pcn != null) {
            for (Polyline line: pcn) {
                line.setVisible(true);
            }
        }
    }

    private void removePcn() {
        if (mMap != null && pcn != null) {
            for (Polyline line : pcn) {
                line.setVisible(false);
            }
        }
    }

    public void setStartingPoint() {
        controller.setStartListener(new GoogleMapController.StartListener() {
            @Override
            public void onChange() {
                setStartPoint = controller.isSetStartPoint();
                userRoute = controller.getUserRoute();
                if (setStartPoint) {
                    for (Marker marker : accessPoint) {
                        marker.setVisible(true);
                    }
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (!marker.getTitle().equals("Your Location"))
                                if (endPoint == null || !marker.getTitle().equals("Your Ending Location")) {
                                    if (startPoint != null)
                                        startPoint.remove();
                                    startPoint = mMap.addMarker(new MarkerOptions().position(marker.getPosition())
                                            .title(marker.getTitle())
                                            .snippet("Your Starting Point")
                                            .zIndex(2f)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    UserRouteController.setStartMarkerInfo(userRoute, startPoint);

                                }
                            return false;
                        }


                    });
                }
                else {
                    for (Marker marker : accessPoint) {
                        marker.setVisible(false);
                    }
                }
            }
        });
    }

    public void setEndingPoint() {
        controller.setEndListener(new GoogleMapController.EndListener() {
            @Override
            public void onChange() {
                setEndPoint = controller.isSetEndPoint();
                userRoute = controller.getUserRoute();
                if (setEndPoint) {
                    for (Marker marker : accessPoint) {
                        marker.setVisible(true);
                    }
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (!marker.getTitle().equals("Your Location"))
                                if (startPoint == null || !marker.getTitle().equals(startPoint.getTitle())) {
                                    if (endPoint != null)
                                        endPoint.remove();
                                    endPoint = mMap.addMarker(new MarkerOptions().position(marker.getPosition())
                                            .title(marker.getTitle())
                                            .snippet("Your Ending Location")
                                            .zIndex(1f)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    UserRouteController.setEndMarkerInfo(userRoute, endPoint);
                                }
                            return false;
                        }
                    });
                }
                else {
                    for (Marker marker : accessPoint) {
                        marker.setVisible(false);
                    }
                }
            }
        });
    }


    public void createRoute() {
        controller.setCreateListener(new GoogleMapController.CreateListener() {
            @Override
            public void onChange() {
                createRoute = controller.isCreateRoute();
                if (createRoute) {
                    if (startPoint == null || endPoint == null)
                        controller.setMessage("Missing starting point or ending point");
                    else {
                        userRoute = controller.getUserRoute();
                        createRoute = true;
                        controller.getDirections(startPoint.getPosition(), endPoint.getPosition());
                        routeDone();
                        controller.setMessage("Route created");

                    }
                }
                else {
                    startPoint.remove();
                    endPoint.remove();
                    for (int i = 0; i < routeLine.size(); i++)
                        routeLine.get(i).remove();
                    startPoint = null;
                    endPoint = null;
                    routeLine.clear();
                }
            }

        });

    }

    public void routeDone() {
        controller.setRouteListener(new GoogleMapController.RouteListener() {
            @Override
            public void onChange() {
                ArrayList<LatLng> route = controller.getRoute();
                Log.i("route", route.toString());
                routeLine.add(mMap.addPolyline(new PolylineOptions().addAll(route).width(10.0f).color(Color.GREEN)));

            }
        });
    }

    public void pointChosen() {
        controller.setPointListener(new GoogleMapController.PointListener() {
            @Override
            public void onChange() {
                if (bottomSheetBehavior != null) bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Marker marker = controller.getPointChosen();
                if (pointChosen != null) {
                    pointChosen.remove();
                }
                pointChosen = mMap.addMarker(new MarkerOptions().position(marker.getPosition())
                        .title(marker.getTitle()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointChosen.getPosition(), ZOOM));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setStartingPoint();
        setEndingPoint();
        createRoute();
        pointChosen();

        userRoute = controller.getUserRoute();

        if (userRoute.getStartPointName() != null) {
            startPoint = mMap.addMarker(new MarkerOptions().position(userRoute.getStartPoint())
                    .title(userRoute.getStartPointName())
                    .snippet("Your Starting Point")
                    .zIndex(2f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        if (userRoute.getEndPointName() != null) {
            endPoint = mMap.addMarker(new MarkerOptions().position(userRoute.getEndPoint())
                    .title(userRoute.getEndPointName())
                    .snippet("Your Ending Point")
                    .zIndex(2f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        if (!controller.getRoute().isEmpty()) {
            Toast.makeText(getActivity(), "Inside", Toast.LENGTH_SHORT).show();
            controller.create(userRoute);
            //ArrayList<LatLng> route = controller.getRoute();
            //routeLine.add(mMap.addPolyline(new PolylineOptions().addAll(route).width(10.0f).color(Color.GREEN)));
        }



    locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

    locationListener = new LocationListener() {

        //to prevent camera to move back to user location every second
        boolean isFirstTime = true;

        //method to move location according to user's position
        @Override
        public void onLocationChanged(Location location) {

            if (isFirstTime) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM));
                isFirstTime = false;
            }

            if (userLocation != null)
                lastLocation = userLocation;

            //update current position of user
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());

/*                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }*/
            //Location prevLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //lastLocation = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
            startTrack = controller.isStartTrack();
            if (startTrack) {
                userLocationSession = controller.getUserLocationSession();
                locations.add(lastLocation);
                UserLocation userLocation = new UserLocation();
                UserLocationController.addUserLocation(userLocationSession, lastLocation, Calendar.getInstance().getTime());
                UserLocationController.updateUserLocation(userLocationSession);

            }
                    /*Log.i("LAST LOCATION", lastLocation.toString());
                    for (LatLng loc: locations) {
                        Log.i("LOCATION", loc.toString());
                    }*/

            if (userMarker != null) {
                userMarker.remove();
                userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                if (startTrack) {
                    mMap.addPolyline(new PolylineOptions().addAll(locations).width(10.0f).color(Color.RED));
                    //mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                }
            } else {
                userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }


            //mMap.addPolyline(new PolylineOptions().add(new LatLng(lastLocation.latitude, lastLocation.longitude),
            //        new LatLng(userLocation.latitude, userLocation.longitude)).width(Float.valueOf("10.0")).color(Color.BLACK));

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

        try {
        pcnLayer = new GeoJsonLayer(mMap, R.raw.parkconnectorloopg, getContext());
        accessLayer = new GeoJsonLayer(mMap, R.raw.accesspointsg, getContext());
        parksLayer = new GeoJsonLayer(mMap, R.raw.parksg, getContext());
    } catch (JSONException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }


        try {
        parklayer = new KmlLayer(mMap, R.raw.parkconnectorloop, getContext());
        accesslayer = new KmlLayer(mMap, R.raw.accesspoints, getContext());
        //parklayer.addLayerToMap();


    } catch (XmlPullParserException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

    //get the access points from geojson
        for (GeoJsonFeature feature : accessLayer.getFeatures()) {
        if ("Point".equalsIgnoreCase(feature.getGeometry().getGeometryType())) {
            GeoJsonPoint point = (GeoJsonPoint) feature.getGeometry();

            LatLng latLng = point.getCoordinates();

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(feature.getProperty("Name")).visible(false));
            accessPoint.add(marker);
        }
        controller.setMarkers(accessPoint);
    }

    //get the points of parks from geojson
        for (GeoJsonFeature feature : parksLayer.getFeatures()) {
        if ("Point".equalsIgnoreCase(feature.getGeometry().getGeometryType())) {
            GeoJsonPoint point = (GeoJsonPoint) feature.getGeometry();

            LatLng latLng = point.getCoordinates();

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(feature.getProperty("Name")).visible(false));
            parkPoint.add(marker);
        }
    }

    //get the park connector network from geojson
        for (GeoJsonFeature feature : pcnLayer.getFeatures()) {
        if("LineString".equalsIgnoreCase(feature.getGeometry().getGeometryType())) {
            GeoJsonLineString line = (GeoJsonLineString) feature.getGeometry();
            ArrayList<LatLng> latLngList = (ArrayList)line.getCoordinates();
            switch(feature.getProperty("PCN_LOOP")) {
                case "Central Urban Loop":
                    pcn.add(mMap.addPolyline(new PolylineOptions().addAll(latLngList).visible(false).color(Color.BLUE)));
                    break;
                case "Eastern Coastal Loop":
                    pcn.add(mMap.addPolyline(new PolylineOptions().addAll(latLngList).visible(false).color(Color.GREEN)));
                    break;
                case "Northern Explorer Loop":
                    pcn.add(mMap.addPolyline(new PolylineOptions().addAll(latLngList).visible(false).color(Color.DKGRAY)));
                    break;
                case "North Eastern Riverine Loop":
                    pcn.add(mMap.addPolyline(new PolylineOptions().addAll(latLngList).visible(false).color(Color.MAGENTA)));
                    break;
                case "Southern Ridges Loop":
                    pcn.add(mMap.addPolyline(new PolylineOptions().addAll(latLngList).visible(false).color(Color.BLACK)));
                    break;
                case "Western Adventure Loop":
                    pcn.add(mMap.addPolyline(new PolylineOptions().addAll(latLngList).visible(false).color(Color.CYAN)));
                    break;

            }
        }
    }



        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    } else {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDIST, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //mMap.clear();
        userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

    }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

}

    /*@Override
    public void onStart() {
        super.onStart();
        userRoute = controller.getUserRouteEntity();
        if (userRoute.getStartPointName() != null) {
            startPoint = mMap.addMarker(new MarkerOptions().position(userRoute.getStartPoint())
                    .title(userRoute.getStartPointName())
                    .snippet("Your Starting Point")
                    .zIndex(2f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        if (userRoute.getEndPointName() != null) {
            endPoint = mMap.addMarker(new MarkerOptions().position(userRoute.getEndPoint())
                    .title(userRoute.getEndPointName())
                    .snippet("Your Ending Point")
                    .zIndex(2f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        if (!controller.getRoute().isEmpty())
                routeLine.add(mMap.addPolyline(new PolylineOptions().addAll(controller.getRoute()).width(10.0f).color(Color.GREEN)));
    }*/



}
