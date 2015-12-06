package com.pham.accessmap.Object;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.pham.accessmap.R;

import java.util.ArrayList;

/**
 * Created by mc976 on 1/11/15.
 */
public class LocationType_Adapter extends ArrayAdapter<LocationType> {

    private final ArrayList<LocationType> locationTypes;
    private final Context context;
    public ArrayList<Integer> checkedList;
    public LocationType_Adapter (Context context , ArrayList<LocationType>locationTypes)
    {
        super (context , R.layout.locationtype_cell, locationTypes);
        this.context = context;
        this.locationTypes = locationTypes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.locationtype_cell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.locationType_name);
        //TextView tView1 = (TextView)rowView.findViewById(R.id.guide_accessDes);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.locationType_Image);

        SharedPreferences preferences = context.getSharedPreferences("MyPref",context.MODE_PRIVATE);
        boolean language = preferences.getBoolean("language", false);
        if (language == true)
        {
            textView.setText(locationTypes.get(position).locationName_en);
        }
        else
        {
            textView.setText(locationTypes.get(position).locationName);
        }

        Switch switch_location = (Switch) rowView.findViewById(R.id.switch_locationType);
        switch_location.setChecked((locationTypes.get(position).isCheck == 1 ));
        checkedList = new ArrayList<Integer>();

        final LocationType locationType = new LocationType(context);
        ArrayList<LocationType> data = new ArrayList<LocationType>();
        data = locationType.getAllData();
        for (LocationType locType : data)
        {
            if (locType.isCheck == 1)
            {
                checkedList.add(locType.locationType_ID);
                //Log.v("check list....",String.valueOf(locType.locationType_ID));
            }
        }

        //final ArrayList<LocationType> finalData = data;
        switch_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    checkedList.add(locationTypes.get(position).locationType_ID);
                    //Log.v("cell turn on",String.valueOf(locationTypes.get(position).locationName_en));

                }
                else
                {
                    for (int i : checkedList)
                    {
                        if (i == locationTypes.get(position).locationType_ID)
                        {
                            checkedList.remove(checkedList.indexOf(i));
                            //Log.v("check list :",String.valueOf(checkedList.indexOf(i)));
                            //Log.v("cell turn off",String.valueOf(locationTypes.get(position).locationName_en));
                            //Log.v("cell turn off",String.valueOf(locationTypes.get(position).locationType_ID));

                            break;
                        }
                    }
                }
            }
        });
        //tView1.setText(accessTypes.get(position).accessDes);
        byte[] bytes = Base64.decode(locationTypes.get(position).locationImage, Base64.DEFAULT);
        Bitmap bmp = getBitmap(bytes);
        imageView.setImageBitmap(bmp);

        return rowView;
    }

    public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }
}

