package com.pham.accessmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pham.accessmap.Object.AccessType;
import com.pham.accessmap.Object.AddLocationDialogFragment;
import com.pham.accessmap.Object.GPSTracker;
import com.pham.accessmap.Object.LanguageHelper;
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
import java.util.List;


public class EndUserActivity extends ActionBarActivity implements AddLocationDialogFragment.NoticeDialogListener{

    EditText endUser_locationName, endUser_locationPhone, endUser_locationAdd, endUser_UserName;

    String mUserPhone;
    String mPassword;
    boolean mIsAdmin;
    User mUser;
    ArrayList <Integer> mMeasureTypes;

    Button mMeasureButton;
    int mSavedLocationID = 0;

    public ArrayList<Integer> getMeasureTypes() {
        if (mMeasureTypes == null) {
            mMeasureTypes = new ArrayList<>();
        }
        return mMeasureTypes;
    }


    GPSTracker gpsTracker;
    PostLocation postLocation;
    //ArrayList<Integer> m_select;
    //CharSequence[] test;
    Spinner list_locationType;
    //boolean[] _selections;
    MultiSelectionSpinner spinner;
    ArrayList<String> data1;
    ArrayList<String> data;
    ArrayList <LocationType> mLocationTypes;
    ArrayList <AccessType> mAccessTypes;
    static String uri = "http://www.drdvietnam.org/bandotiepcan/api/post/location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_user);
        getSupportActionBar().setTitle(R.string.title_activity_end_user);
        mMeasureButton = (Button)findViewById(R.id.shareLocation_measure_accessibility_button);
        //
        postLocation = new PostLocation();
        postLocation.Location_AccessType = new ArrayList<Integer>();
        //
        LocationType locationType = new LocationType(this);
        AccessType accessType = new AccessType(this);

        mLocationTypes = locationType.getAllData();
        mAccessTypes  = accessType.getAllData();

        String tLanguageCode = LanguageHelper.getInstance().getAppLanguage(this);
        // Add to Alert Dialog
        if (tLanguageCode.equals(LanguageHelper.VIETNAMESE))
        {
            data1 = accessType.getAllName();
            data = locationType.getNameVn();
        }
        else
        {
            data1 = accessType.getAllName_En();
            data = locationType.getNameEng();
        }


        //test = data1.toArray(new CharSequence[data1.size()]);
        gpsTracker = new GPSTracker(this);


        // Add to Spinner
        //ArrayList<LocationType> data = locationType.getAllData();

        list_locationType = (Spinner) findViewById(R.id.endUser_selectLocationType);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, data);
// Specify the layout to use when the list of choices appears
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        list_locationType.setAdapter(dataAdapter);
        endUser_locationName = (EditText) findViewById(R.id.endUser_locationName);
        endUser_locationAdd = (EditText) findViewById(R.id.endUser_locationAdd);
        endUser_locationPhone = (EditText) findViewById(R.id.endUser_locationPhone);
