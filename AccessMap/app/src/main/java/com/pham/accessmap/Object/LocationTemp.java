package com.pham.accessmap.Object;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by mc976 on 1/19/15.
 */
public class LocationTemp {
    public String locationName , locationPhone , locationAddress, userPhone , accessType , locationType , longitude , latitute;
    public boolean isUser;
    public int locationID;
    SQLiteDatabase db;
    private final Context mContext;
    public LocationTemp (Context context)
    {
        this.mContext = context;
    }

    public void insertLocation(LocationTemp location)
    {
        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        cv.put("accesstype", location.accessType);
        cv.put("address", location.locationAddress);
        cv.put("latitude", location.latitute);
        cv.put("longitude", location.longitude);
        cv.put("phone", location.locationPhone);
        cv.put("title", location.locationName);
        //int user = (isUser) ? 1:0;
        cv.put("isUser", (isUser)? 1:0);
        cv.put("locationType", location.locationType);
        cv.put("userphone",location.userPhone);
        db.insert("LocationTemp", null, cv);
        db.close();
    }

    public void editLocation (LocationTemp location)
    {

        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        cv.put("accesstype", location.accessType);
        cv.put("address", location.locationAddress);
        cv.put("latitude", location.latitute);
        cv.put("longitude", location.longitude);
        cv.put("phone", location.locationPhone);
        cv.put("title", location.locationName);
        cv.put("isUser", (isUser) ? 1 : 0);
        cv.put("locationType", location.locationType);
        cv.put("userphone",location.userPhone);
        db.update("LocationTemp", cv, "id=?", new String[]{String.valueOf(location.locationID)});
        db.close();
    }

    public void deleteLocationTemp (int locationTempID)
    {
        db = new DataHelper(mContext).openDataBase();
        db.delete("LocationTemp","id=?",new String[]{String.valueOf(locationTempID)});
        db.close();
    }

    public void deleteAllLocationTemp (int isUser)
    {
        db = new DataHelper(mContext).openDataBase();
        db.delete("LocationTemp","isUser=?",new String[]{String.valueOf(isUser)});
        db.close();
    }

    public int getLocationID (String locationName)
    {
        LocationTemp locationTemp = new LocationTemp(mContext);
        int locationTempID = 0;
        try {
            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("select * from LocationTemp where title ='" + locationName + "'", null);
            while (cursor.moveToNext())
            {

                locationTempID = cursor.getInt(cursor.getColumnIndex("id"));
            }
            db.close();
            return locationTempID;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    public ArrayList<LocationTemp> getAllData(int isUser)
    {
        ArrayList<LocationTemp> result = new ArrayList<LocationTemp>();

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from LocationTemp where isUser ='"+ isUser + "'",null);
        while (cursor.moveToNext())
        {
            LocationTemp location = new LocationTemp(mContext);
            location.locationAddress = cursor.getString(cursor.getColumnIndex("address"));
            location.latitute = cursor.getString(cursor.getColumnIndex("latitude"));
            location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            location.locationID = cursor.getInt(cursor.getColumnIndex("id"));
            location.locationName  = cursor.getString(cursor.getColumnIndex("title"));
            location.accessType = cursor.getString(cursor.getColumnIndex("accesstype"));
            location.locationType = cursor.getString(cursor.getColumnIndex("locationType"));
            location.locationPhone = cursor.getString (cursor.getColumnIndex("phone"));
            result.add(location);
        }
        db.close();
        return result;
    }

    public LocationTemp getAllDataWithID(int isUser,int locationID)
    {
        LocationTemp result = new LocationTemp(mContext);

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from LocationTemp where isUser ='"+ isUser + "' and id ='" + locationID +"'" ,null);
        while (cursor.moveToNext())
        {

            result.locationAddress = cursor.getString(cursor.getColumnIndex("address"));
            result.latitute = cursor.getString(cursor.getColumnIndex("latitude"));
            result.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            result.locationID = cursor.getInt(cursor.getColumnIndex("id"));
            result.locationName  = cursor.getString(cursor.getColumnIndex("title"));
            result.accessType = cursor.getString(cursor.getColumnIndex("accesstype"));
            result.locationType = cursor.getString(cursor.getColumnIndex("locationType"));
            result.locationPhone = cursor.getString (cursor.getColumnIndex("phone"));

        }
        db.close();
        return result;
    }




}
