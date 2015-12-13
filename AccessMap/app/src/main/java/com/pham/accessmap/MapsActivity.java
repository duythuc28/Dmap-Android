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
import android.widget.SearchView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.pham.accessmap.Model.BusDirectionParser;
import com.pham.accessmap.Model.DirectionParser;
import com.pham.accessmap.Object.BusDirectionJSONParser;
import com.pham.accessmap.Object.BusObject;
import com.pham.accessmap.Object.DataHelper;
import com.pham.accessmap.Object.DirectionJSONParser;
import com.pham.accessmap.Object.Download;
import com.pham.accessmap.Object.GPSTracker;
import com.pham.accessmap.Object.LanguageHelper;
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
    List<Polyline> mPolylines;
    List<Marker> mBusMarkers;
    private static final int GOOGLE_MAP_BOUND_PADDING = 200;

    private Map<Marker, MapMarker> allMarkersMap = new HashMap<Marker, MapMarker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markerPoints = new ArrayList<>();
        fixNetworkOnMainThreadException();
        SharedPreferences prefs = getSharedPreferences(LanguageHelper.PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);
        activity = this;
        String tAppLanguageCode = LanguageHelper.getInstance().getAppLanguage(getApplicationContext());
        if (isFirstTime) {
            // set default language
            tAppLanguageCode = LanguageHelper.VIETNAMESE;
            DataHelper dataHelper = new DataHelper(this);
            dataHelper.createDataBase();
            Download download = new Download(this);
            download.getLocationType();
            download.getAccessibilityType();
            download.getLocation();
            isFirstTime = false;
        }
        SharedPreferences.Editor editor = getSharedPreferences(LanguageHelper.PREFS_NAME, 0).edit();
        editor.putBoolean("isFirstTime", isFirstTime);
        editor.commit();

        setUpMapIfNeeded();
        // Set language
        LanguageHelper.getInstance().setAppLanguage(tAppLanguageCode, this.getApplicationContext());

//        SearchView searchView = (SearchView)findViewById(R.id.mapSearchView);
//        searchView.clearFocus();
//        searchView.setEnabled(false);
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
        Intent intent = new Intent(this, InfoSettingActivity.class);
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

    public void onClick_Sharing(View view) {
        Intent intent = new Intent(this, EndUserActivity.class);
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
    private class ParserTask extends AsyncTask<String, Integer, List<DirectionParser>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<DirectionParser> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<DirectionParser> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                // Starts parsing data
//                routes = parser.parse(jObject);
                routes = parser.parsed(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<DirectionParser> result) {
            ArrayList<LatLng> points = null;


            if (lineOptions != null && polyline != null && marker != null) {
                polyline.remove();
                lineOptions = null;
                marker.remove();
            }

            if (mPolylines != null) {
                for (Polyline tPolyline : mPolylines) {
                    tPolyline.remove();
                }
                mPolylines.clear();
            }

            if (mBusMarkers != null) {
                for (Marker tMaker : mBusMarkers) {
                    tMaker.remove();
                }
                mBusMarkers.clear();
            }

            //MarkerOptions markerOptions = new MarkerOptions();
            markerPoints.add(origin);
            markerPoints.add(destination);

            Bitmap tBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.motorcycle);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(origin);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(tBitMap.createScaledBitmap(tBitMap, 120, 139, false)));
            markerOptions.title(getResources().getString(R.string.label_motorbike_button));
            DirectionParser tDirectionParser = result.get(0);
            markerOptions.snippet(tDirectionParser.mDistance);
            marker = mMap.addMarker(markerOptions);
            for (DirectionParser directionParser : result) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < directionParser.mListLocations.size(); j++) {
                    HashMap<String, String> point = directionParser.mListLocations.get(j);

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
    private class ParserBusTask extends AsyncTask<String, Integer, List<BusDirectionParser>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<BusDirectionParser> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<BusDirectionParser> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                BusDirectionJSONParser parser = new BusDirectionJSONParser();

                // Starts parsing data
//                routes = parser.parse(jObject);
                routes = parser.parseBusJSON(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<BusDirectionParser> result) {
            super.onPostExecute(result);
            ArrayList<LatLng> points = null;

            if (lineOptions != null && mPolylines != null && mBusMarkers != null) {
                for (Polyline tPolyline : mPolylines) {
                    tPolyline.remove();
                }
                for (Marker tMarker : mBusMarkers) {
                    tMarker.remove();
                }
                mBusMarkers.clear();
                mPolylines.clear();
                lineOptions = null;
            }

            if (polyline != null) {
                polyline.remove();
            }

            if (marker != null) {
                marker.remove();
            }

            //MarkerOptions markerOptions = new MarkerOptions();
            markerPoints.add(origin);
            markerPoints.add(destination);

            mPolylines  = new ArrayList<>();
            mBusMarkers = new ArrayList<>();

            for (BusDirectionParser busDirectionParser : result) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching all the points in i-th route
                for (int j = 0; j < busDirectionParser.mListLocations.size(); j++) {
                    HashMap<String, String> point = busDirectionParser.mListLocations.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    //Log.v("test",String.valueOf(lat));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(busDirectionParser.getmStartLocation());
                // Adding all the points in the route to LineOptions
                if (busDirectionParser.mIsBus) {
                    lineOptions.color(Color.BLUE);
                    Bitmap tBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bus);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(tBitMap.createScaledBitmap(tBitMap, 120, 139, false)));
                    markerOptions.title(getResources().getString(R.string.label_bus_button));
                    markerOptions.snippet(busDirectionParser.getmBusNumber());
                } else {
                    lineOptions.color(Color.GREEN);
                    Bitmap tBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.walking);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(tBitMap.createScaledBitmap(tBitMap, 120, 139, false)));
                    markerOptions.title(getResources().getString(R.string.marker_title_walking));
                    markerOptions.snippet(busDirectionParser.mDistance);
                }
                mBusMarkers.add(mMap.addMarker(markerOptions));
                lineOptions.addAll(points);
                lineOptions.width(5);
//                polyline = mMap.addPolyline(lineOptions);
                mPolylines.add(mMap.addPolyline(lineOptions));
            }

            // Drawing polyline in the Google Map for the i-th route
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(destination);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, GOOGLE_MAP_BOUND_PADDING));
        }
    }
}
