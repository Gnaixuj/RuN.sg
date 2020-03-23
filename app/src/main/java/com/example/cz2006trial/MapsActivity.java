package com.example.cz2006trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.graphics.Color;

import com.example.cz2006trial.history.recyclerview.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlLineString;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter; // Swa
    private ViewPager mViewPager; // Swa

    private final long MINTIME = 1000 * 2;
    private final float MINDIST = 0;
    private final int ZOOM = 12;
    private static final int REQUEST_LOCATION_PERMISSION = 1;


    private boolean startTrack = false; // Swa
    private UserLocationSessionEntity userLocationSession = new UserLocationSessionEntity();
    private LatLng userLocation;
    private LatLng destination;
    private LatLng lastLocation;

    private boolean createRoute = false;
    private boolean setStartPoint = false;
    private boolean setEndPoint = false;
    private UserRouteEntity userRoute = new UserRouteEntity();
    private Marker startPoint;
    private Marker endPoint;
    private ArrayList<Polyline> routeLine = new ArrayList<>();


    ArrayList<LatLng> locations = new ArrayList<>();
    ArrayList<Marker> accessPoint = new ArrayList<>();

    private String api_key = "AIzaSyCz0G1WVYtj1njKUMnPRf5A4FVfvxMvzZs";

    LocationManager locationManager;
    LocationListener locationListener;

    KmlLayer parklayer;
    GeoJsonLayer parklayerjson;
    KmlLayer accesslayer;
    GeoJsonLayer accesslayerjson;

    Button button;
    Marker userMarker;

    private String date;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    //method to check whether permission for location access has been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //how often location is updated
                    //startTrackerService();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDIST, locationListener);
                }
            }
        }
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MapsActivity", "oncreate");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map, R.id.nav_goals, R.id.nav_faq)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()== R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                else
                    return false;
            }
        });


        mDb = FirebaseFirestore.getInstance();
        //loginToFirebase();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Swa
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.mapContainer);
        // Set Up the Pager
        setupViewPager(mViewPager);
    }

    // Swa
    private void setupViewPager(ViewPager viewPager) {
        mSectionsStatePagerAdapter.addFragment(new MapsMainFragment(), "MapsMainFragment");
        mSectionsStatePagerAdapter.addFragment(new MapsTrackFragment(), "MapsTrackFragment");
        mSectionsStatePagerAdapter.addFragment(new MapsCreateFragment(), "MapsCreateFragment");
        viewPager.setAdapter(mSectionsStatePagerAdapter);
    }

    // Swa
    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }

    // Swa
    public void setLayoutWeight(int weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
        );
        findViewById(R.id.mapContainer).setLayoutParams(params);
    }

    // Swa
    public void beginTracking(UserLocationSessionEntity userLocationSession) {
        this.userLocationSession = userLocationSession;
        startTrack = true;
    }

    public void resumeTracking() {
        startTrack = true;
    }
    // Swa
    public void endTracking() {
        startTrack = false;
    }

    // Fazli

    public void clearRouteDetails() {
        startPoint.remove();
        endPoint.remove();
        for (int i = 0; i < routeLine.size(); i++)
            routeLine.get(i).remove();
        startPoint = null;
        endPoint = null;
        routeLine.clear();
    }

    public void setStartingPoint(UserRouteEntity userRoute) {
        setStartPoint = true;
        this.userRoute = userRoute;
        for (Marker marker : accessPoint) {
            marker.setVisible(true);
        }
    }

    public void setEndingPoint(UserRouteEntity userRoute) {
        setEndPoint = true;
        this.userRoute = userRoute;
        for (Marker marker : accessPoint) {
            marker.setVisible(true);
        }
    }

    public void stopSettingPoints() {
        setStartPoint = false;
        setEndPoint = false;
        for (Marker marker : accessPoint) {
            marker.setVisible(false);
        }
    }

    public String createRoute(UserRouteEntity userRoute) {
        if (startPoint == null || endPoint == null)
            return ("Missing starting point or ending point");
        this.userRoute = userRoute;
        createRoute = true;
        getDirections(mMap, startPoint.getPosition(), endPoint.getPosition(), api_key);
        return ("Route created");
    }

    /*public void setStartPoint(final UserRouteEntity userRoute) {
        try {
            accesslayer.addLayerToMap();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (endPoint != null) {
            endPoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getTitle().equals("Your Location"))
                    if (endPoint == null || !marker.getTitle().equals(endPoint.getTitle())) {
                        if (startPoint != null)
                            startPoint.remove();
                        startPoint = mMap.addMarker(new MarkerOptions().position(marker.getPosition())
                                .title(marker.getTitle())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        UserRouteController.setStartMarkerInfo(userRoute, startPoint);
                        Toast.makeText(getApplicationContext(), "Starting Point updated", Toast.LENGTH_SHORT).show();
                        accesslayer.removeLayerFromMap();
                    }
                return true;
            }
        });

    }

    public void setEndPoint(final UserRouteEntity userRoute) {
        try {
            accesslayer.addLayerToMap();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (startPoint != null) {
            startPoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getTitle().equals("Your Location"))
                    if (startPoint == null || !marker.getTitle().equals(startPoint.getTitle())) {
                        if (endPoint != null)
                            endPoint.remove();
                        endPoint = mMap.addMarker(new MarkerOptions().position(marker.getPosition())
                                .title(marker.getTitle() + " " + marker.getId())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        UserRouteController.setEndMarkerInfo(userRoute, endPoint);
                        Toast.makeText(getApplicationContext(), "Ending Point updated", Toast.LENGTH_SHORT).show();
                        accesslayer.removeLayerFromMap();
                    }
                return true;
            }
        });

    }*/

