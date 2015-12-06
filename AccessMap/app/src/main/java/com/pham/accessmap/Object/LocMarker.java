package com.pham.accessmap.Object;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Pham on 26/12/2014.
 */
public class LocMarker {
    public  String address, latitude, longitude, phone, location_title , Image;
    public  int locationID , locationType_ID_Ref;

    SQLiteDatabase db;
    private final Context mContext;
    //private DataHelper dataHelper;


    public LocMarker (Context context)
    {
        this.mContext = context;

    }

    public ArrayList<LocMarker> getAllMarker()
    {
        try {
            ArrayList<LocMarker> data = new ArrayList<LocMarker>();

            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("SELECT * FROM Location,LocationType WHERE Location.locationType_ID_Ref = LocationType.locationType_ID", null);
            while (cursor.moveToNext()) {
                LocMarker locMarker = new LocMarker(mContext);
                locMarker.address = cursor.getString(cursor.getColumnIndex("address"));
                locMarker.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                locMarker.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                locMarker.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                locMarker.location_title = cursor.getString(cursor.getColumnIndex("title"));
                locMarker.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                locMarker.Image = cursor.getString(cursor.getColumnIndex("locationImage"));
                locMarker.phone = cursor.getString(cursor.getColumnIndex("phone"));
                data.add(locMarker);
            }
            db.close();
            return data;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }



    public double CalculationByDistance(double startLatitude , double endLatitude , double startLongitude , double endLongitude) {
        int Radius=6371;//radius of earth in Km
        double lat1 = startLatitude;
        double lat2 = endLatitude;
        double lon1 = startLongitude;
        double lon2 = endLongitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;

//        DecimalFormat newFormat = new DecimalFormat("####");
//        kmInDec =  Integer.valueOf(newFormat.format(km));
//        meter=valueResult%1000;
//        meterInDec= Integer.valueOf(newFormat.format(meter));


        return Radius * c;
    }

    public ArrayList<LocMarker> getAllMarkerWithDistance(double radius , double gpsLat , double gpsLong)
    {
        try {
            ArrayList<LocMarker> data = new ArrayList<LocMarker>();
            db = new DataHelper(mContext).openDataBase();
            Cursor cursor = db.rawQuery("SELECT * FROM Location,LocationType WHERE Location.locationType_ID_Ref = LocationType.locationType_ID and LocationType.isCheck = 1", null);
            while (cursor.moveToNext()) {
                LocMarker locMarker = new LocMarker(mContext);
                locMarker.address = cursor.getString(cursor.getColumnIndex("address"));
                locMarker.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                locMarker.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                locMarker.locationID = cursor.getInt(cursor.getColumnIndex("locationID"));
                locMarker.location_title = cursor.getString(cursor.getColumnIndex("title"));
                locMarker.locationType_ID_Ref = cursor.getInt(cursor.getColumnIndex("locationType_ID_Ref"));
                locMarker.Image = cursor.getString(cursor.getColumnIndex("locationImage"));
                locMarker.phone = cursor.getString(cursor.getColumnIndex("phone"));

                double gpsRadius = CalculationByDistance (gpsLat,Double.parseDouble(locMarker.latitude),gpsLong,Double.parseDouble(locMarker.longitude));
                if (gpsRadius < radius) {
                    data.add(locMarker);
                }
            }

            db.close();
            return data;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }

    }

}
