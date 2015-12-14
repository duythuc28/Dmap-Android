package com.pham.accessmap.Object;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pham on 25/12/2014.
 */
public class Download  {

    static String API_GET_LOCATION = "http://www.drdvietnam.org/bandotiepcan/api/get/locations?Time=";
    static String API_GET_LOCATIONTYPE = "http://www.drdvietnam.org/bandotiepcan/api/get/location_types?Time=";
    static String API_GET_FEEDBACK_BY_ID = "http://www.drdvietnam.org/bandotiepcan/api/get/feedback?Locationid=";
    static String API_GET_ACCESSTYPE = "http://www.drdvietnam.org/bandotiepcan/api/get/access_types?Time=";

    private Context mcontext;
    public static final String PREFS_NAME = "MyPref";


    public  Download (Context context)
    {
        this.mcontext = context;
    }

    public ArrayList<Feedback> getFeedbackFromLocationID (int locationID)
    {
        ArrayList<Feedback> result = new ArrayList<Feedback>();
        String uri = API_GET_FEEDBACK_BY_ID + locationID;
        String json = GetService.requestWebService(uri);
        //String json  = new HttpAsyncTask().execute(uri).toString();
        try {
            //JSONObject comment = new JSONObject(json);
            JSONArray comment = new JSONArray(json);
            for (int i = 0 ; i < comment.length() ; i++)
            {
                Feedback feedback = new Feedback();
                JSONObject object = comment.getJSONObject(i);
                feedback.content = object.getString("Content");
                feedback.name = object.getString("name");
                result.add(feedback);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }


    public void getLocationType() {

        //ServiceHandler sh = new ServiceHandler();
        SharedPreferences prefs = mcontext.getSharedPreferences(PREFS_NAME,0);
        String uri;
        String json;

        int time = prefs.getInt("time",0);
        if (time == 0)
        {
            uri = API_GET_LOCATIONTYPE + time;
            json = GetService.requestWebService(uri);
            //json = new HttpAsyncTask().execute(uri).toString();
        }
        else
        {
            uri = API_GET_LOCATIONTYPE + time;
            json = GetService.requestWebService(uri);
            //json = new HttpAsyncTask().execute(uri).toString();
        }

        try {

            JSONObject obj_lt = new JSONObject(json);
            JSONArray locationT = obj_lt.getJSONArray("LocationType");
            for (int j = 0; j < locationT.length(); j++) {
                JSONObject locationTt = locationT.getJSONObject(j);
                LocationType locationType = new LocationType(mcontext);

                locationType.locationType_ID = locationTt.getInt("LocationTypeID");
                locationType.locationName = locationTt.getString("Name");
                locationType.locationName_en = locationTt.getString("Name_en");
                locationType.locationImage = locationTt.getString("Image");

                if (locationType.checkDataExist(locationType.locationType_ID) == false)
                {
                    locationType.insertLocationType(locationType);
                }
                else locationType.editLocationType(locationType);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getAccessibilityType() {

        //ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response

        SharedPreferences prefs = mcontext.getSharedPreferences(PREFS_NAME,0);
        String uri;
        String json;
        int time = prefs.getInt("time",0);
        if (time == 0)
        {
            uri = API_GET_ACCESSTYPE + time;
            //json = new HttpAsyncTask().execute(uri).toString();
            json = GetService.requestWebService(uri);
        }
        else
        {
            uri = API_GET_ACCESSTYPE + time;
            //json = new HttpAsyncTask().execute(uri).toString();
            json = GetService.requestWebService(uri);
        }


        try {
            JSONObject obj = new JSONObject(json);
            //JSONArray result = new JSONArray(json);

            JSONArray accessTypes = obj.getJSONArray("AccessibilityType");
            for (int i = 0; i < accessTypes.length(); i++) {
                JSONObject accessType = accessTypes.getJSONObject(i);
                AccessType storeAccessType = new AccessType(mcontext);
                storeAccessType.accessType_ID = accessType.getInt("ACType_ID");
                storeAccessType.accessName = accessType.getString("Name");
                storeAccessType.accessDes  = accessType.getString("Description");
                storeAccessType.Image  = accessType.getString("Image");
                storeAccessType.accessName_en = accessType.getString("Name_en");
                //Store to SQLite
                if (storeAccessType.checkDataExist(storeAccessType.accessType_ID) == false )
                {
                    storeAccessType.insertData(storeAccessType);
                }
                else storeAccessType.editData(storeAccessType);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getLocation() {
        //ServiceHandler sh = new ServiceHandler();
        SharedPreferences prefs = mcontext.getSharedPreferences(PREFS_NAME,0);
        String uri;
        String json;
        int time = prefs.getInt("time", 0);
        if (time == 0) {
            uri = API_GET_LOCATION + time;
            //json = new HttpAsyncTask().execute(uri).toString();
            json = GetService.requestWebService(uri);
        } else {
            uri = API_GET_LOCATION + time;
            //json = new HttpAsyncTask().execute(uri).toString();
            json = GetService.requestWebService(uri);
        }


        try {

            JSONObject obj_l = new JSONObject(json);
            int getTime = obj_l.getInt("Time");
            JSONArray lct = obj_l.getJSONArray("Location");

            for (int j = 0; j < lct.length(); j++) {
                JSONObject loc = lct.getJSONObject(j);
                com.pham.accessmap.Object.Location location = new com.pham.accessmap.Object.Location(mcontext);
                location.locationID = loc.getInt("LocationID");
                location.latitude = loc.getString("Latitude");
                location.longitude = loc.getString("Longitude");
                location.location_title = loc.getString("Title");
                location.address = loc.getString("Address");
                location.locationType_ID_Ref = loc.getInt("LocationType");
                location.phone = loc.getString("Phone");
                int isActive = loc.getInt("isActive");

                JSONArray acs = loc.getJSONArray("AccessType");
                if (isActive == 0) {
                    location.deleteLocationwithLatLong(location.locationID);
                } else {
                    if (location.checkDataExist(location.locationID) == false) {
                        location.insertLocation(location);

                        for (int k = 0; k < acs.length(); k++) {
                            int accessType_ID = acs.getInt(k);
                            location.InsertIntoLocation_AccessType(location.locationID, accessType_ID);
                        }
                    } else {
                        location.editLocation(location);
                        location.deleteLocation_AccessType(location.locationID);
                        for (int k = 0; k < acs.length(); k++) {
                            int accessType_ID = acs.getInt(k);
                            location.InsertIntoLocation_AccessType(location.locationID, accessType_ID);
                        }

                    }
                }

            }
            SharedPreferences.Editor editor = mcontext.getSharedPreferences(PREFS_NAME,0).edit();
            editor.putInt("time", getTime);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
