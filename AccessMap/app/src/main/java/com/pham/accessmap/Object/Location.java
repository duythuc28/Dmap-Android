package com.pham.accessmap.Object;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.Normalizer;
import java.util.ArrayList;


/**
 * Created by Pham on 25/12/2014.
 */
public class Location {
    public  String address, latitude, longitude, phone, location_title , titleASCII , addressASCII;
    public  int locationID , isBookmark, locationType_ID_Ref;

    SQLiteDatabase db;
    private final Context mContext;
    //private DataHelper dataHelper;


    public Location (Context context)
    {
        this.mContext = context;


    }



    public ArrayList<Location> getAllData()
    {
        ArrayList<Location> result = new ArrayList<Location>();

        db = new DataHelper(mContext).openDataBase();
        Cursor cursor = db.rawQuery("select * from Location order by title ASC",null);
        while (cursor.moveToNext())
        {
            Location location = new Location(mContext);
            location.address = cursor.getString(cursor.getColumnIndex("address"));
            location.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            location.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
            location.location_title  = cursor.getString(cursor.getColumnIndex("title"));
            location.isBookmark = cursor.getInt(cursor.getColumnIndex("isBookmark"));
            location.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
            location.phone = cursor.getString (cursor.getColumnIndex("phone"));
            result.add(location);
        }
        db.close();
        return result;
    }


    public ArrayList<Location> getDataWithDis (int radius )
    {
        ArrayList<Location> result = new ArrayList<Location>();
        return result;
    }

