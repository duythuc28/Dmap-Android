package com.pham.accessmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.pham.accessmap.Object.AccessType;
import com.pham.accessmap.Object.GPSTracker;
import com.pham.accessmap.Object.LocationTemp;
import com.pham.accessmap.Object.LocationType;
import com.pham.accessmap.Object.MultiSelectionSpinner;
import com.pham.accessmap.Object.PostLocation;
import com.pham.accessmap.Object.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DRDStaffActivity extends ActionBarActivity {

    ArrayList<Integer> m_select;
    EditText drd_password, drd_username, drd_name, drd_address, drd_phone;
    MultiSelectionSpinner accessSpinner;
    GPSTracker gpsTracker;
    PostLocation postLocation;
    Spinner spinner;
    User user;
    ArrayList<String> data;
    ArrayList<String> data1;
    static String uri = "http://www.drdvietnam.org/bandotiepcan/api/post/location";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drdstaff);
        getSupportActionBar().setTitle(R.string.title_activity_end_user);
        LocationType locationType = new LocationType(this);
        AccessType accessType = new AccessType(this);
        SharedPreferences preferences = this.getSharedPreferences("MyPref",this.MODE_PRIVATE);
        boolean language = preferences.getBoolean("language", false);
        // Add to Alert Diaglog
        if (language == true)
        {
            data1 = accessType.getAllName_En();
            data = locationType.getNameEng();
        }
        else
        {
            data1 = accessType.getAllName();
            data = locationType.getNameVn();
        }

        //ArrayList<LocationType> data = locationType.getAllData();
        //data = locationType.getNameEng();
        spinner = (Spinner) findViewById(R.id.addDRD_selectLocationType);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, data);
