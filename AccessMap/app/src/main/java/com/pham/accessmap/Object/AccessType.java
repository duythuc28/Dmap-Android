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
public class AccessType {

    public int accessType_ID;
    public String accessName, accessName_en, accessDes,Image;

    SQLiteDatabase db;
    private final Context mContext;
    //private DataHelper dataHelper;


    public AccessType (Context context)
    {
        this.mContext = context;


    }

    public ArrayList<AccessType> getAllData()
    {

        ArrayList<AccessType> data = new ArrayList<AccessType>();

        db = new DataHelper(mContext).openDataBase();
        SharedPreferences preferences = mContext.getSharedPreferences("MyPref",mContext.MODE_PRIVATE);
        boolean language = preferences.getBoolean("language", false);
        String query = "";
        if (language == true)
        {
            query = "select * from AccessType order by accessName_en ASC";
        }
        else
        {
            query = "select * from AccessType order by accessName ASC";
        }
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext())
        {
            AccessType accessType = new AccessType(mContext);
            accessType.accessType_ID = cursor.getInt(cursor.getColumnIndex("accessType_ID"));
            accessType.accessName = cursor.getString(cursor.getColumnIndex("accessName"));
            accessType.accessName_en = cursor.getString(cursor.getColumnIndex("accessName_en"));
            accessType.accessDes = cursor.getString (cursor.getColumnIndex("accessDes"));
            accessType.Image = cursor.getString (cursor.getColumnIndex("Image"));
            data.add(accessType);
        }
        db.close();
        return data;
    }

    public ArrayList<String> getAllName_En()
    {

        ArrayList<String> data = new ArrayList<String>();

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from AccessType order by accessName_en ASC",null);
        while (cursor.moveToNext())
        {
            AccessType accessType = new AccessType(mContext);

            accessType.accessName_en = cursor.getString(cursor.getColumnIndex("accessName_en"));

            data.add(accessType.accessName_en);
        }
        db.close();
        return data;
    }

    public ArrayList<String> getAllName()
    {
        ArrayList<String> data = new ArrayList<String>();

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from AccessType order by accessName ASC",null);
        while (cursor.moveToNext())
        {
            AccessType accessType = new AccessType(mContext);

            accessType.accessName_en = cursor.getString(cursor.getColumnIndex("accessName"));

            data.add(accessType.accessName_en);
        }
        db.close();
        return data;
    }

    public void insertData(AccessType accessType)
    {

        try {
            db = new DataHelper(mContext).openDataBase();
            ContentValues cv = new ContentValues();
            cv.put("accessType_ID", accessType.accessType_ID);
            cv.put("accessName", accessType.accessName);
            cv.put("accessName_en", accessType.accessName_en);
            cv.put("accessDes", accessType.accessDes);
            cv.put("Image", accessType.Image);
            db.insert("AccessType", null, cv);
            db.close();
        }
        catch (Exception ex)
        { ex.printStackTrace();}
    }

    public  void editData (AccessType accessType)
    {

        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        //cv.put("accessType_ID",accessType.accessType_ID);
        cv.put("accessName",accessType.accessName);
        cv.put("accessName_en",accessType.accessName_en);
        cv.put("accessDes",accessType.accessDes);
        cv.put("Image",accessType.Image);
        db.update("AccessType", cv, "accessType_ID=?", new String[]{String.valueOf(accessType.accessType_ID)});
        db.close();
    }

    public AccessType getDatawithID (int accessType_ID)
    {
        try {
            db = new DataHelper(mContext).openDataBase();
            AccessType accessType = new AccessType(mContext);
            Cursor cursor = db.rawQuery("select * from AccessType where accessType_ID = '" + accessType_ID + "'", null);
            while (cursor.moveToNext()) {
                accessType.accessType_ID = cursor.getInt(cursor.getColumnIndex("accessType_ID"));
                accessType.accessName_en = cursor.getString(cursor.getColumnIndex("accessName_en"));
                accessType.accessName = cursor.getString(cursor.getColumnIndex("accessName"));
                accessType.accessDes = cursor.getString(cursor.getColumnIndex("accessDes"));
                accessType.Image = cursor.getString(cursor.getColumnIndex("Image"));
            }
            db.close();
            return accessType;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public boolean checkDataExist (int accessType_ID)
    {
        try {
            db = new DataHelper(mContext).openDataBase();
            AccessType accessType = new AccessType(mContext);
            Cursor cursor = db.rawQuery("select * from AccessType where accessType_ID = '" + accessType_ID + "'", null);
            while (cursor.moveToNext()) {

                accessType.accessType_ID = cursor.getInt(cursor.getColumnIndex("accessType_ID"));
                accessType.accessName_en = cursor.getString(cursor.getColumnIndex("accessName_en"));
                accessType.accessName = cursor.getString(cursor.getColumnIndex("accessName"));
                accessType.accessDes = cursor.getString(cursor.getColumnIndex("accessDes"));
                accessType.Image = cursor.getString(cursor.getColumnIndex("Image"));
            }

            if (accessType.accessName == null)
            {
                db.close();
                return false;
            }
            else {
                db.close();
                return true;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public ArrayList<AccessType> getAccessTypeByLocationID (int locationID)
    {
        ArrayList<AccessType> data = new ArrayList<AccessType>();
        db = new DataHelper(mContext).openDataBase();
        SharedPreferences preferences = mContext.getSharedPreferences("MyPref",mContext.MODE_PRIVATE);
        boolean language = preferences.getBoolean("language", false);
        String query = "";
        if (language == true)
        {
            query = "Select * from  Location_AccessType , AccessType" +
                    " Where Location_AccessType.accesstype_ID_Ref = AccessType.accessType_ID " +
                    "and Location_AccessType.location_ID_Ref ='" +locationID+"' order by accessName_en ASC";
        }
        else
        {
            query = "Select * from  Location_AccessType , AccessType" +
                    " Where Location_AccessType.accesstype_ID_Ref = AccessType.accessType_ID " +
                    "and Location_AccessType.location_ID_Ref ='" +locationID+"' order by accessName ASC";
        }

        Cursor cursor = db.rawQuery(query,null);

        //Cursor cursor = db.rawQuery("Select * from Location_AccessType where Location_AccessType.location_ID_Ref =15",null);
        while (cursor.moveToNext())
        {
            AccessType accessType = new AccessType(mContext);

            accessType.accessName = cursor.getString(cursor.getColumnIndex("accessName"));
            accessType.accessName_en = cursor.getString(cursor.getColumnIndex("accessName_en"));
            accessType.Image = cursor.getString(cursor.getColumnIndex("Image"));

            accessType.accessType_ID = cursor.getInt(cursor.getColumnIndex("accesstype_ID_Ref"));
            data.add(accessType);
        }
        db.close();
        return data;
    }


}