//        endUser_UserName = (EditText) findViewById(R.id.endUser_UserName);

        // Add to MultiSelection Spinner


    }

    @Override
    protected void onResume() {
        super.onResume();
        String tLanguageCode = LanguageHelper.getInstance().getAppLanguage(this);
        LanguageHelper.getInstance().setAppLanguage(tLanguageCode,this);
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

    public static String POST(String url, PostLocation postLocation) {
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

            jsonArray.put(0, postLocation.latitude);
            jsonArray.put(1, postLocation.longitude);

            JSONArray jsonArray1 = new JSONArray();
            for (int items : postLocation.Location_AccessType) {
                jsonArray1.put(items);
            }
            jsonArray.put(2, jsonArray1);

            jsonArray.put(3, postLocation.location_Type);
            jsonArray.put(4, postLocation.postLocationName);
            jsonArray.put(5, postLocation.postLocationAddress);
            jsonArray.put(6, postLocation.postLocationPhone);
            jsonArray.put(7, postLocation.userPhone);
            jsonObject.accumulate("Location", jsonArray);


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
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public void onClick_measureAccessibility(View view) {
        Intent intent = new Intent(this, MeasureAccessibilityActivity.class);
        int requestCode = 2;
        intent.putIntegerArrayListExtra("LIST_MEASURE_PLACES",getMeasureTypes());
        startActivityForResult(intent, requestCode);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], postLocation);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
//            endUser_UserName.setText("");
            endUser_locationPhone.setText("");
            endUser_locationName.setText("");
            endUser_locationAdd.setText("");
            postLocation.Location_AccessType.clear();
            mMeasureTypes.clear();
            mMeasureButton.setText(getResources().getString(R.string.share_location_select_button));

            LocationTemp locationTemp = new LocationTemp(EndUserActivity.this);
            locationTemp.deleteLocationTemp(mSavedLocationID);
            //m_select.clear();
            //_selections = null;
            new AlertDialog.Builder(EndUserActivity.this)
                    .setTitle(getResources().getString(R.string.alert_success_title))
                    .setMessage(getResources().getString(R.string.dialog_shared_success))
                    .setPositiveButton(getResources().getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
        }
    }

    private class HttpAdminPOSTAsyncTask extends AsyncTask <String , Void , String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], postLocation , mUser);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            endUser_locationPhone.setText("");
            endUser_locationName.setText("");
            endUser_locationAdd.setText("");
            postLocation.Location_AccessType.clear();
            mMeasureTypes.clear();
            mMeasureButton.setText(getResources().getString(R.string.share_location_select_button));

            LocationTemp locationTemp = new LocationTemp(EndUserActivity.this);
            locationTemp.deleteLocationTemp(mSavedLocationID);
            //m_select.clear();
            //_selections = null;
            new AlertDialog.Builder(EndUserActivity.this)
                    .setTitle(getResources().getString(R.string.alert_success_title))
                    .setMessage(getResources().getString(R.string.dialog_shared_success))
                    .setPositiveButton(getResources().getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .show();
        }
    }

    private boolean validate() {
        if (mIsAdmin) {
            if (endUser_locationAdd.getText().toString().equals(""))
                return false;
            else if (endUser_locationName.getText().toString().equals(""))
                return false;
            else if (endUser_locationPhone.getText().toString().equals(""))
                return false;
            else if (mUserPhone == null)
                return false;
            else if (gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0) {
                return false;
            } else if (getMeasureTypes().size() == 0) {
                return false;
            } else if (mPassword == null) {
              return false;
            } else
                return true;
        } else {
            if (endUser_locationAdd.getText().toString().equals(""))
                return false;
            else if (endUser_locationName.getText().toString().equals(""))
                return false;
            else if (endUser_locationPhone.getText().toString().equals(""))
                return false;
            else if (mUserPhone == null)
                return false;
            else if (gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0) {
                return false;
            } else if (getMeasureTypes().size() == 0) {
                return false;
            } else
                return true;
        }
    }

    public boolean saveValidate () {
        if (endUser_locationAdd.getText().toString().equals(""))
            return false;
        else if (endUser_locationName.getText().toString().equals(""))
            return false;
        else if (endUser_locationPhone.getText().toString().equals(""))
            return false;
        else if (gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0) {
            return false;
        } else if (getMeasureTypes().size() == 0) {
            return false;
        } else
            return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showUserList) {
            Intent intent = new Intent(this, EndUser_SaveList.class);
            int requestCode = 1;
            startActivityForResult(intent, requestCode);
            //startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    int locationID = bundle.getInt("locationID", 0);
                    mSavedLocationID = locationID;
                    reloadInformation(locationID);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mMeasureTypes = bundle.getIntegerArrayList("LIST_SELECTED_CELL");
                    if (getMeasureTypes().size() > 0) {
                        String tButtonType = "";
                        for (int i : getMeasureTypes()) {
                            if (getMeasureTypes().indexOf(i) > 0) {
                                tButtonType = tButtonType +","+(i+1);
                            } else  {
                                tButtonType = tButtonType + (i+1);
                            }
                        }
                        mMeasureButton.setText(tButtonType);
                    } else {
                        mMeasureButton.setText(getResources().getString(R.string.share_location_select_button));
                    }
                }
                break;
        }
    }

    public void reloadInformation(int locationID) {
        LocationTemp locationTemp = new LocationTemp(this);
        LocationTemp locationTemp1 = locationTemp.getAllDataWithID(0, locationID);
//        endUser_UserName.setText(locationTemp1.userPhone);
        endUser_locationPhone.setText(locationTemp1.locationPhone);
        endUser_locationName.setText(locationTemp1.locationName);
        endUser_locationAdd.setText(locationTemp1.locationAddress);
        list_locationType.setSelection(Integer.parseInt(locationTemp1.locationType));
        getMeasureTypes().clear();
//        for (int i = 0; i < locationTemp1.accessType.length(); i++) {
////            postLocation.Location_AccessType.add(locationTemp.accessType.indexOf(i));
//            Log.e("tag",String.valueOf(locationTemp1.accessType.indexOf(i)));
//            getMeasureTypes().add(locationTemp1.accessType.indexOf(i));
//        }

        for (char index : locationTemp1.accessType.toCharArray()) {
            Log.e("tag",String.valueOf(index));
            getMeasureTypes().add(Integer.parseInt(String.valueOf(index)));
        }

        //TODO: Work around code

        if (getMeasureTypes().size() > 0) {
            String tButtonType = "";
            for (int i : getMeasureTypes()) {
                if (getMeasureTypes().indexOf(i) > 0) {
                    tButtonType = tButtonType +","+(i+1);
                } else  {
                    tButtonType = tButtonType + (i+1);
                }
            }
            mMeasureButton.setText(tButtonType);
        } else {
            mMeasureButton.setText(getResources().getString(R.string.share_location_select_button));
        }
    }

    private void setPostLocation (PostLocation sPostLocation, boolean isAdmin){
        sPostLocation.postLocationName = (endUser_locationName.getText().toString());
        // FIXME: Fix this issue
        LocationType locationType = mLocationTypes.get(list_locationType.getSelectedItemPosition());
//        int locationTypeID = locationType.getLocationTypeByID(list_locationType.getSelectedItemPosition());
        sPostLocation.location_Type = locationType.locationType_ID;
        sPostLocation.latitude = String.valueOf(gpsTracker.getLatitude());
        sPostLocation.longitude = String.valueOf(gpsTracker.getLongitude());
        for (int i : getMeasureTypes()) {
//            sPostLocation.Location_AccessType.add(i);
            AccessType accessType = mAccessTypes.get(i);
            sPostLocation.Location_AccessType.add(accessType.accessType_ID);
        }
//        for (int i = 0; i < spinner.getSelectedIndicies().size(); i++) {
//            sPostLocation.Location_AccessType.add(spinner.getSelectedIndicies().indexOf(i) + 2);
//        }
        if (isAdmin) {
            mUser = new User();
            mUser.userName = mUserPhone;
            mUser.password = mPassword;
        } else {
            sPostLocation.userPhone = mUserPhone;
        }
        //postLocation.Location_AccessType.add(1);
        sPostLocation.postLocationAddress = endUser_locationAdd.getText().toString();
        sPostLocation.postLocationPhone = endUser_locationPhone.getText().toString();
    }


    public void onClick_enPost(View v) {
        AddLocationDialogFragment addLocationDialogFragment = new AddLocationDialogFragment();
        addLocationDialogFragment.show(getSupportFragmentManager(),"AddLocationDialogFragment");
    }

    public void onDialogPostButtonClick(AddLocationDialogFragment dialog) {
        if (dialog != null) {
            if (validate()) {
                setPostLocation(this.postLocation,this.mIsAdmin);
//                new HttpAsyncTask().execute(uri);
                if (this.mIsAdmin) {
                    new HttpAdminPOSTAsyncTask().execute(uri);
                } else {
                    new HttpAsyncTask().execute(uri);
                }
            } else {
                new AlertDialog.Builder(EndUserActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_error_title))
                    .setMessage(getResources().getString(R.string.dialog_error_description))
                        .setPositiveButton(getResources().getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                    .show();
            }
        }
    }

    public void onDialogCancelButtonClick(AddLocationDialogFragment dialog) {
        if (dialog != null) {
            dialog.setCancelable(true);
        }
    }

    public void onUserCheckedChanged (boolean isAdmin) {
        this.mIsAdmin = isAdmin;
    }

    public void onUserPhoneChanged (String userPhone) {
        if (userPhone != null) {
            Log.e("User phone",userPhone);
            mUserPhone = userPhone;
        }
    }

    public void onPasswordChanged (String password) {
        if (password != null) {
            Log.e("Password",password);
            mPassword = password;
        }
    }

    public void onClick_enSave(View v) {
        if (saveValidate()) {
            LocationTemp locationTemp = new LocationTemp(this);
            locationTemp.latitute = String.valueOf(gpsTracker.getLatitude());
            locationTemp.longitude = String.valueOf(gpsTracker.getLongitude());
            locationTemp.locationPhone = endUser_locationPhone.getText().toString();
            locationTemp.locationName = endUser_locationName.getText().toString();
//            locationTemp.userPhone = endUser_UserName.getText().toString();
            locationTemp.locationAddress = endUser_locationAdd.getText().toString();
//            LocationType tLocationType = mLocationTypes.get(list_locationType.getSelectedItemPosition());
            locationTemp.locationType = String.valueOf(list_locationType.getSelectedItemPosition());
            locationTemp.isUser = false;


//        int locationTypeID = locationType.getLocationTypeByID(list_locationType.getSelectedItemPosition());
//            sPostLocation.location_Type = locationType.locationType_ID;

            String tAccessType = "";
            for (int i : getMeasureTypes()) {
//            sPostLocation.Location_AccessType.add(i);
//                AccessType accessType = mAccessTypes.get(i);
                 tAccessType = tAccessType + String.valueOf(i);
            }
            locationTemp.accessType = tAccessType;
//            for (int i = 0; i < spinner.getSelectedIndicies().size(); i++) {
//                locationTemp.accessType = String.valueOf(spinner.getSelectedIndicies().indexOf(i) + 2);
//            }


            if (locationTemp.getLocationID(endUser_locationName.getText().toString()) != 0) {
                locationTemp.editLocation(locationTemp);
            } else {
                locationTemp.insertLocation(locationTemp);
            }

//            endUser_UserName.setText("");
            endUser_locationPhone.setText("");
            endUser_locationName.setText("");
            endUser_locationAdd.setText("");
            postLocation.Location_AccessType.clear();
            mMeasureTypes.clear();
            mMeasureButton.setText(getResources().getString(R.string.share_location_select_button));

            new AlertDialog.Builder(EndUserActivity.this)
                    .setTitle(getString(R.string.alert_success_title))
                    .setMessage(getString(R.string.alert_save_data))
                    .setPositiveButton(getResources().getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
        } else {
            new AlertDialog.Builder(EndUserActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_error_title))
                    .setMessage(getResources().getString(R.string.dialog_error_description))
                    .setPositiveButton(getResources().getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .show();
        }
    }
}
