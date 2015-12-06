package com.pham.accessmap;

import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLngBounds;
import com.pham.accessmap.Object.BusDirectionJSONParser;
import com.pham.accessmap.Object.BusObject;
import com.pham.accessmap.Object.DataHelper;
import com.pham.accessmap.Object.DirectionJSONParser;
import com.pham.accessmap.Object.Download;
import com.pham.accessmap.Object.GPSTracker;
import com.pham.accessmap.Object.LocMarker;
import com.pham.accessmap.Object.MapMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Circle circle;
    private Marker marker;
    private Download download;
    private boolean isFirstTime;
    public static Activity activity;
    GPSTracker gpsTracker;
    PolylineOptions lineOptions = null;
    Polyline polyline;
    ArrayList<LatLng> markerPoints;
    LatLng origin;
    LatLng destination;
    PolylineOptions busLine = null;
    Polyline busPolyline;
    List<Marker> listMarker;
    private static final int GOOGLE_MAP_BOUND_PADDING = 200;

    private Map<Marker, MapMarker> allMarkersMap = new HashMap<Marker, MapMarker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markerPoints = new ArrayList<LatLng>();
        fixNetworkOnMainThreadException();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        isFirstTime = prefs.getBoolean("isFirstTime", true);
        activity = this;
        if (isFirstTime == true) {
            // set language
            Locale locale = new Locale("vi_VN");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            this.getApplicationContext().getResources().updateConfiguration(config, null);
            //download = new Download();
            DataHelper dataHelper = new DataHelper(this);
            dataHelper.createDataBase();

            download = new Download(this);
            download.getLocationType();
            download.getAccessibilityType();
            download.getLocation();
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.clear();
            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }

        setUpMapIfNeeded();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    double latitude = Double.valueOf(bundle.getString("latitude"));
                    double longitude = Double.valueOf(bundle.getString("longitude"));
                    updateBookmarkCamera(latitude, longitude);
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    double latitude = Double.valueOf(bundle.getString("latitude"));
                    double longitude = Double.valueOf(bundle.getString("longitude"));
                    updateBookmarkCamera(latitude, longitude);
                }
                break;
            case 3: {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    double originLatitude = Double.valueOf(bundle.getString("originLatitude"));
                    double originLongitude = Double.valueOf(bundle.getString("originLongitude"));
                    origin = new LatLng(originLatitude, originLongitude);
                    destination = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    boolean isBus = bundle.getBoolean("isBus");
                    boolean isSwitch = bundle.getBoolean("isSwitch");

                    if (isSwitch == false) {
                        origin = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        destination = new LatLng(originLatitude, originLongitude);
                    }

                    //reloadMap();
                    if (isBus == false) {
                        String url = getDirectionsUrl(origin, destination);
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);

                    } else {
                        String url = getBusDirectionsUrl(origin, destination);
                        DownloadBusTask downloadBusTask = new DownloadBusTask();
                        downloadBusTask.execute(url);
                    }
                }
                break;
            }
            case 4:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    LatLng newOriginal = (LatLng) (bundle.get("origin"));
                    LatLng newDestination = (LatLng) bundle.get("destination");
                    boolean isBus = bundle.getBoolean("isBus");
                    //boolean isSwitch = bundle.getBoolean("isSwitch");
                    origin = newOriginal;
                    destination = newDestination;

                    //reloadMap();
                    if (isBus == false) {
                        String url = getDirectionsUrl(origin, destination);
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);

                    } else {
                        String url = getBusDirectionsUrl(origin, destination);
                        DownloadBusTask downloadBusTask = new DownloadBusTask();
                        downloadBusTask.execute(url);
                    }
                }
                break;
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String getBusDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String mode = "mode=transit";

        String key = "AIzaSyDTX7S7hB_AtIF52VJ0Q00BLGIH1QxLbhA";

        String time = "departure_time=1343376768";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key + "&" + time;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    public void updateBookmarkCamera(double latitude, double longitude) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), 16));
        //LatLng newLocation = new LatLng(latitude,longitude);
        for (Marker item : listMarker)
        {
            if (item.getPosition().latitude==latitude && item.getPosition().longitude == longitude)
            {
                item.showInfoWindow();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            gpsTracker = new GPSTracker(this);
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 13));

            showMarkers();
        }
    }

    public void onClick_Routes(View v) {
        Intent intent = new Intent(this, RoutesActivity.class);
        int requestCode = 4;
        startActivityForResult(intent, requestCode);
    }

    public void onClick_BookMark(View v) {
        Intent intent = new Intent(this, BookMarkActivity.class);
        int requestCode = 1;
        startActivityForResult(intent, requestCode);
    }


    public void onClick_Search(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        int requestCode = 2;
        startActivityForResult(intent, requestCode);
    }


    public void onClick_Info(View v) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }


    public void onClick_Reload(View v) {
        reloadMap();
    }

    public void reloadMap() {
        if (circle != null) {
            circle.remove();
        }

        if (marker != null) {
            mMap.clear();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Your dialog code.
                            showMarkers();
                        }
                    });
                } catch (Exception e) {
                }
            }

        }).start();
    }

    public void onClick_Setting(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void fixNetworkOnMainThreadException() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }

    private void showMarkers() {

        LocMarker locMarker = new LocMarker(this);
        listMarker = new ArrayList<Marker>();
        SharedPreferences prefs = this.getSharedPreferences("MyPref", this.MODE_PRIVATE);
        int km = prefs.getInt("radius", 3);
        LatLng center = mMap.getCameraPosition().target;

        List<LocMarker> data = new ArrayList<LocMarker>(locMarker.getAllMarkerWithDistance(km, center.latitude, center.longitude));

        for (LocMarker lc : data) {
            byte[] bytes = Base64.decode(lc.Image, Base64.DEFAULT);
            Bitmap bmp = getBitmap(bytes);

            Double dataLat = Double.valueOf(lc.latitude);
            Double dataLong = Double.valueOf(lc.longitude);

            marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bmp.createScaledBitmap(bmp, 140, 160, false)))
                    .position(new LatLng(dataLat, dataLong)).title(lc.location_title)
                    .snippet(lc.address));

            listMarker.add(marker);

            MapMarker mydata = new MapMarker();
            mydata.markerID = lc.locationID;
            mydata.Address = lc.address;
            mydata.Title = lc.location_title;
            mydata.Phone = lc.phone;
            allMarkersMap.put(marker, mydata);

        }
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(center.latitude, center.longitude))
                .radius(km * 1000)
                .strokeWidth(3.5f)
                .strokeColor(Color.BLUE));


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                MapMarker myDataObj = allMarkersMap.get(marker);
                Intent intent = new Intent(MapsActivity.this, TabMenuActivity.class);
                intent.putExtra("id", myDataObj.markerID);
                intent.putExtra("title", myDataObj.Title);
                intent.putExtra("address", myDataObj.Address);
                intent.putExtra("phone", myDataObj.Phone);
                int requestCode = 3;
                startActivityForResult(intent, requestCode);
            }
        });

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;

            if (lineOptions != null) {
                polyline.remove();
                lineOptions = null;
                marker.remove();
            }

            if (busLine != null) {
                busLine = null;
                marker.remove();
                busPolyline.remove();
            }

            //MarkerOptions markerOptions = new MarkerOptions();
            markerPoints.add(origin);
            markerPoints.add(destination);


            Bitmap tBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.motorcycle);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(origin);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(tBitMap.createScaledBitmap(tBitMap,120,139,false)));
            marker = mMap.addMarker(markerOptions);


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    //Log.v("test",String.valueOf(lat));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            polyline = mMap.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(destination);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, GOOGLE_MAP_BOUND_PADDING));
        }
    }


    private class DownloadBusTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserBusTask parserTask = new ParserBusTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserBusTask extends AsyncTask<String, Integer, List<List<BusObject>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<BusObject>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<BusObject>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                BusDirectionJSONParser parser = new BusDirectionJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<BusObject>> result) {
            ArrayList<LatLng> points = null;
            ArrayList<LatLng> busPoints = null;

            if (lineOptions != null) {

                polyline.remove();
                lineOptions = null;
                marker.remove();
            }

            if (busLine != null) {
                marker.remove();
                busPolyline.remove();
                busLine = null;
                //polyline.remove();
                //lineOptions = null;
            }

            markerPoints.add(origin);
            markerPoints.add(destination);
            Bitmap tBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bus);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(origin);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(tBitMap.createScaledBitmap(tBitMap,120,139,false)));
            marker = mMap.addMarker(markerOptions);

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                busPoints = new ArrayList<LatLng>();

                lineOptions = new PolylineOptions();
                busLine = new PolylineOptions();
                // Fetching i-th route
                List<BusObject> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    BusObject busObject = path.get(j);

                    for (int k = 0; k < busObject.hashMapList.size(); k++) {
                        HashMap<String, String> point = busObject.hashMapList.get(k);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                        //Log.v("test",String.valueOf(lat));
                    }

                    for (int k = 0; k < busObject.busMapList.size(); k++) {
                        HashMap<String, String> point = busObject.busMapList.get(k);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        busPoints.add(position);

                        //Log.v("test",String.valueOf(lat));
                    }
                }

                // Adding all the points in the route to LineOptions
//                if (busObject.isWalking == true

                busLine.addAll(busPoints);
                busLine.width(5);
                busLine.color(Color.RED);

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            polyline = mMap.addPolyline(lineOptions);
            busPolyline = mMap.addPolyline(busLine);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(destination);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, GOOGLE_MAP_BOUND_PADDING));
        }
    }

}
