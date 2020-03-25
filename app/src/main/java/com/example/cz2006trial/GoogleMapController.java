package com.example.cz2006trial;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

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
import java.util.List;

public class GoogleMapController {

    private static final GoogleMapController controller = new GoogleMapController();
    private StartListener startListener;
    private EndListener endListener;
    private CreateListener createListener;
    private RouteListener routeListener;


    private boolean startTrack = false;
    private boolean setStartPoint = false;
    private UserLocationSessionEntity userLocationSession;
    private UserRouteEntity userRouteEntity;
    private boolean setEndPoint;
    private String message;

    private ArrayList<LatLng> route;
    private boolean createRoute;

    private GoogleMapController () {
        userLocationSession = new UserLocationSessionEntity();
        userRouteEntity = new UserRouteEntity();
        route = new ArrayList<>();
    }

    public static GoogleMapController getController() {
        return controller;
    }

    public interface Listener {
        void onChange();
    }

    public abstract static class StartListener implements Listener{};

    public abstract static class CreateListener implements Listener{};

    public abstract static class EndListener implements Listener{};

    public abstract static class RouteListener implements Listener{};


    public void setStartListener(StartListener listener) {
        this.startListener = listener;
    }

    public void setEndListener(EndListener listener) {
        this.endListener = listener;
    }

    public void setCreateListener(CreateListener listener) {
        this.createListener = listener;
    }

    public void setRouteListener(RouteListener routeListener) {
        this.routeListener = routeListener;
    }

    public void beginTracking(UserLocationSessionEntity userLocationSession) {
        this.userLocationSession = userLocationSession;
        startTrack = true;
    }

    public void resumeTracking() {
        startTrack = true;
    }

    public void endTracking() {
        startTrack = false;
    }

    public boolean isStartTrack() {
        return startTrack;
    }

    public UserLocationSessionEntity getUserLocationSession() {
        return userLocationSession;
    }

    public void setStartingPoint(UserRouteEntity userRoute) {
        setStartPoint = true;
        if (startListener != null ) startListener.onChange();
        this.userRouteEntity = userRoute;

    }

    public void setEndingPoint(UserRouteEntity userRoute) {
        setEndPoint = true;
        if (endListener != null ) endListener.onChange();
        this.userRouteEntity = userRoute;

    }

    public void stopSettingPoints() {
        setStartPoint = false;
        setEndPoint = false;
        if (endListener != null ) endListener.onChange();
        if (startListener != null ) startListener.onChange();

    }

    public void create(UserRouteEntity userRoute) {
        createRoute = true;
        if (createListener != null ) createListener.onChange();
        this.userRouteEntity = userRoute;
    }

    public void clearRoute() {
        createRoute = false;
        route.clear();
        userRouteEntity = new UserRouteEntity();
        if (createListener != null ) createListener.onChange();
    }


    public boolean isSetStartPoint() {
        return setStartPoint;
    }

    public boolean isSetEndPoint() {
        return setEndPoint;
    }

    public UserRouteEntity getUserRouteEntity() {
        return userRouteEntity;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<LatLng> getRoute() {
        return route;
    }

    public boolean isCreateRoute() {
        return createRoute;
    }

    //method to get directions url
    public void getDirections(LatLng origin, LatLng destination) {

        //Forming an URL string which will return JSON as a result.
        String originString = "origin=" + origin.latitude + "," + origin.longitude;
        String destinationString = "destination=" + destination.latitude + "," + destination.longitude;

        //IF THIS GENERATES ERROR, HARD CODE API KEY INTO URL.
        String apiKey = "AIzaSyCz0G1WVYtj1njKUMnPRf5A4FVfvxMvzZs";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + originString
                + "&" + destinationString + "&key=" + apiKey + "&mode=walking";


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

                        route = (ArrayList) markers;
                        routeListener.onChange();

                    }
                }

            } catch (JSONException e) {
                // TODO: 24-Mar-20
                /*Toast.makeText(getActivity(), "WELL WE MESSED UP!", Toast.LENGTH_LONG).show();*/
            }

            //toastData(totalDistance, totalTravelTime);
        }
/*
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

            Toast.makeText(getActivity(), "DISTANCE : " + displayDistance + "\nTIME REQUIRED : " + displayTravelTime, Toast.LENGTH_LONG).show();
        }*/
    }
}
