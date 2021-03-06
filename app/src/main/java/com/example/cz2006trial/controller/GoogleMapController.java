package com.example.cz2006trial.controller;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cz2006trial.model.UserLocationSession;
import com.example.cz2006trial.model.UserRoute;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import java.util.HashMap;
import java.util.List;

public class GoogleMapController {

    private static final GoogleMapController controller = new GoogleMapController();
    private StartListener startListener;
    private EndListener endListener;
    private CreateListener createListener;
    private RouteListener routeListener;
    //private PointListener pointListener;
    private PlaceListener placeListener;
    private DisplayTrackingDistanceListener displayTrackingDistanceListener;
    private ClearTrackListener clearTrackListener;

    private boolean startTrack = false;
    private boolean trackPaused = false;
    private boolean trackEnded = false;
    private long timeElapsed = 0;
    private ArrayList<LatLng> locations = new ArrayList<>();
    private UserLocationSession userLocationSession;

    private boolean setStartPoint = false;
    private UserRoute userRoute;
    private boolean setEndPoint;
    private String message;

    private ArrayList<LatLng> route;
    private ArrayList<Marker> markers;
    private Marker pointChosen;

    private ArrayList<LatLng> historyRoute;
    private boolean createHistoryRoute;
    private boolean createRoute;

    private HashMap<String, Object> parkInfo = new HashMap<>();

    private GoogleMapController () {
        userLocationSession = new UserLocationSession();
        userRoute = new UserRoute();
        route = new ArrayList<>();
    }

    public static GoogleMapController getController() {
        return controller;
    }

    public interface Listener {
        void onChange();
    }

    public abstract static class StartListener implements Listener{};

    public abstract static class EndListener implements Listener {
    }

    ;

    public abstract static class CreateListener implements Listener{};

    public abstract static class RouteListener implements Listener{};

    //public abstract static class PointListener implements Listener{};

    public abstract static class PlaceListener implements Listener{};

    public abstract static class DisplayTrackingDistanceListener implements Listener {
    }

    ;

    public abstract static class ClearTrackListener implements Listener {
    }

    ;

    public void setPlaceListener(PlaceListener listener) {
        this.placeListener = listener;
    }

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

    /*
    public void setPointListener(PointListener pointListener) {
        this.pointListener = pointListener;
    }
*/

    public void setDisplayTrackingDistanceListener(DisplayTrackingDistanceListener listener) {
        this.displayTrackingDistanceListener = listener;
    }

    public void setClearTrackListener(ClearTrackListener listener) {
        this.clearTrackListener = listener;
    }

 /*   public void setMarkers(ArrayList<Marker> markers) {
        this.markers = markers;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public void setPointChosen(Marker pointChosen) {
        this.pointChosen = pointChosen;
        pointListener.onChange();
    }

    public Marker getPointChosen() {
        return pointChosen;
    }
*/

    // FOR ROUTE TRACKING
    public boolean isStartTrack() {
        return startTrack;
    }

    public boolean isTrackPaused() {
        return trackPaused;
    }

    public boolean isTrackEnded() {
        return trackEnded;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public ArrayList<LatLng> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LatLng> locations) {
        this.locations = locations;
    }

    public UserLocationSession getUserLocationSession() {
        return userLocationSession;
    }

    public void displayTrackingDistance(UserLocationSession userLocationSession) {
        if (displayTrackingDistanceListener != null) displayTrackingDistanceListener.onChange();
        this.userLocationSession = userLocationSession;
    }

    public void clearTrack() {
        if (clearTrackListener != null) clearTrackListener.onChange();
        locations.clear();
        userLocationSession = new UserLocationSession();
    }

    public void beginTracking(UserLocationSession userLocationSession) {
        startTrack = true;
        trackPaused = false;
        trackEnded = false;
        this.userLocationSession = userLocationSession;
    }

    public void resumeTracking(UserLocationSession userLocationSession, long timeElapsed) {
        startTrack = true;
        trackPaused = false;
        this.timeElapsed = timeElapsed;
        this.userLocationSession = userLocationSession;
    }

    public void pauseTracking(UserLocationSession userLocationSession, long timeElapsed) {
        startTrack = false;
        trackPaused = true;
        Log.d("pause", "startTrack: " + startTrack + " " + trackPaused);
        this.timeElapsed = timeElapsed;
        this.userLocationSession = userLocationSession;
    }

