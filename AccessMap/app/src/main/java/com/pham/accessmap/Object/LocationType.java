package com.pham.accessmap.Object;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Pham on 25/12/2014.
 */
public class LocationType {

    public int locationType_ID, isCheck;
    public String locationImage , locationName , locationName_en;

    SQLiteDatabase db;
    private final Context mContext;
    //private DataHelper dataHelper;

    public LocationType (Context context)
    {
        this.mContext = context;
    }

    public ArrayList<LocationType> getAllData()
    {
        ArrayList<LocationType> data = new ArrayList<LocationType>();

        db = new DataHelper(mContext).openDataBase();
        SharedPreferences preferences = mContext.getSharedPreferences("MyPref",mContext.MODE_PRIVATE);
        boolean language = preferences.getBoolean("language", false);
        String query = "";
        if (language == true)
        {
            query = "select * from LocationType order by locationName_en ASC";
        }
        else
        {
            query = "select * from LocationType order by locationName ASC";
        }
        Cursor cursor = db.rawQuery(query,null);
        //Cursor cursor = db.rawQuery("select * from LocationType",null);
        while(cursor.moveToNext())
        {
            LocationType locationType = new LocationType(mContext);
            locationType.locationName = cursor.getString(cursor.getColumnIndex("locationName"));
            locationType.locationType_ID = cursor.getInt (cursor.getColumnIndex("locationType_ID"));
            locationType.locationImage = cursor.getString(cursor.getColumnIndex("locationImage"));
            locationType.locationName_en = cursor.getString(cursor.getColumnIndex("locationName_en"));
            locationType.isCheck = cursor.getInt(cursor.getColumnIndex("isCheck"));
            data.add(locationType);
        }
        db.close();
        return data;
    }

    public ArrayList<String> getNameEng()
    {
        ArrayList<String> data = new ArrayList<String>();

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from LocationType order by locationName_en ASC",null);
        while(cursor.moveToNext())
        {
            LocationType locationType = new LocationType(mContext);
            locationType.locationName_en = cursor.getString(cursor.getColumnIndex("locationName_en"));

            data.add(locationType.locationName_en);
        }
        db.close();
        return data;
    }

    public ArrayList<String> getNameVn()
    {
        ArrayList<String> data = new ArrayList<String>();

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from LocationType order by locationName ASC",null);
        while(cursor.moveToNext())
        {
            LocationType locationType = new LocationType(mContext);
            locationType.locationName_en = cursor.getString(cursor.getColumnIndex("locationName"));

            data.add(locationType.locationName_en);
        }
        db.close();
        return data;
    }



    public void insertLocationType(LocationType locationType)
    {
        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        cv.put("locationType_ID",locationType.locationType_ID);
        cv.put("locationName_en",locationType.locationName_en);
        cv.put("locationName",locationType.locationName);
        cv.put("locationImage",locationType.locationImage);
        cv.put("isCheck",1);
        db.insert("LocationType",null,cv);
        db.close();
    }

    public void editLocationType(LocationType locationType)
    {
        try {
        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        //cv.put("locationType_ID",locationType.locationType_ID);
        cv.put("locationName_en",locationType.locationName_en);
        cv.put("locationName",locationType.locationName);
        cv.put("locationImage",locationType.locationImage);
        cv.put("isCheck",1);
        db.update("LocationType", cv, "locationType_ID=?", new String[]{String.valueOf(locationType_ID)});
        db.close();
        }
        catch (Exception ex) {
        ex.printStackTrace();
        }
    }
    public void checkLocationType (int locationType_ID)
    {
        try {

            db = new DataHelper(mContext).openDataBase();
            ContentValues cv = new ContentValues();
            cv.put("isCheck",1);
            db.update("LocationType", cv, "locationType_ID=?", new String[]{String.valueOf(locationType_ID)});
            db.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void uncheckLocationType(int locationType_ID)
    {
        try {
            db = new DataHelper(mContext).openDataBase();
            ContentValues cv = new ContentValues();
            cv.put("isCheck",0);
            db.update("LocationType", cv, "locationType_ID=?", new String[]{String.valueOf(locationType_ID)});
            db.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public LocationType getLocationTypeByID (int locationType_ID)
    {
        LocationType locationType = new LocationType(mContext);
        try {

            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("Select * from LocationType where locationType_ID ='" + locationType_ID + "'", null);
            while (cursor.moveToNext()) {
                locationType.locationName = cursor.getString(cursor.getColumnIndex("locationName"));
                locationType.locationType_ID = cursor.getInt(cursor.getColumnIndex("locationType_ID"));
                locationType.locationImage = cursor.getString(cursor.getColumnIndex("locationImage"));
                locationType.locationName_en = cursor.getString(cursor.getColumnIndex("locationName_en"));
                locationType.isCheck = cursor.getInt(cursor.getColumnIndex("isCheck"));
            }
            db.close();
            return locationType;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public boolean checkDataExist(int locationType_ID)
    {
        LocationType locationType = new LocationType(mContext);
        try {


            Cursor cursor = db.rawQuery("Select * from LocationType where locationType_ID ='" + locationType_ID + "'", null);
            while (cursor.moveToNext()) {
                locationType.locationName = cursor.getString(cursor.getColumnIndex("locationName"));
                locationType.locationType_ID = cursor.getInt(cursor.getColumnIndex("locationType_ID"));
                locationType.locationImage = cursor.getString(cursor.getColumnIndex("locationImage"));
                locationType.locationName_en = cursor.getString(cursor.getColumnIndex("locationName_en"));
                locationType.isCheck = cursor.getInt(cursor.getColumnIndex("isCheck"));
            }
            if (locationType.locationName == null) {
                db.close();
                return false;
            }
            else {db.close();return true;}
        }
        catch (Exception ex)
        {
            return false;
        }
    }

}
