package com.pham.accessmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pham.accessmap.Object.LocationTemp;
import com.pham.accessmap.Object.LocationTempAdapter;
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


public class DRD_SaveList extends ActionBarActivity {
    ListView listview;
    LocationTempAdapter adapter;
    User user;
    ArrayList<LocationTemp> data;
    LocationTemp location;
    static String uri = "http://www.drdvietnam.org/bandotiepcan/api/post/location";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drd__save_list);
        listview  = (ListView) findViewById(R.id.list_drd);
        location = new LocationTemp(this);
        data = location.getAllData(1);
        adapter = new LocationTempAdapter(this,data);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "selected Item Name is " + position, Toast.LENGTH_SHORT).show();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(DRD_SaveList.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete ");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("data",String.valueOf(data.get(position).locationID));
                        location.deleteLocationTemp(data.get(position).locationID);
                        reloadAllData();

                    }});
                adb.show();

                return true;
            }
        });
    }

    private void reloadAllData(){
        // get new modified random data
        data = location.getAllData(1);
        // update data in our adapter
        adapter.clear();
        adapter.addAll(data);
        // fire the event
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drd__save_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_postAll) {
            new HttpAsyncTask().execute(uri);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String POST(String url, ArrayList<PostLocation> postLocations,User user ){
        InputStream inputStream = null;
        String result = "";
        for (PostLocation post : postLocations)
        {
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

                jsonArray.put(0,post.latitude);
                jsonArray.put(1,post.longitude);

                JSONArray jsonArray1 = new JSONArray();
                for (int items : post.Location_AccessType)
                {
                    jsonArray1.put(items);
                }
                jsonArray.put(2,jsonArray1);

                jsonArray.put(3,post.location_Type);
                jsonArray.put(4,post.postLocationName);
                jsonArray.put(5,post.postLocationAddress);
                jsonArray.put(6,post.postLocationPhone);
                jsonArray.put(7,post.userPhone);
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
        }

        // 11. return result
        return result;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            ArrayList<PostLocation> postLocations = new ArrayList<PostLocation>();
            for (LocationTemp locationTemp : data)
            {
                PostLocation postLocation = new PostLocation();
                postLocation.postLocationName = locationTemp.locationName;
                postLocation.location_Type = Integer.valueOf(locationTemp.locationType);
                postLocation.latitude = locationTemp.latitute;
                postLocation.longitude = locationTemp.longitude;
                postLocation.Location_AccessType = new ArrayList<Integer>();
                for (int i = 0 ; i < locationTemp.accessType.length(); i++)
                {
                    postLocation.Location_AccessType.add(locationTemp.accessType.indexOf(i));
                }
                //postLocation.Location_AccessType.add(1);
                //postLocation.userPhone = endUser_UserName.getText().toString();
                postLocation.postLocationAddress = locationTemp.locationAddress;
                postLocation.postLocationPhone = locationTemp.locationPhone;
                postLocations.add(postLocation);
            }
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            String username = prefs.getString("userName","");
            String password = prefs.getString("password","");
            user.userName = username;
            user.password = password;

            return POST(urls[0],postLocations,user);
    }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            LocationTemp locationTemp = new LocationTemp(DRD_SaveList.this);
            locationTemp.deleteAllLocationTemp(1);
            new AlertDialog.Builder(DRD_SaveList.this)
                    .setTitle("Success")
                    .setMessage("Your feedback has been posted")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
            reloadAllData();
        }
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

}
