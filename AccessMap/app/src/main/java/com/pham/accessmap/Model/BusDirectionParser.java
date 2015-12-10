package com.pham.accessmap.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mc976 on 12/10/15.
 */
public class BusDirectionParser extends DirectionParser {
    public boolean mIsBus;

    private String mBusNumber;

    public String getmBusNumber() {
        return mBusNumber;
    }

    public void setmBusNumber (String busNumber) {
        mBusNumber = busNumber;
    }

    private LatLng mStartLocation;

    public void setmStartLocation(LatLng mStartLocation) {
        this.mStartLocation = mStartLocation;
    }

    public LatLng getmStartLocation() {
        return mStartLocation;
    }
}