/*
    private void loginToFirebase() {

        //Authenticate with Firebase, using the email and password we created earlier//
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        //Call OnCompleteListener if the user is signed in successfully//
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                //If the user has been authenticated...//
                if (task.isSuccessful()) {
                    //...then call requestLocationUpdates//
                    requestLocationUpdates();
                } else {
                    //If sign in fails, then log the error//
                    Log.d("ERROR", "Firebase authentication failed");
                }
            }
        });
    }
*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        button = (Button) findViewById(R.id.button2);

        //button.setAlpha(0);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Fragment fragment = mSectionsStatePagerAdapter.getItem(2);
                if (setStartPoint) {
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
                            Toast.makeText(getApplicationContext(), "Starting Point updated", Toast.LENGTH_SHORT).show();
                            ((MapsCreateFragment) getSupportFragmentManager().findFragmentById(fragment.getId())).displayStartEndText();
                        }
                } else if (setEndPoint) {
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
                            Toast.makeText(getApplicationContext(), "Ending Point updated", Toast.LENGTH_SHORT).show();
                            ((MapsCreateFragment) getSupportFragmentManager().findFragmentById(fragment.getId())).displayStartEndText();
                        }
                }
                return false;
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            //to prevent camera to move back to user location every second
            boolean isFirstTime = true;

            //method to move location according to user's position
            @Override
            public void onLocationChanged(Location location) {

                userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location prevLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lastLocation = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
                if (startTrack) {

                    locations.add(lastLocation);
                    UserLocationEntity userLocation = new UserLocationEntity();
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
                if (isFirstTime) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM));
                    isFirstTime = false;
                }
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
            parklayerjson = new GeoJsonLayer(mMap, R.raw.parkconnectorloopg, getApplicationContext());
            accesslayerjson = new GeoJsonLayer(mMap, R.raw.accesspointsg, getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            parklayer = new KmlLayer(mMap, R.raw.parkconnectorloop, getApplicationContext());
            accesslayer = new KmlLayer(mMap, R.raw.accesspoints, getApplicationContext());
            //parklayer.addLayerToMap();


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (GeoJsonFeature feature : accesslayerjson.getFeatures()) {
            if ("Point".equalsIgnoreCase(feature.getGeometry().getGeometryType())) {
                GeoJsonPoint point = (GeoJsonPoint) feature.getGeometry();

                LatLng latLng = point.getCoordinates();

                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(feature.getProperty("Name")).visible(false));
                accessPoint.add(marker);
            }
        }

/*        ArrayList<ArrayList<LatLng>> pathLines = new ArrayList();
        for (GeoJsonFeature feature : parklayerjson.getFeatures()) {
            if("LineString".equalsIgnoreCase(feature.getGeometry().getGeometryType())) {
                GeoJsonLineString line = (GeoJsonLineString) feature.getGeometry();
                ArrayList<LatLng> latLngList = (ArrayList)line.getCoordinates();
                pathLines.add(latLngList);
            }
        }

        for (ArrayList <LatLng> latLngList: pathLines) {
            mMap.addPolyline(new PolylineOptions().addAll(latLngList));
        }*/


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDIST, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //mMap.clear();
            userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_restart: {
                mMap.clear();
                userMarker.remove();
                destination = null;
                locations.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM));
                return true;
            }

            case R.id.action_track: {

                return true;
            }

            case R.id.action_direction: {
                mMap.clear();
                button.setVisibility(View.VISIBLE);
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                for (Marker marker : accessPoint) {
                    mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).visible(true));
                }

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        destination = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        return false;
                    }
                });

                return true;

            }
            case R.id.action_search: {

                ArrayList<String> accessStr = new ArrayList<>();

                for (Marker marker : accessPoint) {
                    accessStr.add(marker.getTitle());
                }

                Intent i = new Intent(this, SearchActivity.class);
                i.putStringArrayListExtra("key", accessStr);
                startActivityForResult(i, 1);

                return true;
            }

            case R.id.faq: {
                startActivity(new Intent(MapsActivity.this, FAQActivity.class));
                return true;
            }

            case R.id.goal: {
                startActivity(new Intent(MapsActivity.this, CalendarGoalsActivity.class));
                return true;
            }

            case R.id.history: {
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
                return true;
            }

            case R.id.userProfile: {
                startActivity(new Intent(MapsActivity.this, UserProfileActivity.class));
                return true;
            }

            case R.id.log_out: {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //method to get back result from SearchActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String loc = data.getStringExtra("locationkey");
                for (Marker marker : accessPoint) {
                    if (marker.getTitle().equalsIgnoreCase(loc)) {
                        destination = marker.getPosition();
                        break;
                    }
                }
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addMarker(new MarkerOptions().position(destination).title("Your Destination"));
                getDirections(mMap, userLocation, destination, api_key);
            }
        }
    }

    //method on click of button "Show Directions"
    public void showDirection(View view) {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if (destination != null) {
            button.setVisibility(View.INVISIBLE);
            mMap.addMarker(new MarkerOptions().position(destination).title("Your Destination"));
            getDirections(mMap, userLocation, destination, api_key);
        } else {
            Toast.makeText(this, "Choose your destination", Toast.LENGTH_LONG).show();
        }
    }


    //method to get directions url
    private void getDirections(GoogleMap mMap, LatLng origin, LatLng destination, String api_key) {

        //Forming an URL string which will return JSON as a result.
        String originString = "origin=" + origin.latitude + "," + origin.longitude;
        String destinationString = "destination=" + destination.latitude + "," + destination.longitude;

        //IF THIS GENERATES ERROR, HARD CODE API KEY INTO URL.
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + originString
                + "&" + destinationString + "&key=" + api_key + "&mode=walking";


        //Run the URL formed in above step and wait for result.
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String downloadUrl(String url) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL actualURL = new URL(url);
            urlConnection = (HttpURLConnection) actualURL.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
        } catch (Exception e) {
            Log.d("EXCEPTION DOWNLOADING", e.toString());
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = downloadUrl(strings[0]);
            } catch (Exception e) {
                Log.d("ASYNC TASK", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(showPreviousOrder.this, s, Toast.LENGTH_LONG).show();

            int totalDistance = 0;
            int totalTravelTime = 0;

            try {
                JSONObject parentMain = new JSONObject(s);
                JSONArray legs = parentMain.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

                for (int i = 0; i < legs.length(); i++) {
                    JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
                    JSONObject distance = legs.getJSONObject(i).getJSONObject("distance");
                    JSONObject duration = legs.getJSONObject(i).getJSONObject("duration");

                    totalDistance += Integer.parseInt(distance.getString("value"));
                    totalTravelTime += Integer.parseInt(duration.getString("value"));

                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject polyline = steps.getJSONObject(j).getJSONObject("polyline");
                        List<LatLng> markers = PolyUtil.decode(polyline.getString("points"));

                        //save distance and timeTaken to userRoute
                        if (createRoute)
                            routeLine.add(mMap.addPolyline(new PolylineOptions().addAll(markers).width(10.0f).color(Color.GREEN)));
                        else
                            mMap.addPolyline(new PolylineOptions().addAll(markers).width(10.0f).color(Color.RED));
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(MapsActivity.this, "WELL WE MESSED UP!", Toast.LENGTH_LONG).show();
            }

            toastData(totalDistance, totalTravelTime);
        }

        //Simply displays a toast message containing total distance and total time required.
        public void toastData(int totalDistance, int totalTravelTime) {
            int km = 0, m = 0;
            String displayDistance = "";

            if (totalDistance < 1000) {
                displayDistance = "0." + String.valueOf(totalDistance) + " km";
            } else {
                while (totalDistance >= 1000) {
                    km++;
                    totalDistance -= 1000;
                }
                m = totalDistance;
                displayDistance = String.valueOf(km) + "." + String.valueOf(m) + " km";
            }

            int min = 0, sec = 0;
            String displayTravelTime = "";
            if (totalDistance < 60)
                displayTravelTime = "1 minute";
            else {
                while (totalTravelTime >= 60) {
                    min++;
                    totalTravelTime -= 60;
                }
                sec = totalTravelTime;
                displayTravelTime = String.valueOf(min) + ":" + String.valueOf(sec) + " minutes";
            }
            if (createRoute) {
                UserRouteController.setDistanceTimeTaken(userRoute, displayDistance, displayTravelTime);
                createRoute = false;
            }

            Toast.makeText(MapsActivity.this, "DISTANCE : " + displayDistance + "\nTIME REQUIRED : " + displayTravelTime, Toast.LENGTH_LONG).show();
        }
    }


    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));
        Log.i("Tracker", "Start tracking");
        //Notify the user that tracking has been enabled
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
        //finish();
    }


}