// Specify the layout to use when the list of choices appears
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(dataAdapter);

        // Get EditText id;
        drd_password = (EditText) findViewById(R.id.addDRD_Password);
        drd_phone = (EditText) findViewById(R.id.addDRD_locationPhone);
        drd_username = (EditText) findViewById(R.id.addDRD_UserName);
        drd_name = (EditText) findViewById(R.id.addDRD_locationName);
        drd_address = (EditText) findViewById(R.id.addDRD_locationAdd);

        //Init PostLocation & gpsTracker
        postLocation = new PostLocation();
        postLocation.Location_AccessType = new ArrayList<Integer>();
        gpsTracker = new GPSTracker(this);
        user = new User();

        // Add to MultiSelection Spinner

        //data1 = accessType.getAllName_En();
        accessSpinner = (MultiSelectionSpinner) findViewById(R.id.addDRD_spinnerAccess);
        accessSpinner.setItems(data1);

        // Get username and password
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String username = prefs.getString("userName","");
        String password = prefs.getString("password","");
        drd_username.setText(username);
        drd_password.setText(password);

    }

    public static String POST(String url, PostLocation postLocation,User user ){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            //JSONArray accessType = new JSONArray();
            //accessType.put(0,postLocation.Location_AccessType);

            jsonArray.put(0,postLocation.latitude);
            jsonArray.put(1,postLocation.longitude);

            JSONArray jsonArray1 = new JSONArray();
            for (int items : postLocation.Location_AccessType)
            {
                jsonArray1.put(items);
            }
            jsonArray.put(2,jsonArray1);

            jsonArray.put(3,postLocation.location_Type);
            jsonArray.put(4,postLocation.postLocationName);
            jsonArray.put(5,postLocation.postLocationAddress);
            jsonArray.put(6,postLocation.postLocationPhone);
            jsonArray.put(7,postLocation.userPhone);
            jsonObject.accumulate("Location",jsonArray);

            JSONArray arrayUser = new JSONArray();
            arrayUser.put(0,user.userName);
            arrayUser.put(1,user.password);
            jsonObject.accumulate("User",arrayUser);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            //json = json.replace("\"","");
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

//            postLocation = new PostLocation();
//            postLocation.Location_AccessType = new ArrayList<Integer>();
            //gpsTracker = new GPSTracker(EndUserActivity.this);
            postLocation.postLocationName = (drd_name.getText().toString());
            postLocation.location_Type = spinner.getSelectedItemPosition() + 1;
            postLocation.latitude = String.valueOf(gpsTracker.getLatitude());
            postLocation.longitude = String.valueOf(gpsTracker.getLongitude());
            for (int i = 0 ; i < accessSpinner.getSelectedIndicies().size(); i++)
            {
                postLocation.Location_AccessType.add(accessSpinner.getSelectedIndicies().indexOf(i) + 2);
            }
            //postLocation.Location_AccessType.add(1);
            //postLocation.userPhone = endUser_UserName.getText().toString();
            postLocation.postLocationAddress = drd_address.getText().toString();
            postLocation.postLocationPhone = drd_phone.getText().toString();

            //Add user
            user.userName = drd_username.getText().toString();
            user.password = drd_password.getText().toString();

            //person.setTwitter(etTwitter.getText().toString());

            return POST(urls[0],postLocation,user);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            drd_phone.setText("");
            drd_address.setText("");
            drd_name.setText("");

            setSharePreference();

            //m_select.clear();
            //_selections = null;
            new AlertDialog.Builder(DRDStaffActivity.this)
                    .setTitle("Success")
                    .setMessage("Your feedback has been posted")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
        }
    }

    private boolean validate()
    {

        if(drd_name.getText().toString().trim().equals(""))
            return false;
        else if(drd_address.getText().toString().trim().equals(""))
            return false;
        else if (drd_phone.getText().toString().trim().equals(""))
            return false;
        else if (drd_username.getText().toString().trim().equals(""))
            return false;
        else if (gpsTracker.getLatitude() ==0 && gpsTracker.getLatitude() == 0)
        {
            return false;
        }
        else if (accessSpinner.getSelectedIndicies().size() == 0)
        {
            return false;
        }
        else if (drd_password.getText().toString().trim().equals(""))
        {
            return false;
        }
        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drdstaff, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showList) {
            Intent intent = new Intent(this, DRD_SaveList.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_DRD_Save (View v)
    {
        if (validate())
        {
            LocationTemp locationTemp = new LocationTemp(this);
            locationTemp.latitute = String.valueOf(gpsTracker.getLatitude());
            locationTemp.longitude = String.valueOf(gpsTracker.getLongitude());
            locationTemp.locationPhone = drd_phone.getText().toString();
            locationTemp.locationName = drd_name.getText().toString();
            locationTemp.userPhone = "0";
            locationTemp.locationAddress = drd_address.getText().toString();
            locationTemp.locationType = String.valueOf(spinner.getSelectedItemPosition() + 1);
            locationTemp.isUser = true;
            for (int i = 0 ; i < accessSpinner.getSelectedIndicies().size(); i++)
            {
                locationTemp.accessType = String.valueOf(accessSpinner.getSelectedIndicies().indexOf(i) + 2);
            }

            if (locationTemp.getLocationID(drd_name.getText().toString()) != 0)
            {
                locationTemp.editLocation(locationTemp);
            }
            else
            {
                locationTemp.insertLocation(locationTemp);
            }
            setSharePreference();
            new AlertDialog.Builder(DRDStaffActivity.this)
                    .setTitle("Success")
                    .setMessage("You have added new data")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
            drd_phone.setText("");
            drd_address.setText("");
            drd_name.setText("");
        }
        else
        {
            new AlertDialog.Builder(DRDStaffActivity.this)
                    .setTitle("Error")
                    .setMessage("Please check your information")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
        }
    }

    public void onClick_DRD_Post (View v)
    {
        if (validate())
        {
            new HttpAsyncTask().execute(uri);
        }
        else
        {
            new AlertDialog.Builder(DRDStaffActivity.this)
                    .setTitle("Error")
                    .setMessage("Please check your information")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
        }
    }

    public void setSharePreference()
    {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.clear();
        editor.putString("userName", drd_username.getText().toString());
        editor.putString("password" , drd_password.getText().toString());
        editor.commit();
    }
}