    public Location getDataWithID (int locationID)
    {
        Location location = new Location(mContext);

        try {
            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("select * from Location where locationID ='" + locationID + "'", null);
            while (cursor.moveToNext()) {
                location.address = cursor.getString(cursor.getColumnIndex("address"));
                location.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                location.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                location.location_title = cursor.getString(cursor.getColumnIndex("title"));
                location.isBookmark = cursor.getInt(cursor.getColumnIndex("isBookmark"));
                location.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                location.phone = cursor.getString(cursor.getColumnIndex("phone"));
            }
            db.close();
            return location;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public boolean checkDataExist(int locationID)
    {
        Location location = new Location(mContext);

        try {
            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("select * from Location where locationID ='" + locationID + "'", null);
            while (cursor.moveToNext()) {
                location.address = cursor.getString(cursor.getColumnIndex("address"));
                location.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                location.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                location.location_title = cursor.getString(cursor.getColumnIndex("title"));
                location.isBookmark = cursor.getInt(cursor.getColumnIndex("isBookmark"));
                location.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                location.phone = cursor.getString(cursor.getColumnIndex("phone"));
            }
            if (location.location_title == null)
            {
                db.close();
                return  false;
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

    public void removeBookmark(int locationID)
    {
        try {
            db = new DataHelper(mContext).openDataBase();
            ContentValues cv = new ContentValues();
            cv.put("isBookmark",0);
            db.update("Location", cv, "locationID=?", new String[]{String.valueOf(locationID)});
            db.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void insertLocation(Location location)
    {
        db = new DataHelper(mContext).openDataBase();
            ContentValues cv = new ContentValues();
            cv.put("locationID", location.locationID);
            cv.put("address", location.address);
            cv.put("latitude", location.latitude);
            cv.put("longitude", location.longitude);
            cv.put("phone", location.phone);
            cv.put("title", location.location_title);
            cv.put("isBookmark", 0);
            cv.put("locationType_ID_Ref", location.locationType_ID_Ref);
        if (location_title.contains("Đ") ) {
//            location_title = location_title.replace(location_title.substring(0,location_title.indexOf("Đ")),"D");
//            location_title = location_title.replaceAll("\\p{}","D");
            location_title = location_title.replace("Đ","D");
        }

        if (location_title.contains("đ")) {
            location_title = location_title.replace("đ","d");
        }

        if (location.address.contains("Đ") ) {
//            location_title = location_title.replace(location_title.substring(0,location_title.indexOf("Đ")),"D");
//            location_title = location_title.replaceAll("\\p{}","D");
            location.address = location.address.replace("Đ","D");
        }

        if (location.address.contains("đ")) {
            location.address = location.address.replace("đ","d");
        }


        String asciiTitle = Normalizer.normalize(location_title, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        cv.put("title_ASCII",asciiTitle);
        Log.e("title", asciiTitle);

        String addressAscii = Normalizer.normalize(location.address, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        cv.put("address_ASCII",addressAscii);
        Log.e("address", addressAscii);

            db.insert("Location", null, cv);
        db.close();
    }

    public void editLocation (Location location)
    {
        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        //cv.put("locationID", location.locationID);
        cv.put("address", location.address);
        cv.put("latitude", location.latitude);
        cv.put("longitude", location.longitude);
        cv.put("phone", location.phone);
        cv.put("title", location.location_title);
        cv.put("isBookmark", 0);
        cv.put("locationType_ID_Ref", location.locationType_ID_Ref);
        db.update("Location", cv, "locationID=?", new String[]{String.valueOf(location.locationID)});
        db.close();
    }

    public void editBookmark (int locationID)
    {
        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        cv.put("isBookmark", 1);
        db.update("Location", cv, "locationID=?", new String[]{String.valueOf(locationID)});
        db.close();
    }

    public void deleteLocationwithLatLong (int locationID)
    {
        db = new DataHelper(mContext).openDataBase();
        db.delete("Location","locationID=?",new String[]{String.valueOf(locationID)});
        db.close();
    }

    public void InsertIntoLocation_AccessType(int location_ID_Ref , int accesstype_ID_Ref )
    {
        db = new DataHelper(mContext).openDataBase();
        ContentValues cv = new ContentValues();
        cv.put("location_ID_Ref",location_ID_Ref);
        cv.put("accesstype_ID_Ref",accesstype_ID_Ref);
        db.insert("Location_AccessType",null,cv);
        db.close();
    }

    public void deleteLocation_AccessType(int location_ID_Ref)
    {
        db = new DataHelper(mContext).openDataBase();
        db.delete("Location_Accesstype","location_ID_Ref=?",new String[]{String.valueOf(location_ID_Ref)});
        db.close();
    }

    public ArrayList<Location> getBookmarkDataWithID ()
    {
        ArrayList<Location> result = new ArrayList<Location>();

        db = new DataHelper(mContext).openDataBase();


        try {
            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("select * from Location where isBookmark = '1' order by title ASC", null);
            while (cursor.moveToNext())
            {
                Location location = new Location(mContext);
                location.address = cursor.getString(cursor.getColumnIndex("address"));
                location.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                location.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                location.location_title  = cursor.getString(cursor.getColumnIndex("title"));
                location.isBookmark = cursor.getInt(cursor.getColumnIndex("isBookmark"));
                location.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                location.phone = cursor.getString (cursor.getColumnIndex("phone"));
                result.add(location);
            }
            db.close();
            return result;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public ArrayList<Location> getBookmarkDataWithText (CharSequence text)
    {
        ArrayList<Location> result = new ArrayList<Location>();

        db = new DataHelper(mContext).openDataBase();

        try {
            db = new DataHelper(mContext).openDataBase();
            String newText = ("%"+text+"%");
            String query = "select * from Location where isBookmark = '1' and title like '" + newText + "' order by title ASC";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext())
            {
                Location location = new Location(mContext);
                location.address = cursor.getString(cursor.getColumnIndex("address"));
                location.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                location.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                location.location_title  = cursor.getString(cursor.getColumnIndex("title"));
                location.isBookmark = cursor.getInt(cursor.getColumnIndex("isBookmark"));
                location.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                location.phone = cursor.getString (cursor.getColumnIndex("phone"));
                result.add(location);
            }
            db.close();
            return result;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public ArrayList<Location> getSearchData (CharSequence text)
    {
        ArrayList<Location> result = new ArrayList<Location>();

        db = new DataHelper(mContext).openDataBase();

        try {
            db = new DataHelper(mContext).openDataBase();
            String newText = ("%"+text+"%");
            String query = "select * from Location where address_ASCII like '"+ newText +"' or title_ASCII like '" + newText + "' order by title COLLATE UNICODE ASC";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext())
            {
                Location location = new Location(mContext);
                location.address = cursor.getString(cursor.getColumnIndex("address"));
                location.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                location.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                location.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                location.location_title  = cursor.getString(cursor.getColumnIndex("title"));
                location.isBookmark = cursor.getInt(cursor.getColumnIndex("isBookmark"));
                location.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                location.phone = cursor.getString (cursor.getColumnIndex("phone"));
                result.add(location);
            }
            db.close();
            return result;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

}