    public void endTracking(UserLocationSession userLocationSession, long timeElapsed) {
        startTrack = false;
        trackEnded = true;
        this.timeElapsed = timeElapsed;
        this.userLocationSession = userLocationSession;
    }


    // FOR ROUTE HISTORY
    public ArrayList<LatLng> getHistoryRoute() {
        return route;
    }

    public void setCreateHistoryRoute(boolean createHistoryRoute) {
        this.createHistoryRoute = createHistoryRoute;
    }


    // FOR DISPLAYING PARK CONNECTORS
    public HashMap<String, Object> getParkInfo() {
        return parkInfo;
    }


    // FOR CREATING ROUTE

    /*    public void setStartingPoint(UserRoute userRoute) {
        setStartPoint = true;
        if (startListener != null ) startListener.onChange();
        this.userRoute = userRoute;

    }

    public void setEndingPoint(UserRoute userRoute) {
        setEndPoint = true;
        if (endListener != null ) endListener.onChange();
        this.userRoute = userRoute;

    }*/

    public boolean isSetStartPoint() {
        return setStartPoint;
    }

    public boolean isSetEndPoint() {
        return setEndPoint;
    }

    public UserRoute getUserRoute() {
        return userRoute;
    }

    public ArrayList<LatLng> getRoute() {
        return route;
    }

    public boolean isCreateRoute() {
        return createRoute;
    }

    public void setCreatePoint(boolean isStart, UserRoute userRoute) {
        if (isStart) {
            setStartPoint = true;
            if (startListener != null) startListener.onChange();
        } else {
            setEndPoint = true;
            if (endListener != null) endListener.onChange();
        }
        this.userRoute = userRoute;
    }

    public void stopSettingPoints() {
        setStartPoint = false;
        setEndPoint = false;
        if (endListener != null) endListener.onChange();
        if (startListener != null) startListener.onChange();

    }

    public void create(UserRoute userRoute) {
        createRoute = true;
        if (createListener != null) createListener.onChange();
        this.userRoute = userRoute;
    }

    public void clearRoute() {
        createRoute = false;
        route.clear();
        userRoute = new UserRoute();
        if (createListener != null) createListener.onChange();
    }


    public void getInfo(String title) {

        String apiKey = "AIzaSyCz0G1WVYtj1njKUMnPRf5A4FVfvxMvzZs";

        String url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" + "key=" + apiKey +
                "&input=" + title + "&inputtype=textquery" + "&fields=photos,formatted_address,name,rating,opening_hours,geometry,place_id";



        //Run the URL formed in above step and wait for result.
        DownloadPlacesTask downloadPlacesTask = new DownloadPlacesTask();
        downloadPlacesTask.execute(url);

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

                        if (!createHistoryRoute)
                            route = (ArrayList) markers;
                        else
                            System.out.println("Inside");
                        historyRoute = (ArrayList) markers;
                        routeListener.onChange();

                    }
                }

            } catch (JSONException e) {
                // TODO: 24-Mar-20
                /*Toast.makeText(getActivity(), "WELL WE MESSED UP!", Toast.LENGTH_LONG).show();*/
            }

            getDistAndTime(totalDistance, totalTravelTime);
        }

        //Stores distance and time
        public void getDistAndTime(int totalDistance, int totalTravelTime) {
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

            UserRouteController.setDistanceTimeTaken(userRoute, displayDistance, displayTravelTime);
        }
    }

    private class DownloadPlacesTask extends AsyncTask<String, Void, String> {


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
            //set default values
            double rating = 0;
            boolean open = true;
            String address;
            String title;
            LatLng location;
            //todo photos

            try {
                JSONObject parentMain = new JSONObject(s);
                JSONObject result = parentMain.getJSONArray("candidates").getJSONObject(0);

                //check for rating
                if (result.has("rating"))
                    rating = result.getDouble("rating");
                parkInfo.put("rating", rating);

                //check if open
                if (result.has("opening_hours")) {
                    if (result.getJSONObject("opening_hours").has("open_now"))
                        open = result.getJSONObject("opening_hours").getBoolean("open_now");
                }
                parkInfo.put("open", open);

                address = result.getString("formatted_address");
                parkInfo.put("address", address);
                title = result.getString("name");
                parkInfo.put("name", title);
                location = new LatLng(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                        result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                parkInfo.put("location", location);

                placeListener.onChange();

            } catch (JSONException e) {
                // TODO: 24-Mar-20
                Log.e("JSON call", e.getMessage());
            }
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
